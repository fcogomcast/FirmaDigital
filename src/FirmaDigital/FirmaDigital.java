package FirmaDigital;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.prefs.Preferences;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import es.tributasenasturias.circe.FirmaCirce;
import es.tributasenasturias.services.impl.FirmarSOAPImpl;
import es.tributasenasturias.utils.Preferencias.Preferencias;


import org.bouncycastle.util.encoders.Base64;




/*****************************************************************************
 *
 * Clase WebService que encapsula las llamadas a la clase estatica
 * DigitalSignatureManager, encargada de firmar y validar digitalmente un xml
 *
 * Esta clase esta encargada de obtener el almacen de claves y la contraseña
 * de este desde un fichero xml de preferencias (se crea la primera vez si no existe)
 * 
 * Adquiere la responsabilidad de decidir que la parte a firmar del documento
 * sea lo identificado como #Body y que la firma se incluya en la etiqueta
 * soapenv:Header configurado en constantes
 *
 * ***************************************************************************/
@WebService(serviceName="wsFirmaDigital", targetNamespace="http://localhost:7001", portName="servicesPort")
public class FirmaDigital
{
    private Preferences m_preferencias;
    private String m_almacen;
    private String m_password;

    //constantes para trabajar con las preferencias
    private final String FICHERO_PREFERENCIAS = "prefsFirmaDigital.xml";
    private final String DIRECTORIO_PREFERENCIAS = "proyectos/FirmaDigital";
    private final String NOMBRE_PREF_ALMACEN = "Almacen";
    private final String NOMBRE_PREF_PASSWORD = "Password";
    private final String VALOR_INICIAL_PREF_ALMACEN = "Escribe aqui la ruta fisica del fichero que representa el almacen de claves";
    private final String VALOR_INICIAL_PREF_PASSWORD = "Escribe aqui la contraseña del almacen";
    
    //constantes de configuracion de firma
    private final String PARTE_A_FIRMAR = "#Body"; //parte del documento a firmar
    private final String PARTE_A_FIRMAR_ANCERT = "#declaracion"; //parte del documento a firmar
    private final String PADRE_DE_LA_FIRMA_ANCERT = "remesa"; //parte del documento donde se incluye la firma
    private final String PADRE_DE_LA_FIRMA = "soapenv:Header"; //parte del documento donde se incluye la firma

    public FirmaDigital()
    {
        //de este modo, al instalar el webservice, se creara el fichero de preferencias si no existe
        CompruebaFicheroPreferencias();
    }

    /**
     * Firma digitalmente con el certificado indicado, el xml recibido por parametro
     *
     * @param xmlData - Requiere el contenido de un XML
     * @param identificadorCertificado - Alias o subject del certificado con el que firmar
     * @return Devuelve un xml con la firma
     */
    @WebMethod()
    @WebResult (name="xmlFirmado")
    public String Firmar(@WebParam (name="xmlData")String xmlData, 
    					 @WebParam (name="identificadorCertificado")String identificadorCertificado,
    					 @WebParam (name="passwordCertificado") String passwordCertificado) throws Exception
    {
        try
        {
        	Preferencias pr = Preferencias.getPreferencias();
            //cargamos los datos del almacen de un fichero xml preferencias
            //CargarPreferencias();
        	
            //chequeo parametros
            if(xmlData == null || xmlData.length() == 0 || identificadorCertificado == null || identificadorCertificado.length() == 0)
            {
                throw new Exception("Los campos xmlData e identificadorCertificado son obligatorios.");
            }
            m_almacen = pr.getAlmacen();
            m_password= pr.getClaveAlmacen();
            if(passwordCertificado==null || passwordCertificado.length()==0)
            {
                //asumimos que la contraseña del certificado sera la misma que la del almacen
                passwordCertificado = m_password;
            }

            String resultXML = DigitalSignatureManager.Firma(
                xmlData,                    //xml
                PARTE_A_FIRMAR,             //parte del documento a firmar
                PADRE_DE_LA_FIRMA,          //parte del documento donde se incluye la firma
                m_almacen,                  //nombre del almacen de claves por preferencia
                m_password,                 //passworde del almacen de claves por preferencia 
                identificadorCertificado,   //recibido por parametro, puede ser el subject o el alias del certificado (se buscara en el almacen) 
                passwordCertificado);       //password del certificado recibida por parametro

            return resultXML;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw e;
        }
    }

