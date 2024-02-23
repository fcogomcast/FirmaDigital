/**
 * 
 */
package es.tributasenasturias.firmas;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;

import es.tributasenasturias.Exceptions.PreferenciasException;
import es.tributasenasturias.Exceptions.SOAPProcessException;
import es.tributasenasturias.Exceptions.SignatureException;
import es.tributasenasturias.Exceptions.XMLDOMDocumentException;
import es.tributasenasturias.utils.Utils;
import es.tributasenasturias.utils.Preferencias.Preferencias;
import es.tributasenasturias.utils.soap.SOAPProcessor;
import es.tributasenasturias.utils.xml.XMLDOMUtils;
/**
 * Firmador de XML para SOAP mediante un algorimo SHA1.
 * @author crubencvs
 * 
 */
public class SHA1SOAPSigner implements ISigner<SOAPMessage,String> {


	/**
	 * Formatea el mensaje y lo prepara para la firma.
	 * Añade una cabecera SOAP si no existe e incrusta un Id al cuerpo del mensaje SOAP.
	 * @param msg
	 * @return
	 * @throws SOAPProcessException
	 */
	private SOAPMessage formateaMensaje(SOAPMessage msg) throws SOAPProcessException
	{
		if (msg==null)
			return msg;
		SOAPMessage msgChanged=null;
		msgChanged = msg;
		//Añadimos la cabecera al mensaje, es necesaria pues será la parte en la que se incluirá la firma.
		msgChanged  = SOAPProcessor.addHeader(msgChanged);
		//Añadimos el id del cuerpo
		msgChanged = SOAPProcessor.addBodyId(msgChanged);
		return msgChanged;
	}
	
