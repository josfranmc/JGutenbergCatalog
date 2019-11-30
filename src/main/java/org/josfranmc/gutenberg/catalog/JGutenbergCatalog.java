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

import java.io.File;
import java.util.Map;
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
 * You can query the RDF files and you can create a database where to load the data.
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
	 * Catalog of RDF files
	 */
	private CatalogRdf catalogRdf;	
	
	/**
	 * Catalog of RDF files
	 */
	private CatalogDb catalogDb;

	
	/**
	 * Creates the object for managing the construction of the catalog.
	 * @param rdfFilesPath path to the folder that store the RDF files
	 */
	public JGutenbergCatalog(String rdfFilesPath) {
		if (rdfFilesPath == null) {
			throw new IllegalArgumentException("Invalid null value for path to RDF container");
		}
		File path = new File(rdfFilesPath);
		if (!path.exists()) {
			throw new IllegalArgumentException("Invalid path to RDF container");
		}

		this.catalogRdf = new CatalogRdf(path);
		
		this.catalogDb = new CatalogDb(getDbConnection(DB_DEFAULT));
	}
	
	/**
	 * It reads the book catalog, which is in the form of RDF files. The data is stored in memory.<br>
	 */
	public void readRdfFiles() {
		this.catalogRdf.readFiles();
	}
	
	/**
	 * Returns the collection that make up the book catalog.
	 * @return the collection that make up the book catalog
	 */
	public Map<String, RdfFile> getRdfCatalog() {
		return this.catalogRdf.getRdfCatalog();
	}

	/**
	 * Sets the database setting to use for load data.
	 * @param dbConfigFile database setting file
	 */
	public void setDatabase(String dbConfigFile) {
		if (dbConfigFile == null) {
			throw new IllegalArgumentException("Invalid null value for database setting file");
		}
		
		this.catalogDb = new CatalogDb(getDbConnection(dbConfigFile));
	}
	
	/**
	 * It loads the book catalog in a database.<br>
	 * If it hasn't done it before, the RDF files are read.<br>
	 * It only loads new information. RDF files already loaded are ignored.
	 * @param dbConfigFile file with data to set up the database connection
	 */
	public void loadDb() {
		if (catalogRdf.getRdfCatalog().isEmpty()) {
			catalogRdf.readFiles();
		}
		catalogDb.setRdfCatalog(catalogRdf.getRdfCatalog());
		catalogDb.load();
	}
	
	/**
	 * Returns a <code>Book</code> object .
	 * @param id book identifier
	 * @return the book acording to the identifier that is passed
	 */
	public Book getBook(String id) {
		return catalogRdf.getRdfFile(id).getBook();
	}
	
	private DbConnection getDbConnection(String dbConfigFile) {
		Properties properties = null;
		if (dbConfigFile.equals(DB_DEFAULT)) {
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
	 * Main method for running the application.
	 * @param args list of arguments with application parameters
	 */
	public static void main(String [] args){
		if (args.length == 0 || (args[0].equals("-h") || args[0].equals("-help"))) {
			showHelp();
		} else {
			String rdfFolder = null;
			String dbFile = null;
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

			JGutenbergCatalog jg = new JGutenbergCatalog(rdfFolder);
			if (dbFile != null) {
				jg.setDatabase(dbFile);
			}
			jg.loadDb();
		}
	}
	
	private static void showHelp() {
		log.info("");
		log.info("Usage: java -jar JGutenbergCatalog [options]");
		log.info("Options:");
		log.info("   -r xxx (xxx path to the RDF files folder)");
		log.info("   -b xx  (xx  path to the database setting file)");
		log.info("");
		log.info("(only -h to show options list)");
		log.info("");
	}
}
