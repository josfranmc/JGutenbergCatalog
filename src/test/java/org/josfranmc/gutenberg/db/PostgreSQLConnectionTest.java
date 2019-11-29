package org.josfranmc.gutenberg.db;

import org.junit.Test;

public class PostgreSQLConnectionTest {

	@Test(expected=IllegalArgumentException.class)
	public void setConnectionSettingTest() {
		new PostgreSQLConnection().setConnectionSetting(null);
	}
}
