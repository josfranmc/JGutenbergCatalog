package org.josfranmc.gutenberg.catalog.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.util.PropertiesFile;

/**
 * Permite acceder a una base de datos HSQL.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see IDBConnection
 */
public class HSQLConnection implements IDBConnection {

	private static final Logger log = Logger.getLogger(HSQLConnection.class);
	
	/**
	 * Ruta del fichero de propiedades en el classpath
	 */
	private final static String DB_PROPERTIES_FILE = "db/DBHSQL.properties";
	
	/**
	 * URL de conexión a la base de datos
	 */
	private static String DB_URL;
	
	/**
	 * Nombre de la cuenta de usuario para conectar con la base de datos
	 */
	private static String DB_USER;
	
	/**
	 * Clave de la cuenta de usuario para conectar con la base de datos
	 */
	private static String DB_PASSWORD;
	
	/**
	 * Constructor principal. Lee el fichero de configuración y prepara el objeto para obtener conexiones a la base de datos
	 */
	HSQLConnection() {
		loadConfigConnection();
	}
	
	/**
	 * Devuelve una conexión a la base de datos
	 */
	@Override
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			log.error(e);
			//e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Lee el fichero de propiedades que contiene los datos de configuración para la conexión a la base de datos.
	 * @see PropertiesFile
	 */
	private void loadConfigConnection() {
		Properties prop = PropertiesFile.loadProperty(DB_PROPERTIES_FILE);
		DB_URL = "jdbc:hsqldb:hsql://localhost/" + prop.getProperty("dbname");
		DB_USER = prop.getProperty("user");;
		DB_PASSWORD = prop.getProperty("password");
	}
}
