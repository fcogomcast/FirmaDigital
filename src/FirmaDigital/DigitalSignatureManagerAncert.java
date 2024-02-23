package FirmaDigital;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
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
public class DigitalSignatureManagerAncert
{
    //modificar esta constante para generar log
    private static final boolean LOG_ACTIVATE = true;
    //fichero donde generamos el log de la aplicacion
    private static final String LOG_FILE = "DigitalSignatureManagerLog.log";
    private static final String LOG_DIR = "proyectos/FirmaDigital";

    //Nombre del tag que agrupa la firma (para la validacion)
    private static final String NOMBRE_TAG_FIRMA = "Signature";
    private static final String ID_ELEM_SIGNATURE= "signature2";

    /**
     * @param xmlData - Contenido del XML
     * @param elementoXmlAFirmar - Pide un elemento del XML para firmar solo este (en caso de vacio firmara todo el documento)
     * @param elementoXmlPadreDeFirma - Pide un elemento del XML donde ira ubicada la firma
     * @param rutaFicheroAlmacen - El nombre del almacen puede ser null (coge por defecto)
     * @param passwordAlmacen -
     * @param identificadorCertificado - El nombre de certificado o el subject de este sera obligatorio como identificador del certificado a utilizar
     * @return Contenido de el XML generado - Devuelve vacio en caso de error
     * @throws Exception
     */
    public static String Firma(String xmlData, String elementoXmlAFirmar, String elementoXmlPadreDeFirma, String rutaFicheroAlmacen, String passwordAlmacen, String identificadorCertificado, String passwordCertificado) throws Exception
    {
        try
        {
            HacerLog("INICIA PROCESO DE FIRMA DE XML");
            HacerLog("Almacen:     '"+rutaFicheroAlmacen + "'");
            HacerLog("Certificado: '"+identificadorCertificado + "'");
            
            Document doc = ObtenerXML(xmlData);
            if (doc == null)
            {
                throw new Exception("No se ha podido cargar el fichero: " + xmlData);
            }

            KeyStore.PrivateKeyEntry clavePrivada = ObtenerClavePrivada(rutaFicheroAlmacen, passwordAlmacen, identificadorCertificado, passwordCertificado);
            if (clavePrivada == null)
            {
                throw new Exception("No se ha podido cargar la clave privada del certificado " + identificadorCertificado);
            }

            X509Certificate certificado = (X509Certificate)clavePrivada.getCertificate();
            if (certificado == null)
            {
                throw new Exception("No se ha podido cargar el certificado " + identificadorCertificado);
            }

            String result = InternalSign(doc, elementoXmlAFirmar, elementoXmlPadreDeFirma, clavePrivada.getPrivateKey(), certificado);
            
            HacerLog("   FINALIZA PROCESO DE FIRMA CORRECTAMENTE");
            
            return result;
        }
        catch (Exception ex)
        {
            HacerLog("ERROR -> " + ex.getMessage());
            HacerLog(ex.getStackTrace());
            
            ex.printStackTrace();

            throw ex;
        }
    }

    /* Chequeo de validacion de firma */
    //Solo necesitamos que por parametro se nos pase el contenido del XML
    public static boolean Valida(String xmlData) throws Exception
    {
        try
        {
            HacerLog("INICIA PROCESO DE VALIDACION DE XML");
            
            Document xml = ObtenerXML(xmlData);

            boolean bResult = InternalValidate(xml);
            
            HacerLog("Firma correcta? " + bResult);
            HacerLog("PROCESO DE VALIDACION DE FIRMA FINALIZADO CORRECTAMENTE");
            
            return bResult;
        }
        catch (Exception ex)
        {
            HacerLog("ERROR -> " + ex.getMessage());
            HacerLog(ex.getStackTrace());
            
            ex.printStackTrace();

            throw ex;
        }
    }

