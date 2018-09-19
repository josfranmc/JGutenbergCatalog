# JGutenbergCatalog
Permite la creación de una base de datos con la información de los libros existentes en el catálogo del proyecto Gutenberg.

## Contenido
+ jar-flat: fichero jar con las clases de la aplicación junto a las dependencias necesarias, que se ubican en la carpeta lib
+ jar-shaded: fichero jar con las clases de la aplicación y con las dependencias utilizadas incluidas en él
+ javadoc: documentación del código
+ src: Código fuente

## Uso
Desde línea de comandos:

java -jar JGutenbergCatalog-1.0-shaded.jar

## Notas  
Si se usa el jar flat hay que asegurarse que exista la carpeta lib dentro de la carpeta desde la que ejecutemos la aplicación.

La base de datos creada se guarda en la carpe db/HSQLDB.

El fichero de log generado se guarda en la carpeta log.
