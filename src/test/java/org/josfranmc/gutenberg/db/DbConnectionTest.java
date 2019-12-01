package org.josfranmc.gutenberg.db;

import static org.junit.Assert.fail;

import org.junit.Test;

public class DbConnectionTest {

	@Test
	public void loadDriverTest() {
	   try{
		   DbConnection db = new HSQLConnection();
		   db.loadDriver("Bad_driver");
	   } catch(Exception e){
		   fail("Should not have thrown any exception");
	   }
	}
}
