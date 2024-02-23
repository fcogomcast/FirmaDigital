/**
 * 
 */
package es.tributasenasturias.utils.Log;

/**
 * Helper para log de errores. Construir� y llamar� a la funci�n de log adecuada, 
 * para no tener que construir el objeto cada vez. Como este log s�lo se usar�
 * en excepciones, el coste de construir el objeto y llamar a la funci�n no ser� apreciable,
 * porque ser� probablemente la �nica operaci�n que se har� antes de salir del servicio. 
 * @author crubencvs
 *
 */
public class LogErrorHelper {
	public enum FormatosSalida {Texto,XML} ;
	/**
	 * Generaci�n de log de error gen�rico.
	 * @param mensaje
	 * @param tipo
	 */
	public static void doErrorLog (String mensaje, FormatosSalida tipo)
	{
		if (tipo.equals(FormatosSalida.Texto))
		{
			GenericAppLogger log = new TributasLogger();
			log.error (mensaje);
		}
		else
		{
			throw new UnsupportedOperationException("No existe manejador de log para ese tipo");
		}
	}
	/**
	 * Generaci�n de un log de nivel "INFO".
	 * @param mensaje Mensaje a mostrar
	 * @param tipo Tipo de salida
	 */
	public static void doInfoLog (String mensaje, FormatosSalida tipo)
	{
		if (tipo.equals(FormatosSalida.Texto))
		{
			GenericAppLogger log = new TributasLogger();
			log.info (mensaje);
		}
		else
		{
			throw new UnsupportedOperationException("No existe manejador de log para ese tipo");
		}
	}
	/**
	 * Generaci�n de un mensaje de tipo Info.
	 * @param mensaje Mensaje a mostrar
	 */
	public static void doInfoLog (String mensaje)
	{
		doInfoLog (mensaje,FormatosSalida.Texto);
	}
	public static void doErrorLog (String mensaje)
	{
		doErrorLog (mensaje,FormatosSalida.Texto);
	}
	public static void printStackTrace(Exception ex, FormatosSalida tipo)
	{
		if (tipo.equals(FormatosSalida.Texto))
		{
			GenericAppLogger log = new TributasLogger();
			log.trace(ex.getStackTrace());
		}
		else
		{
			throw new UnsupportedOperationException("No existe manejador de log para ese tipo");
		}
	}
}
