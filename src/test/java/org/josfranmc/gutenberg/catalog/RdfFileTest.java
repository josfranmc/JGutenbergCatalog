package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase RdfFile
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class RdfFileTest {

	@Test(expected=IllegalArgumentException.class)
	public void createObjectParameterNullTest() {
		File file = null;
		new RdfFile(file);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createObjectParameterWrongTest() {
		File file = new File("bad/path/file");
		new RdfFile(file);
	}
	
	@Test
	public void getIdTest() {
		File file = new File("target/test-classes/rdftest/10607");
		RdfFile rf = new RdfFile(file);
		Book book = rf.getBook();
		assertEquals("Wrong Book Id", "10607", rf.getId());
		assertEquals("Wrong Book title", "The Real Mother Goose", book.getTitle());
	}
	
	@Test
	public void equalTest() {
		File file1 = new File("target/test-classes/rdftest/10607");
		RdfFile rf1 = new RdfFile(file1);
		assertTrue(rf1.equals(rf1));
		
		File file2 = new File("target/test-classes/rdftest/14229");
		RdfFile rf2 = new RdfFile(file2);
		
		assertFalse(rf1.equals(rf2));
		
		File file3 = new File("target/test-classes/rdftest/10607");
		RdfFile rf3 = new RdfFile(file3);
		
		assertTrue(rf1.equals(rf3));
	}
}
