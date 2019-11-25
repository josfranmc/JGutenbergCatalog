package org.josfranmc.gutenberg.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.log4j.Logger;

/**
 * It represents a RDF file about a book and allows to obtain its data.<br>
 * It uses Apache Jena for quering data.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class RdfFile {

	private static final Logger log = Logger.getLogger(RdfFile.class);
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static final String FILE_PREFIX = "pg";
	
	public static final String FILE_EXTENSION = ".rdf";
	
	/**
	 * The path to the RDF file
	 */
	private String filePath;
	
	private Book book;

	/**
	 * Initializes the object with the folder path that contains the RDF file about a book.
	 * @param folder folder path
	 */
	RdfFile(String folder) {
		this(new File(folder));
	}
	
	/**
	 * Initializes the object with the folder path that contains the RDF file about a book.
	 * @param folder folder path as a <code>File</code> object
	 */
	RdfFile(File folder) {
		if (folder == null) {
			throw new IllegalArgumentException("Invalid path to RDF file.");
		}
		String filePath = folder.getAbsolutePath() + FILE_SEPARATOR + FILE_PREFIX + folder.getName() + FILE_EXTENSION;
		File file = new File(filePath);
		if (!file.exists()) {
			throw new IllegalArgumentException("Wrong rdf file. Id: " + folder.getName());
		} 
		this.filePath = filePath;
		this.book = new Book();
		this.book.setId(folder.getName());
	}
	
	/**
	 * Returns a <code>Book</code> object according to the RDF file this object represent.
	 * @return a <code>Book</code> object
	 * @see Book
	 */
	public Book getBook() {
		try (InputStream is = FileManager.get().open(getFilePath())) {
			Model model = ModelFactory.createDefaultModel();
			model.read(is, "http://www.gutenberg.org/", "RDF/XML");
			
			Query query = QueryFactory.create(getQueryStatement());
			QueryExecution qexec = QueryExecutionFactory.create(query, model);

			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qsol = results.nextSolution();
				
				book.setTitle(getFieldValue(qsol, "title").replaceAll("[\n\r]", ""));
				book.setAuthor(getFieldValue(qsol, "author"));
				book.setLanguage(getFieldValue(qsol, "language"));
			}
		} catch (IOException e) {
			log.error("RDF file not found: " + filePath);
		} catch (Exception e) {
			log.error(e);
		}
		return book;
	}
	
	private String getFieldValue(QuerySolution qsol, String field) {
		String value = null;
		Literal literal = qsol.getLiteral(field);
		if (literal != null) {
			value = literal.getString();
		}
		return value;
	}
	
	private String getQueryStatement() {
		String queryString = "PREFIX dcterms: <http://purl.org/dc/terms/> \n";
		queryString +=       "PREFIX pgterms: <http://www.gutenberg.org/2009/pgterms/> \n";
	    queryString +=       "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n\n";			

	    queryString += "SELECT ?title ?author (str(?lan) as ?language) \n";
	    queryString += " WHERE { ?s dcterms:title ?title .  \n";
	   
	    queryString += "        OPTIONAL { ?u dcterms:creator ?c .      \n";
	    queryString += "                   ?c pgterms:name ?author . }  \n";
	    
	    queryString += "        OPTIONAL { ?b dcterms:language ?l .    \n";
	    queryString += "                   ?l rdf:value ?lan .      }  \n";
	    queryString += "       } \n";
	    
	    return queryString;		
	}

	/**
	 * Returns the path of the RDF file this object represent.
	 * @return the path of the RDF file
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Returns the id of the RDF file this object represent, which is the id of the book.
	 * @return the id of the RDF file
	 */
	public String getId() {
		return this.book.getId();
	}
}
