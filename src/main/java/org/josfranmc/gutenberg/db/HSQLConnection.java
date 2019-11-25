package org.josfranmc.gutenberg.db;

import java.util.Properties;

/**
 * Class for managing connections to HSQL databases.
 * @version 1.0
 * @author josfranmc
 * @see DbConnection
 */
public final class HSQLConnection extends DbConnection {

	HSQLConnection() {

	}

	/**
	 * Sets up a database connection to a HSQL database.
	 * <code>Properties</code> object must contain the following properties keys:
	 * <ul>
	 * <li>HSQL.url</li>
	 * <li>HSQL.user</li>
	 * <li>HSQL.password</li>
	 * </ul>
	 * @param settingProperties <code>Properties</code> object with setting data
	 * @throws IllegalArgumentException
	 * @see DbConnection
	 */
	@Override
	protected void setConnectionSetting(Properties settingProperties) {
		if (settingProperties == null) {
			throw new IllegalArgumentException("Properties type parameter must not be null");
		}

		setUrlDb(settingProperties.getProperty("HSQL.url"));
		setUser(settingProperties.getProperty("HSQL.user"));
		setPassword(settingProperties.getProperty("HSQL.password"));
	}
}