    /**
     * Dado un fichero .der con la informacion de una clave privada y
     * otro fichero .x509 con un certificado, se creara una entrada de clave
     * privada en el almacen.
     * 
     * @param rutaAlmacen - direccion fisica de un almacen de claves
     * @param passwordAlmacen - password del almacen
     * @param rutaClavePrivada - ruta del fichero que contiene la clave privada en formato PKCS8 (.der)
     * @param rutaCertificado - ruta del fichero que contiene la informacion del certificado en formato X509
     * @param nuevoAlias - alias que se le asignara al certificado dentro del almacen
     * @param nuevaPasswordCertificado - password del certificado dentro del almacen
     * @throws Exception
     */
    public static void InsertarCertificado(String rutaAlmacen, String passwordAlmacen, String rutaClavePrivada, String rutaCertificado, String nuevoAlias, String nuevaPasswordCertificado) throws Exception
    {
        try
        {
            HacerLog("INICIA PROCESO DE INSERCCION DE CERTIFICADO");
            HacerLog("Almacen:       '" + rutaAlmacen + "'");
            HacerLog("Clave privada: '" + rutaClavePrivada + "'");
            HacerLog("Certificado:   '" + rutaCertificado + "'");
            
            
            // Obtenemos el almacen
            KeyStore almacen = ObtenerAlmacen(rutaAlmacen, passwordAlmacen);

            // Carga del certificado
            HacerLog("Carga del certificado...");
            FileInputStream certificateStream = new FileInputStream(rutaCertificado);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            java.security.cert.Certificate[] chain = { };
            chain = certificateFactory.generateCertificates(certificateStream).toArray(chain);
            certificateStream.close();
            HacerLog("Certificado cargado");

            // Carga de la clave privada (PKCS#8 DER).
            HacerLog("Carga de la clave privada...");
            File keyFile = new File(rutaClavePrivada);
            byte[] encodedKey = new byte[(int)keyFile.length()];
            FileInputStream keyInputStream = new FileInputStream(keyFile);
            keyInputStream.read(encodedKey);
            keyInputStream.close();

            KeyFactory rSAKeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = rSAKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
            HacerLog("Clave privada cargada");

            // agregamos la entrada privada
            HacerLog("Agregando la entrada de tipo clave privada...");
            KeyStore.PrivateKeyEntry entry = new KeyStore.PrivateKeyEntry(privateKey, chain);
            KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(nuevaPasswordCertificado.toCharArray());
            almacen.setEntry(nuevoAlias, entry, protection);


            // Escribimos el almacen
            HacerLog("Guardando almacen...");
            FileOutputStream keyStoreOutputStream = new FileOutputStream(rutaAlmacen);
            almacen.store(keyStoreOutputStream, passwordAlmacen.toCharArray());
            keyStoreOutputStream.close();

            HacerLog("FINALIZA PROCESO DE INSERCCION DE CERTIFICADO");

        }
        catch (Exception ex)
        {
            HacerLog("ERROR -> " + ex.getMessage());
            HacerLog(ex.getStackTrace());

            ex.printStackTrace();

            throw ex;
        }
    }

    /* Obtiene el almacen especificado */
    protected static KeyStore ObtenerAlmacen(String rutaAlmacen, String passwordAlmacen) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException
    {
        HacerLog("Obteniendo almacen...");
        // obtiene un objeto almacen del tipo por defecto (JKS)
        KeyStore almacen = KeyStore.getInstance(KeyStore.getDefaultType());
        // cargamos el almacen
        almacen.load(new FileInputStream(rutaAlmacen), passwordAlmacen.toCharArray());

        return almacen;
    }

