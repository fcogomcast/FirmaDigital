/**
 * 
 */
package es.tributasenasturias.utils.Preferencias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

import es.tributasenasturias.Exceptions.PreferenciasException;
import es.tributasenasturias.utils.Utils;
public class Preferencias 
{
	static private Preferencias _pref = new Preferencias();
	private Preferences m_preferencias;
	private final String FICHERO_PREFERENCIAS = "prefsFirmaDigital.xml";
	private final String DIRECTORIO_PREFERENCIAS = "proyectos/FirmaDigital";
 
 private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();
	
	//nombres de las preferencias
	private final String KEY_PREF_ALMACEN = "Almacen";
	private final String KEY_PREF_ALMACEN_PASSWORD = "ClaveAlmacen";
	private final String KEY_PREF_CERTIFICADO = "Certificado";
	private final String KEY_PREF_CERTIFICADO_PASSWORD = "ClaveCertificado";
	private final String KEY_PREF_SIGNATURE_EL = "TagFirma";
	private final String KEY_PREF_LOG = "ModoLog";
	//CRUBENCVS 47084 27/01/2023. Algoritmos de firma y digest por defecto para 
	//la nueva operación de firma indicando algoritmo.
	private final String KEY_PREF_SIGN_DEF_ALG = "AlgoritmoFirmaDefecto";
	private final String KEY_PREF_DIG_DEF_ALG  = "AlgoritmoDigestDefecto";
	//FIN CRUBENCVS 47084

	
	private Preferencias() 
	{		
		try
		{
			CargarPreferencias();
		}
		catch (Exception e)
		{

		}
	}
	protected void CargarPreferencias() throws PreferenciasException
 {
		try
		{
			if(CompruebaFicheroPreferencias())
			{		       
			
		        FileInputStream inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		        Preferences.importPreferences(inputStream);
		        inputStream.close();
		
		        m_preferencias = Preferences.systemNodeForPackage(this.getClass());
		        
		        String[] keys = m_preferencias.keys();
		        String msgKeys ="Leyendo las siguientes claves -> ";
		        for(int i=0;i<keys.length;i++)
		        {
		        	msgKeys += "["+keys[i]+"] ";
		        }
		        //Logger.debug(msgKeys);
		        
		        for(int i=0;i<keys.length;i++)
		        {
		        	String value = m_preferencias.get(keys[i], "");
		        	tablaPreferencias.put(keys[i], value);
		        }
			}
		}
		catch (Exception ex)
		{
			throw new PreferenciasException ("Error al cargar las preferencias: " +ex.getMessage(),ex,DIRECTORIO_PREFERENCIAS,FICHERO_PREFERENCIAS);
		}
		
 }
	
	private void InicializaTablaPreferencias()
	{
		
		tablaPreferencias.clear();
		
		tablaPreferencias.put(KEY_PREF_ALMACEN,								"Informar Nombre de Almacén");
		tablaPreferencias.put(KEY_PREF_ALMACEN_PASSWORD,					"Informar Clave de Almacén");
		tablaPreferencias.put(KEY_PREF_CERTIFICADO,							"Informar nombre de Certificado dentro del almacén");
		tablaPreferencias.put(KEY_PREF_CERTIFICADO_PASSWORD,			    "Informar clave del Certificado dentro del almacén.");
		tablaPreferencias.put(KEY_PREF_SIGNATURE_EL,			  		    "Informar etiqueta del elemento de cabecera que contendrá la firma.");
		tablaPreferencias.put(KEY_PREF_LOG,			    					"ALL");
		//CRUBENCVS 47084 27/01/2023. Algoritmo de firma por defecto para la nueva 
		//operación de firma xml indicando algoritmo
		tablaPreferencias.put(KEY_PREF_SIGN_DEF_ALG,	    				"http://www.w3.org/2000/09/xmldsig#rsa-sha1");
		tablaPreferencias.put(KEY_PREF_DIG_DEF_ALG,	    					"http://www.w3.org/2000/09/xmldsig#sha1");
		//FIN CRUBENCVS 47084
	}
	
