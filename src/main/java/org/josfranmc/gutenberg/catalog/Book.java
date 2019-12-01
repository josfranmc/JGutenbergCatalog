/*
 *  Copyright (C) 2018-2019 Jose Francisco Mena Ceca <josfranmc@gmail.com>
 *
 *  This file is part of JGutenbergCatalog.
 *
 *  JGutenbergCatalog is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JGutenbergCatalog is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JGutenbergCatalog.  If not, see <https://www.gnu.org/licenses/>. 
 */
package org.josfranmc.gutenberg.catalog;

import java.io.Serializable;

/**
 * For managing the books in the application.<br>
 * Every book is identified by a number. This number is the RDF file name that contains the book data. 
 * @author Jose Francisco Mena Ceca
 * @version 2.0
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
		} else {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		if (title == null) {
			if (other.title != null)
				return false;
		} else { 
			if (!title.equals(other.title)) {
				return false;
			}
		}
		return true;
	}	
}
