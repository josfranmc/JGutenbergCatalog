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
 * It allows to store in database the information about Gutenberg book catalog.<p>
 * El catálogo se construye utilizando los ficheros RDF que previamente han tenido que ser descargados y descomprimidos en una carpeta. La dirección para
 * obtener el zip es <a href="http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip">http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip</a>
 * Dentro de la carpeta epub del zip se encuentra una carpeta por cada libro del catálogo. El nombre de la carpeta es el mismo que el identificador del
 * libro, es decir, igual que el nombre del fichero del libro. Dentro de cada carpeta se encuentra el fichero RDF corespondiente cuyo nombre es 
 * pg+<i>identificador_libro</i>+.rdf.<p>
 * Ej.: Para libro identificado como <i>45238</i> existe una carpeta <i>45238</i> y dentro de ella un fichero <i>pg45238.rdf</i><p>
 * Los datos que se obtienen de cada libro son: identificador, título, autor e idioma. Esots datos se obtienen mediante consultas de tipo sparql a los
 * ficheros RDF.<p>
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see 
 */
public class Catalog {
	
	private static final Logger log = Logger.getLogger(Catalog.class);
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static final String FILE_PREFIX = "pg";
	
	public static final String FILE_EXTENSION = ".rdf";
	
	/**
	 * The path to the folder that contains RDF files
	 */
	private String rdfFilesPath = null;
	
	/**
	 * Connection to the database where to save the read data
	 */
	private Connection connection = null;
	
	/**
	 * SQL statement for insert a book
	 */
	private PreparedStatement insertStatament = null;
	
	/**
	 * SQL statement for select a book
	 */
	private PreparedStatement selectStatament = null;
	
	
	/**
	 * @param rdfFilesPath path to the folder that store the RDF files
	 * @param dbConnection connection to the database where to load data
	 */
	Catalog(String rdfFilesPath, DbConnection dbConnection) {
		if (rdfFilesPath == null || rdfFilesPath.isEmpty()) {
			throw new IllegalArgumentException("You must specify the folder where RDF files are stored.");
		}
		if (!rdfFilesPath.endsWith(FILE_SEPARATOR)) {
			this.rdfFilesPath = rdfFilesPath + FILE_SEPARATOR;
		}

		if (dbConnection == null) {
			throw new IllegalArgumentException("There is not object to managing database connection.");
		}
		try {
			this.connection = dbConnection.getConnection();
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new GutenbergCatalogException(e.getMessage());
		}
	}
	
	/**
	 * Creates the book catalog. It reads the RDF files that make up the catalog and loads these data in a database.
	 * @see Book
	 * @see QueryRdfBook
	 */
	public void create() {
		log.info("LOADING CATALOG IN DB" + getCurrentTime());
		createTableForBooks();
		createStatementForInsert();
		createStatementForSelect();
		
		File catalogFolder = new File(this.rdfFilesPath);		
		for (File rdfFile : getFilesPath(catalogFolder)) {
			if (rdfFile.exists() && !isBookInDatabase(rdfFile)) {
				Book book = QueryRdfBook.getBook(rdfFile.getAbsolutePath());
				saveBook(book);
			}
		}
		commitAndClose();
		log.info("END LOAD CATALOG IN DB " + getCurrentTime());
	}
	
	/**
	 * Returns a <code>List</code> with the path of all files to read.<p>
	 * The base folder is scrolled. The name of every directory in this folder is a number. Every directory contains a RDF file.
	 * The name of the file is: pg + directory_name + .rdf<p>
	 * e.g.: the book with id 56789 is stored in the file <i>pg56789.rdf</i>, inside of the <i>56789</i> folder.
	 * @param folderCatalog base folder with RDF files
	 * @return the <code>List</code> with the path of all files to read
	 * 
	 */
	private List<File> getFilesPath(File folderCatalog) {
		List<File> filesPath = new ArrayList<>();
		File catalogFolder = new File(this.rdfFilesPath);
		for (File folder : catalogFolder.listFiles()) {
			if (!folder.getName().startsWith("DELETE")) {
				String filePath = this.rdfFilesPath + folder.getName() + FILE_SEPARATOR + FILE_PREFIX + folder.getName() + FILE_EXTENSION;
				File file = new File(filePath);
				if (file.exists()) {
					filesPath.add(file);
				} else {
					log.warn("Wrong file: " + filePath);
				}
			}
		}
		return filesPath;
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
       	 	log.debug("Guardado libro " + book.getId());
		} catch (SQLIntegrityConstraintViolationException e) {
			log.warn("Error saving " + book.getId() + ". Book already exists");
		} catch (SQLException e) {
			log.warn("Error saving " + book.getId() + ". " + e.toString());
		}
	}

	/**
	 * Checks if a book already exists in database.
	 * @param id book identify to check
	 * @return <i>true</i> if the book exists in database, <i>false</i> otherwise
	 */
	public boolean isBookInDatabase(File filePath) {
		boolean result = false;
		PreparedStatement pstatement = null;
		ResultSet resultSet = null;
		
		String id = getBookId(filePath.getName());
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
	
	private String getBookId(String cad) {
		int init = cad.lastIndexOf(Catalog.FILE_PREFIX) + 2;
		int end = cad.lastIndexOf(Catalog.FILE_EXTENSION);
		return cad.substring(init, end);		
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
	
	private String getCurrentTime() {
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		return hourFormat.format(date);
	}
}
