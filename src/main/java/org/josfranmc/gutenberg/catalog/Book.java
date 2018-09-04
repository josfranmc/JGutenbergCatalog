package org.josfranmc.gutenberg.catalog;

import java.io.Serializable;

/**
 * Encapsula los datos de un libro.<p>
 * Los libros quedan identificados mediante el código obtenido del proyecto Gutenberg (<a href="http://www.gutenberg.org/">http://www.gutenberg.org/</a>).
 * Este código se corresponde con el nombre del fichero del libro.<br>
 * Para cada libro se guarda identificador, autor, título e idioma.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class Book implements Serializable {

	private static final long serialVersionUID = 1029959754259245879L;
	
	private String id;
	private String author;
	private String title;
	private String language;
	
	public Book() {
		id = null;
		author = null;
		title = null;
		language = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", author=" + author + ", title=" + title + ", language=" + language + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}	
}
