package org.josfranmc.gutenberg.catalog.client;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.catalog.Book;
import org.josfranmc.gutenberg.catalog.JGutenbergCatalog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que permite ejecutar un progrma cliente para realizar la construcción y consulta del catálogo de libros del proyecto Gutenberg.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class JGutenbergCatalogClient {

	private static final Logger log = Logger.getLogger(JGutenbergCatalogClient.class);
	
	/**
	 * Base de datos del catálogo de libros
	 */
	private static JGutenbergCatalog CATALOGDB = null;
	
	/**
	 * Para guardar el identificador de libro pasado por parámetro
	 */
	private static String idBook;
	
	/**
	 * Para guardar los identificadores de los libros pasados por parámetro
	 */
	private static String idBooks;
	
	/**
	 * Para guardar la ruta de los ficheros RDF pasada por parámetro
	 */
	private static String rdfPath;
	
	/**
	 * Tareas que pueden ejecutarse dentro de la clase
	 */
	private enum Tasks {
		CREATE_DB,
		GET_BOOK,
		GET_BOOKS,
		GET_ALL_BOOKS,
		HELP,
		EXIT,
		ERROR
	}

	
	/**
	 * Método principal de ejecución.
	 * @param args lista de argumentos pasados en la invocación del programa
	 */
	public static void main(String [] args){
		CATALOGDB = JGutenbergCatalog.getInstance(true);
		showHelp();
		String entradaTeclado = "";
		Scanner entradaEscaner = new Scanner(System.in);
		
		Tasks task = null;
		do {
			System.out.print("opción: ");
			entradaTeclado = entradaEscaner.nextLine();
			task = readParameters(entradaTeclado.split(" "));
			switch(task) {
			case CREATE_DB:
				createCatalog();
				break;
			case GET_BOOK:
				getBook();
				break;
			case GET_BOOKS:
				getBooks();
				break;				
			case GET_ALL_BOOKS:
				getAllBooks();
				break;
			case HELP:
				showHelp();
				break;
			case ERROR:
				System.out.println("Lista de parámetros errónea.");
				break;
			case EXIT:
				break;
			}
		} while (task != Tasks.EXIT);
		
		entradaEscaner.close();
		CATALOGDB.shutdownDb();
		while (CATALOGDB.isDbRunning()) {}
		System.exit(0);
	}

	/**
	 * Lee los parámetros de configuración pasados como argumentos. 
	 * @param args lista de parámetros con los valores que toman
	 * @return la tarea que debe ejecuarse según los parámetros indicados
	 */
	private static Tasks readParameters(String [] args) {
		log.debug("Total parámetros: " + args.length);
		Tasks task = null;
		if (args.length == 0 || (args[0].equals("-h") || args[0].equals("-help"))) {
			task = Tasks.HELP;
		} else {
			if (args.length == 2 && args[0].equals("-c")) {
				task = Tasks.CREATE_DB;
				rdfPath = args[1];
			} else if (args.length == 1 && args[0].equals("-f")) {
				task = Tasks.GET_ALL_BOOKS;
			} else if (args.length == 2 && args[0].equals("-q")) {
				task = Tasks.GET_BOOK;			
				idBook = args[1];
			} else if (args.length == 2 && args[0].equals("-v")) {
				task = Tasks.GET_BOOKS;			
				idBooks = args[1];
			} else if (args.length == 1 && args[0].equals("exit")) {
				task = Tasks.EXIT;
			} else {
				task = Tasks.ERROR;
			}
		}
		return task;
	}

	/**
	 * Carga en base de datos los datos del catálogo
	 */
	private static void createCatalog() {
		CATALOGDB.createCatalog(rdfPath);
	}
	
	/**
	 * Muestra por pantalla los datos de un libro
	 * @param book libro a mostrar
	 */
	private static void showBook(Book book) {
		System.out.println(" ");
		System.out.println("Book: " + book.getId());
		System.out.println("  Título: " + book.getTitle());
		System.out.println("  Autor: " + book.getAuthor());
		System.out.println("  Idioma: " + book.getLanguage());
		System.out.println(" ");
	}
	
	/**
	 * Consulta un libro
	 */
	private static void getBook() {
		Book book = CATALOGDB.getBookById(idBook);
		showBook(book);
	}
	
	private static void getBooks() {
		List<Book> books = CATALOGDB.getBooksById(Arrays.asList(idBooks.split(",")));
		for (Book book : books) {
			showBook(book);
		}
	}
	
	
	/**
	 * Crea un fichero con la lista de libros
	 */
	private static void getAllBooks() {
		List<Book> books = CATALOGDB.getAllBooks();
		if (books.size() > 0) {
			File file = new File("catalog.txt");
			try {
				if (file.createNewFile()) {
					BufferedWriter bw = null;
					bw = new BufferedWriter(new FileWriter(file));
					for (Book book : books) {
						bw.write(book.getId() + " " + book.getLanguage() + " " + book.getTitle() + " " +  book.getAuthor());
						bw.newLine();
					}
					bw.close();
					log.info("Creado fichero " + file.getAbsolutePath());
				} else {
					log.error("No se ha podido crear fichero de descarga. Ya existe");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log.info("No existen datos en la base de datos");
		}
	}
	
	/**
	 * Muestra menú de ayuda
	 */
	private static void showHelp() {
		System.out.println("");	
		System.out.println("Opciones:");
		System.out.println("");		
		System.out.println("Cargar base de datos:");
		System.out.println("   -c ruta_archivos_RDF");
		System.out.println("Consultar:");
		System.out.println("   -q id       (id = identicador de libro)");
		System.out.println("   -v list_id  (list_id = lista de identicadores, separadas por comas)");		
		System.out.println("Volcar en fichero de texto los datos de todos los libros:");
		System.out.println("   -f");
		System.out.println("Mostrar ayuda:");
		System.out.println("   -h");		
		System.out.println("");
		System.out.println("(exit para terminar)");
		System.out.println("");
	}
}
