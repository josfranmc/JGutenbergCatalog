package org.josfranmc.gutenberg.db;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;

public class DbConnectionBuilderTest {

	@Test(expected=IllegalArgumentException.class)
	public void setSettingPropertiesTest() {
		new DbConnectionBuilder().setSettingProperties(null);
	}
	
	@Test
	public void buildHSQLTest() {
		Properties properties = new Properties();
		properties.put("DbType", "HSQL");
		properties.put("DatabaseDriver", "org.hsqldb.jdbcDriver");
		properties.put("HSQL.url", "jdbc:hsqldb:mem:buildtest");
		properties.put("HSQL.user", "SA");
		properties.put("HSQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		
		assertNotNull(db);
	}
	
	@Test
	public void buildPostgresSQLTest() {
		Properties properties = new Properties();
		properties.put("DbType", "PostgresSQL");
		properties.put("DatabaseDriver", "org.postgresql.Driver");
		properties.put("PostgreSQL.host", "localhost");
		properties.put("PostgreSQL.port", "5432");
		properties.put("PostgreSQL.dbname", "");
		properties.put("PostgreSQL.user", "");
		properties.put("PostgreSQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		
		assertNotNull(db);
	}
	
	@Test
	public void buildMySQLTest() {
		Properties properties = new Properties();
		properties.put("DbType", "MySQL");
		properties.put("DatabaseDriver", "com.mysql.cj.jdbc.Driver");
		properties.put("MySQL.host", "localhost");
		properties.put("MySQL.port", "3306");
		properties.put("MySQL.dbname", "");
		properties.put("MySQL.params", "useUnicode=true");
		properties.put("MySQL.ssl", "FALSE");
		properties.put("MySQL.allowPublicKeyRetrieval", "TRUE");
		properties.put("MySQL.user", "");
		properties.put("MySQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		
		assertNotNull(db);
	}
	
	@Test
	public void buildDefaultTest() {
		Properties properties = new Properties();
		properties.put("DbType", "OtherDb");
		properties.put("DatabaseDriver", "com.mysql.cj.jdbc.Driver");
		properties.put("MySQL.host", "localhost");
		properties.put("MySQL.port", "3306");
		properties.put("MySQL.dbname", "");
		properties.put("MySQL.params", "useUnicode=true");
		properties.put("MySQL.ssl", "FALSE");
		properties.put("MySQL.allowPublicKeyRetrieval", "TRUE");
		properties.put("MySQL.user", "");
		properties.put("MySQL.password", "");
		
		DbConnection db = new DbConnectionBuilder().setSettingProperties(properties).build();
		
		assertNotNull(db);
	}
}
