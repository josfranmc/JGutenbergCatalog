package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Properties;

import org.josfranmc.gutenberg.db.DbConnection;
import org.josfranmc.gutenberg.db.DbConnectionBuilder;
import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CatalogDb
 * @author Jose Francisco Mena Ceca
 * @version 2.2
 */
public class CatalogDbTest {

	/**
	 * Si el parámetro rdfCatalog del constructor es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfCatalogParameterWhenNullThenIllegalArgumentException() {
		new CatalogDb(null, null);
	}
	
	/**
	 * Si el parámetro dbConnection es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenDbConnectionParameterWhenNullThenIllegalArgumentException() {
		new CatalogDb(new HashMap<String, RdfFile>(), null);
	}
	
	@Test
	public void isBookInDatabase() {
		Properties properties = new Properties();
		properties.put("DbType", "HSQL");
		properties.put("DatabaseDriver", "org.hsqldb.jdbcDriver");
		properties.put("HSQL.url", "jdbc:hsqldb:mem:isbooktest");
		properties.put("HSQL.user", "SA");
		properties.put("HSQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		CatalogDb c = new CatalogDb(db);
		
		assertFalse(c.isBookInDatabase("123"));
	}
	
	@Test
	public void createObject() {
		Properties properties = new Properties();
		properties.put("DbType", "HSQL");
		properties.put("DatabaseDriver", "org.hsqldb.jdbcDriver");
		properties.put("HSQL.url", "jdbc:hsqldb:mem:createtest");
		properties.put("HSQL.user", "SA");
		properties.put("HSQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		CatalogDb c = new CatalogDb(new HashMap<String, RdfFile>(), db);
		
		assertNotNull(c.getRdfCatalog());
	}
	
	@Test
	public void rdfCatalogIsNullTest() {
		Properties properties = new Properties();
		properties.put("DbType", "HSQL");
		properties.put("DatabaseDriver", "org.hsqldb.jdbcDriver");
		properties.put("HSQL.url", "jdbc:hsqldb:mem:createtest2");
		properties.put("HSQL.user", "SA");
		properties.put("HSQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		CatalogDb c = new CatalogDb(db);
		assertNull(c.getRdfCatalog());
		c.load(false);
		assertFalse(c.isBookInDatabase("123"));
	}
}
