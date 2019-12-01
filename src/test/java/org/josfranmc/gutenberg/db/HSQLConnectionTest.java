package org.josfranmc.gutenberg.db;

import org.junit.Test;

public class HSQLConnectionTest {

	@Test(expected=IllegalArgumentException.class)
	public void setConnectionSettingTest() {
		new HSQLConnection().setConnectionSetting(null);
	}
}
