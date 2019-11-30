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
