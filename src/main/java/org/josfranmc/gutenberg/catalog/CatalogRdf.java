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
 *    
 *  This file includes software developed at
 *  The Apache Software Foundation (http://www.apache.org/). 
 */
package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * It allows to read the RDF files that make up the Gutenberg catalog.<p>
 * The data is stored in memory as a <code>HashMap</code>. Each RDF file is stored as an <code>RdfFile</code> object, with key the file identifier.<br>
 * This identifier is the same as the book it represents.
 * @author Jose Francisco Mena Ceca
 * @version 2.2
 * @see RdfFile
 * @see Book
 */
public class CatalogRdf {

	private static final Logger log = LogManager.getLogger(CatalogRdf.class);

	/**
	 * The path to the folder that contains RDF files about books
	 */
	private File rdfFilesFolder;
	
	/**
	 * Collection of RDF files
	 */
	private Map<String, RdfFile> rdfCatalog;

	
	/**
	 * Default constructor.
	 * @param rdfFilesFolder a <code>RdfFile</code> objects collection that make up the Gutenberg catalog.
	 */
	public CatalogRdf(File rdfFilesFolder) {
		if (rdfFilesFolder == null || !rdfFilesFolder.exists()) {
			throw new IllegalArgumentException("Invalid path to RDF container.");
		}
		this.rdfFilesFolder = rdfFilesFolder;

		this.rdfCatalog = new HashMap<>();
	}
	
	/**
	 * Reads the RDF files to extrac its data and store it as a collection of <code>RdfFile</code> objects.
	 * @see RdfFile
	 */
	public void readFiles() {
		log.info("[INFO] Processing RDF files... " + getCurrentTime());
		for (File folder : this.rdfFilesFolder.listFiles()) {
			if (!folder.getName().toLowerCase().contains("delete")) {
				try {
					RdfFile rdfFile = new RdfFile(folder);
					this.rdfCatalog.put(folder.getName(), rdfFile);
				} catch (IllegalArgumentException e) {
					log.warn("[WARN] File " + folder.getName() + " not read");
				}
			}
		}
		log.info("[INFO] RDF files processed " + getCurrentTime());
	}
	
	/**
	 * Returns the <code>RdfFile</code> objects collection.
	 * @return a HashMap collection of <code>RdfFile</code> objects 
	 */
	public Map<String, RdfFile> getRdfCatalog() {
		return this.rdfCatalog;
	}
	
	/**
	 * Sets a HashMap collection of <code>RdfFile</code> objects that make up the Gutenberg catalog.
	 * @param rdfCatalog a HashMap collection of <code>RdfFile</code> objects 
	 */
	public void setRdfCatalog(Map<String, RdfFile> rdfCatalog) {
		this.rdfCatalog = rdfCatalog;
	}
	
	/**
	 * Returns a <code>RdfFile</code> object by its identifier.
	 * @param idFile RDF file identifier
	 * @return a <code>RdfFile</code> object 
	 */
	public RdfFile getRdfFile(String idFile) {
		RdfFile rdfFile = this.rdfCatalog.get(idFile);
		if (rdfFile == null) {
			rdfFile = new RdfFile(new File(rdfFilesFolder.getAbsolutePath() + System.getProperty("file.separator") + idFile));
		}
		return rdfFile;
	}
	
	private String getCurrentTime() {
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		return hourFormat.format(date);
	}
}
