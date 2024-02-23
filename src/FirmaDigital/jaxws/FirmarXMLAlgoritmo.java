
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "FirmarXMLAlgoritmo", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmarXMLAlgoritmo", namespace = "http://localhost:7001", propOrder = {
    "xmlData",
    "aliasCertificado",
    "idNodoAFirmar",
    "nodoPadre",
    "nsNodoPadre",
    "uriAlgoritmoFirma",
    "uriAlgoritmoDigest"
})
public class FirmarXMLAlgoritmo {

    @XmlElement(name = "xmlData", namespace = "")
    private String xmlData;
    @XmlElement(name = "aliasCertificado", namespace = "")
    private String aliasCertificado;
    @XmlElement(name = "idNodoAFirmar", namespace = "")
    private String idNodoAFirmar;
    @XmlElement(name = "nodoPadre", namespace = "")
    private String nodoPadre;
    @XmlElement(name = "nsNodoPadre", namespace = "")
    private String nsNodoPadre;
    @XmlElement(name = "uriAlgoritmoFirma", namespace = "")
    private String uriAlgoritmoFirma;
    @XmlElement(name = "uriAlgoritmoDigest", namespace = "")
    private String uriAlgoritmoDigest;

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

    /**
     * 
     * @return
     *     returns String
     */
    public String getIdNodoAFirmar() {
        return this.idNodoAFirmar;
    }

    /**
     * 
     * @param idNodoAFirmar
     *     the value for the idNodoAFirmar property
     */
    public void setIdNodoAFirmar(String idNodoAFirmar) {
        this.idNodoAFirmar = idNodoAFirmar;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getNodoPadre() {
        return this.nodoPadre;
    }

    /**
     * 
     * @param nodoPadre
     *     the value for the nodoPadre property
     */
    public void setNodoPadre(String nodoPadre) {
        this.nodoPadre = nodoPadre;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getNsNodoPadre() {
        return this.nsNodoPadre;
    }

    /**
     * 
     * @param nsNodoPadre
     *     the value for the nsNodoPadre property
     */
    public void setNsNodoPadre(String nsNodoPadre) {
        this.nsNodoPadre = nsNodoPadre;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getUriAlgoritmoFirma() {
        return this.uriAlgoritmoFirma;
    }

    /**
     * 
     * @param uriAlgoritmoFirma
     *     the value for the uriAlgoritmoFirma property
     */
    public void setUriAlgoritmoFirma(String uriAlgoritmoFirma) {
        this.uriAlgoritmoFirma = uriAlgoritmoFirma;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getUriAlgoritmoDigest() {
        return this.uriAlgoritmoDigest;
    }

    /**
     * 
     * @param uriAlgoritmoDigest
     *     the value for the uriAlgoritmoDigest property
     */
    public void setUriAlgoritmoDigest(String uriAlgoritmoDigest) {
        this.uriAlgoritmoDigest = uriAlgoritmoDigest;
    }

}
