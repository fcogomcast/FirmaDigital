
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "InsertarCertificado", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertarCertificado", namespace = "http://localhost:7001", propOrder = {
    "rutaCertificado",
    "rutaClavePrivada",
    "aliasCertificado",
    "passwordCertificado"
})
public class InsertarCertificado {

    @XmlElement(name = "rutaCertificado", namespace = "")
    private String rutaCertificado;
    @XmlElement(name = "rutaClavePrivada", namespace = "")
    private String rutaClavePrivada;
    @XmlElement(name = "aliasCertificado", namespace = "")
    private String aliasCertificado;
    @XmlElement(name = "passwordCertificado", namespace = "")
    private String passwordCertificado;

    /**
     * 
     * @return
     *     returns String
     */
    public String getRutaCertificado() {
        return this.rutaCertificado;
    }

    /**
     * 
     * @param rutaCertificado
     *     the value for the rutaCertificado property
     */
    public void setRutaCertificado(String rutaCertificado) {
        this.rutaCertificado = rutaCertificado;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getRutaClavePrivada() {
        return this.rutaClavePrivada;
    }

    /**
     * 
     * @param rutaClavePrivada
     *     the value for the rutaClavePrivada property
     */
    public void setRutaClavePrivada(String rutaClavePrivada) {
        this.rutaClavePrivada = rutaClavePrivada;
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
