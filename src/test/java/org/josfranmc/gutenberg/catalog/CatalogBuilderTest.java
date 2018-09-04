package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertNull;

import org.josfranmc.gutenberg.catalog.db.HSQLServer;
import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase CatalogBuilder
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class CatalogBuilderTest {

	/**
	 * Si el parámetro pathRDFs del constructor es null, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenPathRDFsParameterWhenNullThenIllegalArgumentException() {
		new CatalogBuilder(null);
	}
	
	/**
	 * Si el parámetro pathRDFs del constructor es una cadena vacía, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenPathRDFsParameterWhenEmptyThenIllegalArgumentException() {
		new CatalogBuilder("");
	}
	
	/**
	 * Si la ruta de los archivos RDF es errónea, entonces lanzar excepción IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void givenPathRDFsWhenWrongThenIllegalArgumentException() {
		HSQLServer server = new HSQLServer();
		server.startDb();
		CatalogBuilder cb = new CatalogBuilder("c:\\qsf\\wqf\\errorr");
		server.shutdownDb();
		cb.build();
	}
	
	/**
	 * Si no se ha podido establecer conexión con la base de datos, entonces el objeto Connection de la clase debe ser null
	 */
	@Test
	public void testConstructor() {
		CatalogBuilder cb = new CatalogBuilder("c:\\ejemplo");
		assertNull("La conexión no es null" , cb.getConnection());
	}
}
