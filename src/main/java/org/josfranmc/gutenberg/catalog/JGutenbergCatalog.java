package org.josfranmc.gutenberg.catalog;

import java.util.List;

import org.apache.log4j.Logger;
import org.josfranmc.db.DbConnection;
import org.josfranmc.gutenberg.catalog.dao.CatalogDao;
import org.josfranmc.gutenberg.catalog.dao.ICatalogDao;
import org.josfranmc.gutenberg.catalog.db.HSQLServer;

/**
 * Crea y carga en memoria una base de datos con el catálogo de libros del proyecto Gutenberg (<a href="http://www.gutenberg.org/">http://www.gutenberg.org/</a>)<p>
 * Internamente se usa una base de datos HSQLDB, la cual utiliza los ficheros creados en el directorio db/HSQLDB/ que se crea en la carpeta de ejecución del programa.<p>
 * La información de los libros disponibles se encuentra en ficheros RDF, existiendo un fichero por cada libro.
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
	
	private String rdfFilesPath;
	
	DbConnection dbConnection;

	/**
	 * Operaciones que pueden realizarse con la base de datos.
	 */
	private ICatalogDao catalogDao = null;
	

	/**
	 * Creates the object for managing the construction of the catalog.
	 * @param rdfFilesPath path to the folder that store the RDF files
	 * @param dbConnection connection to the database where to load data
	 */
	JGutenbergCatalog(String rdfFilesPath, DbConnection dbConnection) {
		this.rdfFilesPath = rdfFilesPath;
		this.dbConnection = dbConnection;
		
		catalogDao = new CatalogDao();
	}
	
	
	/**
	 * It loads the book catalog, which is stored in RDF files, in a database.<br>
	 * It only loads new information. RDF files already loaded are ignored.
	 */
	public void createCatalog() {
		new Catalog(this.rdfFilesPath, this.dbConnection).create();
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
	 * @param ids lista de identificadores de los libros a recuperar
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
	 * @param id identificador del libro a eliminar
	 */
	public void deleteBook(String id) {
		catalogDao.deleteBook(id);
	}
}
