package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase Book
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class BookTest {

	@Test
	public void equalTest() {
		Book b1 = new Book();
		b1.setId("1");
		b1.setTitle("Example 1");
		
		Book b2 = new Book();
		b2.setId("2");
		b2.setTitle("Example 2");
		
		assertFalse(b1.equals(b2));
		
		Book b1_twin = new Book();
		b1_twin.setId("1");
		b1_twin.setTitle("Example 1");
		
		assertTrue(b1.equals(b1_twin));
		
		b1.setId(null);
		
		assertFalse(b1.equals(b1_twin));
	}
	
	@Test
	public void toStringTest() {
		Book b1 = new Book();
		b1.setId("1");
		b1.setAuthor("Probe");
		b1.setTitle("Example 1");
		b1.setLanguage("fr");
	
		assertEquals("Bad string for book", "Book [id=1, author=Probe, title=Example 1, language=fr]", b1.toString());
	}
	
	@Test
	public void hashCodeTest() {
		Book b1 = new Book();
		b1.setId("1");
		b1.setAuthor("Probe");
		b1.setTitle("Example 1");
		b1.setLanguage("fr");

		assertEquals("Bad hashCode()", 1939377291, b1.hashCode());
	}
}
