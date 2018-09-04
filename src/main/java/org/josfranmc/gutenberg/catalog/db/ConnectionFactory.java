package org.josfranmc.gutenberg.catalog.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.catalog.db.IDBConnection;
import org.josfranmc.gutenberg.util.PropertiesFile;

import org.josfranmc.gutenberg.catalog.db.ConnectionFactory;

/**
 * Factoría para crear y gestionar el acceso a la base de datos. Para crear un objeto de esta clase hay que llamar al método de clase
 * <i>getInstance().</i> La primera vez que se llama se crea una instancia de la factoría, que será la misma que se obtenga en llamadas sucesivas,
 * e internamente se instancia el objeto apropiado para el acceso a la base de datos. El tipo de este objeto a instanciar se obtiene leyendo
 * el fichero de propiedades.<br>Una vez creada la factoría, se pueden obtener conexiones a la base de datos.<p>
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see IDBConnection
 */
public class ConnectionFactory {

	private static final Logger log = Logger.getLogger(ConnectionFactory.class);
	
	/**
	 *  Ruta del fichero de propiedades en el classpath
	 */
	private final static String DB_PROPERTY_FACTORY_FILE = "db/DBConnectionFactory.properties";
	
	/**
	 *  Nombre de la propiedad del fichero que indica el nombre de la clase a instanciar
	 */
	private final static String PROPERTY_DEFAULT_DB_CLASS = "defaultDBClass";
	
	/**
	 *  Nombre de la clase a instanciar
	 */
	private static String DEFAULT_DB_CLASS;

	/**
	 * Referencia a la propia clase
	 */
	private static ConnectionFactory connectionFactory = null;
	
	/**
	 * Referencia al objeto que gestiona el acceso a la base de datos
	 */
	private static IDBConnection dbConnection;
	

	/**
	 * Constructor por defecto.
	 */
	private ConnectionFactory() {
		log.debug("Configurando acceso a base de datos");
		readPropertiesFile();
		try {
			dbConnection = (IDBConnection) Class.forName(DEFAULT_DB_CLASS).newInstance();
			log.debug("Creada factoría para conexión a base de datos. Utilizando clase " + DEFAULT_DB_CLASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permite obtener una conexión a la base de datos que almacena el catálogo de libros.
	 * @return conexión a la base de datos
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return dbConnection.getConnection();
	}

	/**
	 * Crea la instancia de la factoría que permite obtener conexiones a la base de datos. Si es la primera vez que se invoca se crea un nuevo objeto
	 * ConnectionFactory. En caso contrario, se devuelve la instancia creada previamente.
	 * @return factoría para obtener conexiones a la base de datos
	 */
	public static ConnectionFactory getInstance() {
		if (connectionFactory == null) {
			synchronized (ConnectionFactory.class) {
				if (connectionFactory == null) {
					connectionFactory = new ConnectionFactory();
				}
			}
		}
		return connectionFactory;
	}
	
	/**
	 * Lee el fichero de propiedades de configuración.
	 */
	private void readPropertiesFile() {
		try {
			Properties prop = PropertiesFile.loadProperty(DB_PROPERTY_FACTORY_FILE);
			DEFAULT_DB_CLASS = prop.getProperty(PROPERTY_DEFAULT_DB_CLASS);
			log.debug("Cargando datos de fichero " + DB_PROPERTY_FACTORY_FILE);
			log.debug("DefaultDBClass = " + DEFAULT_DB_CLASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
