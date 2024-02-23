
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "FirmarSOAP", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmarSOAP", namespace = "http://localhost:7001", propOrder = {
    "xmlData",
    "aliasCertificado"
})
public class FirmarSOAP {

    @XmlElement(name = "xmlData", namespace = "")
    private String xmlData;
    @XmlElement(name = "aliasCertificado", namespace = "")
    private String aliasCertificado;

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

    /**
     * 
     * @return
     *     returns String
     */
    public String getAliasCertificado() {
        return this.aliasCertificado;
    }

    /**
     * 
     * @param aliasCertificado
     *     the value for the aliasCertificado property
     */
    public void setAliasCertificado(String aliasCertificado) {
        this.aliasCertificado = aliasCertificado;
    }

}
