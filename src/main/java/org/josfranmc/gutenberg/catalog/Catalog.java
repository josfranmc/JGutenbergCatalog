package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.db.DbConnection;

/**
 * It allows to store in a database the information about the book catalog of the Gutenberg project.<br>
 * It needs two elements:
 * <ul>
 * <li>the path to the folder that contains RDF files about books</li>
 * <li>a connection to the database where to save the data</li>
 * </ul>
 * The catalog exists as a collection of RDF files. These files are queried and the result is loaded into a database.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see JGutenbergCatalog
 * @see RdfFile
 * @see Book
 */
public class Catalog {
	
	private static final Logger log = Logger.getLogger(Catalog.class);

	/**
	 * The path to the folder that contains RDF files about books
	 */
	private File rdfFilesFolder;
	
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
	private List<RdfFile> rdfFiles;
	
	/**
	 * @param rdfFilesFolder path to the folder that store the RDF files
	 * @param dbConnection connection to the database where to load data
	 */
	Catalog(File rdfFilesFolder, DbConnection dbConnection) {
		if (rdfFilesFolder == null || !rdfFilesFolder.exists()) {
			throw new IllegalArgumentException("Invalid path to RDF container.");
		}
		this.rdfFilesFolder = rdfFilesFolder;

		if (dbConnection == null) {
			throw new IllegalArgumentException("There is not object to managing database connection.");
		}
		try {
			this.connection = dbConnection.getConnection();
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new GutenbergCatalogException(e.getMessage());
		}
		
		rdfFiles = new ArrayList<>();
	}
	
	/**
	 * Creates the book catalog. It reads the RDF files that make up the catalog and loads these data in a database.
	 * @see RdfFile
	 * @see Book
	 */
	public void create() {
		log.info("[INFO] LOADING CATALOG IN DB " + getCurrentTime());
		createTableForBooks();
		createStatementForInsert();
		createStatementForSelect();

		for (RdfFile rdfFile : getRdfFiles()) {
			if (!isBookInDatabase(rdfFile.getId())) {
				Book book = rdfFile.getBook();
				saveBook(book);
			}
		}
		commitAndClose();
		log.info("[INFO] LOAD COMPLETE " + getCurrentTime());
	}
	
	/**
	 * Returns a <code>List</code> of <code>RdfFile</code> objects that represents the collection of RDF files.<br>
	 * The base folder is scrolled. For each folder a <code>RdfFile</code> object is created.
	 * @return a <code>List</code> of <code>RdfFile</code> objects
	 * @see RdfFile
	 */
	public List<RdfFile> getRdfFiles() {
		for (File folder : this.rdfFilesFolder.listFiles()) {
			if (!folder.getName().toLowerCase().contains("delete")) {
				try {
					RdfFile rdfFile = new RdfFile(folder);
					this.rdfFiles.add(rdfFile);
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage());
				}
			}
		}
		return this.rdfFiles;
	}
	
	/**
	 * Checks if a book already exists in database.
	 * @param id book identify to check
	 * @return <i>true</i> if the book exists in database, <i>false</i> otherwise
	 */
	public boolean isBookInDatabase(String id) {
		boolean result = false;
		PreparedStatement pstatement = null;
		ResultSet resultSet = null;
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
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e) {
				log.error(e);
			}
		}
		return result;
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
