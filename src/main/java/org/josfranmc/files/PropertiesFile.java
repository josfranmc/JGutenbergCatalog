package org.josfranmc.files;

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
