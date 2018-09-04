package org.josfranmc.gutenberg.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.josfranmc.gutenberg.catalog.Book;
import org.josfranmc.gutenberg.catalog.db.ConnectionFactory;

/**
 * Implementa las operaciones que pueden realizarse sobre la tabla libros de la base de datos que almacena los datos de los libros existentes en
 * el proyecto Gutenberg.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Book
 */
public class CatalogDao implements ICatalogDao {

	private static final Logger log = Logger.getLogger(CatalogDao.class);
	
	private Connection connection = null;
	
	private PreparedStatement pstatement = null;
	
	private ResultSet resultSet = null;

	
	/**
	 * Constructor por defecto.
	 */
	public CatalogDao() {
		
	}
	
	/**
	 * @return una  conexión a la base de datos
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		return ConnectionFactory.getInstance().getConnection();
	}
	
	/**
	 * Devuelve una lista con todos los libros existentes en la base de datos.
	 */
	@Override
	public List<Book> getAllBooks() {
		List<Book> books = new ArrayList<Book>();
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("SELECT * FROM libros");
			resultSet = pstatement.executeQuery();
			while (resultSet.next()) {
				books.add(getBookFromResultSet(resultSet));
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
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return books;
	}

	/**
	 * Devuelve un libro concreto según el identificador pasado.
	 * @param id identificador del libro a recuperar
	 * @return objeto de tipo Book
	 * @see Book
	 */
	@Override
	public Book getBookById(String id) {
		Book book = new Book();
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("SELECT * FROM libros WHERE id = ?");
			pstatement.setString(1, id);
			resultSet = pstatement.executeQuery();
			while (resultSet.next()) {
				book = getBookFromResultSet(resultSet);
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
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return book;
	}
	
	/**
	 * Devuelve una lista de libros según los identificadores pasados.
	 * @param ids lista deidentificadores de los libros a recuperar
	 * @return lista de objetos de tipo Book
	 * @see Book
	 */
	@Override
	public List<Book> getBooksById(List<String> ids) {
		List<Book> books = new ArrayList<Book>();
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(getSqlForBooksById(ids));
			while (resultSet.next()) {
				books.add(getBookFromResultSet(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return books;
	}	
	
	private String getSqlForBooksById(List<String> ids) {
		String sql = "SELECT * FROM libros WHERE id in ('" + ids.get(0) + "'";
		for (int i = 1; i < ids.size(); i++) {
			sql = sql.concat(", '").concat(ids.get(i)).concat("'");
		}
		sql = sql.concat(")");
		return sql;
	}
	
	/**
	 * Guarda un nuevo libro a la base de datos.
	 * @param book objeto libro a guardar
	 * @see Book
	 */
	@Override
	public void addBook(Book book) {
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("INSERT INTO libros VALUES (?, ?, ?, ?)");
			pstatement.setString(1, book.getId());
			pstatement.setString(2, book.getAuthor());
			pstatement.setString(3, book.getTitle());
			pstatement.setString(4, book.getLanguage());
			pstatement.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			log.error("Error al guardar " + book.getId() + ". Ya existe esta clave");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Elimina un libro de la base de datos.
	 * @param id identificador del libro a eliminar
	 */
	@Override
	public void deleteBook(String id) {
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("DELETE FROM libros WHERE id = ?");
			pstatement.setString(1, id);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Devuelve un objeto Book con los datos existentes en el ResultSet.
	 * @param rs ResultSet del que coger los datos
	 * @return un objeto Book
	 */
	private Book getBookFromResultSet(ResultSet rs) {
		Book book = new Book();
		try {
			book.setId(rs.getString("id"));
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setLanguage(rs.getString("language"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return book;
	}
}
