package org.josfranmc.gutenberg.db;

import java.util.Properties;

/**
 * This class allows to build a concrete <code>DbConnection</code> object according to a specific setting.<br>
 * Configuration can be specificed by file or with an object <code>Properties</code>. By default, the file <i>db/DbConnection.properties</i> is loaded
 * @version 1.0
 * @author josfranmc
 * @see DbConnection
 */
public class DbConnectionBuilder {

	private Properties properties;

	public DbConnectionBuilder() {
		properties = new Properties();
		properties.put("DbType", "HSQL");
		properties.put("DatabaseDriver", "org.hsqldb.jdbcDriver");
		properties.put("HSQL.url", "jdbc:hsqldb:file:catalog/gutenberg");
		properties.put("HSQL.user", "SA");
		properties.put("HSQL.password", "");
	}
	
	/**
	 * Builds a <code>DbConnection</code> object that allow 
	 * @return a <code>DbConnection</code> object
	 * @see DbConnection
	 */
	public DbConnection build() {
		DbConnection dbConnection = getConnectionClass();
		dbConnection.loadDriver(properties.getProperty("DatabaseDriver"));
		dbConnection.setConnectionSetting(properties);
		return dbConnection;
	}
	
	/**
	 * Sets a <code>Properties</code> object with the setting to connect to a database. This setting will be used to configure the <code>DatabaseConnection</code> object that will
	 * allow to get connections to database.
	 * @param properties <code>Properties</code> object with setting params 
	 * @throws IllegalArgumentException
	 * @return a reference to the <code>DatabaseConnectionBuilder</code> object that call this method
	 */
	public DbConnectionBuilder setSettingProperties(Properties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("Properties object can not be null");
		}
		this.properties = properties;
		return this;
	}
	
	private DbConnection getConnectionClass() {
		DbConnection dbconnection = null;
		if (properties.getProperty("DbType").equals("HSQL")) {
			dbconnection = new HSQLConnection();
		} else if (properties.getProperty("DbType").equals("MySQL")) {
			dbconnection = new MySQLConnection();
		} else if (properties.getProperty("DbType").equals("PostgresSQL")) {
			dbconnection = new PostgreSQLConnection();
		} else {
			dbconnection = new HSQLConnection();
		}
		return dbconnection;
	}
}
