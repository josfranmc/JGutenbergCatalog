package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase JGutenbergCatalog
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class JGutenbergCatalogTest {
	
	/**
	 * Si el parámetro rdfFilesPath es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesPathParameterWhenNullThenIllegalArgumentException() {
		new JGutenbergCatalog(null);
	}
	
	/**
	 * Si el parámetro rdfFilesPath indica una carpeta que no existe, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesPathParameterWhenWrongThenIllegalArgumentException() {
		new JGutenbergCatalog("bad/path");
	}
	
	/**
	 * Si el parámetro dbConfigFile es null, entonces lanzar excepción IllegalArgumentException
	 */
//	@Test(expected=IllegalArgumentException.class)
//	public void givenDbConfigFileParameterWhenNullThenIllegalArgumentException() {
//		File folder = new File("rdftest1");
//		folder.mkdir();
//		try {
//			new JGutenbergCatalog("rdftest1");
//		} catch (Exception e) {
//			folder.delete();
//			throw e;
//		}
//	}

//	@Test
//	public void createDefaultDbTest() {
//		File folder = new File("rdftest2");
//		folder.mkdir();
//		new JGutenbergCatalog("rdftest2");
//		
//		File database = new File("catalog");
//		assertTrue(database.exists());
//		
//		folder.delete();
//		database.delete();
//	}
	
	@Test
	public void createDefaultDbTest() {

		JGutenbergCatalog j = new JGutenbergCatalog("target/test-classes/rdftest");
		
		File database = new File("catalog");
		assertTrue(database.exists());
		
		//j.createCatalog();
		
	
		
		for (File f : database.listFiles()) {
			f.delete();
		}
		database.delete();
	}
}
