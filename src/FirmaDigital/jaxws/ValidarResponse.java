
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ValidarResponse", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidarResponse", namespace = "http://localhost:7001")
public class ValidarResponse {

    @XmlElement(name = "esValido", namespace = "")
    private boolean esValido;

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isEsValido() {
        return this.esValido;
    }

    /**
     * 
     * @param esValido
     *     the value for the esValido property
     */
    public void setEsValido(boolean esValido) {
        this.esValido = esValido;
    }

}
