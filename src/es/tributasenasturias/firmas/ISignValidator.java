/**
 * 
 */
package es.tributasenasturias.firmas;

/**
 * Interfaz de operaciones de validaci�n de Firma.
 * @author crubencvs
 *
 */
public interface ISignValidator<T> {
	/**
	 * Acepta un par�metro firmado de un tipo, y comprueba si est� correctamente firmado.
	 * El tipo de entrada se concretar� en las clases que implementen la interfaz, por ejemplo
	 * un java.lang.String con texto de un xml, o javax.xml.soap.SOAPMessage, un objeto SOAP completo.
	 * @param objeto
	 * @return
	 */
	public boolean isValid(T objeto);
}
