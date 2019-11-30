/*
 *  Copyright (C) 2018-2019 Jose Francisco Mena Ceca <josfranmc@gmail.com>
 *
 *  This file is part of JGutenbergCatalog.
 *
 *  JGutenbergCatalog is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JGutenbergCatalog is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JGutenbergCatalog.  If not, see <https://www.gnu.org/licenses/>.
 *    
 *  This file includes software developed at
 *  The Apache Software Foundation (http://www.apache.org/). 
 */
package org.josfranmc.gutenberg.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.db.DbConnection;

/**
 * It allows to load a collection of <code>RdfFiles</code> objects in a database.<p>
 * Each <code>RdfFile</code> object is inserted in a table called <i>Books</i>. If this table doesn't exist in the database, it is created.<br>
 * You may get a collection of <code>RdfFiles</code> objects from a <code>CatalogRdf</code> object.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see CatalogRdf
 * @see RdfFile
 * @see Book
 */
public class CatalogDb {

	private static final Logger log = Logger.getLogger(CatalogDb.class);

	/**
	 * Connection to the database where to save the data about books
	 */
	private Connection connection;
	
	/**
	 * SQL statement for insert a book
	 */
	private PreparedStatement insertStatament;
	
	/**
	 * SQL statement for select a book
	 */
	private PreparedStatement selectStatament;
	
	/**
	 * Collection of RDF files
	 */
	private Map<String, RdfFile> rdfCatalog;
	
	
	/**
	 * @param rdfFiles a <code>RdfFile</code> objects collection that make up the Gutenberg catalog.
	 * @param dbConnection database where to save the data about books
	 */
	public CatalogDb(Map<String, RdfFile> rdfCatalog, DbConnection dbConnection) {
		if (rdfCatalog == null) {
			throw new IllegalArgumentException("RDF collection is Null.");
		}
		this.rdfCatalog = rdfCatalog;

		initializeDbConnection(dbConnection);
	}
	
	/**
	 * @param dbConnection database where to save the data about books
	 */
	public CatalogDb(DbConnection dbConnection) {
		initializeDbConnection(dbConnection);
	}
	
	private void initializeDbConnection(DbConnection dbConnection) {
		if (dbConnection == null) {
			throw new IllegalArgumentException("No manager to database connection.");
		}
		try {
			this.connection = dbConnection.getConnection();
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**
	 * Loads the book catalog in a database. It takes the <code>RdfFile</code> objects collection that make up the catalog (collection previously assigned).<br>
	 * It only loads new information. RDF files already loaded are ignored.
	 * @see RdfFile
	 * @see Book
	 */
	public void load() {
		if (getRdfCatalog() != null && !getRdfCatalog().isEmpty()) {
			log.info("Loading catalog in DB... " + getCurrentTime());
			createTableForBooks();
			createStatementForInsert();
			createStatementForSelect();
	
			getRdfCatalog().forEach((bookId, rdfFile) -> {
				if (!isBookInDatabase(bookId)) {
					Book book = rdfFile.getBook();
					saveBook(book);
				}
			});
			
			commitAndClose();
			log.info("Load complete " + getCurrentTime());
		} else {
			log.warn("Loading catalog in DB: No RDF catalog to process");
		}
	}
	
	/**
	 * Checks if a book already exists in the database.
	 * @param id book identify to check
	 * @return <i>true</i> if the book exists in database, <i>false</i> otherwise
	 */
	public boolean isBookInDatabase(String id) {
		boolean result = false;
		ResultSet resultSet = null;
		if (this.selectStatament == null) {
			createTableForBooks();
			createStatementForSelect();
		}
		try {
			this.selectStatament.setString(1, id);
			resultSet = this.selectStatament.executeQuery();
			while (resultSet.next()) {
				result = true;
			}
		} catch (SQLException e) {
			log.error(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				log.error(e);
			}
		}
		return result;
	}
	
	/**
	 * Returns the <code>RdfFile</code> objects collection with which the object was initialized.
	 * @return a <code>RdfFile</code> objects collection
	 */
	public Map<String, RdfFile> getRdfCatalog() {
		return this.rdfCatalog;
	}
	
	/**
	 * Sets a <code>RdfFile</code> objects collection that make up the Gutenberg catalog.
	 * @param rdfCatalog a <code>RdfFile</code> objects collection
	 */
	public void setRdfCatalog(Map<String, RdfFile> rdfCatalog) {
		this.rdfCatalog = rdfCatalog;
	}
	
	/**
	 * It saves a book in the database.
	 * @param book object <code>Book</code> to save
	 */
	private void saveBook(Book book) {
		try {
			this.insertStatament.setString(1, book.getId());
			this.insertStatament.setString(2, book.getAuthor());
			this.insertStatament.setString(3, book.getTitle());
			this.insertStatament.setString(4, book.getLanguage());
			this.insertStatament.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			log.warn("SQLIntegrityConstraintViolationException: book " + book.getId());
		} catch (SQLException e) {
			log.warn("Error saving " + book.getId() + ". " + e.toString());
		}
	}

	private void createTableForBooks() {
		Statement statement = null;
		try {
			statement = this.connection.createStatement();
       	 	statement.executeUpdate("CREATE TABLE books (" + 
     	 		" ID varchar(10) PRIMARY KEY," + 
     	 		" author varchar(300) NULL, " + 
     	 		" title varchar(1000) NULL," + 
     	 		" language varchar(3) NULL," +
     	 		");");
       	 	log.info("BOOKS table created.");
		} catch (SQLException e) {
			log.info("Using the existing BOOKS table.");
		} finally {
        	try {
    		    if (statement != null) {
    		     statement.close();
    		    }
        	} catch (Exception e) {
        		log.error(e);
        	}
		}
	}
	
	private void createStatementForInsert() {
		try {
			this.insertStatament = this.connection.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?)");
		} catch (SQLException e) {
			log.error(e);
		}
	}
	
	private void createStatementForSelect() {
		try {
			this.selectStatament = this.connection.prepareStatement("SELECT * FROM books WHERE id = ?");
		} catch (SQLException e) {
			log.error(e);
		}
	}
	
	/**
	 * Commit changes and close database connection.
	 */
	private void commitAndClose() {
		try {
			if (this.insertStatament != null) {
				this.insertStatament.close();
			}
			if (this.selectStatament != null) {
				this.selectStatament.close();
			}
			if (this.connection != null) {
				this.connection.commit();
				this.connection.close();
			}
		} catch (SQLException e) {
			log.error(e);
		}
	}
	
	private String getCurrentTime() {
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		return hourFormat.format(date);
	}
}