	private boolean CompruebaFicheroPreferencias() throws PreferenciasException
 {
		boolean existeFichero = false;
		
     File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
     existeFichero = f.exists();
     if (existeFichero == false)
     {
         CrearFicheroPreferencias();
     }
     
     return existeFichero;
 }
	
	 /***********************************************************
  * 
  * Creamos el fichero de preferencias con los valores por 
  * defecto
  * 
  ***************************************************************/
 private synchronized void CrearFicheroPreferencias() throws PreferenciasException
 {
 	
     //preferencias por defecto
     m_preferencias = Preferences.systemNodeForPackage(this.getClass());
     
     InicializaTablaPreferencias();
     
     //recorremos la tabla cargada con las preferencias por defecto
     Iterator<Map.Entry<String, String>> itr = tablaPreferencias.entrySet().iterator();
     while(itr.hasNext())
     {
     	Map.Entry<String, String> e = (Map.Entry<String,String>)itr.next();
     	//Logger.debug("Cargando en fichero preferencias ['"+e.getKey()+"' - '"+e.getValue()+"']");
     	
     	m_preferencias.put(e.getKey(),e.getValue());
     }

     FileOutputStream outputStream = null;
     File fichero;
     try
     {
         fichero = new File(DIRECTORIO_PREFERENCIAS);
         if(fichero.exists() == false)
             if (fichero.mkdirs()==false)
             	{
             	 throw new java.io.IOException ("No se puede crear el directorio de las preferencias.");
             	}
         
         outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
         m_preferencias.exportNode(outputStream);
     }
     catch (Exception e)
     {
    	throw new PreferenciasException("Error al crear el fichero de preferencias:" + e.getMessage(),e,DIRECTORIO_PREFERENCIAS,FICHERO_PREFERENCIAS);
     }
     finally
     {
         try
         {
             if(outputStream != null)
                 outputStream.close();
         }
         catch(Exception e)
         {
        	 throw new PreferenciasException ("Error al cerrar el flujo del fichero de preferencias:" + e.getMessage(),e,DIRECTORIO_PREFERENCIAS,FICHERO_PREFERENCIAS);
         }
     }
 }
 
 public void recargaPreferencias() throws PreferenciasException
 {
 	CargarPreferencias();
 }
 
 private String getValueFromTablaPreferencias(String key)
 {
 	String toReturn="";
 	
 	if(tablaPreferencias.containsKey(key))
 	{
 		toReturn = tablaPreferencias.get(key);
 	}
 	
 	//Logger.debug("Se ha pedido la preferencia '"+key+"' a lo que el sistema devuelve '"+toReturn+"'");
 	
 	return toReturn;
 }
 
 private void setValueIntoTablaPreferencias(String key, String value)
 {
 	//Logger.debug("Se actualizara el valor de la preferencia '"+key+"' a '"+value+"'");
 	tablaPreferencias.put(key, value);
 }
	
