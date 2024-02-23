package FirmaDigital;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.tributasenasturias.utils.Utils;
import es.tributasenasturias.utils.Preferencias.Preferencias;




/*************************************************************************
 * Clase que firma y valida un documento XML con un certificado digital.
 *
 * Nueva funcionalidad (20/02/09):
 *      Ahora tambien es capaz de insertar un certificado con clave privada
 *      en el almacen (InsertarCertificado)
 *
 * Para firmar, carga un documento XML del contenido del parametro
 * recibido, especificando el elemento del XML a firmar (p.ej. "#Body")
 * y buscando el certificado en un almacen de claves recibido por parametros
 * (su contraseña tambien sera necesaria)
 *
 * Para validar, recibe un XML y devuelve true o false
 *
 * El resultado sera un string con el documento formado.
 *
 * @author Andres Ugarriza - andres.ugarriza@thalesgroup.com
 *
 */
public class DigitalSignatureManagerXml
{

    /**
     * @param xmlData - Contenido del XML
     * @param nodoAFirmar - Pide un elemento del XML para firmar solo este (en caso de vacio firmara todo el documento)
     * @param nodoPadre - Pide un elemento del XML donde ira ubicada la firma
     * @param nsNodoPadre - Namespace del elemento padre donde dejar el nodo de firma.
     * @param rutaFicheroAlmacen - El nombre del almacen puede ser null (coge por defecto)
     * @param passwordAlmacen -
     * @param identificadorCertificado - El nombre de certificado o el subject de este sera obligatorio como identificador del certificado a utilizar
     * @return Contenido de el XML generado - Devuelve vacio en caso de error
     * @throws Exception
     */
    public static String Firma(String xmlData, 
    						   String nodoAFirmar,
    						   String nodoPadre,
    						   String nsNodoPadre,
    						   String rutaFicheroAlmacen,
    						   String passwordAlmacen,
    						   String aliasCertificado
    						   ) throws Exception
    {
        try
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("INICIA PROCESO DE FIRMA DE XML");
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Almacen:     '"+rutaFicheroAlmacen + "'");
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Alias Certificado: '"+aliasCertificado + "'");
            
            Document doc = ObtenerXML(xmlData);
            if (doc == null)
            {
                throw new Exception("No se ha podido cargar el fichero: " + xmlData);
            }
            Preferencias pr = Preferencias.getPreferencias();
            KeyStore.PrivateKeyEntry clavePrivada = pr.ObtenerClavePrivada(aliasCertificado);
            //KeyStore.PrivateKeyEntry clavePrivada = ObtenerClavePrivada(rutaFicheroAlmacen, passwordAlmacen, identificadorCertificado, passwordCertificado);
            if (clavePrivada == null)
            {
                throw new Exception("No se ha podido cargar la clave privada del certificado de alias " + aliasCertificado);
            }
            X509Certificate certificado = (X509Certificate)clavePrivada.getCertificate();
            if (certificado == null)
            {
                throw new Exception("No se ha podido cargar el certificado de alias " + aliasCertificado);
            }

            String result = InternalSign(doc, 
            							nodoAFirmar, 
            							nodoPadre,
            							nsNodoPadre,
            							clavePrivada.getPrivateKey(), 
            							certificado
            							);
            
            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("FINALIZA PROCESO DE FIRMA CORRECTAMENTE");
            
            return result;
        }
        catch (Exception ex)
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("ERROR -> " + ex.getMessage());
            //ex.printStackTrace();

