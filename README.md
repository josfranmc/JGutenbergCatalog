# JGutenbergCatalog
This software allows you to query and build the book catalog of Gutenberg project.  
You can download the catalog from this link: [https://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip](https://www.gutenberg.org/cache/epub/feeds/rdf-files.tar.zip)  
This zip file is updated nightly and constains a set of files in RDF format. There is a RDF file for every book with information about it. Each RDF file is in its own folder. 
 
With this software you can query the set of RDF files and retrieve for each book its title, author and language. This data may be loaded in a database. By default, a HSQL database is used. Its name is _gutenberg_ and it is stored in a folder called _catalog_, which is located in the application execution directory.  
You may also use either a PostgreSQL or a MySQL database, using the appropriate setting file.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Getting Started

The project is a Maven project, so you can import it in your favorite IDE as any other Maven project.

~~~
mvn install
~~~

will install the artifact in your local repository, being ready to be used as a dependency in any project:

~~~
<dependency>
  <groupId>org.josfranmc.gutenberg</groupId>
  <artifactId>JGutenbergCatalog</artifactId>
  <version>2.2.2</version>
</dependency>
~~~

When you build the project with Maven you get two jars in the target directory: _JGutenbergCatalog-2.2.2.jar_ and _JGutenbergCatalog-2.2.2-shaded.jar_. The first one is the standard jar of the project. The second one is an _uber_ jar with all necessary dependencies, which is suitable to use from command line.    

Download the latest _uber_ jar from [Releases](https://github.com/josfranmc/JGutenbergCatalog/releases).

## Usage

The main class to use is `JGutenbergCatalog`, which offers some methods to query and build the book catalog of Gutenberg project.  

If you unzip the catalog into a folder called _RdfFiles_ in the application's execution directory, the following code will read all RDF files and load them into memory:

~~~
JGutenbergCatalog jcatalog = new JGutenbergCatalog("RdfFiles/cache/epub");
jcatalog.readRdfFiles();
~~~

Then, you can get the catalog as a Map collection where the key is the book identifier. You can retrieve the books from this collection using their identifier:

~~~
Map<String, RdfFile> catalog = jcatalog.getRdfCatalog();
catalog.get("10607").getBook();
~~~

Or you can retrieve a book directly:

~~~
Book book = jcatalog.getBook("10607");
~~~

After reading the files and loading their data into memory you can load these data into a database with the following method: 

~~~
jcatalog.loadDb();
~~~

By default a HSQL database is used. This database is created inside a folder called _catalog_ in the application's execution directory.

If you want to use either a PostgreSQL or a MySQL database you can specify the access configuration using a properties file (you can use the templates in the repository to specify the database connection data):

~~~
jcatalog.setDatabase("postgresql-connection.properties");
jcatalog.loadDb();
~~~

Finally, you can execute the JGutenbergCatalog's main method by passing the setting options as argument. The following code reads RDF files from RdfFiles/cache/epub folder and loads the read data into a database, deleting previous data if it exists (-d argument):

~~~
String[] args = {"-r", "RdfFiles/cache/epub", "-d"};
JGutenbergCatalog.main(args);
~~~

These are the options you can use as arguments: 
 
~~~
-r xxx (xxx path to the RDF files folder)
-b xxx (xxx  path to the database setting file)
-d     (delete previous data)

(only -h to show options list);
~~~
 
---

It is possible to run the program from the command line. To this purpose, you may use the _JGutenbergCatalog-2.2.2-shaded.jar_ package this way (the -r parameter is mandatory):

~~~
java -jar JGutenbergCatalog-2.2.2-shaded.jar -r "path/to/catalog/rdf" [-b "path/to/database/setting/file" -d]
~~~

## License

[GPLv3](https://www.gnu.org/licenses/gpl-3.0) or later, see
[LICENSE](LICENSE) for more details.