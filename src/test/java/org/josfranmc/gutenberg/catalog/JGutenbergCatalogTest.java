package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
	 * Si el parámetro dbConfigFile del método loadDb() es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void loadDbWhenNullParameterThenIllegalArgumentException() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.loadDb(null);
	}

	@Test
	public void readRdfFilesTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.readRdfFiles();
		assertFalse(jg.getRdfCatalog().isEmpty());
		
		Book book = jg.getBook("10607");
		
		assertNotNull(book);
		
		assertEquals("Wrong Book Id", "10607", book.getId());
		assertEquals("Wrong Book title", "The Real Mother Goose", book.getTitle());
	}

	@Test
	public void loadDbTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.loadDb("target/test-classes/db/hsql1_connection.properties");
	}
	
	@Test
	public void loadDbWithDefaultDbTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.loadDb();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadDbWithBadSettingFileTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.loadDb("bad/path/setting.properties");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void main1Test() {
		String[] args = {"-r", "target/test-classes/rdftest", "-b"};
		JGutenbergCatalog.main(args); 
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void main2Test() {
		String[] args = {"-r", "-b"};
		JGutenbergCatalog.main(args); 
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void main3Test() {
		String[] args = {"-x", "-b"};
		JGutenbergCatalog.main(args); 
	}
}
