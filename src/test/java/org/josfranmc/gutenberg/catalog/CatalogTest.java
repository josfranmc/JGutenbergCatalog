package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CatalogBuilder
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class CatalogTest {

	/**
	 * Si el parámetro pathRDFs del constructor es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesPathParameterWhenNullThenIllegalArgumentException() {
		new Catalog(null, null);
	}
	
	/**
	 * Si el parámetro pathRDFs del constructor es una cadena vacía, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesPathParameterWhenEmptyThenIllegalArgumentException() {
		new Catalog("", null);
	}
	
	/**
	 * Si la ruta de los archivos RDF es errónea, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenRdfFilesPathWhenWrongThenIllegalArgumentException() {
		new Catalog("c:\\qsf\\wqf\\errorr", null);
	}
	
	/**
	 * Si no se ha podido establecer conexión con la base de datos, entonces el objeto Connection de la clase debe ser null
	 */
//	@Test
//	public void testConstructor() {
//		Catalog cb = new Catalog("c:\\ejemplo", null);
//		assertNull("La conexión no es null" , cb.getConnection());
//	}
	
	/**
	 * Si la ruta de los archivos RDF es errónea, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenDbConnectionWhenNullThenIllegalArgumentException() {
		new Catalog("c:\\test", null);
	}
}
