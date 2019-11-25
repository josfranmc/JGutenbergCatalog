package org.josfranmc.gutenberg.db;

import java.util.Properties;

/**
 * Class for managing connections to PostgreSQL databases.
 * @version 1.0
 * @author josfranmc
 * @see DbConnection
 */
public final class PostgreSQLConnection extends DbConnection {
	
	PostgreSQLConnection() {
		
	}

	/**
	 * Sets up a database connection to a PostgreSQL database.
	 * <code>Properties</code> object must contain the following properties keys:
	 * <ul>
	 * <li>PostgreSQL.host</li>
	 * <li>PostgreSQL.port</li>
	 * <li>PostgreSQL.dbname</li>
	 * <li>PostgreSQL.user</li>
	 * <li>PostgreSQL.password</li>
	 * </ul>
	 * @param settingProperties <code>Properties</code> object with setting data
	 * @throws IllegalArgumentException
	 * @see DbConnection
	 */
	@Override
	protected void setConnectionSetting(Properties settingProperties) {
		if (settingProperties == null) {
			throw new IllegalArgumentException("Properties parameter must not be null");
		}
		
		setUrlDb(buildUrl(settingProperties));
		setUser(settingProperties.getProperty("PostgreSQL.user"));
		setPassword(settingProperties.getProperty("PostgreSQL.password"));
	}
	
	private String buildUrl(Properties settingProperties) {
		String url = "jdbc:postgresql://" + settingProperties.getProperty("PostgreSQL.host") + ":" + settingProperties.getProperty("PostgreSQL.port") + "/" + settingProperties.getProperty("PostgreSQL.dbname");
		if (settingProperties.getProperty("PostgreSQL.params") != null) {
			url = url + "?" + settingProperties.getProperty("PostgreSQL.params");
		}
		return url;
	}
}
