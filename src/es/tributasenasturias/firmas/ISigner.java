package es.tributasenasturias.firmas;

import es.tributasenasturias.Exceptions.SignatureException;

/**
 * 
 * @author crubencvs
 * Interfaz para operaciones de firma de mensaje. Acepta un objeto de un tipo T,
 * y devuelve un objeto S de otro o el mismo tipo que representa el objeto T firmado.
 */
public interface ISigner<T,S> {
	/**
	 * Operaci�n de firma. Acepta el objeto a firmar y el alias del certificado que se utilizar� para
	 * firmarlo.
	 * @param objeto Objeto a firmar
	 * @param aliasCertificado Alias del certificado que se utilizar� para firmar. Puede ser el nombre real
	 * o bien un c�digo.
	 * @return Objeto firmado.
	 * @throws SignatureException
	 */
	public S firmar(T objeto, String aliasCertificado) throws SignatureException;
}
