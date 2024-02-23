/**
 * 
 */
package es.tributasenasturias.Exceptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPException;

/**
 * @author crubencvs
 *
 */
public class SignatureException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3659599451032302423L;
	//Mensaje SOAP asociado con el error, en formato SOAPMessage
	private javax.xml.soap.SOAPMessage _msgBin;
	
	public SignatureException(String error, Throwable original) {
		super(error, original);
	}

	public SignatureException(String error) {
		super(error);
	}

	//Constructor para incluir el mensaje SOAP asociado al error
	public SignatureException (String error, Throwable original, javax.xml.soap.SOAPMessage mesg)
	{
		this (error,original);
		this._msgBin = mesg;
	}
	public SignatureException (String error, javax.xml.soap.SOAPMessage mesg)
	{
		this (error);
		this._msgBin = mesg;
	}
	@Override
	public String getMessage()
	{
		String message="";
		if (this._msgBin!=null)
		{
			String msg;
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			try {
				this._msgBin.writeTo(bo);
			} catch (SOAPException e) {
				msg="";
			} catch (IOException e) {
				msg="";
			}
			msg = new String(bo.toByteArray());
			message = "["+this.getClass().getName()+"][msg a firmar:"+msg+"]"+super.getMessage();
		}
		return message;
	}
}
