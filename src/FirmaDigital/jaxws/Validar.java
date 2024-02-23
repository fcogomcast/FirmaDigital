
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Validar", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Validar", namespace = "http://localhost:7001")
public class Validar {

    @XmlElement(name = "xmlData", namespace = "")
    private String xmlData;

    /**
     * 
     * @return
     *     returns String
     */
    public String getXmlData() {
        return this.xmlData;
    }

    /**
     * 
     * @param xmlData
     *     the value for the xmlData property
     */
    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

}
