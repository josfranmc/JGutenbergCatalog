package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		jg.setDatabase(null);
		jg.loadDb();
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
		jg.setDatabase("target/test-classes/db/hsql1_connection.properties");
		jg.loadDb();
		
		assertTrue(query("jdbc:hsqldb:mem:gutenbergtest1"));
	}
	
	@Test
	public void loadDbWithDefaultDbTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.loadDb();
		
		assertTrue(query("jdbc:hsqldb:mem:gutenbergtest1"));
	}
	
	@Test
	public void loadDbWithRdfAlreadyReadTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.readRdfFiles();
		jg.setDatabase("target/test-classes/db/hsql2_connection.properties");
		jg.loadDb();
		
		assertTrue(query("jdbc:hsqldb:mem:gutenbergtest2"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadDbWithBadSettingFileTest() {
		JGutenbergCatalog jg = new JGutenbergCatalog("target/test-classes/rdftest");
		jg.setDatabase("bad/path/setting.properties");
		jg.loadDb();
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
	
	@Test
	public void main4Test() {
		String[] args = {"-r", "target/test-classes/rdftest", "-b", "target/test-classes/db/hsql3_connection.properties"};
		JGutenbergCatalog.main(args);
		
		assertTrue(query("jdbc:hsqldb:mem:gutenbergtest3"));
	}
	
	private boolean query(String url) {
		boolean result = false;
		ResultSet resultSet = null;
		PreparedStatement selectStatament = null;
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, "SA", "");
			selectStatament = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
			selectStatament.setString(1, "10607");
			resultSet = selectStatament.executeQuery();
			while (resultSet.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (selectStatament != null) {
					selectStatament.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
