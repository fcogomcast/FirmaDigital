/**
 * 
 */
package es.tributasenasturias.utils.xml;

import java.security.Key;
import java.security.PublicKey;

import javax.xml.crypto.KeySelectorResult;

/**
 * Clase copiada de la firma de Andr�s.
 * Implementa una interfaz de selecci�n de claves. Se utiliza en la validaci�n de firma.
 * @author crubencvs
 *
 */
public class SimpleKeySelectorResult implements KeySelectorResult {
    private PublicKey pk;

    SimpleKeySelectorResult(PublicKey pk)
    {
        this.pk = pk;
    }

    public Key getKey()
    {
        return pk;
    }
}
