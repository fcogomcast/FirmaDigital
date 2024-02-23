
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "FirmarXMLAlgoritmoResponse", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmarXMLAlgoritmoResponse", namespace = "http://localhost:7001")
public class FirmarXMLAlgoritmoResponse {

    @XmlElement(name = "xmlFirmado", namespace = "")
    private String xmlFirmado;

    /**
     * 
     * @return
     *     returns String
     */
    public String getXmlFirmado() {
        return this.xmlFirmado;
    }

    /**
     * 
     * @param xmlFirmado
     *     the value for the xmlFirmado property
     */
    public void setXmlFirmado(String xmlFirmado) {
        this.xmlFirmado = xmlFirmado;
    }

}
