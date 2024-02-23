/**
 * 
 */
package es.tributasenasturias.utils.soap;


import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;



import es.tributasenasturias.Exceptions.SOAPProcessException;

/**
 * @author crubencvs
 * Clase de utilidad con ciertas funciones que se ejecutarán sobre los mensajes SOAP,
 * como convertir de texto a mensaje SOAP, crear cabecera, recuperar texto del mensaje, etc.
 */
public class SOAPProcessor {

	private final static String TAG_ID = "Id";
	/**
	 * Convierte un objeto javax.xml.soap.SOAPMessage a org.w3c.dom.Document
	 * @param msg
	 * @return objeto org.w3c.dom.Document
	 * @throws SOAPProcessException
	 */
	 public static Document converSOAPToDocument(SOAPMessage msg)  throws SOAPProcessException {
		 try
		 {
		     javax.xml.transform.Source src = msg.getSOAPPart().getContent();  
		     TransformerFactory tf = TransformerFactory.newInstance();  
		     Transformer transformer = tf.newTransformer();  
		     DOMResult result = new DOMResult();  
		     transformer.transform(src, result);  
		     return (Document)result.getNode();
		 }
		 catch (javax.xml.transform.TransformerConfigurationException ex)
		 {
			 throw new SOAPProcessException ("Error al convertir el mensaje a documento:" + ex.getMessage(),ex,msg);
		 }
		 catch (javax.xml.transform.TransformerException ex)
		 {
			 throw new SOAPProcessException ("Error al convertir el mensaje a documento:" + ex.getMessage(),ex,msg);
		 }
		 catch (javax.xml.soap.SOAPException ex)
		 {
			 throw new SOAPProcessException ("Error al convertir el mensaje a documento, no se puede recuperar el cuerpo de mensaje:" + ex.getMessage(),ex,msg);
		 }
	   }
	 /**
	  *  Añade un id al cuerpo del mensaje SOAP para poder firmarlo, si ya lo tiene no hace nada.
	  * @param msg
	  * @return
	  * @throws SOAPProcessException
	  */
	 public static SOAPMessage addBodyId(SOAPMessage msg) throws SOAPProcessException
		{
			if (msg==null)
				return msg;
			SOAPMessage msgChanged = null;
			try
			{
				msgChanged = msg; 
				javax.xml.soap.SOAPBody bdy = msgChanged.getSOAPBody();
				org.w3c.dom.Attr idAtt = bdy.getAttributeNodeNS(null,TAG_ID);
				if (idAtt==null) //Se ha de crear.
				{
					bdy.addAttribute(msg.getSOAPPart().getEnvelope().createName(TAG_ID),"Body");
				}
				return msgChanged;
			}
			catch (javax.xml.soap.SOAPException ex)
			{
				throw new SOAPProcessException ("Error al modificar el Id del cuerpo:" + ex.getMessage(),ex);
			}
		}
	 /**
	  * Recupera el id del cuerpo del mensaje SOAP.
	  * @param msg
	  * @return
	  * @throws SOAPProcessException
	  */
		public static String getBodyId (SOAPMessage msg)throws SOAPProcessException
		{
			String id=null;
			try
			{
				javax.xml.soap.SOAPBody bdy = msg.getSOAPBody();
				id = "#" + bdy.getAttributeNS(null,TAG_ID);
			}
			catch (javax.xml.soap.SOAPException ex)
			{
				throw new SOAPProcessException ("Error al recuperar el Id del cuerpo:" + ex.getMessage(),ex);
			}
			catch (Throwable e)
			{
				throw new SOAPProcessException ("Error al recuperar el Id del cuerpo:" + e.getMessage(),e);
			}
			return id;
		}
	/**
	 * Convierte el texto de entrada en un mensaje SOAP si es posible.
	 * @param msgText
	 * @return
	 * @throws javax.xml.soap.SOAPException
	 */
	public static SOAPMessage convertStringToMsg(String msgText) throws SOAPProcessException
	{
		if (msgText==null)
			return null;
		SOAPMessage msg=null;
		SOAPPart part=null;
		try
		{
			MessageFactory msgFactory     = MessageFactory.newInstance();   
			msg = msgFactory.createMessage();
			//Recupera el SOAP Part. Es la primera parte enviada, si hay attachments. Si no hay,
			// como en el caso de los mensajes que estamos firmando, es la única.
			//TODO: Comprobar qué pasa con mensajes SOAP con attachments.
			part = msg.getSOAPPart();
			java.io.ByteArrayInputStream bas  = new java.io.ByteArrayInputStream(msgText.getBytes());
			javax.xml.transform.stream.StreamSource strs = new javax.xml.transform.stream.StreamSource (bas);
			part.setContent(strs);
			return msg;
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			throw new SOAPProcessException ("Error al convertir la cadena a SOAPMessage:" + ex.getMessage(),ex,msgText);
		}
	}
	
	/**
	 * Añade una cabecera a un mensaje si no existe.
	 * @param soapIn
	 * @return
	 * @throws javax.xml.soap.SOAPException
	 */
	public static SOAPMessage addHeader (SOAPMessage soapIn) throws SOAPProcessException
	{
		SOAPMessage msg = soapIn;
		try
		{
			SOAPHeader header = msg.getSOAPHeader();
			if (header==null)
			{
			     msg.getSOAPPart().getEnvelope().addHeader();
			}
			return msg;
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			throw new SOAPProcessException ("Error al añadir la cabecera al mensaje SOAP:" + ex.getMessage(),ex);
		}
	}
}