    /* Obtenemos la clave privada del certificado
         * en base al alias del certificado o al subject de este (valen los dos) */
    protected static KeyStore.PrivateKeyEntry ObtenerClavePrivada(String rutaAlmacen, String passwordAlmacen, String identificadorCertificado, String passwordCertificado) throws Exception
    {
        HacerLog("Obteniendo clave privada del certificado '"+identificadorCertificado+"'");

        KeyStore almacen = ObtenerAlmacen(rutaAlmacen, passwordAlmacen);
        if (almacen == null)
        {
            throw new Exception("No se encuentra almacen " + rutaAlmacen + " en el sistema");
        }

        //obtenemos la clave
        KeyStore.PasswordProtection passProtection = new KeyStore.PasswordProtection(passwordCertificado.toCharArray());
        KeyStore.PrivateKeyEntry thePrivKeyEntry = (KeyStore.PrivateKeyEntry)almacen.getEntry(identificadorCertificado, passProtection);

        //en caso de no obtener nada intentamos buscar por el subject
        if (thePrivKeyEntry == null)
        {
            HacerLog("No se ha podido obtener la clave por el alias, se intentara por el subject...");
            
            String elemento;
            X509Certificate certificado;
            String certificadoDelSubject = null;

            //obtenemos y recorremos los alias del almancen de claves
            //obteniendo cada certificado y de cada uno sacando su subject
            //para compararlo con lo pasado por parametro
            Enumeration<String> aliasEnAlmacen = almacen.aliases();
            while (aliasEnAlmacen.hasMoreElements())
            {
                elemento = aliasEnAlmacen.nextElement();
                certificado = (X509Certificate)almacen.getCertificate(elemento);

                if (certificado.getSubjectDN().getName().equals(identificadorCertificado))
                {
                    HacerLog("Subject encontrado en el almacen para el alias: " + elemento);
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

    /* Obtenemos el certificado del almacen */
   /* protected static X509Certificate ObtenerCertificado(String rutaAlmacen, String passwordAlmacen, String nombreCertificado) throws Exception
    {
        KeyStore almacen = ObtenerAlmacen(rutaAlmacen, passwordAlmacen);
        if(almacen == null)
        {
            throw new Exception("No se encuentra almacen "+rutaAlmacen+" en el sistema");
        }

        // obtiene el certificado por el alias
        X509Certificate certificado = (X509Certificate)almacen.getCertificate(nombreCertificado);
        
        //si no encontramos nada lo buscamos por el subjet (recorrer los certificados del almacen y comparar el subject)
        if (certificado == null)
        {
            String elemento;
            String certificadoDelSubject = null;

            //obtenemos y recorremos los alias del almancen de claves
            //obteniendo cada certificado y de cada uno sacando su subject
            //para compararlo con lo pasado por parametro
            Enumeration<String> aliasEnAlmacen = almacen.aliases();
            while (aliasEnAlmacen.hasMoreElements())
            {
                elemento = aliasEnAlmacen.nextElement();
                certificado = (X509Certificate)almacen.getCertificate(elemento);

                if (certificado.getSubjectDN().getName().equals(nombreCertificado))
                {
                    //en caso de que coincida salimos
                    certificadoDelSubject = elemento;
                    break;
                }
            }

            if (certificadoDelSubject != null)
            {
                //trustedCertificateEntry = (KeyStore.TrustedCertificateEntry)almacen.getEntry(certificadoDelSubject, null);
                certificado = (X509Certificate)almacen.getCertificate(certificadoDelSubject);
            }
        }

        if(DEBUG_MODE) System.out.println("Certificado cargado...");

        return certificado;
    }*/

    /* Cargar un XML en un objeto Document */
    protected static Document ObtenerXML(String xmlData) throws ParserConfigurationException, SAXException, IOException
    {
        Document toReturn = null;

        HacerLog("Cargando XML...");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();      
        dbf.setNamespaceAware(true);

        //cargamos el contenido
        //del parametro como un XML
        toReturn = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlData)));
        
        

              
        HacerLog("XML cargado con exito");

        return toReturn;
    }

    /* Creamos el xml
     *
     * Devolvera el contenido del documento XML*/
    protected static String CrearXMLFirmado(Document doc) throws TransformerConfigurationException, TransformerException
    {
        String toReturn = new String();              
        HacerLog("Creando XML resultante...");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();                       
        trans.setOutputProperty("encoding", "ISO-8859-1");
        
        StringWriter stringWriter = new StringWriter();
        // transforma el objeto Document en un String
        trans.transform(new DOMSource(doc), new StreamResult(stringWriter));
              
        toReturn = stringWriter.getBuffer().toString();        
        HacerLog("XML firmado creado correctamente!");
        return toReturn;
    }

    /* Metodo interno que utilza las clases java necesarias para
     * firmar un documento XML con un certificado digital
     * Devuelve un string con la ruta del fichero generado
     * o el contenido de este. En caso de error, devuelve la excepcion */
    protected static String InternalSign(Document xml, String elementoXmlAFirmar, String elementoXmlPadreDeFirma, Key clavePrivada, X509Certificate certificado) throws Exception
    {
        try
        {           
            HacerLog("Iniciando firma...");
            /****************************************/
            /* Para crear la firma en el XML */
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            /****************************************/

            HacerLog("Creando informacion de la firma...");
            /****************************************/
            /* Especificamos caracteristicas de la firma */
            DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
            //Transform t = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec)null);
            Transform t = fac.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec)null);
            

            ArrayList<Reference> refList = new ArrayList<Reference>();
            //Desde Java1.7u25, el "elementoXmlAFirmar" tiene que ser de tipo identificador, de otra 
            //manera no funcionará y dará un error.
            //El elemento "elementoXmlAFirmar" viene con "#" delante, se ha de eliminar
            String element=elementoXmlAFirmar.substring(1);
            Utils.setNodoId(xml, element);
            refList.add(fac.newReference(elementoXmlAFirmar, dm, Collections.singletonList(t), null, null));

            CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec)null);

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

            HacerLog("Obteniendo clave publica del certificado...");
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

            HacerLog("Creando contexto entre el certificado y el xml...");
            /****************************************/
            /*
             * Crearemos una clave privada para firmar y el nodo del documento
             * donde ira la fimra para crear un contexto de firma
             */
            
            Node nHeader = xml.getElementsByTagName(elementoXmlPadreDeFirma).item(0);
            
            DOMSignContext contextoFirma = new DOMSignContext
            (  clavePrivada, // clave publica del certificado
               nHeader); // nodo del xml donde ira incluida la firma
            
            contextoFirma.putNamespacePrefix(XMLSignature.XMLNS, "ds");
            
            /****************************************/

            
            HacerLog("Firmando...");
            /****************************************/
            /* Firmamos el xml */
            XMLSignature firma = fac.newXMLSignature(infoFirma, infoClaveCertificado, null,ID_ELEM_SIGNATURE, null);
            firma.sign(contextoFirma);
            /****************************************/
            HacerLog("Documento firmado con exito");

            return CrearXMLFirmado(xml);
        }
        catch (Exception ex)
        {
            System.out.println("ERROR firmando documento: " + ex.getMessage());
            ex.printStackTrace();

            throw ex;
        }
    }

    /* Metodo interno que utiliza las clases java necesarias
     * para obtener la firma de un documento y validar si es correcta
     * Si hay error devuelve la excepcion */
    /////////////////////////////////////////////////////////////////////
    //IMPORTANTE!!!
    //Para validar un XML parece que da problemas con los espacios en blanco
    /////////////////////////////////////////////////////////////////////
    protected static boolean InternalValidate(Document xml) throws Exception
    {
        try
        {
            HacerLog("Buscando firma en el documento (Elemento a buscar: "+NOMBRE_TAG_FIRMA+")");
            /***********************************************/
            // Buscamos la firma en el documento
            NodeList nl = xml.getElementsByTagNameNS(XMLSignature.XMLNS, NOMBRE_TAG_FIRMA);
            if (nl.getLength() == 0)
            {
                throw new Exception("No se encuentra " + NOMBRE_TAG_FIRMA + " en el documento");
            }
            /***********************************************/

            HacerLog("Creando objetos de validacion...");
            /***********************************************/
            // Creamos un DOM XMLSignatureFactory que sera usado para decodificar
            // el documento que contiene el XMLSignature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            /***********************************************/

            /***********************************************/
            // Creamos un contexto de firma
            DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(0));
            /***********************************************/

            /***********************************************/
            // obtener la firma
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);
            /***********************************************/

            HacerLog("Validando firma...");
            
            return signature.validate(valContext);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();

            throw ex;
        }
    }
    
    protected static void HacerLog(String mensaje)
    {
        if(LOG_ACTIVATE)
        {
            File file;
            FileWriter fichero = null;
            PrintWriter pw;

            try
            {
                Date today = new Date();
                String completeMsg = "DigitalSignatureManager :: " + today + " :: " + mensaje;
                
                file = new File(LOG_DIR);
                if(file.exists() == false)
                    file.mkdirs();
                
                fichero = new FileWriter(LOG_DIR + "/" + LOG_FILE, true);//true para que agregemos al final del fichero
                pw = new PrintWriter(fichero);
                
                pw.println(completeMsg);
                
            }
            catch (IOException e)
            {
                System.out.println("Error escribiendo log '"+mensaje+"' -> "+e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                if(fichero != null)
                {
                    try
                    {
                        fichero.close();
                    }
                    catch(Exception e)
                    {
                        System.out.println("Error cerrando fichero de log -> "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected static void HacerLog(StackTraceElement[] stackTraceElements)
    {
        if (stackTraceElements == null)
            return;

        for (int i = 0; i < stackTraceElements.length; i++)
        {
            HacerLog("StackTrace -> " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getClassName() + " :: " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getMethodName() + " :: " + stackTraceElements[i].getLineNumber());
        }
    }

    private static InputStream fullStream(String fname) throws IOException
    {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }
    
}
