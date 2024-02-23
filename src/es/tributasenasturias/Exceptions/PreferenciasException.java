package es.tributasenasturias.Exceptions;


/**
 * @author crubencvs
 *
 */
public class PreferenciasException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -320072959957805881L;
	private String _dir="";
	private String _file="";
	/**
	 * 
	 */
	
	public PreferenciasException(String error, Throwable original, String directory, String file)
	{
		this(error, original);
		this._dir=directory;
		this._file = file;
	}
	public PreferenciasException(String error, Throwable original){
		super(error,original);
	}
	public PreferenciasException (String error)
	{
		super(error);
	}
	
	@Override
	public String getMessage() {
		String message = 
			"["+this.getClass().getName()+"]["+this._dir+"/"+this._file+"]";
		message += super.getMessage();
		return message;
	}
	

}
