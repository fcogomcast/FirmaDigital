/**
 * 
 */
package es.tributasenasturias.firmas;

/**
 * Interfaz de operaciones de validación de Firma.
 * @author crubencvs
 *
 */
public interface ISignValidator<T> {
	/**
	 * Acepta un parámetro firmado de un tipo, y comprueba si está correctamente firmado.
	 * El tipo de entrada se concretará en las clases que implementen la interfaz, por ejemplo
	 * un java.lang.String con texto de un xml, o javax.xml.soap.SOAPMessage, un objeto SOAP completo.
	 * @param objeto
	 * @return
	 */
	public boolean isValid(T objeto);
}
