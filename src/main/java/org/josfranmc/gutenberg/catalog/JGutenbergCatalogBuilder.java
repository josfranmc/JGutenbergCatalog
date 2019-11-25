package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.util.Properties;

import org.josfranmc.gutenberg.db.DbConnection;
import org.josfranmc.gutenberg.db.DbConnectionBuilder;
import org.josfranmc.gutenberg.files.PropertiesFile;

/**
 * Crea y carga en memoria una base de datos con el catálogo de libros del proyecto Gutenberg (<a href="http://www.gutenberg.org/">http://www.gutenberg.org/</a>)<p>
 * Internamente se usa una base de datos HSQLDB, la cual utiliza los ficheros creados en el directorio db/HSQLDB/ que se crea en la carpeta de ejecución del programa.<p>
 * La información de los libros disponibles se encuentra en ficheros RDF, existiendo un fichero por cada libro.
 * Estos ficheros se pueden obtener
 * descargando el zip de <a href="http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip">http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip</a>.
 * Dentro de la carpeta epub del zip se encuentra una carpeta por cada libro del catálogo. El nombre de la carpeta es el mismo que el identificador del
 * libro, es decir, igual que el nombre del fichero del libro. Dentro de cada carpeta se encuentra el fichero RDF corespondiente cuyo nombre es 
 * pg+<i>identificador_libro</i>+.rdf.<p>
 * Ej.: Para libro identificado como <i>45238</i> existe una carpeta <i>45238</i> y dentro de ella un fichero <i>pg45238.rdf</i><p> 
 * Mediante el método <i>createCatalog()</i> se leen los ficheros y se carga en memoria la información consultada. Los datos que se obtienen de cada
 * libro son: identificador, título, autor e idioma. Esots datos se obtienen mediante consultas de tipo sparql a los ficheros RDF. Se usa la librería
 * Apache Jena para ello<p>
 * También se ofrecen una serie de métodos para consultar la base de datos.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see JGutenbergCatalog
 */
public class JGutenbergCatalogBuilder {
	
	private static final String DB_DEFAULT = "db/DbConnection.properties";
	
	private String dbConfigFile;
	
	private String rdfRepository;

	
	public JGutenbergCatalogBuilder () {
		this.dbConfigFile = null;
		this.rdfRepository = null;
	}
	
	/**
	 * Builds a <code>JGutenbergCatalog</code> object acoording to the previously established configuration.<br>
	 * If a database connection has not been specifed, a HSQL database is created. This database is called <i>gutenbergcatalog</i> and is stored in <i>db</i> folder.
	 * @return a <code>JGutenbergCatalog</code> object
	 */
	public JGutenbergCatalog build() {
		if (this.rdfRepository == null) {
			throw new GutenbergCatalogException("You must specify the folder where RDF files are stored");
		}
		return new JGutenbergCatalog(this.rdfRepository, getDbConnection());
	}

	private DbConnection getDbConnection() {
		Properties properties = new Properties();
		if (this.dbConfigFile == null) {
			properties = PropertiesFile.loadPropertiesFromResource(DB_DEFAULT);
		} else {
			properties = PropertiesFile.loadPropertiesFromFileSystem(this.dbConfigFile);
		}
		return new DbConnectionBuilder().setSettingProperties(properties).build();
	}

	/**
	 * Sets the path to the folder where RDF files are.
	 * @param rdfPath folder path
	 */
	public void setRdfRepository(String rdfPath) {
		File path = new File(rdfPath);
		if (rdfPath == null || !path.exists()) {
			throw new IllegalArgumentException("Invalid path to RDF container");
		}
		this.rdfRepository = rdfPath;
	}
	
	/**
	 * Sets the path to the database setting file.
	 * @param dbConfigFile file path
	 */
	public void setDatabase(String dbConfigFile) {
		File path = new File(dbConfigFile);
		if (dbConfigFile == null || !path.exists()) {
			throw new IllegalArgumentException("Invalid database setting file");
		}
		this.dbConfigFile = dbConfigFile;
	}
}
