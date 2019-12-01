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
package org.josfranmc.gutenberg.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class for managing properties files.
 * @version 1.0
 */
public class PropertiesFile {
	
	PropertiesFile() {
	    throw new IllegalStateException("Class cannot be instantiated");
  }
	
	/**
	 * It reads a properties file from classpath and returns a <code>Properties</code> object with the data read.
	 * @param propertiesFile path of file to load
	 * @return a <code>Properties</code> object with the data read
	 * @throws IllegalArgumentException
	 * @see Properties
	 */
	public static Properties loadPropertiesFromResource(String propertiesFile){
		if (propertiesFile == null || propertiesFile.isEmpty()) {
			throw new IllegalArgumentException("Properties file can not be null or empty");
		}
		Properties properties = new Properties();
		try {
			InputStream inputStream = PropertiesFile.class.getClassLoader().getResourceAsStream(propertiesFile);
			properties.load(inputStream);
		} catch (IOException | NullPointerException | IllegalArgumentException e) {
			throw new IllegalArgumentException("Properties file could not be loaded");
		}
		return properties;
	}
	
	/**
	 * It reads a properties file from file system and returns a <code>Properties</code> object with the data read.
	 * @param propertiesFile path of file to load
	 * @return a <code>Properties</code> object with the data read
	 * @throws IllegalArgumentException
	 * @see Properties
	 */
	public static Properties loadPropertiesFromFileSystem(String propertiesFile){
		if (propertiesFile == null || propertiesFile.isEmpty()) {
			throw new IllegalArgumentException("Properties file can not be null or empty");
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (IOException | NullPointerException | IllegalArgumentException e) {
			throw new IllegalArgumentException("Properties file could not be loaded");
		}
		return properties;
	}
}
