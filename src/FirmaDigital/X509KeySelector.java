package FirmaDigital;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.xml.crypto.*; 
import javax.xml.crypto.dsig.*;  
import javax.xml.crypto.dsig.keyinfo.*; 



public class X509KeySelector extends KeySelector {
    @SuppressWarnings("unchecked")
	public KeySelectorResult select(KeyInfo keyInfo,
                                    KeySelector.Purpose purpose,
                                    AlgorithmMethod method,
                                    XMLCryptoContext context)
        throws KeySelectorException {
        Iterator ki = keyInfo.getContent().iterator();
        while (ki.hasNext()) {
            XMLStructure info = (XMLStructure) ki.next();
            if (!(info instanceof X509Data))
                continue;
            X509Data x509Data = (X509Data) info;
            Iterator xi = x509Data.getContent().iterator();
            while (xi.hasNext()) {
                Object o = xi.next();
                if (!(o instanceof X509Certificate))
                    continue;
                final PublicKey key = ((X509Certificate)o).getPublicKey();
                // Make sure the algorithm is compatible
                // with the method.
            	// CRUBENCVS 30/01/2023 47084
                // No filtro los algoritmos soportados por el momento, ya que sólo
                // se estaban aceptando RSA SHA1 y DSA SHA1.
                // No tenemos restricción sobre algoritmos definida. 
                //if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                //    return new KeySelectorResult() {
                //        public Key getKey() { return key; }
                //    };
                //}
                return new KeySelectorResult() {
                            public Key getKey() { return key; }
                };
                // FIN CRUBENCVS 30/01/2023
            }
        }
        throw new KeySelectorException("No se ha encontrado la clave!");
    }

    // CRUBENCVS 30/01/2023 47084.
    // Obsoleto, se comprobarán los algoritmos de firma.
    @Deprecated
    static boolean algEquals(String algURI, String algName) {
        if ((algName.equalsIgnoreCase("DSA") &&
            algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) ||
            (algName.equalsIgnoreCase("RSA") &&
            algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
            return true;
        } else {
            return false;
        }
    }
    
}

