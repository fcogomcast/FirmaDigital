/**
 * 
 */
package es.tributasenasturias.utils.Log;

/**
 * @author crubencvs
 * 
 */



public class TributasLogger extends GenericAppLogger
{
	private final String LOG_FILE = "Application.log";
	private final String LOG_DIR = "proyectos/FirmaDigital";

	public TributasLogger()
	{
		this.setLogFile(LOG_FILE);
		this.setLogDir(LOG_DIR);
		this.setNombre("FirmaDigital"); //Nombre de proceso que aparecerá en el log.
	}
	
	
	
}
