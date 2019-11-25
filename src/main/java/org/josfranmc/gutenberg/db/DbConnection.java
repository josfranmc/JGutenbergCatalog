package org.josfranmc.gutenberg.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class serves as template to define specific classes that serve to connect to databases.
 * @version 1.0
 * @author josfranmc
 */
public abstract class DbConnection {

	private static final Logger log = Logger.getLogger(DbConnection.class);
	
	private String urlDb;

	private String user;
	
	private String password;
	
	
	/**
	 * Loads a database driver class. This implementation should be valid for most cases.
	 * @param driverClass driver class name to load
	 */
	protected void loadDriver(String driverClass) {
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			log.warn("Problems loading database driver " + driverClass + ". Using default mechanism.");
		}
	}
	
	/**
	 * Sets up a database connection acording to a particular database.<br>
	 * Classes that extend <code>AbstractDatabaseConnection</code> must overwrite this method with the specific data to establish a connection
	 * to the type of database they represent.<p>
	 * <code>Properties</code> object should contain at least <i>database url</i>, <i>user name</i> and <i>password</i>.
	 * @param settingProperties <code>Properties</code> object with setting data
	 */
	protected abstract void setConnectionSetting(Properties settingProperties);
	
	/**
	 * @return returns a <code>Connection</code> object to a database
	 * @throws SQLException
	 * @see Connection
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getUrlDb(), getUser(), getPassword());
	}

	/**
	 * @return database url
	 */
	public String getUrlDb() {
		return urlDb;
	}

	/**
	 * Allows to set database url
	 * @param urlDb database url
	 */
	protected void setUrlDb(String urlDb) {
		this.urlDb = urlDb;
	}

	/**
	 * @return database user used to connect
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Allows to set user name.
	 * @param user user name
	 */
	protected void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return user password used to connect
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Allows to set the user password used to connect
	 * @param password password user
	 */
	protected void setPassword(String password) {
		this.password = password;
	}
}
