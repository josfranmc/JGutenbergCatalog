package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.catalog.dao.CatalogDao;
import org.josfranmc.gutenberg.catalog.dao.ICatalogDao;
import org.josfranmc.gutenberg.catalog.db.HSQLServer;

/**
 * Crea y carga en memoria una base de datos con el catálogo de libros del proyecto Gutenberg (<a href="http://www.gutenberg.org/">http://www.gutenberg.org/</a>)<p>
 * Internamente se usa una base de datos HSQLDB, la cual la utiliza los ficheros creados en el directorio db/HSQLDB/ que se crea en la carpeta de ejecución del programa.<p>
 * 
 * La información de los libros disponibles se encuentra en ficheros RDF, existiendo un fichero por cada libro.
 * 
 *  
 * Estos ficheros se pueden obtener
 * descargando el zip de <a href="http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip">http://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip</a>.
 * Dentro de la carpeta epub del zip se encuentra una carpeta por cada libro del catálogo. El nombre de la carpeta es el mismo que el identificador del
 * libro, es decir, igual que el nombre del fichero del libro. Dentro de cada carpeta se encuentra el fichero RDF corespondiente cuyo nombre es 
 * pg+<i>identificador_libro</i>+.rdf.<p>
 * Ej.: Para libro identificado como <i>45238</i> existe una carpeta <i>45238</i> y dentro de ella un fichero <i>pg45238.rdf</i><p> 
 * Mediante el método <i>createCatalog()</i> se leen los ficheros y se carga en memoria la información consultada. Los datos que se obtienen de cada
 * libro son: identificador, título, autor e idioma. Esots datos se obtienen mediante consultas de tipo sparql a los ficheros RDF. Se usa la librería
 * Apache Jena para ello<p>
 * También se ofrecen una serie de métodos para consultar la base de datos.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see HSQLServer
 * @see Book
 */
public class JGutenbergCatalog {

	private static final Logger log = Logger.getLogger(JGutenbergCatalog.class);
	
	/**
	 * Referencia a la instancia del servidor de base de datos
	 */
	private HSQLServer hsqlServer = null;

	/**
	 * Referencia para patrón Singleton
	 */
	private static JGutenbergCatalog jgcatalog = null;
	
	/**
	 * Operaciones que pueden realizarse con la base de datos.
	 */
	private ICatalogDao catalogDao = null;
	

	/**
	 * Constructor por defecto.<br>
	 * Crea una instancia de la base de datos.
	 * @param startDb <i>true</i> si se quiere iniciar la base de datos, <i>false</i> en caso contrario
	 */
	private JGutenbergCatalog (boolean startDb) {
		this.hsqlServer = new HSQLServer();
		if (startDb) {
			startDb();
		}
		catalogDao = new CatalogDao();
	}
	
	/**
	 * Crea una instancia del tipo JGutenbergCatalog.<p>
	 * Si es la primera vez que se invoca se crea una nueva instancia, devolviéndose esta misma en llamadas sucesivas.
	 * @param startDb <i>true</i> si se quiere inicar el servidor de base de datos, <i>false</i> en caso contrario
	 * @return la única instancia de la clase JGutenbergCatalog
	 */
	public static JGutenbergCatalog getInstance(boolean startDb) {
		if (jgcatalog == null) {
			synchronized (JGutenbergCatalog.class) {
				if (jgcatalog == null) {
					jgcatalog = new JGutenbergCatalog(startDb);
				}
			}
		}
		return jgcatalog;
	}
	
	/**
	 * Inicia la ejecución del servidor de base de datos.
	 * @return <i>true</i> si se ha iniciado la base de datos correctamente, <i>false</i> en caso contrario
	 */
	public boolean startDb() {
		boolean result = false;
		if (!this.hsqlServer.isServerRunning()) {
			this.hsqlServer.startDb();
			result = true;
		} else {
			log.warn("Nada que iniciar. La base de datos ya se está ejecutando.");
		}
		return result;
	}
	
	/**
	 * Termina la ejecución del servidor de base de datos.
	 * @return  <i>true</i> si se ha terminado la ejecución correctamente, <i>false</i> en caso contrario
	 */
	public boolean shutdownDb() {
		boolean result = false;
		if (this.hsqlServer.isServerRunning()) {
			this.hsqlServer.shutdownDb();
			result = true;
		} else {
			log.warn("Nada que parar. La base de datos no se está ejecutando.");
		}
		return result;
	}
	
	/**
	 * @return <i>true</i> si el servidor de base de datos se está ejecutando, <i>false</i> en caso contrario
	 */
	public boolean isDbRunning() {
		return this.hsqlServer.isServerRunning();
	}
	
	/**
	 * @return la ruta donde se guardan los ficheros de la base de datos
	 */
	public String getPathDb() {
		return this.hsqlServer.getPathDb();
	}
	
	/**
	 * Carga en la base de datos la información del catálogo de libros.<br>
	 * Se extrae la información de una serie de ficheros RDF y se guarda en base de datos. Solo se guada la información nueva que no esté ya guardada.
	 * @param pathRDFs ruta de la carpeta que contine los archivos RDF
	 */
	public void createCatalog(String pathRDFs) {
		if (this.hsqlServer.isServerRunning()) {
			if (isValidPath(pathRDFs)) {
				new CatalogBuilder(pathRDFs).build();
			} else {
				log.error("La ruta del almacén de los ficheros RDF no es correcta.");
			}
		} else {
			log.warn("Imposible crear catálogo. Base de datos no inicializada.");
		}
	}
	
	/**
	 * Comprueba si la ruta indicada es válida.
	 * @param pathRDFs ruta a comprobar
	 * @return <i>true</i> si la ruta es válida, <i>false</i> en caso contrario
	 */
	private boolean isValidPath(String pathRDFs) {
		boolean response = false;
		if (pathRDFs != null) {
			if (new File(pathRDFs).exists()) {
				response = true;
			}
		}
		return response;
	}

	/**
	 * Devuelve una lista con todos loa libros existentes en el catálogo.<br>
	 * Cada libro se devuelve como un objeto de tipo Book.
	 * @return lista con todos los libros del catálogo
	 * @see Book
	 */
	public List<Book> getAllBooks() {
		return catalogDao.getAllBooks();
	}

	/**
	 * Devuelve un libro según su identificador.<br>
	 * Se devuelve como un objeto de tipo Book.
	 * @param id identificador del libro
	 * @return libro buscado
	 * @see Book
	 */
	public Book getBookById(String id) {
		return catalogDao.getBookById(id);
	}
	
	/**
     * Devuelve una lista de libros según los identificadores pasados.<br>
	 * Cada libro se devuelve como un objeto de tipo Book.
	 * @param ids lista deidentificadores de los libros a recuperar
	 * @return lista de objetos de tipo Book
	 * @see Book
	 */
	public List<Book> getBooksById(List<String> ids) {
		return catalogDao.getBooksById(ids);
	}

	/**
	 * Añade un libro al catálogo.
	 * @param book libro a añadir
	 * @see Book
	 */
	public void addBook(Book book) {
		catalogDao.addBook(book);
	}

	/**
	 * Elimina un libro del catálogo.
	 * @param identificador del libro a eliminar
	 */
	public void deleteBook(String id) {
		catalogDao.deleteBook(id);
	}
}
