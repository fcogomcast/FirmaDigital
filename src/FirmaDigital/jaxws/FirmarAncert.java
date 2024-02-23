
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "FirmarAncert", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmarAncert", namespace = "http://localhost:7001", propOrder = {
    "xmlData",
    "identificadorCertificado",
    "passwordCertificado"
})
public class FirmarAncert {

    @XmlElement(name = "xmlData", namespace = "")
    private String xmlData;
    @XmlElement(name = "identificadorCertificado", namespace = "")
    private String identificadorCertificado;
    @XmlElement(name = "passwordCertificado", namespace = "")
    private String passwordCertificado;

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
    public String getIdentificadorCertificado() {
        return this.identificadorCertificado;
    }

    /**
     * 
     * @param identificadorCertificado
     *     the value for the identificadorCertificado property
     */
    public void setIdentificadorCertificado(String identificadorCertificado) {
        this.identificadorCertificado = identificadorCertificado;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getPasswordCertificado() {
        return this.passwordCertificado;
    }

    /**
     * 
     * @param passwordCertificado
     *     the value for the passwordCertificado property
     */
    public void setPasswordCertificado(String passwordCertificado) {
        this.passwordCertificado = passwordCertificado;
    }

}
