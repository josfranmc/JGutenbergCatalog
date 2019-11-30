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
 */
package org.josfranmc.gutenberg.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * Class for managing connections to MySQL databases. It uses a MysqlDataSource object.
 * @version 1.0
 * @author josfranmc
 * @see DbConnection
 * @see MysqlDataSource
 */
public final class MySQLConnection extends DbConnection {

	private MysqlDataSource dataSource;
	
	MySQLConnection() {
		
	}

	/**
	 * Sets up a database connection to a MySQL database using a MysqlDataSource object.
	 * <code>Properties</code> object must contain the following properties keys:
	 * <ul>
	 * <li>MySQL.host</li>
	 * <li>MySQL.port</li>
	 * <li>MySQL.dbname</li>
	 * <li>MySQL.user</li>
	 * <li>MySQL.password</li>
	 * </ul>
	 * Optional properties are:
	 * <ul>
	 * <li>MySQL.ssl (default FALSE)</li>
	 * <li>MySQL.allowPublicKeyRetrieval (default TRUE)</li>
	 * <li>MySQL.params (url parameters)</li>
	 * </ul>
	 * @param settingProperties <code>Properties</code> object with setting data
	 * @throws IllegalArgumentException
	 * @see DbConnection
	 * @see MysqlDataSource
	 */
	@Override
	protected void setConnectionSetting(Properties settingProperties) {
		if (settingProperties == null) {
			throw new IllegalArgumentException("Properties parameter must not be null");
		}

		dataSource = new MysqlDataSource();
		
		dataSource.setUser(settingProperties.getProperty("MySQL.user"));
		dataSource.setPassword(settingProperties.getProperty("MySQL.password"));
		dataSource.setDatabaseName(settingProperties.getProperty("MySQL.dbname"));
		dataSource.setServerName(settingProperties.getProperty("MySQL.host"));
		try {
			String sslProperty = settingProperties.getProperty("MySQL.ssl");
			if (sslProperty == null) {
				sslProperty = "FALSE";
			}
			dataSource.setUseSSL(Boolean.parseBoolean(sslProperty.toUpperCase()));
			
			String allowPublic = settingProperties.getProperty("MySQL.allowPublicKeyRetrieval");
			if (allowPublic == null) {
				allowPublic = "TRUE";
			}
			dataSource.setAllowPublicKeyRetrieval(Boolean.parseBoolean(allowPublic.toUpperCase()));
			
			dataSource.setServerTimezone("UTC");
		} catch (SQLException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, "Problems setting MysqlDataSource.", e);
		}
		
		setUrlDb(buildUrl(settingProperties));
		setUser(settingProperties.getProperty("MySQL.user"));
		setPassword(settingProperties.getProperty("MySQL.password"));
	}
	
	/**
	 * @return returns a Connection object to a MySQL database
	 * @throws SQLException
	 * @see Connection
	 * @see DbConnection
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	private String buildUrl(Properties settingProperties) {
		String url = "jdbc:mysql://" + settingProperties.getProperty("MySQL.host") + ":" + settingProperties.getProperty("MySQL.port") + "/" + settingProperties.getProperty("MySQL.dbname");
		if (settingProperties.getProperty("MySQL.params") != null) {
			url = url + "?" + settingProperties.getProperty("MySQL.params");
		}
		return url;
	}
}
