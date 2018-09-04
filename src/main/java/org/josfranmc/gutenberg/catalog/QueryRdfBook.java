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
 * Permite consultar los ficheros RDF que componen el catálogo del proyecto Gutenberg. 
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Book
 */
public class QueryRdfBook {

	private static final Logger log = Logger.getLogger(QueryRdfBook.class);
	
	/**
	 * Consulta el fichero RDF correspondiente a un libro, extrayendo los siguientes datos:
	 * <ul>
	 * <li>título del libro</li>
	 * <li>autor</li>
	 * <li>idioma</li>
	 * </ul>
	 * @param path ruta del fichero a consultar
	 * @return un objeto de tipo Book que encapsula los datos obtenidos
	 * @see Book
	 */
	public static Book getBook(String path) {
		Book book = new Book();
		Model model = ModelFactory.createDefaultModel();
		InputStream is = FileManager.get().open(path);
		if (is == null) {
			log.warn("Archivo no encontrado: " + path);
		} else {
			model.read(is, "http://www.gutenberg.org/", "RDF/XML");
			String title = executeSparqlStatement(model, "title");
			String creator = executeSparqlStatement(model, "author");
			String language = executeSparqlStatement(model, "language");
			book.setTitle(title.replaceAll("[\n\r]", ""));
			book.setAuthor(creator);
			book.setLanguage(language);
		}
		return book;
	}
	
	/**
	 * Ejecuta una consulta sparql.
	 * @param model objeto model usado por Jena
	 * @param field campo que consultar
	 * @return el resultado de la cosulta ejecutada
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
			log.error("Consultando campo = " + field);
			log.error(e);
		} finally {
			qexec.close();
		}
		return (literal != null) ? literal.getString() : " ";
	}
	
	/**
	 * @return las sentencias PREFIX para las consultas SPARQL
	 */
	private static String getSparqlQueryPrefix() {
		String prefix = "PREFIX dcterms: <http://purl.org/dc/terms/> \n";
		      prefix += "PREFIX pgterms: <http://www.gutenberg.org/2009/pgterms/> \n";
		      prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";		
		return prefix;
	}

	/**
	 * Obtiene la consulta sparql adecuada para recuperar la información indicada por parámetro.<br>Se pueden obtener tres tipos de consulta según el
	 * valor del parámetro <i>field</i>:
	 * <ul>
	 * <li><b>title</b>: para consultar el título de un libro</li>
	 * <li><b>author</b>: para consultar el autor de un libro</li>
	 * <li><b>language</b>: para consultar el idioma de un libro/li>
	 * </ul>
	 * @param field tipo de consulta a recuperar
	 * @return consulta sparql
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
	 * @return la consulta sparql para recuperar el título de un libro
	 */
	private static String getTitleQuery() {
		String queryString = getSparqlQueryPrefix();
	    queryString += "SELECT ?title \n";
	    queryString += "WHERE { ?s dcterms:title ?title . } \n";
	    return queryString;		
	}
	
	/**
	 * @return la consulta sparql para recuperar el autor de un libro
	 */
	private static String getAuthorQuery() {
		String queryString = getSparqlQueryPrefix();
	    queryString += "SELECT ?author \n";
	    queryString += "WHERE { ?s dcterms:creator ?c .  \n";
	    queryString += "        ?c pgterms:name ?author . } \n";
	    return queryString;		
	}
	
	/**
	 * @return la consulta sparql para recuperar el idioma de un libro
	 */
	private static String getLanguageQuery() {
		String queryString = getSparqlQueryPrefix();
	    queryString += "SELECT (str(?lan) as ?language) \n";
	    queryString += "WHERE { ?s dcterms:language ?l . \n";
	    queryString += "        ?l rdf:value ?lan . } \n";
	    return queryString;		
	}
}
