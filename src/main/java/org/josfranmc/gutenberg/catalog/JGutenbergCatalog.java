package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.db.DbConnection;
import org.josfranmc.gutenberg.db.DbConnectionBuilder;
import org.josfranmc.gutenberg.files.PropertiesFile;

/**
 * It allows to manager the Gutenberg project book catalog.<p>
 * The catalog is a collection of RDF files that you can get from this link: <a href="http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip">http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip</a>.<br>
 * There are a series of folders inside the <i>cache/epub</i> directory, a folder for each book. The name of each folder is the identifier of each book.
 * Inside each folder is the corresponding RDF file whose name is "pg+<i>book_identifier</i>+.rdf".<p>
 * e.g.: For book <i>45238</i> there is a folder called <i>45238</i> and within it a file called <i>pg45238.rdf</i><p>
 * By default, if no database is specified a HSQL database is created. This database is located in a folder called <i>catalog</i> and its name is <i>gutenberg</i>.
 * Setting data are loaded from a resource file which path is <i>db/DbConnection.properties</i>.<br>
 * Alternativaly, you can specify a setting file to use either a MySQL or a PostgresSQL database.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see Catalog
 * @see DbConnection
 */
public class JGutenbergCatalog {

	private static final Logger log = Logger.getLogger(JGutenbergCatalog.class);
	
	private static final String DB_DEFAULT = "db/DbConnection.properties";
	
	/**
	 * The path to the folder that contains RDF files about books
	 */
	private File rdfFilesFolder;
	
	/**
	 * Database where to save the data about books
	 */
	DbConnection dbConnection;

	/**
	 * Catalog of RDF files
	 */
	private Catalog catalog;

	
	/**
	 * Creates the object for managing the construction of the catalog. It uses a default database.
	 * @param rdfFilesPath path to the folder that store the RDF files
	 */
	public JGutenbergCatalog(String rdfFilesPath) {
		this(rdfFilesPath, DB_DEFAULT);
	}
	
	/**
	 * Creates the object for managing the construction of the catalog.
	 * @param rdfFilesPath path to the folder that store the RDF files
	 * @param dbConnection connection to the database where to load data
	 */
	public JGutenbergCatalog(String rdfFilesPath, String dbConfigFile) {
		if (rdfFilesPath == null) {
			throw new IllegalArgumentException("Invalid null value for path to RDF container");
		}
		File path = new File(rdfFilesPath);
		if (!path.exists()) {
			throw new IllegalArgumentException("Invalid path to RDF container");
		}
		this.rdfFilesFolder = path;
		
		if (dbConfigFile == null) {
			throw new IllegalArgumentException("Invalid null value for database setting file");
		}
		this.dbConnection = getDbConnection(dbConfigFile);

		this.catalog = new Catalog(this.rdfFilesFolder, this.dbConnection);
	}
	
	private DbConnection getDbConnection(String dbConfigFile) {
		Properties properties = new Properties();
		if (dbConfigFile == DB_DEFAULT) {
			properties = PropertiesFile.loadPropertiesFromResource(DB_DEFAULT);
		} else {
			File path = new File(dbConfigFile);
			if (!path.exists()) {
				throw new IllegalArgumentException("Invalid path to database setting file");
			}
			properties = PropertiesFile.loadPropertiesFromFileSystem(dbConfigFile);
		}
		return new DbConnectionBuilder().setSettingProperties(properties).build();
	}
	
	/**
	 * It loads the book catalog, which is stored in RDF files, in a database.<br>
	 * It only loads new information. RDF files already loaded are ignored.
	 */
	public void createCatalog() {
		this.catalog.create();
	}

	/**
	 * Main method for running the application.
	 * @param args list of arguments with application parameters
	 */
	public static void main(String [] args){
		if (args.length == 0 || (args[0].equals("-h") || args[0].equals("-help"))) {
			showHelp();
		} else {
			String rdfFolder = null;
			String dbFile = DB_DEFAULT;
			for (int i = 0; i < args.length; i+=2) {
				try {
					if (args[i].equals("-r")) {
						rdfFolder = args[i+1];
					} else if (args[i].equals("-b")) {
						dbFile = args[i+1];
					} else {
						throw new IllegalArgumentException("Parameter " + args[i]);
					}
				} catch (ArrayIndexOutOfBoundsException a) {
					throw new IllegalArgumentException("Parameter " + args[i]);
				}
			}

			JGutenbergCatalog jg = new JGutenbergCatalog(rdfFolder, dbFile);					
			jg.createCatalog();
		}
	}
	
	private static void showHelp() {
		log.info("");
		log.info("Usage: java -jar JGutenbergCatalog [options]");
		log.info("Options:");
		log.info("   -r xxx (xxx type of files to download, default: txt)");
		log.info("   -b xx  (xx  language of books to download, default: es)");
		log.info("");
		log.info("(only -h to show options list)");
		log.info("");
	}
}
