package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CatalogRdf
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class CatalogRdfTest {

	/**
	 * Si el parámetro rdfFiles del constructor es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test
	public void getRdfFileTest() {
		CatalogRdf cr = new CatalogRdf(new File("target/test-classes/rdftest"));
		RdfFile rf = cr.getRdfFile("10607");
		
		assertNotNull(rf);
		assertEquals("Wrong Book Id", "10607", rf.getBook().getId());
		assertEquals("Wrong Book title", "The Real Mother Goose", rf.getBook().getTitle());
	}
	
	/**
	 * Si el parámetro rdfFilesFolder es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesFolderParameterWhenNullThenIllegalArgumentException() {
		new CatalogRdf(null);
	}

	/**
	 * Si el parámetro rdfFilesFolder es una ruta que no existe, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesFolderParameterWhenWrongThenIllegalArgumentException() {
		new CatalogRdf(new File("bad/path"));
	}

}
