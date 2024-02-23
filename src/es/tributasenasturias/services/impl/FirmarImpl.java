/**
 * 
 */
package es.tributasenasturias.services.impl;

import es.tributasenasturias.firmas.ISigner;

/**
 * @author crubencvs
 * Clase abstracta de implementación de funcionalidad del servicio Web. Se utilizará una clase hija
 * para la implementación concreta. Esta se tiene sólo para asociar el tipo de implementación con las
 * interfaces de firma
 */
public abstract class FirmarImpl {
	//Asociación con un firmador. Cada una de las clases hijas deberán indicar qué tipos
	//aceptará el firmador.
	protected ISigner<?, ?> signer;
}