            throw ex;
        }
    }

        /* Cargar un XML en un objeto Document */
    protected static Document ObtenerXML(String xmlData) throws ParserConfigurationException, SAXException, IOException
    {
        Document toReturn = null;

        es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Cargando XML...");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        //cargamos el contenido
        //del parametro como un XML
        toReturn = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlData)));
        
        es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("XML cargado con exito");

        return toReturn;
    }

    /* Creamos el xml
     *
     * Devolvera el contenido del documento XML*/
    protected static String CrearXMLFirmado(Document doc) throws TransformerConfigurationException, TransformerException
    {
        String toReturn = new String();

        es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Creando XML resultante...");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();

        StringWriter stringWriter = new StringWriter();

        // transforma el objeto Document en un String
        trans.transform(new DOMSource(doc), new StreamResult(stringWriter));

        toReturn = stringWriter.getBuffer().toString();
        
        es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("XML firmado creado correctamente!");

        return toReturn;
    }

    /* Metodo interno que utilza las clases java necesarias para
     * firmar un documento XML con un certificado digital
     * Devuelve un string con la ruta del fichero generado
     * o el contenido de este. En caso de error, devuelve la excepcion */
    protected static String InternalSign(Document xml, 
    									 String idNodoAFirmar, 
    									 String nodoPadre, 
    									 String nsNodoPadre,
    									 Key clavePrivada, 
    									 X509Certificate certificado) throws Exception
    {
        try
        {           
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Iniciando firma...");
            /****************************************/
            /* Para crear la firma en el XML */
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            /****************************************/

            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Creando informacion de la firma...");
            /****************************************/
            /* Especificamos caracteristicas de la firma */
            DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
            //Transform t = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec)null);
            Transform t = fac.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec)null);
            

            ArrayList<Reference> refList = new ArrayList<Reference>();
            if (idNodoAFirmar==null || idNodoAFirmar.equals(""))
            {
            	//Se firma  todo el documento.
            	refList.add(fac.newReference("", dm, Collections.singletonList(t), null, null));
            }
            else
            {
            	//Comprobamos si existe el atributo.
            	if (!Utils.existsAttributeNode(xml,idNodoAFirmar))
            	{
            		throw new Exception ("No se ha podido firmar. No existe el nodo a firmar o se ha producido un error al buscarlo.");
            	}
            	//Hacemos que el atributo sea de tipo "ID". Desde Java 1.7u25, no basta con buscarlo, hay 
            	//que indicar que el atributo es de tipo "ID" o si no no puede utilizarlo para firma.
            	Utils.setNodoId (xml, idNodoAFirmar);
            	refList.add(fac.newReference("#"+idNodoAFirmar, dm, Collections.singletonList(t), null, null));
            }
            

            CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec)null);

            SignatureMethod sm = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);

            /*
             * Este objeto (SignedInfo) representa un xml:
             *
             * <element name="SignedInfo" type="ds:SignedInfoType"/> <complexType
             * name="SignedInfoType"> <sequence> <element
             * ref="ds:CanonicalizationMethod"/> <element ref="ds:SignatureMethod"/>
             * <element ref="ds:Reference" maxOccurs="unbounded"/> </sequence>
             * <attribute name="Id" type="ID" use="optional"/> </complexType>
             */
            SignedInfo infoFirma = fac.newSignedInfo(cm, sm, refList);
            /****************************************/

            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Obteniendo clave publica del certificado...");
            /****************************************/
            /* Obtenemos la clave publica del certificado y los datos del certificado */
            KeyInfoFactory kif = fac.getKeyInfoFactory();

            KeyValue kv = kif.newKeyValue(certificado.getPublicKey()); //calve publica
            X509Data x509d = kif.newX509Data(Collections.singletonList(certificado)); //datos del certificado

            //de este modo agregamos datos a la firma que ira en el xml resultante
            ArrayList keyInfoItems = new ArrayList();
            keyInfoItems.add(kv);
            keyInfoItems.add(x509d);

            KeyInfo infoClaveCertificado = kif.newKeyInfo(keyInfoItems);
            /****************************************/

            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Creando contexto entre el certificado y el xml...");
            /****************************************/
            /*
             * Crearemos una clave privada para firmar y el nodo del documento
             * donde ira la fimra para crear un contexto de firma
             */
            NodeList nPadres=null;
            if (nsNodoPadre==null||nsNodoPadre.equals(""))
            {
            	
            	nPadres = xml.getElementsByTagName(nodoPadre);
            }
            else
            {
            	nPadres = xml.getElementsByTagNameNS(nsNodoPadre, nodoPadre);
            }
            if (nPadres.getLength()==0)
            {
            	throw new Exception("No se ha podido firmar. No se ha encontrado el nodo padre donde dejar la firma.");
            }
            Node nPadre = nPadres.item(0);
            
            DOMSignContext contextoFirma = new DOMSignContext
            (  clavePrivada, // clave publica del certificado
               nPadre); // nodo del xml donde ira incluida la firma
            
            contextoFirma.putNamespacePrefix(XMLSignature.XMLNS, "ds");
            
            /****************************************/

            
            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Firmando...");
            /****************************************/
            /* Firmamos el xml */
            //XMLSignature firma = fac.newXMLSignature(infoFirma, infoClaveCertificado, null, idNodoAFirmar, null);
            XMLSignature firma = fac.newXMLSignature(infoFirma, infoClaveCertificado, null, null, null);
            firma.sign(contextoFirma);
            /****************************************/
            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Documento firmado con exito");

            return CrearXMLFirmado(xml);
        }
        catch (Exception ex)
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("ERROR firmando documento: " + ex.getMessage());
            //ex.printStackTrace();

            throw ex;
        }
    }
    
    /* Metodo interno que utilza las clases java necesarias para
     * firmar un documento XML con un certificado digital
     * Devuelve un string con la ruta del fichero generado
     * o el contenido de este. En caso de error, devuelve la excepcion */
    //CRUBENCVS 47084 27/01/2023. 
    //Realmente lo preparo para que ejecute cualquier algoritmo,
    //teniendo como defecto el SHA1.
    //Podría usarse incluso en la implementación antigua.
    protected static String InternalSignAlgorithm(Document xml, 
    									 	      String idNodoAFirmar, 
    									          String nodoPadre, 
    									          String nsNodoPadre,
    									          String uriAlgoritmoFirma,
    									          String uriAlgoritmoDigest,
      									          Key clavePrivada, 
    									          X509Certificate certificado
    									 ) throws Exception
    {
        try
        {           
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Iniciando firma con algoritmo de firma \"" + uriAlgoritmoFirma +"\" y digest \"" + uriAlgoritmoDigest + "...");
            /****************************************/
            /* Para crear la firma en el XML */
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            /****************************************/

            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Creando informacion de la firma...");
            /****************************************/
            /* Especificamos caracteristicas de la firma */
            
            
            DigestMethod dm = fac.newDigestMethod(uriAlgoritmoDigest, null);
            
            Transform t = fac.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec)null);
            

            ArrayList<Reference> refList = new ArrayList<Reference>();
            if (idNodoAFirmar==null || idNodoAFirmar.equals(""))
            {
            	//Se firma  todo el documento.
            	refList.add(fac.newReference("", dm, Collections.singletonList(t), null, null));
            }
            else
            {
            	//Comprobamos si existe el atributo.
            	if (!Utils.existsAttributeNode(xml,idNodoAFirmar))
            	{
            		throw new Exception ("No se ha podido firmar. No existe el nodo a firmar o se ha producido un error al buscarlo.");
            	}
            	//Hacemos que el atributo sea de tipo "ID". Desde Java 1.7u25, no basta con buscarlo, hay 
            	//que indicar que el atributo es de tipo "ID" o si no no puede utilizarlo para firma.
            	Utils.setNodoId (xml, idNodoAFirmar);
            	refList.add(fac.newReference("#"+idNodoAFirmar, dm, Collections.singletonList(t), null, null));
            }
            

            CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec)null);

            SignatureMethod sm = fac.newSignatureMethod(uriAlgoritmoFirma, null);

            /*
             * Este objeto (SignedInfo) representa un xml:
             *
             * <element name="SignedInfo" type="ds:SignedInfoType"/> <complexType
             * name="SignedInfoType"> <sequence> <element
             * ref="ds:CanonicalizationMethod"/> <element ref="ds:SignatureMethod"/>
             * <element ref="ds:Reference" maxOccurs="unbounded"/> </sequence>
             * <attribute name="Id" type="ID" use="optional"/> </complexType>
             */
            SignedInfo infoFirma = fac.newSignedInfo(cm, sm, refList);
            /****************************************/

            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Obteniendo clave publica del certificado...");
            /****************************************/
            /* Obtenemos la clave publica del certificado y los datos del certificado */
            KeyInfoFactory kif = fac.getKeyInfoFactory();

            KeyValue kv = kif.newKeyValue(certificado.getPublicKey()); //calve publica
            X509Data x509d = kif.newX509Data(Collections.singletonList(certificado)); //datos del certificado

            //de este modo agregamos datos a la firma que ira en el xml resultante
            ArrayList keyInfoItems = new ArrayList();
            keyInfoItems.add(kv);
            keyInfoItems.add(x509d);

            KeyInfo infoClaveCertificado = kif.newKeyInfo(keyInfoItems);
            /****************************************/

            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Creando contexto entre el certificado y el xml...");
            /****************************************/
            /*
             * Crearemos una clave privada para firmar y el nodo del documento
             * donde ira la fimra para crear un contexto de firma
             */
            NodeList nPadres=null;
            if (nsNodoPadre==null||nsNodoPadre.equals(""))
            {
            	
            	nPadres = xml.getElementsByTagName(nodoPadre);
            }
            else
            {
            	nPadres = xml.getElementsByTagNameNS(nsNodoPadre, nodoPadre);
            }
            if (nPadres.getLength()==0)
            {
            	throw new Exception("No se ha podido firmar. No se ha encontrado el nodo padre donde dejar la firma.");
            }
            Node nPadre = nPadres.item(0);
            
            DOMSignContext contextoFirma = new DOMSignContext
            (  clavePrivada, // clave publica del certificado
               nPadre); // nodo del xml donde ira incluida la firma
            
            contextoFirma.putNamespacePrefix(XMLSignature.XMLNS, "ds");
            
            /****************************************/

            
            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Firmando...");
            /****************************************/
            /* Firmamos el xml */
            //XMLSignature firma = fac.newXMLSignature(infoFirma, infoClaveCertificado, null, idNodoAFirmar, null);
            XMLSignature firma = fac.newXMLSignature(infoFirma, infoClaveCertificado, null, null, null);
            firma.sign(contextoFirma);
            /****************************************/
            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Documento firmado con exito");

            return CrearXMLFirmado(xml);
        }
        catch (Exception ex)
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("ERROR firmando documento: " + ex.getMessage());

            throw ex;
        }
    }
    
    
    /**
     * @param xmlData - Contenido del XML
     * @param algoritmoFirma - Algoritmo a utilizar en la firma
     * @param nodoAFirmar - Pide un elemento del XML para firmar solo este (en caso de vacio firmara todo el documento)
     * @param nodoPadre - Pide un elemento del XML donde ira ubicada la firma
     * @param nsNodoPadre - Namespace del elemento padre donde dejar el nodo de firma.
     * @param uriAlgoritmoFirma Uri del algoritmo de firma según "https://www.w3.org/TR/2013/REC-xmldsig-core1-20130411/". <p>No tienen por qué soportarse todos en la implementación actual</p>
     * @param uriAlgoritmoDigest uriAlgoritmoDigest Uri del método de Digest según "https://www.w3.org/TR/2013/REC-xmldsig-core1-20130411/". <p>No tienen por qué soportarse todos en la implementación actual</p>
     * @param rutaFicheroAlmacen - El nombre del almacen puede ser null (coge por defecto)
     * @param passwordAlmacen -
     * @param identificadorCertificado - El nombre de certificado o el subject de este sera obligatorio como identificador del certificado a utilizar
     * @return Contenido de el XML generado - Devuelve vacio en caso de error
     * @throws Exception
     */
    public static String FirmaAlgoritmo(String xmlData, 
    						   			String nodoAFirmar,
    						   			String nodoPadre,
    						   			String nsNodoPadre,
    									String uriAlgoritmoFirma,
    									String uriAlgoritmoDigest,
    						   			String rutaFicheroAlmacen,
    						   			String passwordAlmacen,
    						   			String aliasCertificado
    						   ) throws Exception
    {
        try
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("INICIA PROCESO DE FIRMA DE XML (Indicando algoritmo de Hash)");
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Almacen:     '"+rutaFicheroAlmacen + "'");
        	es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("Alias Certificado: '"+aliasCertificado + "'");
            
            Document doc = ObtenerXML(xmlData);
            if (doc == null)
            {
                throw new Exception("No se ha podido cargar el fichero: " + xmlData);
            }
            Preferencias pr = Preferencias.getPreferencias();
            KeyStore.PrivateKeyEntry clavePrivada = pr.ObtenerClavePrivada(aliasCertificado);
            //KeyStore.PrivateKeyEntry clavePrivada = ObtenerClavePrivada(rutaFicheroAlmacen, passwordAlmacen, identificadorCertificado, passwordCertificado);
            if (clavePrivada == null)
            {
                throw new Exception("No se ha podido cargar la clave privada del certificado de alias " + aliasCertificado);
            }
            X509Certificate certificado = (X509Certificate)clavePrivada.getCertificate();
            if (certificado == null)
            {
                throw new Exception("No se ha podido cargar el certificado de alias " + aliasCertificado);
            }

            String algoritmoFirma=uriAlgoritmoFirma;
            String algoritmoDigest =uriAlgoritmoDigest;
            if (algoritmoFirma==null || "".equals(algoritmoFirma)){
            	algoritmoFirma= pr.getDefaultSignatureAlgorithm();
            }
            if (algoritmoDigest==null || "".equals(algoritmoDigest)){
            	algoritmoDigest= pr.getDefaultDigestAlgorithm();
            }

            String result = InternalSignAlgorithm(doc, 
            									  nodoAFirmar, 
            									  nodoPadre,
            									  nsNodoPadre,
            									  algoritmoFirma,
            									  algoritmoDigest,
            									  clavePrivada.getPrivateKey(), 
            									  certificado
            							);
            
            es.tributasenasturias.utils.Log.LogErrorHelper.doInfoLog("FINALIZA PROCESO DE FIRMA CORRECTAMENTE");
            
            return result;
        }
        catch (Exception ex)
        {
        	es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("ERROR -> " + ex.getMessage());
            //ex.printStackTrace();

            throw ex;
        }
    }
}
