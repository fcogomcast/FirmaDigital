
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "FirmarSOAPResponse", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmarSOAPResponse", namespace = "http://localhost:7001", propOrder = {
    "xmlFirmado",
    "resultado"
})
public class FirmarSOAPResponse {

    @XmlElement(name = "xmlFirmado", namespace = "")
    private String xmlFirmado;
    @XmlElement(name = "resultado", namespace = "")
    private String resultado;

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

    /**
     * 
     * @return
     *     returns String
     */
    public String getResultado() {
        return this.resultado;
    }

    /**
     * 
     * @param resultado
     *     the value for the resultado property
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

}