	private String doSign (SOAPMessage msg, String aliasCertificado) throws SOAPProcessException, PreferenciasException,
						NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException,
						MarshalException, XMLSignatureException, XMLDOMDocumentException,
						InvalidCanonicalizerException,CanonicalizationException
	{
			String xmlFirmado=null;
			//lo convertimos a org.w3c.dom.Document
			Document doc = SOAPProcessor.converSOAPToDocument(msg);
			//Canonicalizamos.
			//Canonicalizer can;
			//can = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
			//can.canonicalizeSubtree(doc);
			//Recuperamos el nodo en que se va a poner la información de seguridad. Será el de cabecera.
			NodeList nodos = doc.getElementsByTagNameNS("*", "Header");
			Node nHeader=null;
			if (nodos.getLength()> 0 )
			{
				nHeader = nodos.item(0);
			}
			else
			{
				throw new SOAPProcessException ("No se puede encontrar la cabecera del mensaje SOAP.");
			}
			//Recuperamos  los datos de almacenes y certificados que vamos a necesitar.
			Preferencias pr =Preferencias.getPreferencias();
			//Almacén
			//Clave privada
			KeyStore.PrivateKeyEntry clavePrivada = pr.ObtenerClavePrivada(aliasCertificado);
			//Certificado
			X509Certificate certificado = (X509Certificate)clavePrivada.getCertificate();
			//****************************************************************
			//*  Sección de Firma
			//*
			//****************************************************************
			//Factory para Firmas mediante procesado DOM.
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			//Elemento digest definido en xmldsig
			DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
			//Elemento Transform.
			//En este caso se trata de una transformación que se tendrá que aplicar al
			//documento. Como es de tipo "ENVELOPED", significará que a la hora
			//de aplicar el "Digest" se eliminará la información de firma para que el
			// "Digest" no tenga en cuenta la firma, sino sólo el xml como si no estuviera firmado.Esto se
			// hace porque el tipo "ENVELOPED" indica que la firma y el elemento firmado están dentro
			// del mismo documento.
			// Como en este caso firmamos el cuerpo y la única referencia que hay es a él (y por 
			// tanto el Digest es del cuerpo) es posible que no necesitásemos indicar el Transform.
			//Pero está bien tenerlo por si se cambia el formato de mensaje con firma.
			Transform t = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec)null);
			java.util.ArrayList<Reference> refList = new java.util.ArrayList<Reference>();
			//Recuperamos el Id del cuerpo. Este Id será el que indique la parte a firmar.
			String id = SOAPProcessor.getBodyId(msg);
			if (id==null) //Tiene que especificar un elemento, si no firmaría, pero no lo que esperamos.
			{
				throw new SOAPProcessException("No se ha encontrado el Id de elemento a firmar.");
			}
			//Referencia al elemento a firmar, se indicará por el Id del cuerpo.
			//La referencia será interna al documento, es decir "#<nombre elemento>".
			//Si no se pone nada (id="") la referencia sería al documento completo, pero en este caso 
			//sólo queremos firmar el cuerpo.
			//Desde Java 1.7u25, es necesario indicar que el atributo es un ID.
			//Primero hemos de convertir a Document, porque el tipo javax.xml.soap.SOAPBody
			//no parece tener implementada la operación setIdAttribute en la versión
			//de Weblogic 10gR3
			//Debemos encontrar el nodo "Body" y dentro de él, indicar que el atributo con
			//nombre "TAG_ID" es el que actúa como ID.
			try {
				String element=id.substring(1);
				Utils.setNodoId(doc,element);
			} catch (Exception e) {
				//Lo ignoramos, fallará posteriormente ya que no habrá atributo "ID"
			}
            refList.add(fac.newReference(id, dm, java.util.Collections.singletonList(t), null, null));
            //Método de canonicalización, que no es otra cosa que la conversión a un formato canónico de XML
            //para que diferentes parsers puedan entender la firma, porque aunque el XML sea lógicamente igual,
            //es decir, misma información contenida, el formato puede ser diferente y esas diferencias pueden
            //afectar a la validación posterior.
            CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec)null);
            //Método de firma (RSA_SHA1). Se podría parametrizar posteriormente.
            //TODO: implementar una estrategia para recuperarlo
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
            /* Obtenemos la clave publica del certificado y los datos del certificado */
            KeyInfoFactory kif = fac.getKeyInfoFactory();

            KeyValue kv = kif.newKeyValue(certificado.getPublicKey()); //clave publica
            X509Data x509d = kif.newX509Data(java.util.Collections.singletonList(certificado)); //datos del certificado

            //de este modo agregamos datos a la firma que ira en el xml resultante
            java.util.ArrayList<XMLStructure> keyInfoItems = new java.util.ArrayList<XMLStructure>();
            keyInfoItems.add(kv);
            keyInfoItems.add(x509d);
            KeyInfo infoClaveCertificado = kif.newKeyInfo(keyInfoItems);
            DOMSignContext contextoFirma = new DOMSignContext
            (  clavePrivada.getPrivateKey(), // clave publica del certificado
               nHeader); // nodo del xml donde ira incluida la firma
            //Prefijo de espacio de nombres de la sección firmada.
            contextoFirma.putNamespacePrefix(XMLSignature.XMLNS, "ds");
            XMLSignature firma = fac.newXMLSignature(infoFirma, infoClaveCertificado, null, id, null);
            firma.sign(contextoFirma);
            //En este momento se habrá modificado el Document doc para contener la firma.
            //Sólo hemos de devolverlo.
            xmlFirmado = XMLDOMUtils.getXMLText(doc);
            return xmlFirmado;
	}
	/**
	 * Operación principal de firma de mensaje SOAP.
	 */
	@Override
	public String firmar(SOAPMessage msg, String aliasCertificado) throws SignatureException{
		
		//Formatear mensaje. 
		try
		{
			msg = formateaMensaje (msg);
			//Mensaje ya formateado, lo pasamos al 
			//algoritmo de firma.
			return doSign (msg, aliasCertificado);
		}
		catch (SOAPProcessException ex)
		{
			//Mantenemos la excepción original (getCause), de otro modo estaríamos enmascarando el error.
			throw new SignatureException ("Error al firmar: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (PreferenciasException ex)
		{
			throw new SignatureException ("Error al firmar, problema con las preferencias: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new SignatureException ("Error al firmar, problema con el algoritmo estándar de firma seleccionado: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (InvalidAlgorithmParameterException ex)
		{
			throw new SignatureException ("Error al firmar, problema con el algoritmo de firma seleccionado: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (KeyException ex)
		{
			throw new SignatureException ("Error al firmar, problema con una de las claves: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (MarshalException ex)
		{
			throw new SignatureException ("Error al firmar, problema con modificación de XML por parte del algoritmo de firma: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (XMLSignatureException ex)
		{
			throw new SignatureException ("Error al firmar, problema con la el proceso de firma del xml: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (XMLDOMDocumentException ex)
		{
			throw new SignatureException ("Error al firmar, problema al convertir el xml firmado a texto en salida: "+ex.getMessage(),ex.getCause(),msg);
		}
		catch (InvalidCanonicalizerException ex)
		{
			throw new SignatureException ("Imposible validar la firma del xml:" + ex.getMessage(),ex.getCause(),msg);
		}
		catch (CanonicalizationException ex)
		{
			throw new SignatureException ("Imposible validar la firma del xml:" + ex.getMessage(),ex.getCause(),msg);
		}
	}

	

}
