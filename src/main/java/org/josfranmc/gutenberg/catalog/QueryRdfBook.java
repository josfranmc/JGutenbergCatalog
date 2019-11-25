package org.josfranmc.gutenberg.catalog;

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
 * It allows to query the RDF files that make up the Gutenberg project catalog. 
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see Book
 * @see Catalog
 */
public class QueryRdfBook {

	private static final Logger log = Logger.getLogger(QueryRdfBook.class);
	
	/**
	 * It queries a RDF file about a book in order to extracting its data:
	 * <ul>
	 * <li>title</li>
	 * <li>author</li>
	 * <li>language</li>
	 * </ul>
	 * @param filePath the path of the file to query
	 * @return a <code>Book</code> object
	 * @see Book
	 */
	public static Book getBook(String filePath) {
		Book book = new Book();
		Model model = ModelFactory.createDefaultModel();
		InputStream is = FileManager.get().open(filePath);
		if (is == null) {
			log.warn("File not found: " + filePath);
		} else {
			model.read(is, "http://www.gutenberg.org/", "RDF/XML");
			String title = executeSparqlStatement(model, "title");
			String creator = executeSparqlStatement(model, "author");
			String language = executeSparqlStatement(model, "language");
			book.setTitle(title.replaceAll("[\n\r]", ""));
			book.setAuthor(creator);
			book.setLanguage(language);
			book.setId(getBookId(filePath));
		}
		return book;
	}
	
	/**
	 * It executes a SPARQL query.
	 * @param model model object used by Jena
	 * @param field query field
	 * @return the result of the query
	 */
	private static String executeSparqlStatement(Model model, String field) {
		Literal literal = null;		
		QueryExecution qexec = null;
		try {
			Query query = QueryFactory.create(getQueryString(field));
			qexec = QueryExecutionFactory.create(query, model);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qsol = results.nextSolution();
				literal = qsol.getLiteral(field);
			}
		} catch (Exception e) {
			log.error("Query field = " + field);
			log.error(e);
		} finally {
			qexec.close();
		}
		return (literal != null) ? literal.getString() : " ";
	}
	
	/**
	 * @return the PREFIX stataments to SPARQL queries
	 */
	private static String getSparqlQueryPrefix() {
		String prefix = "PREFIX dcterms: <http://purl.org/dc/terms/> \n";
		      prefix += "PREFIX pgterms: <http://www.gutenberg.org/2009/pgterms/> \n";
		      prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";		
		return prefix;
	}

	/**
	 * Returns the SPARQL query according to the indicate parameter.<br>
	 * Three types of query can be obtained depending on the value of the parameter <i>field</i>:
	 * <ul>
	 * <li>title</li>
	 * <li>author</li>
	 * <li>language</li>
	 * </ul>
	 * @param field query type to execute
	 * @return a SPARQL query
	 */
	private static String getQueryString(String field) {
		String queryString = null;
		if (field.equals("title")) {
			queryString = getTitleQuery();
		} else if (field.equals("author")) {
			queryString = getAuthorQuery();
		} else if (field.equals("language")) {
			queryString = getLanguageQuery();
		}		
		return queryString;
	}
	
	/**
	 * @return the sparql query for getting the book title
	 */
	private static String getTitleQuery() {
		String queryString = getSparqlQueryPrefix();
	    queryString += "SELECT ?title \n";
	    queryString += "WHERE { ?s dcterms:title ?title . } \n";
	    return queryString;		
	}
	
	/**
	 * @return the sparql query for getting the book author
	 */
	private static String getAuthorQuery() {
		String queryString = getSparqlQueryPrefix();
	    queryString += "SELECT ?author \n";
	    queryString += "WHERE { ?s dcterms:creator ?c .  \n";
	    queryString += "        ?c pgterms:name ?author . } \n";
	    return queryString;		
	}
	
	/**
	 * @return the sparql query for getting the book language
	 */
	private static String getLanguageQuery() {
		String queryString = getSparqlQueryPrefix();
	    queryString += "SELECT (str(?lan) as ?language) \n";
	    queryString += "WHERE { ?s dcterms:language ?l . \n";
	    queryString += "        ?l rdf:value ?lan . } \n";
	    return queryString;		
	}
	
	private static String getBookId(String filePath) {
		int init = filePath.lastIndexOf(Catalog.FILE_PREFIX) + 2;
		int end = filePath.lastIndexOf(Catalog.FILE_EXTENSION);
		return filePath.substring(init, end);
	}
}
