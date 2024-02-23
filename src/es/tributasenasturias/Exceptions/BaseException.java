package es.tributasenasturias.Exceptions;

/**
 * @author crubencvs
 *
 */
public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6685880375356325724L;
	/**
	 * 
	 */

	
	private String _error;
	private Throwable _original;
	
	public BaseException(String error, Throwable original){
		this._error = error;
		this._original = original;
	}
	public BaseException (String error)
	{
		this._error = error;
		_original=null;
	}
	
	public Throwable getCause(){
		return _original;
	}
	
	public String getMessage(){
		StringBuffer strBuffer = new StringBuffer();
		if(super.getMessage()!=null){
			strBuffer.append(super.getMessage());
		}
		if(_original !=null){
			strBuffer.append("[");
			strBuffer.append(_original.getMessage());
			strBuffer.append("]");
		}
		return strBuffer.toString();
	}
	
	public String getError(){
		return _error;
	}

}
