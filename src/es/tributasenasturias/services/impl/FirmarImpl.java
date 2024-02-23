/**
 * 
 */
package es.tributasenasturias.services.impl;

import es.tributasenasturias.firmas.ISigner;

/**
 * @author crubencvs
 * Clase abstracta de implementaci�n de funcionalidad del servicio Web. Se utilizar� una clase hija
 * para la implementaci�n concreta. Esta se tiene s�lo para asociar el tipo de implementaci�n con las
 * interfaces de firma
 */
public abstract class FirmarImpl {
	//Asociaci�n con un firmador. Cada una de las clases hijas deber�n indicar qu� tipos
	//aceptar� el firmador.
	protected ISigner<?, ?> signer;
}