    @WebMethod()
    @WebResult (name="esValido")
    public boolean Validar(@WebParam (name="xmlData")String xmlData) throws Exception
    {
        try
        {
            //chequeo parametros
            if(xmlData == null || xmlData.length() == 0)
            {
                throw new Exception("El campo xmlData es obligatorio.");
            }
                    
            return DigitalSignatureManager.Valida(xmlData);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();

            return false;
        }
    }
    
    /**
     *
     * @param rutaCertificado - fichero .X509 que contiene el certificado
     * @param rutaClavePrivada - fichero .der que contiene la clave privada del certificado en formato pkcs
     * @param nuevoAlias - nuevo alias a asignar al certificado dentro del almacen
     * @param password - password que se le asignara al certificado dentro del almacen
     *
     * @throws Exception
     *
     * Para obtener los ficheros X509 y DER de un CER utilizaremos la herramienta gnu openssl
     * Comandos necesarios:
     *
     *      openssl.exe pkcs12 -in prueba.pfx -out prueba.pem -nodes
     *
     *      openssl.exe x509 -in prueba.pem -out prueba.x509
     *
     *      openssl pkcs8 -topk8 -inform PEM -in prueba.pem -outform DER -nocrypt -out prueba.der
     */
    @WebMethod
    public void InsertarCertificado(@WebParam (name="rutaCertificado")String rutaCertificado, 
    								@WebParam (name="rutaClavePrivada")String rutaClavePrivada, 
    								@WebParam (name="aliasCertificado")String aliasCertficado, 
    								@WebParam (name="passwordCertificado")String passwordCertificado) throws Exception
    {
        try
        {
            //CargarPreferencias();
        	Preferencias pr = Preferencias.getPreferencias();
            
            //chequeo parametros
            if(rutaCertificado == null || rutaCertificado.length() == 0 || rutaClavePrivada == null || rutaClavePrivada.length() ==0 ||
                aliasCertficado == null || aliasCertficado.length() == 0)
            {
                throw new Exception("Los campos rutaCertificado, rutaClavePrivada y aliasCertificado son obligatorios.");
            }
            
            m_almacen = pr.getAlmacen();
            m_password = pr.getClaveAlmacen();
            if(passwordCertificado == null || passwordCertificado.length()==0)
            {
                //en caso de vacio asignamos la password del almacen
                passwordCertificado = m_password;
            }

            DigitalSignatureManager.InsertarCertificado(m_almacen, m_password, rutaClavePrivada, rutaCertificado, aliasCertficado, passwordCertificado);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw e;
        }
    }

    // Obtencion de las preferencias que especificaran el almacen y su contraseña
    protected void CargarPreferencias() throws Exception
    {
        File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
        if (f.exists())
        {
            //si existe el fichero de preferencias lo cargamos
            FileInputStream inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
            Preferences.importPreferences(inputStream);
            inputStream.close();

            m_preferencias = Preferences.systemNodeForPackage(this.getClass());

            //obtenemos las preferencias
            m_almacen = m_preferencias.get(NOMBRE_PREF_ALMACEN, "");
            m_password = m_preferencias.get(NOMBRE_PREF_PASSWORD, "");

            //chequeo de valores por defecto
            if (m_almacen.equals(VALOR_INICIAL_PREF_ALMACEN) || m_password.equals(VALOR_INICIAL_PREF_PASSWORD))
            {
                //todavia no las ha inicializado en el fichero
                throw new Exception("Debe especificar primero las preferencias en el fichero: " + f.getAbsolutePath() + " (parar el servicio)");
            }
        }
        else
        {
            //si no existe el fichero de preferencias lo crearemos
            CrearFicheroPreferencias();

            throw new Exception("Debe especificar primero las preferencias en el fichero: " + f.getAbsolutePath() + " (parar el servicio)");
        }
    }

    private void CompruebaFicheroPreferencias()
    {
        File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
        if (f.exists() == false)
        {
            CrearFicheroPreferencias();
        }
    }
    
