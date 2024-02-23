/**
 * 
 */
package es.tributasenasturias.services.impl;

import javax.xml.soap.SOAPMessage;

import es.tributasenasturias.utils.Log.LogErrorHelper;
import es.tributasenasturias.utils.soap.SOAPProcessor;

import es.tributasenasturias.Exceptions.SOAPProcessException;
import es.tributasenasturias.Exceptions.SignatureException;
import es.tributasenasturias.firmas.SHA1SOAPSigner;
import es.tributasenasturias.firmas.XMLSignatureValidator;

/**
 * @author crubencvs
 *
 */
public class FirmarSOAPImpl extends FirmarImpl {
	public FirmarSOAPImpl()
	{
		signer = new SHA1SOAPSigner();
	}
	public String firmar(String xmlData, String aliasCertificado)
	{
		String msgFirmado=null;
		try
		{
			//Primero, convertir el texto a mensaje SOAP. De esta forma implícitamente comprobaremos si el 
			//mensaje es válido.
			SOAPMessage msg = SOAPProcessor.convertStringToMsg(xmlData);
			//Ahora, simplemente enviaremos el mensaje a un firmador adecuado. En este caso es 
			//el asociado a la clase, que es el de SOAP.
			msgFirmado = ((SHA1SOAPSigner) signer).firmar(msg, aliasCertificado);
		}
		catch (SOAPProcessException ex)
		{
			LogErrorHelper.doErrorLog (ex.getError() + "-"+ ex.getMessage());
		}
		catch (SignatureException ex)
		{
			LogErrorHelper.doErrorLog (ex.getError() + "-" + ex.getMessage());
		}
		return msgFirmado;
	}
	public boolean validar (String xmlFirmado)
	{
		return new XMLSignatureValidator().isValid(xmlFirmado);
	}
}
