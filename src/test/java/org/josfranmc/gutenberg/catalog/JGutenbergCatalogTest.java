package org.josfranmc.gutenberg.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Clase que implementa los test para probar los métodos de la clase JGutenbergCatalog
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class JGutenbergCatalogTest {
	
	/**
	 * Comprueba que no se inicia la base de datos si no se indica.
	 */
	@Test
	public void testGetInstanceParameterEqualFalse() {
		//JGutenbergCatalog jgc = JGutenbergCatalog.getInstance(false);
		//assertFalse("La base de datos está ejecutándose", jgc.isDbRunning());
	}
}