    /***********************************************************
     * 
     * Creamos el fichero de preferencias con los valores por 
     * defecto
     * 
     ***************************************************************/
    private void CrearFicheroPreferencias()
    {
        //preferencias por defecto
        m_preferencias = Preferences.systemNodeForPackage(this.getClass());
        m_preferencias.put(NOMBRE_PREF_ALMACEN, VALOR_INICIAL_PREF_ALMACEN);
        m_preferencias.put(NOMBRE_PREF_PASSWORD, VALOR_INICIAL_PREF_PASSWORD);

        FileOutputStream outputStream = null;
        File fichero;
        try
        {
            fichero = new File(DIRECTORIO_PREFERENCIAS);
            if(fichero.exists() == false)
                fichero.mkdirs();
            
            outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
            m_preferencias.exportNode(outputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
                System.out.println("Error cerrando fichero de preferencias -> " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
	 * Firma el mensaje SOAP que le llega en formato de cadena de texto
	 * Los parámetros de salida se devolverán como etiquetas en el mensaje SOAP 
	 * correctamente.
	 * @param xmlData
	 * @param resultado
	 * @param xmlFirmado
	 */
    @WebMethod (operationName="FirmarSOAP")
	public void FirmarSOAP(@WebParam (name="xmlData")String xmlData, 
					   @WebParam (name="aliasCertificado")String aliasCertificado, 
					   @WebParam (name="xmlFirmado", mode=WebParam.Mode.OUT)Holder<String> xmlFirmado,
					   @WebParam (name="resultado", mode=WebParam.Mode.OUT)Holder<String> resultado) {
		try
		{
			String firmado = new FirmarSOAPImpl().firmar(xmlData, aliasCertificado);
			if (firmado!=null)
			{
				resultado.value="OK";
			}
			else
			{
				resultado.value="KO";
			}
			xmlFirmado.value= firmado;
		}
		catch (Exception ex)
		{
			es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("Error inesperado en el procesamiento de la firma:" + ex.getMessage());
			resultado.value="KO";
			xmlFirmado.value="";
		}
		return;
	}
    /*
    *
    * La intencion era que recibiera el xml en base64
    * ya que, internamente, al recibir el webservice
    * un xml dentro de otro xml (encima SOAP) se penso
    * que podia dar errores de xml mal formado.
    * Pero parece que funciona...
    *
    * protected String TransformFromBase64(String data)
    {
        String dataDecodec = new String();
        BASE64Decoder base64dec = new BASE64Decoder();

        try
        {
            byte[] decodecBytes = base64dec.decodeBuffer(data);
            dataDecodec = new String(decodecBytes);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return dataDecodec;
    }

    protected String TransformToBase64(String data)
    {
        BASE64Encoder base64enc = new BASE64Encoder();
        return base64enc.encode(data.getBytes());
    }*/



    /**
     * Firma digitalmente con el certificado indicado, el xml recibido por parametro
     *
     * @param xmlData - Requiere el contenido de un XML
     * @param identificadorCertificado - Alias o subject del certificado con el que firmar
     * @return Devuelve un xml con la firma
     */
    @WebMethod()
    @WebResult (name="xmlFirmado")
    public String FirmarAncert(@WebParam (name="xmlData")String xmlData, 
    					 @WebParam (name="identificadorCertificado")String identificadorCertificado,
    					 @WebParam (name="passwordCertificado") String passwordCertificado) throws Exception
    {
        try
        {
        	Preferencias pr = Preferencias.getPreferencias();
            //cargamos los datos del almacen de un fichero xml preferencias
            //CargarPreferencias();
        	
            //chequeo parametros
            if(xmlData == null || xmlData.length() == 0 || identificadorCertificado == null || identificadorCertificado.length() == 0)
            {
                throw new Exception("Los campos xmlData e identificadorCertificado son obligatorios.");
            }
            m_almacen = pr.getAlmacen();
            m_password= pr.getClaveAlmacen();
            if(passwordCertificado==null || passwordCertificado.length()==0)
            {
                //asumimos que la contraseña del certificado sera la misma que la del almacen
                passwordCertificado = m_password;
            }

            String resultXML = DigitalSignatureManagerAncert.Firma(
                xmlData,                    //xml
                PARTE_A_FIRMAR_ANCERT,      //parte del documento a firmar
                PADRE_DE_LA_FIRMA_ANCERT,          //parte del documento donde se incluye la firma
                m_almacen,                  //nombre del almacen de claves por preferencia
                m_password,                 //passworde del almacen de claves por preferencia 
                identificadorCertificado,   //recibido por parametro, puede ser el subject o el alias del certificado (se buscara en el almacen) 
                passwordCertificado);       //password del certificado recibida por parametro

            return resultXML;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw e;
        }
    }
    /**
     * Firma digitalmente con el certificado indicado
     *
     * @param xmlData - Requiere el String a firmar
     * @param identificadorCertificado - Alias o subject del certificado con el que firmar
     * @return Devuelve un String FIRMADO
     */
    @WebMethod()
    @WebResult (name="contenidoFirmado")
    public String FirmarCIRCE(@WebParam (name="xmlData")String xmlData, 
    					 @WebParam (name="identificadorCertificado")String identificadorCertificado,
    					 @WebParam (name="passwordCertificado") String passwordCertificado,
    					 @WebParam (name="firmarComoBinario") boolean firmarComoBinario) throws Exception
    {
        try
        {
        	Preferencias pr = Preferencias.getPreferencias();
                    	
            //chequeo parametros
            if(xmlData == null || xmlData.length() == 0)
            {
                throw new Exception("El campo xmlData es obligatorios.");
            }
            m_almacen = pr.getAlmacen();
            m_password= pr.getClaveAlmacen();
            if(passwordCertificado==null || passwordCertificado.length()==0)
            {
                //asumimos que la contrasera del certificado sera la misma que la del almacen
                passwordCertificado = m_password;
            }
            FirmaCirce fE = new FirmaCirce(m_almacen, m_password, identificadorCertificado);
            //CRUBENCVS 05/07/2012. Se añade un nuevo parámetro,
            //firmarComoBinario, que hace que el contenido de xmlData no se interprete 
            //como texto, sino como binario en Base64, a efectos de firmar el contenido
            //binario del mismo.
            //La firma del PDF que se enviaba a CIRCE en el nodo "Contenido" era incorrecta,
            //ya que estaba firmando el Base64 que llegaba, y no el contenido binario 
            //al que correspondía, que era lo que esperaban en ANCERT.
            //En realidad, lo que había antes debería de poder funcionar, pero al tratarse de
            //ficheros binarios, depende de la codificación el que la cadena "xmldata"
            //se convierta de binario a cadena correctamente.
            //Por tanto, se modifica para que acepte el nuevo parámetro, de tal manera
            //que la firma se haga directamente sobre el binario, y así
            //evitamos posibles errores.
            String firmaBase64="";
            if (!firmarComoBinario) //Firmamos como texto
            {
	            xmlData = new String(Base64.decode(xmlData),FirmaCirce.codificacionISO);
	            byte[] byteFirma = fE.sign(xmlData.getBytes(FirmaCirce.codificacionISO));
	            firmaBase64 = new String(Base64.encode(byteFirma), FirmaCirce.codificacionISO);
            }
            else
            {
            	byte[] byteFirma = fE.sign(Base64.decode(xmlData));
            	firmaBase64 = new String(Base64.encode(byteFirma));
            }
            
            return firmaBase64;
        }
        catch (Exception e)
        {                       
            es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("Error inesperado en el procesamiento de la firma de CIRCE:" + e.getMessage());
            throw e;
        }
    }

    /**
     * Firma un XML de entrada, indicando el id de nodo a firmar o vacío para firmar
     * todo el documento y en qué nodo dejar el nodo de firma.
     * @param xmlData Xml a firmar
     * @param aliasCertificado Alias de certificado con el que se firmará.
     * @param nodoAFirmar Id de nodo a firmar o vacío si se quiere firmar todo el documento.
     * @param nodoPadre Tag de nodo donde se dejará la firma 
     * @param nsNodoPadre Namespace de nodo donde se dejará la firma, o vacío si no tiene namespace.
     * @return
     */
    
    @WebMethod (operationName="FirmarXML")
    @WebResult (name="xmlFirmado")
	public String FirmarXML(@WebParam (name="xmlData")String xmlData, 
					   @WebParam (name="aliasCertificado")String aliasCertificado, 
					   @WebParam (name="idNodoAFirmar")String idNodoAFirmar,
					   @WebParam (name="nodoPadre") String nodoPadre,
					   @WebParam (name="nsNodoPadre") String nsNodoPadre) throws Exception{
    	try
        {
        	Preferencias pr = Preferencias.getPreferencias();
            //cargamos los datos del almacen de un fichero xml preferencias
            //CargarPreferencias();
        	
            //chequeo parametros
            if(xmlData == null || xmlData.equals("")|| 
            		aliasCertificado == null || aliasCertificado.equals("")||
            		nodoPadre == null || nodoPadre.equals(""))
            {
                throw new Exception("Falta al menos uno de los campos  necesarios de entrada: xml que se firmará, alias de certificado y nombre de nodo padre en donde se ha de dejar la firma.");
            }
            String resultXML = DigitalSignatureManagerXml.Firma(
                xmlData,                    //xml
                idNodoAFirmar,             //parte del documento a firmar
                nodoPadre		,          //parte del documento donde se incluye la firma
                nsNodoPadre,
                pr.getAlmacen(),                  //nombre del almacen de claves por preferencia
                pr.getClaveAlmacen(),                 //passworde del almacen de claves por preferencia 
                aliasCertificado);		  // Alias del certificado que se usará para la firma.

            return resultXML;
        }
        catch (Exception e)
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("Error inesperado en el procesamiento de la firma del Xml:" + e.getMessage());
            throw e;
        }
	}
    
    //CRUBENCVS 47084
    /**
     * Firma un XML de entrada, indicando el id de nodo a firmar, en qué nodo dejar el nodo de firma
     * el algoritmo de firma y el método de digest
     * @param xmlData Xml a firmar
     * @param aliasCertificado Alias de certificado con el que se firmará.
     * @param nodoAFirmar Id de nodo a firmar o vacío si se quiere firmar todo el documento.
     * @param nodoPadre Tag de nodo donde se dejará la firma 
     * @param nsNodoPadre Namespace de nodo donde se dejará la firma, o vacío si no tiene namespace.
     * @param uriAlgoritmoFirma Uri del algoritmo de firma según "https://www.w3.org/TR/2013/REC-xmldsig-core1-20130411/". <p>No tienen por qué soportarse todos en la implementación actual</p>
     * @param uriAlgoritmoDigest Uri del método de Digest según "https://www.w3.org/TR/2013/REC-xmldsig-core1-20130411/". <p>No tienen por qué soportarse todos en la implementación actual</p>
     * @return
     */
    
    @WebMethod (operationName="FirmarXMLAlgoritmo")
    @WebResult (name="xmlFirmado")
	public String FirmarXMLAlgoritmo(@WebParam (name="xmlData")String xmlData, 
					   @WebParam (name="aliasCertificado")String aliasCertificado, 
					   @WebParam (name="idNodoAFirmar")String idNodoAFirmar,
					   @WebParam (name="nodoPadre") String nodoPadre,
					   @WebParam (name="nsNodoPadre") String nsNodoPadre,
					   //CRUBENCVS 47084 27/01/2023. Indicador del algoritmo de firma
					   @WebParam (name="uriAlgoritmoFirma") String uriAlgoritmoFirma,
					   @WebParam (name="uriAlgoritmoDigest") String uriAlgoritmoDigest
					   //FIN CRUBENCVS 47084 27/01/2023
	) throws Exception{
    	try
        {
        	Preferencias pr = Preferencias.getPreferencias();
            //cargamos los datos del almacen de un fichero xml preferencias
            //CargarPreferencias();
        	
            //chequeo parametros
            if(xmlData == null || xmlData.equals("")|| 
            		aliasCertificado == null || aliasCertificado.equals("")||
            		nodoPadre == null || nodoPadre.equals(""))
            {
                throw new Exception("Falta al menos uno de los campos  necesarios de entrada: xml que se firmará, alias de certificado y nombre de nodo padre en donde se ha de dejar la firma.");
            }
            String resultXML = DigitalSignatureManagerXml.FirmaAlgoritmo(
                xmlData,                    //xml
                idNodoAFirmar,             //parte del documento a firmar
                nodoPadre		,          //parte del documento donde se incluye la firma
                nsNodoPadre,
                uriAlgoritmoFirma,
                uriAlgoritmoDigest,
                pr.getAlmacen(),                  //nombre del almacen de claves por preferencia
                pr.getClaveAlmacen(),                 //passworde del almacen de claves por preferencia 
                aliasCertificado);		  // Alias del certificado que se usará para la firma.

            return resultXML;
        }
        catch (Exception e)
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("Error inesperado en el procesamiento de la firma del Xml:" + e.getMessage());
            throw e;
        }
	}
	
}
