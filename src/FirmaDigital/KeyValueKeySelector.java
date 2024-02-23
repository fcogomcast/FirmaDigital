package FirmaDigital;

import java.security.KeyException;
import java.security.PublicKey;

import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

/**
 * KeySelector which retrieves the public key out of the
 * KeyValue element and returns it.
 * NOTE: If the key algorithm doesn't match signature algorithm,
 * then the public key will be ignored.
 */
public class KeyValueKeySelector extends KeySelector
{
    @SuppressWarnings("unchecked")
	public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException
    {
        if (keyInfo == null)
        {
            throw new KeySelectorException("Null KeyInfo object!");
        }

        SignatureMethod sm = (SignatureMethod)method;
        List list = keyInfo.getContent();

        for (int i = 0; i < list.size(); i++)
        {
            XMLStructure xmlStructure = (XMLStructure)list.get(i);
            if (xmlStructure instanceof KeyValue)
            {
                PublicKey pk = null;
                try
                {
                    pk = ((KeyValue)xmlStructure).getPublicKey();
                }
                catch (KeyException ke)
                {
                    throw new KeySelectorException(ke);
                }
                
                // CRUBENCVS 30/01/2023 47084
                // No filtro los algoritmos soportados por el momento, ya que sólo
                // se estaban aceptando RSA SHA1 y DSA SHA1.
                // No tenemos restricción sobre algoritmos definida. 
                //if (algEquals(sm.getAlgorithm(), pk.getAlgorithm()))
                //{
                //	return new SimpleKeySelectorResult(pk);
                //}
                return new SimpleKeySelectorResult(pk);
                
            }
        }
        throw new KeySelectorException("No KeyValue element found!");
    }

    static boolean algEquals(String algURI, String algName)
    {
        boolean result = false;
        
        if (result == false)
        {
            result = algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1);
        }
        if (result == false)
        {
            result = algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1);
        }
        return result;
    }
    

}
