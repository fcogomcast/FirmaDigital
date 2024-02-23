
package FirmaDigital.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "FirmarCIRCEResponse", namespace = "http://localhost:7001")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmarCIRCEResponse", namespace = "http://localhost:7001")
public class FirmarCIRCEResponse {

    @XmlElement(name = "contenidoFirmado", namespace = "")
    private String contenidoFirmado;

    /**
     * 
     * @return
     *     returns String
     */
    public String getContenidoFirmado() {
        return this.contenidoFirmado;
    }

    /**
     * 
     * @param contenidoFirmado
     *     the value for the contenidoFirmado property
     */
    public void setContenidoFirmado(String contenidoFirmado) {
        this.contenidoFirmado = contenidoFirmado;
    }

}
