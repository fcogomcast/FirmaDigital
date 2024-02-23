package es.tributasenasturias.utils;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class Utils {
	/**
	 * Devuelve la palabra indicada según el siguiente formato:
	 * 	-Primera letra en mayúsculas
	 *  -Siguientes letras en minúsculas.
	 * @param palabra
	 * @return
	 */
	public static String initcap (String palabra)
	{
		char[] cad = palabra.toLowerCase().toCharArray();
		cad[0] = Character.toUpperCase(cad[0]);
		return new String (cad);
	}
	/**
	 * Recupera una lista de nodos que contienen un cierto valor de atributo.
	 * Sólo nos interesa el primero realmente
	 * @param doc Documento xml (org.w3c.Document)
	 * @param attrValue Valor del atributo a buscar
	 * @return Lista de nodos que coinciden (puede no contener elementos) o null en caso de error
	 */
	public static NodeList getNodesByAttributeValue(Document doc, String attrValue)
	{
		XPath xph = XPathFactory.newInstance().newXPath();
		try {
			XPathExpression xe = xph.compile("//*[@*='"+attrValue+"']");
			NodeList nodosCoinciden= (NodeList)xe.evaluate(doc,XPathConstants.NODESET);
			return nodosCoinciden;
			
		} catch (XPathExpressionException e) {
			es.tributasenasturias.utils.Log.LogErrorHelper.doErrorLog("Error al comprobar si existe el atributo que se busca para firmar:" + e.getMessage());
			return null;
		}
	}
	/** 
	 * Indica si existe un node de atributo en función de un valor
	 * @param doc Documento xml
	 * @param attrValue  Valor de atributo 
	 * @return
	 */
	public static boolean existsAttributeNode(Document doc, String attrValue)
	{
		boolean exist=false;
		NodeList nodosCoinciden= getNodesByAttributeValue(doc,attrValue);
		if (nodosCoinciden!=null && nodosCoinciden.getLength()==0)
		{
			exist=false;
		}
		else
		{
			exist=true;
		}
		return exist;
	}
	
	public static void setNodoId(Document doc,String attrValue) throws Exception
	{
		NodeList nodosCoinciden= getNodesByAttributeValue(doc,attrValue);
		if (nodosCoinciden.getLength()==0)
		{
			//No debería, si llegó aquí es que anteriormente se encontró el nodo a firmar. Sería un error
			//grave.
			throw new Exception("No se ha encontrado el atributo con valor " + attrValue + " para indicar que es un atributo ID");
		}
		else
		{
			//Importante!!!!. Además de encontrarlo, a partir de Java 1.7u25, es necesario
			//registrarlo como ID (que se llame ID ya no es suficiente).
			//Creo que no es necesario comprobar que tiene atributos, porque el XPath
			//ya lo ha comprobado
			String attr="";
			//Buscamos el nombre del atributo que contiene el valor buscado
			for (int i=0;i<nodosCoinciden.getLength();i++)
			{
				//Buscamos sus hijos de tipo ATTRIBUTE
				NamedNodeMap h=nodosCoinciden.item(0).getAttributes();
				for (int j=0;j<h.getLength();j++)
				{
					if (attrValue.equals(h.item(j).getNodeValue()))
					{
						attr= h.item(j).getLocalName();
						break;
					}
				}
				if (!"".equals(attr))
				{
					break;
				}
			}
			Element el = (Element)nodosCoinciden.item(0);
			el.setIdAttribute(attr, true);
			}
	}
}
