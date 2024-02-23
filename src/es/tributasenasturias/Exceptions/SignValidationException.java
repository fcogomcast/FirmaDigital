/**
 * 
 */
package es.tributasenasturias.Exceptions;

/**
 * Excepción de validación de firma.
 * @author crubencvs
 *
 */
public class SignValidationException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3439227802933900372L;
	private String _msgTxt=null;
	
	public SignValidationException(String error, Throwable original) {
		super(error, original);
	}

	public SignValidationException(String error) {
		super(error);
	}
	//Constructor para incluir el texto de mensaje XML asociado al error
	public SignValidationException (String error, Throwable original, String mesg)
	{
		this (error,original);
		this._msgTxt = mesg;
	}
	public SignValidationException (String error, String mesg)
	{
		this (error);
		this._msgTxt = mesg;
	}
	@Override
	public String getMessage()
	{
		String message="";
		if (this._msgTxt!=null)
		{
			message = "["+this.getClass().getName()+"][msg a validar:"+this._msgTxt+"]"+super.getMessage();
		}
		return message;
	}
}
