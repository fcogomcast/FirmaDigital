package es.tributasenasturias.firmas;


import org.w3c.dom.Document;


import es.tributasenasturias.Exceptions.PreferenciasException;
import es.tributasenasturias.Exceptions.XMLDOMDocumentException;
import es.tributasenasturias.utils.Log.LogErrorHelper;
import es.tributasenasturias.utils.Preferencias.Preferencias;
import es.tributasenasturias.utils.xml.XMLDOMUtils;

public class XMLSignatureValidator implements ISignValidator<String> {

	@Override
	public boolean isValid(String msg) {
		boolean valido=false;
		try
		{
			//Parsear el mensaje de entrada y convertirlo a org.w3c.dom.Document
			Document doc = XMLDOMUtils.parseXml(msg);
			//Se recupera el tag de firma.
			Preferencias pr = Preferencias.getPreferencias();
			String signatureTag = pr.getTagFirma();
			//Ahora se valida el documento como un xml firmado.
				valido = XMLDOMUtils.validarFirmaXML(doc, signatureTag);
		}
		catch (XMLDOMDocumentException ex)
		{
			LogErrorHelper.doErrorLog("Error en tratamiento/validación de xml: " + ex.getError() +"-"+ex.getMessage());
			valido=false;
		}
		catch (PreferenciasException ex)
		{
			LogErrorHelper.doErrorLog("Error en la carga de preferencias:" + ex.getError() + "-" + ex.getMessage());
		}
		return valido;
	}



}
