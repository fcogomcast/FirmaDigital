package es.tributasenasturias.circe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;



public class FirmaCirce {
	
	public static final String codificacionISO = "ISO-8859-1";
	private PrivateKey privateKey = null;
	private String alias = null;
	private String passKeyStore = null;
	private KeyStore keyStore = null;
	private String rutaKeyStore = null;
	/**
	 * Inicializamos el constructor con los valores necesario para la Firma de CIRCE.
	 * @param rutakeystore Ruta del Almacen.
	 * @param passkeystore password del Almacen de claves.
	 * @throws Exception Error durante la busqueda.
	 */
	public FirmaCirce(String rutakeystore, String passkeystore, String aliasCertificado) throws Exception {
		
		rutaKeyStore = rutakeystore;
		passKeyStore = passkeystore;
		keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(new FileInputStream(new File(rutaKeyStore)), getPasswordAsCharArray());
		alias = aliasCertificado;
		
	}	
	/**
	 * Obtiene la password como Char array.
	 * @return Char array de la password.
	 */
	private char[] getPasswordAsCharArray() {
		char[] pwd = passKeyStore.toCharArray();
		return pwd;

	}
	/**
	 * Firmamos el objeto entrante y devolvemos el objeto firmado.
	 * @param data Byte array del objeto entrante a formar.
	 * @return retornamos el objeto firmado.
	 * @throws GeneralSecurityException Error durante el proceso de firmado de datos.
	 * @throws CMSException error general durante el proceso de firmado del contenido.
	 * @throws IOException Error durante el tratamiento  del objeto IO.
	 */
	public byte[] sign(byte[] data) throws GeneralSecurityException, CMSException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
		PrivateKey privateKey = getPrivateKey();
		X509Certificate certificate = getCertificate();
		generator.addSigner(privateKey, certificate,CMSSignedDataGenerator.DIGEST_SHA1);
		generator.addCertificatesAndCRLs(getCertStore());
		CMSProcessable content = new CMSProcessableByteArray(data);		
				
		CMSSignedData signedData = generator.generate(content, false, "BC");
		ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(signedData.getEncoded());
		return seq.getDEREncoded();
	}
	/**
	 * Obtenemos la clave privada.
	 * @return Retornamos el objeto de la clave privada.
	 * @throws GeneralSecurityException Excepcion general de la clave privada.
	 */
	private PrivateKey getPrivateKey() throws GeneralSecurityException {
		if (this.privateKey == null) {
			this.privateKey = initalizePrivateKey();
		}
		return this.privateKey;
	}
	/**
	 * Inicializamos el metodo de obtencion de la clave privada.
	 * @return retornamos el bojeto de la clave privada.
	 * @throws GeneralSecurityException Error general en el proceso de obtencion de la clave privada.
	 */
	private PrivateKey initalizePrivateKey() throws GeneralSecurityException {
		Key key = keyStore.getKey(alias, getPasswordAsCharArray());
		PrivateKey priKey = (PrivateKey) key;
		return priKey;
	}
	/**
	 * Obtenemos el certificado.
	 * @return Retorna el objeto del Certficado del Principado de Asturias para realizar la firma del documento.
	 * @throws KeyStoreException error en la obtencion del Objeto Certificado para los paramtros especificados.
	 */
	private X509Certificate getCertificate() throws KeyStoreException {
		X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
		return certificate;
	}
	/**
	 * Obtenemos el almacen de claves.
	 * @return Retornamos el alamcen de claves.
	 * @throws GeneralSecurityException Error durante la obtecion del almacen de claves.
	 */
	private CertStore getCertStore() throws GeneralSecurityException {
		ArrayList<Certificate> list = new ArrayList<Certificate>();
		Certificate[] certificates = keyStore.getCertificateChain(alias);
		for (int i = 0, length = certificates == null ? 0 : certificates.length; i < length; i++) {
			list.add(certificates[i]);
		}
		return CertStore.getInstance("Collection",new CollectionCertStoreParameters(list), "BC");
	}
}