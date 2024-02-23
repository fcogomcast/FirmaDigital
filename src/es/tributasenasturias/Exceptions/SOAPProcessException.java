/**
 * 
 */
package es.tributasenasturias.Exceptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPException;

/**
 * Excepción de proceso de mensajes SOAP.
 * @author crubencvs
 *
 */
public class SOAPProcessException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5846147153722380456L;
	//Mensaje SOAP asociado con el error, en formato SOAPMessage
	private javax.xml.soap.SOAPMessage _msgBin;
	//Texto de mensaje SOAP asociado con el error, en formato de texto. Este podría ser o no un SOAP válido.
	private String _msgTxt;
	public SOAPProcessException(String error, Throwable original) {
		super(error, original);
	}

	public SOAPProcessException(String error) {
		super(error);
	}
	//Constructor para incluir el texto que se supone representa un mensaje SOAP
	public SOAPProcessException (String error, Throwable original, String xml)
	{
		this (error,original);
		this._msgTxt = xml;
	}
	public SOAPProcessException (String error, String xml)
	{
		this (error);
		this._msgTxt = xml;
	}
	//Constructor para incluir el mensaje SOAP asociado al error
	public SOAPProcessException (String error, Throwable original, javax.xml.soap.SOAPMessage mesg)
	{
		this (error,original);
		this._msgBin = mesg;
	}
	public SOAPProcessException (String error, javax.xml.soap.SOAPMessage mesg)
	{
		this (error);
		this._msgBin = mesg;
	}
	@Override
	public String getMessage()
	{
		String message="";
		String msg="";
		if (this._msgTxt!=null)
		{
			message = "["+this.getClass().getName()+"][msg:"+this._msgTxt+"]"+super.getMessage();
		}
		else if (this._msgBin!=null)
		{
			
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			try {
				this._msgBin.writeTo(bo);
			} catch (SOAPException e) {
				msg="";
			} catch (IOException e) {
				msg="";
			}
			msg = new String(bo.toByteArray());
			message = "["+this.getClass().getName()+"][msg:"+msg+"]"+super.getMessage();
		}
		return message;
	}
}
