package org.josfranmc.gutenberg.files;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class PropertiesFileTest {

	@Test(expected=IllegalArgumentException.class)
	public void loadPropertiesFileFromResourceExceptionWhenNullTest() {
		PropertiesFile.loadPropertiesFromResource(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadPropertiesFileFromResourceExceptionWhenEmptyTest() {
		PropertiesFile.loadPropertiesFromResource("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadPropertiesFileFromResourceExceptionWhenBadFileTest() {
		PropertiesFile.loadPropertiesFromResource("no_file");
	}
	
	@Test
	public void loadPropertiesFileFromResourceTest() {
		Properties p = PropertiesFile.loadPropertiesFromResource("db/DBConnectionTest.properties");
		String value = p.getProperty("DbType");
		assertEquals("Wrong value for property ConnectionClass", "HSQL", value);
	}
	
	@Test(expected=IllegalStateException.class)
	public void createPropertiesFileClassTest() {
		new PropertiesFile();	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadPropertiesFileFromFileSystemExceptionWhenNullTest() {
		PropertiesFile.loadPropertiesFromFileSystem(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadPropertiesFileFromFileSystemExceptionWhenEmptyTest() {
		PropertiesFile.loadPropertiesFromFileSystem("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadPropertiesFileFromFileSystemExceptionWhenBadFileTest() {
		PropertiesFile.loadPropertiesFromFileSystem("no_file");
	}
	
	@Test
	public void loadPropertiesFileFromFileSystemTest() {
		Properties p = PropertiesFile.loadPropertiesFromFileSystem("target/test-classes/db/postgres_connection.properties");
		String value = p.getProperty("DbType");
		assertEquals("Wrong value for property ConnectionClass", "PostgresSQL", value);
	}
}
