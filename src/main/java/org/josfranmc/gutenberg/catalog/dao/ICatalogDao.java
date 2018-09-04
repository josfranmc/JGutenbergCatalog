package org.josfranmc.gutenberg.catalog.dao;

import java.util.List;

import org.josfranmc.gutenberg.catalog.Book;

/**
 * Establece el interfaz de uso que debe implementarse para interaccionar con la tabla libros de la base de datos que almacena los datos de los libros
 * existentes en el proyecto Gutenberg.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Book
 */
public interface ICatalogDao {


	/**
	 * @return una lista con todos los libros existentes en la base de datos.
	 * @see Book
	 */
	public List<Book> getAllBooks();

	/**
	 * Devuelve un libro concreto según el identificador pasado.
	 * @param id identificador del libro a recuperar
	 * @return objeto de tipo Book
	 * @see Book
	 */
	public Book getBookById(String id);
	
	/**
	 * Devuelve una lista de libros según los identificadores pasados.
	 * @param ids lista deidentificadores de los libros a recuperar
	 * @return lista de objetos de tipo Book
	 * @see Book
	 */
	public List<Book> getBooksById(List<String> ids);
	
	/**
	 * Guarda un nuevo libro a la base de datos.
	 * @param book objeto libro a guardar
	 * @see Book
	 */
	public void addBook(Book book);

	/**
	 * Elimina un libro de la base de datos.
	 * @param id identificador del libro a eliminar
	 */
	public void deleteBook(String id);
}
