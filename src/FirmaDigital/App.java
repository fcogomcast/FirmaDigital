package FirmaDigital;

import java.io.BufferedReader;
import java.io.FileReader;

/************************************************************
 * 
 * Aplicacion de ayuda para lanzar pruebas directamente contra
 * las clases java sin pasar por webservice
 * 
 * **********************************************************/
public class App
{
    public App()
    {
    }

    public static void main(String[] args) throws Exception
    {
        FirmaDigital f = new FirmaDigital();
        
        //f.InsertarCertificado("C:\\Archivos de programa\\GnuWin32\\bin\\prueba.x509", "C:\\Archivos de programa\\GnuWin32\\bin\\prueba.DER","testD",null);

       /* String result = f.Firmar("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soapenv:Envelope soapenv:actor=\"some-uri\" soapenv:mustUnderstand=\"1\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "  <soapenv:Header>" +
                "  </soapenv:Header>" +
                "   <soapenv:Body Id=\"Body\">" +
                "     <SERVICIOWEB servicio=\"certificadoTributario\" cliente=\"\">" +
                "	<PETICION>" +
                "	<CERTIFICADO>" +
                "	  <NIF>71887283R</NIF>" +
                "	  <NOMBRE>Andres Ugarriza</NOMBRE>" +
                "	  <REQUIRENTE>Pepe</REQUIRENTE>" +
                "	  <NIF_SOLICITANTE>71887283R</NIF_SOLICITANTE>" +
                "	  <NOMBRE_SOLICITANTE>Andres Ugarriza</NOMBRE_SOLICITANTE>" +
                "	  <CONSENTIMIENTO>SI</CONSENTIMIENTO>" +
                "	  <MOTIVO></MOTIVO>" +
                "	  <TIPO>A</TIPO>" +
                "	</CERTIFICADO>" +
                "	<PDF></PDF>" +
                "	</PETICION>" +
                "	<RESPUESTA>" +
                "	<RESULTADO></RESULTADO>" +
                "	<IDENTIFICACION></IDENTIFICACION>" +
                "	<FECHA_GENERACION></FECHA_GENERACION>" +
                "	<FECHA_VALIDEZ></FECHA_VALIDEZ>" +
                "	<CERTIFICADO_PDF></CERTIFICADO_PDF>" +
                "	</RESPUESTA>" +
                "    </SERVICIOWEB>" +
                "  </soapenv:Body>" +
                "</soapenv:Envelope>", "pruebafinal","exceso");

        System.out.println("Resultado de la firma:\n" + result);*/

        FileReader fr = new FileReader("C:\\Documents and Settings\\Andres\\Mis documentos\\PruebaFirma_20090616_1.TXT");
        BufferedReader b = new BufferedReader(fr);
        
        String fichero = new String();
        String linea = new String();
        
        while((linea = b.readLine()) != null)
        {
            fichero += linea+"\n";
        }
        
        b.close();
        
        System.out.println(fichero);
        
        
        //boolean bresult = f.Validar("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" soapenv:actor=\"some-uri\" soapenv:mustUnderstand=\"1\">  <soapenv:Header>  <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"#Body\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><ds:Reference URI=\"#Body\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ds:DigestValue>+c5eu+bpgeN6DQwa2wT9MbZcO3Y=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>J8d+SmzoXeXxhA04bWkS6Tfkha12lG2J2lgsV+zseL0SymDZ8D3uZgbtrhrtNn+3Kqo6M4qaQ2T7VjzbgWCdhmBJm4/a+QLL/5DCD3qxbAtjg1JyO4IdFBcG/o/ytA58rOn8292OLVmeMSopaHBj3SAFTPf4O2iSXSZqXOJBXyk=</ds:SignatureValue><ds:KeyInfo><ds:KeyValue><ds:RSAKeyValue><ds:Modulus>wBPbnDxXlKFqcYd2LD129ODN+Vt/yg0PXHmVzBkAm/wtvG168qPe/fFIkJUtkYWJiU2ijmmwJ74oFLDkAQjsbivOC2mKGc9JWMUL1J1kgq+kXCQC9t2tK/9TNHmOVDGHzHsvw2MfG0yQWp9Gh2rt6p/umw7kDOO39pua7E/Kimk=</ds:Modulus><ds:Exponent>AQAB</ds:Exponent></ds:RSAKeyValue></ds:KeyValue><ds:X509Data><ds:X509Certificate>MIIEwDCCBCmgAwIBAgIQIB5Psc2lMTkc5/1fQXMUFDANBgkqhkiG9w0BAQUFADCBujEfMB0GA1UEChMWVmVyaVNpZ24gVHJ1c3QgTmV0d29yazEXMBUGA1UECxMOVmVyaVNpZ24sIEluYy4xMzAxBgNVBAsTKlZlcmlTaWduIEludGVybmF0aW9uYWwgU2VydmVyIENBIC0gQ2xhc3MgMzFJMEcGA1UECxNAd3d3LnZlcmlzaWduLmNvbS9DUFMgSW5jb3JwLmJ5IFJlZi4gTElBQklMSVRZIExURC4oYyk5NyBWZXJpU2lnbjAeFw0wODAzMTIwMDAwMDBaFw0wOTA0MTEyMzU5NTlaMIHEMQswCQYDVQQGEwJFUzERMA8GA1UECBMIQXN0dXJpYXMxDzANBgNVBAcUBk92aWVkbzEsMCoGA1UEChQjR29iaWVybm8gZGVsIFByaW5jaXBhZG8gZGUgQXN0dXJpYXMxCjAIBgNVBAsUAS4xMzAxBgNVBAsUKlRlcm1zIG9mIHVzZSBhdCB3d3cudmVyaXNpZ24uY29tL3JwYSAoYykwNTEiMCAGA1UEAxQZd3d3LnRyaWJ1dGFzZW5hc3R1cmlhcy5lczCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAwBPbnDxXlKFqcYd2LD129ODN+Vt/yg0PXHmVzBkAm/wtvG168qPe/fFIkJUtkYWJiU2ijmmwJ74oFLDkAQjsbivOC2mKGc9JWMUL1J1kgq+kXCQC9t2tK/9TNHmOVDGHzHsvw2MfG0yQWp9Gh2rt6p/umw7kDOO39pua7E/KimkCAwEAAaOCAbkwggG1MAkGA1UdEwQCMAAwCwYDVR0PBAQDAgWgMEQGA1UdIAQ9MDswOQYLYIZIAYb4RQEHFwMwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cudmVyaXNpZ24uY29tL3JwYTA8BgNVHR8ENTAzMDGgL6AthitodHRwOi8vU1ZSSW50bC1jcmwudmVyaXNpZ24uY29tL1NWUkludGwuY3JsMDQGA1UdJQQtMCsGCCsGAQUFBwMBBggrBgEFBQcDAgYJYIZIAYb4QgQBBgorBgEEAYI3CgMDMHEGCCsGAQUFBwEBBGUwYzAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AudmVyaXNpZ24uY29tMDsGCCsGAQUFBzAChi9odHRwOi8vU1ZSSW50bC1haWEudmVyaXNpZ24uY29tL1NWUkludGwtYWlhLmNlcjBuBggrBgEFBQcBDARiMGChXqBcMFowWDBWFglpbWFnZS9naWYwITAfMAcGBSsOAwIaBBRLa7kolgYMu9BSOJsprEsHiyEFGDAmFiRodHRwOi8vbG9nby52ZXJpc2lnbi5jb20vdnNsb2dvMS5naWYwDQYJKoZIhvcNAQEFBQADgYEAu09p7XaHZcZn1G8zg4XzhMhE5J+Nel/ucoB3xnWX0yjnW41oO4rKA+XRlBjK3uQgspDhAMv4lMNyCRWdhArH46nNdi6IwjUmdMAOqke1y0/KqSKjh9GLTa5S/p33Bxrga4gAjBK25+DjP8Ks2rQ4wTzPIdXj4Z7hR7z9b9OOi2s=</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></soapenv:Header>   <soapenv:Body Id=\"Body\">     <SERVICIOWEB cliente=\"\" servicio=\"certificadoTributario\">    <PETICION>    <CERTIFICADO>      <NIF/>      <NOMBRE>Andres Ugarriza</NOMBRE>      <REQUIRENTE/>      <NIF_SOLICITANTE/>      <NOMBRE_SOLICITANTE/>      <CONSENTIMIENTO/>      <MOTIVO/>      <TIPO/>    </CERTIFICADO>    <PDF/>    </PETICION>    <RESPUESTA>    <RESULTADO/>    <IDENTIFICACION/>    <FECHA_GENERACION/>    <FECHA_VALIDEZ/>    <CERTIFICADO_PDF/>    </RESPUESTA>    </SERVICIOWEB>  </soapenv:Body></soapenv:Envelope>");
        boolean bresult = f.Validar(fichero);
        System.out.println("Es valido? " + bresult);
        //System.out.println(f.Firmar(fichero,"pruebafinal",null));

    }
}
