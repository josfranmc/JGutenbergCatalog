package org.josfranmc.gutenberg.catalog.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.josfranmc.gutenberg.util.PropertiesFile;

/**
 * Permite crear y ejecutar un servidor de base de datos HSQL.<p>
 * Los datos de conexión y base de datos a utilizar se recuperan del ficherro de propiedades db/DBHSQL.properties
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class HSQLServer {

	private final static String DB_PROPERTIES_FILE = "db/DBHSQL.properties";

	/**
	 *  Mostrar o no inforamción extra de depuración.
	 */
	private boolean trace;
	
	/**
	 * Referencia a la base de datos
	 */
	private Server server = null;
	
	/**
	 * Nombre (alias) de la base de dato
	 */
	private String dbname = null;
	
	/**
	 * Ruta de los ficheros a usar por la base de datos
	 */
	private String dbpath = null;
	
	/**
	 * Tipo de base de datos
	 */
	private String type = null;


	/**
	 * Constructor por defecto.
	 */
	public HSQLServer() {
		this.trace = false;
		this.server = new Server();
	}
	
	/**
	 * Inicia la ejecución del servidor de base de datos.
	 */
	public void startDb() {
		try {
			this.server.setProperties(getHsqlProperties());
			this.server.setTrace(this.trace);
			this.server.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AclFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Termina la ejecución del servidor de base de datos.
	 */
	public void shutdownDb() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.createStatement();
			statement.executeQuery("SHUTDOWN COMPACT");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return <i>true</i> si el servidor está en ejecución, <i>false</i> en caso contrario.
	 */
	public boolean isServerRunning() {
		return !this.server.isNotRunning();
	}
	
	/**
	 * Estable si se debe mostrar inforamción extra de depuración.
	 * @param trace <i>true</i> para sacar información extra, <i>false</i> si no se desea
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	
	/**
	 * @return la ruta donde se guardan los ficheros de la base de datos
	 */
	public String getPathDb() {
		return server.getDatabasePath(0, true);
	}
	
	/**
	 * @return las propiedades de configuración.
	 */
	private HsqlProperties getHsqlProperties() {
		Properties prop = PropertiesFile.loadProperty(DB_PROPERTIES_FILE);
		this.type = prop.getProperty("type");
		this.dbname = prop.getProperty("dbname");
		this.dbpath = prop.getProperty("dbpath");
		
		HsqlProperties hsqlProperties = new HsqlProperties();
		hsqlProperties.setProperty("server.database.0", this.type + ":" + this.dbpath);
		hsqlProperties.setProperty("server.dbname.0", this.dbname);
		return hsqlProperties;
	}
}
