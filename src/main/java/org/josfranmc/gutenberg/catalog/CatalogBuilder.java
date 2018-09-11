package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.catalog.db.ConnectionFactory;

/**
 * Permite construir en memoria el catálogo de libros del proyecto Gutenberg (<a href="http://www.gutenberg.org/">http://www.gutenberg.org/</a>)<p>
 * El catálogo se construye utilizando los ficheros RDF que previamente han tenido que ser descargados y descomprimidos en una carpeta. La dirección para
 * obtener el zip es <a href="http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip">http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip</a>
 * Dentro de la carpeta epub del zip se encuentra una carpeta por cada libro del catálogo. El nombre de la carpeta es el mismo que el identificador del
 * libro, es decir, igual que el nombre del fichero del libro. Dentro de cada carpeta se encuentra el fichero RDF corespondiente cuyo nombre es 
 * pg+<i>identificador_libro</i>+.rdf.<p>
 * Ej.: Para libro identificado como <i>45238</i> existe una carpeta <i>45238</i> y dentro de ella un fichero <i>pg45238.rdf</i><p>
 * Los datos que se obtienen de cada libro son: identificador, título, autor e idioma. Esots datos se obtienen mediante consultas de tipo sparql a los
 * ficheros RDF.<p>
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class CatalogBuilder {
	
	private static final Logger log = Logger.getLogger(CatalogBuilder.class);
	
	/**
	 * Ruta de la carpeta que contine los archivos RDF con la información de cada libro
	 */
	private String catalogPath;
	
	/**
	 * Conexión a la base de datos en la que guardar los datos
	 */
	private Connection connection = null;
	
	/**
	 * Sentencia SQL a utilizar
	 */
	private PreparedStatement pstatement = null;
	
	
	/**
	 * Constructor. Inicializa un objeto que permite la creación del catálogo de libros.<br>
	 * La información a cargar se obtiene de los ficheros RDF existentes en la ruta indicada.
	 * se indica.
	 * @param pathRDFs ruta de la carpeta que contine los archivos RDF
	 * @throws IllegalArgumentException
	 */
	CatalogBuilder(String pathRDFs) {
		if (pathRDFs == null || pathRDFs.isEmpty()) {
			throw new IllegalArgumentException("No se ha indicado la ruta de la carpeta que contine los archivos RDF.");
		}
		setCatalogPath(pathRDFs);
		try {
			Connection con = ConnectionFactory.getInstance().getConnection();
			if (con != null) {
				setConnection(con);
				getConnection().setAutoCommit(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Obtiene la lista con los identificadores de los archivos a procesar.<p>
	 * Se devuelve una lista con los identificadores de todos los archivos existentes en la carpeta donde se ubican los ficheros RDF.
	 * @param folderCatalog ruta de la carpeta de los archivos RDF
	 * @return la lista de los archivos de los que obtener su información
	 */
	private List<String> getIdentifiersToProcess(File folderCatalog) {
		List<String> filesToProcess = new ArrayList<String>();
		for (File file : folderCatalog.listFiles()) {
			// el identificador de cada archivo rdf es el nombre de la carpeta que lo contiene
			filesToProcess.add(file.getName());
		}
		return filesToProcess;
	}
	
	/**
	 * Construye el catálogo de libros. Analiza los archivos con la información de los libros a cargar y realiza sobre éstos las consultas en
	 * formato sparql necesarias. Para ello se usa Apache Jena.<p>
	 * Crea un map cuyo campo clave es el identificador de cada libro y asociado a cada uno de los identificadores un objeto Book que encapsula 
	 * la información obtenida.
	 * @see Book
	 */
	public void build() {
		log.info("CARGANDO CATÁLOGO EN DB " + getCurrentTime());
		File catalogFolder = new File(getCatalogPath());
		if (!catalogFolder.exists()) {
			log.error("La ruta del almacén de los ficheros RDF no es correcta.");
			throw new IllegalArgumentException("La ruta del almacén de los ficheros RDF no es correcta");
		} else {
			createTable();
			createPreparedStatement();
			List<String> identifiersToProcess = getIdentifiersToProcess(catalogFolder);
			for (String bookId : identifiersToProcess) {
				File rdfFile = new File(getRdfFilePath(bookId));
				if (rdfFile.exists() && !isBookInDb(bookId)) {
					Book book = QueryRdfBook.getBook(rdfFile.getAbsolutePath());
					book.setId(bookId);
					saveBook(book);
				}
			}
			commitAndClose();
		}
		log.info("FIN CARGA CATÁLOGO EN DB " + getCurrentTime());
	}
	
	/**
	 * Crea la tabla para guardar los datos de los libros. Si ya existe la tabla no hace nada.
	 */
	private void createTable() {
		Statement statement = null;
		Connection con = getConnection();
		try {
			statement = con.createStatement();
       	 	statement.executeUpdate("CREATE TABLE libros (" + 
     	 		" ID varchar(10) PRIMARY KEY," + 
     	 		" author varchar(300) NULL, " + 
     	 		" title varchar(1000) NULL," + 
     	 		" language varchar(3) NULL," +
     	 		");");
       	 	log.info("Creada tabla para LIBROS.");
		} catch (SQLException e) {
			log.info("Utilizando tabla LIBROS ya existente.");
		} finally {
        	try {
    		    if (statement != null) {
    		     statement.close();
    		    }
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
		}
	}

	/**
	 * Prepara la sentencia SQL de tipo INSERT a utilizarse.
	 */
	private void createPreparedStatement() {
		try {
			this.pstatement = getConnection().prepareStatement("INSERT INTO libros VALUES (?, ?, ?, ?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Guarda un libro en la base de datos.
	 * @param book libro a guardar
	 */
	private void saveBook(Book book) {
		try {
			getPreparedStatement().setString(1, book.getId());
			getPreparedStatement().setString(2, book.getAuthor());
			getPreparedStatement().setString(3, book.getTitle());
			getPreparedStatement().setString(4, book.getLanguage());
			getPreparedStatement().executeUpdate();
       	 	log.debug("Guardado libro " + book.getId());
		} catch (SQLIntegrityConstraintViolationException e) {
			log.debug("Error al guardar " + book.getId() + ". Ya existe esta clave");
		} catch (SQLException e) {
			log.warn("Error al guardar " + book.getId());
			log.warn(e.toString());
		}
	}

	/**
	 * Comprueba si ya existe un determinado libro en la base de datos
	 * @param id identificador del libro a comprobar
	 * @return <i>true</i> si ya existe el libro en la bas de datos, <i>false</i> en caso contrario
	 */
	private boolean isBookInDb(String id) {
		boolean result = false;
		PreparedStatement pstatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("SELECT * FROM libros WHERE id = ?");
			pstatement.setString(1, id);
			resultSet = pstatement.executeQuery();
			while (resultSet.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * Confirma los cambios realizados y cierra la conexión con la base de datos.
	 */
	private void commitAndClose() {
		try {
			if (getPreparedStatement() != null) {
				getPreparedStatement().close();
			}
			if (getConnection() != null) {
				getConnection().commit();
				getConnection().close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Obtiene la ruta del fichero RDF correspondiente a un determinado libro. Esta ruta se construye concatenando la ruta de la carpeta de los 
	 * archivos rdf + identificador de archivo (que es el nombre de la carpeta) + separador + pg + identificador de archivo + .rdf.<p>
	 * Ej.: Dado el identificador de libro 56789 la ruta de su correpondiente fichero RDF será <i>ruta_carpeta</i>\56789\pg56789.rdf (para un sistema Windows).
	 * @param idFile identificador del libro
	 * @return
	 */
	private String getRdfFilePath(String idFile) {
		return getCatalogPath() + idFile + System.getProperty("file.separator") + "pg" + idFile + ".rdf";
	}
	
	/**
	 * @return la ruta de la carpeta que contine los archivos RDF con la información de cada libro
	 */
	public String getCatalogPath() {
		return catalogPath;
	}
	
	/**
	 * Establece la ruta de la carpeta que contine los archivos RDF con la información de cada libro.
	 * @param catalogPath ruta de la carpeta
	 */
	public void setCatalogPath(String catalogPath) {
		this.catalogPath = catalogPath.endsWith(System.getProperty("file.separator")) ? catalogPath : catalogPath.concat(System.getProperty("file.separator"));
	}

	/**
	 * @return la conexión a la base de datos a utilizar
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Establece la conexión a la base de datos a utilizar.
	 * @param connection conexión a asignar
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * @return la sentencia SQL preparada
	 */
	private PreparedStatement getPreparedStatement() {
		return this.pstatement;
	}
	
	private String getCurrentTime() {
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		return hourFormat.format(date);
	}
}
