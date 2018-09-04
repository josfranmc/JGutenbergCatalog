package org.josfranmc.gutenberg.catalog.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase HSQLServer
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class HSQLServerTest {

	/**
	 * Comprueba que se inicia la base de datos.
	 */
	@Test
	public void testGetInstance() {
		HSQLServer server = new HSQLServer();
		assertFalse("El db se está ejecutando", server.isServerRunning());
		server.startDb();
		assertTrue("El db no se está ejecutando", server.isServerRunning());
	}
}