	// Este método devolverá la instancia de clase.
 public static Preferencias getPreferencias () throws PreferenciasException
 {
 	if (_pref==null)
 	{
 		throw new PreferenciasException("No se han podido recuperar las preferencias.");
 	}
 	_pref.recargaPreferencias();
 	return _pref;
 }
	public String getAlmacen() {
		return getValueFromTablaPreferencias(KEY_PREF_ALMACEN);
	}
	public void setAlmacen(String almacen) {
		setValueIntoTablaPreferencias(KEY_PREF_ALMACEN, almacen);
	}
	public String getClaveAlmacen() {
		return getValueFromTablaPreferencias(KEY_PREF_ALMACEN_PASSWORD);
	}
	public void setClaveAlmacen(String claveAlmacen) {
		setValueIntoTablaPreferencias(KEY_PREF_ALMACEN_PASSWORD, claveAlmacen);
	}
	public String getCertificado(String aliasCertificado) {
		String alias = Utils.initcap(aliasCertificado);
		return getValueFromTablaPreferencias(KEY_PREF_CERTIFICADO + alias);
	}
	public String getClaveCertificado(String aliasCertificado) {
		String alias = Utils.initcap(aliasCertificado);
		return getValueFromTablaPreferencias(KEY_PREF_CERTIFICADO_PASSWORD + alias);
	}
	public String getTagFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_SIGNATURE_EL);
	}
	public void setTagFirma(String tagFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_SIGNATURE_EL, tagFirma);
	}
	public String getModoLog() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG);
	}
	public void setModoLog(String modoLog) {
		setValueIntoTablaPreferencias(KEY_PREF_LOG, modoLog);
	}
	
	//CRUBENCVS 47084 27/01/2023.
	public String getDefaultSignatureAlgorithm() {
		return getValueFromTablaPreferencias(KEY_PREF_SIGN_DEF_ALG);
	}
	public String getDefaultDigestAlgorithm() {
		return getValueFromTablaPreferencias(KEY_PREF_DIG_DEF_ALG);
	}
	//FIN CRUBENCVS 47084
	public KeyStore ObtenerAlmacen(String rutaAlmacen, String passwordAlmacen) throws java.io.IOException, 
						java.security.NoSuchAlgorithmException, 
						java.security.cert.CertificateException, 
						java.security.KeyStoreException
    {
        // obtiene un objeto almacen del tipo por defecto (JKS)
        KeyStore almacen = KeyStore.getInstance(KeyStore.getDefaultType());
        // cargamos el almacen
        almacen.load(new FileInputStream(rutaAlmacen), passwordAlmacen.toCharArray());
        return almacen;
    }
	
	public KeyStore.PrivateKeyEntry ObtenerClavePrivada(String aliasCertificado)  throws PreferenciasException
    {

		try
		{
	        KeyStore almacen = ObtenerAlmacen(this.getAlmacen(), this.getClaveAlmacen());
	        if (almacen == null)
	        {
	            throw new PreferenciasException("No se encuentra el almacén indicado en preferencias en el sistema");
	        }
	        
	        //obtenemos la clave
	        KeyStore.PasswordProtection passProtection = new KeyStore.PasswordProtection(this.getClaveCertificado(aliasCertificado).toCharArray());
	        KeyStore.PrivateKeyEntry thePrivKeyEntry = (KeyStore.PrivateKeyEntry)almacen.getEntry(this.getCertificado(aliasCertificado), passProtection);
	
	        //en caso de no obtener nada intentamos buscar por el subject
	        if (thePrivKeyEntry == null)
	        {
	            String elemento;
	            X509Certificate certificado;
	            String certificadoDelSubject = null;
	
	            //obtenemos y recorremos los alias del almancen de claves
	            //obteniendo cada certificado y de cada uno sacando su subject
	            //para compararlo con lo pasado por parametro
	            java.util.Enumeration<String> aliasEnAlmacen = almacen.aliases();
	            while (aliasEnAlmacen.hasMoreElements())
	            {
	                elemento = aliasEnAlmacen.nextElement();
	                certificado = (X509Certificate)almacen.getCertificate(elemento);
	
	                if (certificado.getSubjectDN().getName().equals(this.getCertificado(aliasCertificado)))
	                {
	                    //en caso de que coincida salimos
	                    certificadoDelSubject = elemento;
	                    break;
	                }
	            }
	
	            if (certificadoDelSubject != null)
	            {
	                thePrivKeyEntry = (KeyStore.PrivateKeyEntry)almacen.getEntry(certificadoDelSubject, passProtection);
	            }
	        }
	
	        return thePrivKeyEntry;
		}
		catch (java.security.KeyStoreException ex)
		{
			throw new PreferenciasException("Error al obtener la clave privada: " + ex.getMessage(),ex);
		}
		catch (java.security.UnrecoverableEntryException ex)
		{
			throw new PreferenciasException("Error al obtener la clave privada, no se puede obtener la entrada del almacén: " + ex.getMessage(),ex);
		}
		catch (java.security.NoSuchAlgorithmException ex)
		{
			throw new PreferenciasException("Error al obtener la clave privada, algoritmo de firma inválido: " + ex.getMessage(),ex);
		}
		catch (java.security.cert.CertificateException ex)
		{
			throw new PreferenciasException("Error al obtener la clave privada, error en certificado: " + ex.getMessage(),ex);
		}
		catch (java.io.IOException ex)
		{
			throw new PreferenciasException("Error al obtener la clave privada, error de E/S: " + ex.getMessage(),ex);
		}
    }
}

