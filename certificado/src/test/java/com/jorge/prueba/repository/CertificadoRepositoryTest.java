package com.jorge.prueba.repository;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import com.jorge.prueba.mapping.Certificado;
import com.jorge.prueba.repositorio.CertificadoRepository;
import com.jorge.prueba.utils.CertificadoUtils;
/*
 * Ejemplo de como cargar un certificado en bbdd, hacer una consulta por el serialNumber, y validar que el certificado sea valido. Para ello regomeos la autoridad certificadora del certificado
 * vamos a buscarlo con la url que venga en la extension, y además cogemos la url del ocsp (online certificate status protocol) para comprobar que el certificado no este revocado
 * Haria falta para la prueba cargar un certificado valido en la ruta de src/test/resources
 * Ejemplo de llamada mvn -DnombreCertificado=certificadoDni.crt
 */
@DataJpaTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CertificadoRepositoryTest {

    @Autowired
    private CertificadoRepository certRepository;

    @Value("${nombreCertificado}")
    private String nombreCertificado;
    @Test
    public void testInsertFindValidateOCSPCertificado() throws Exception {
        Certificado foundCert;

//    	Mockito.when(bus.getExtension( /* tu lógica aquí */));
        File certFile = new File("src/test/resources/"+nombreCertificado);
        byte[] certBytes = Files.readAllBytes(certFile.toPath());
        // Crear y guardar un nuevo usuario
    	
        Certificado certificado = new Certificado();
        certificado.setNombre(nombreCertificado);
        certificado.setContenido(certBytes);
        String serialNumber="";
       	try {
    			serialNumber = CertificadoUtils.obtenerSerialNumber(certFile.getAbsolutePath());
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
       	certificado.setSerialNumber(serialNumber);
       	certificado.setFechaCarga(LocalDateTime.now());
        certRepository.save(certificado);

        foundCert = certRepository.findBySerialNumber(serialNumber)
        .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));
        
        //llamamos al metodo que se encargara de validar que el certificado no este en la lista de revocados del CA que emitio el certificado
        boolean validado = validarOCSP(foundCert);

        // Verificar que el certificado encontrado no sea nulo y tenga el serialNumber correcto
        assertTrue(validado);
    }
    
    
    /*
     * Metodo principal para validar el certificado cargado desde bbdd contra el servidor OCSP
     */
    private boolean validarOCSP(Certificado foundCert) throws Exception {
    	X509Certificate certificate =null; 
    	 byte[] certificateBlob = foundCert.getContenido();
    	 //Recogemos el campo que tiene el certificado guardado en bytes y lo transformamos en un objeto X509Certificate
         try (InputStream inputStream =  new ByteArrayInputStream(certificateBlob)) {
             CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
             certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
         }
         // Obtener el emisor del certificado (el CA del certificado)
         X509Certificate issuerCertificate = getIssuerCertificateFromAIA(certificate);
         
//         // Obtener la URL OCSP
         String ocspUrl = getOcspUrl(certificate);
//         
//         // Validar el certificado utilizando OCSP
         return validateCertificateOCSP(certificate, issuerCertificate, ocspUrl);
//         System.out.println("Certificado válido: " + isValid);
    }

    
    /*
     * Cogemos el CA que emitio el certificado del sujeto, a traves de la propiedad de la extension ( "Authority Information Access") del certificado del sujeto
     */
    private X509Certificate getIssuerCertificateFromAIA(X509Certificate certificate) throws Exception {
        // Obtener la extensión Authority Information Access
        byte[] aiaExtension = certificate.getExtensionValue("1.3.6.1.5.5.7.1.1");
        
        if (aiaExtension == null) {
            return null; // No se encontró la extensión
        }

        // Desempaquetar el DEROctetString para obtener los datos de la extensión
        ASN1InputStream asn1InputStream = new ASN1InputStream(aiaExtension);
        ASN1Primitive derObject = asn1InputStream.readObject();
        asn1InputStream.close();
        
        // Desempaquetar el contenido (Octet String)
        ASN1OctetString octetString = (ASN1OctetString) derObject;
        byte[] octets = octetString.getOctets();

        ASN1InputStream aiaInputStream = new ASN1InputStream(octets);
        ASN1Primitive aiaPrimitive = aiaInputStream.readObject();
        aiaInputStream.close();

        // Convertir a AuthorityInformationAccess
        ASN1Sequence aiaSequence = (ASN1Sequence) aiaPrimitive;
        AuthorityInformationAccess authorityInfoAccess = AuthorityInformationAccess.getInstance(aiaSequence);
        
        // Iterar sobre los AccessDescriptions para encontrar el tipo de CA issuers
        for (AccessDescription accessDescription : authorityInfoAccess.getAccessDescriptions()) {
            if (accessDescription.getAccessMethod().equals(AccessDescription.id_ad_caIssuers)) {
                GeneralName generalName = accessDescription.getAccessLocation();
                
                // Asegurarse de que el nombre general es una URL
                if (generalName.getTagNo() == GeneralName.uniformResourceIdentifier) {
                    String issuerUrl = generalName.getName().toString();
                    System.out.println("Issuer URL: " + issuerUrl);
                    return loadIssuerCertificateFromUrl(issuerUrl);
                }
            }
        }
		return null;
    }
    /*
     * Llamamos a la url que tiene el certificado de la CA y lo cargamos en un objeto X509Certificate
     */
    private X509Certificate loadIssuerCertificateFromUrl(String url) throws Exception {
        // Abrir conexión HTTP para obtener el certificado
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(inputStream);
        }
    }
    
    
    /*
     * Llamamos a la url que tiene la url a la ocsp
     */
    private String getOcspUrl(X509Certificate certificate) throws Exception {
        // Extraer la URL del servidor OCSP de las extensiones del certificado
        byte[] aiaExtension = certificate.getExtensionValue("1.3.6.1.5.5.7.1.1");
        String ocspUrl =null;
        if (aiaExtension == null) {
            return null; // No se encontró la extensión
        }

        // Desempaquetar el DEROctetString para obtener los datos de la extensión
        ASN1InputStream asn1InputStream = new ASN1InputStream(aiaExtension);
        ASN1Primitive derObject = asn1InputStream.readObject();
        asn1InputStream.close();
        
        // Desempaquetar el contenido (Octet String)
        ASN1OctetString octetString = (ASN1OctetString) derObject;
        byte[] octets = octetString.getOctets();

        ASN1InputStream aiaInputStream = new ASN1InputStream(octets);
        ASN1Primitive aiaPrimitive = aiaInputStream.readObject();
        aiaInputStream.close();

        // Convertir a AuthorityInformationAccess
        ASN1Sequence aiaSequence = (ASN1Sequence) aiaPrimitive;
        AuthorityInformationAccess authorityInfoAccess = AuthorityInformationAccess.getInstance(aiaSequence);
        
        // Iterar sobre los AccessDescriptions para encontrar el tipo de OCSP
        for (AccessDescription accessDescription : authorityInfoAccess.getAccessDescriptions()) {
            if (accessDescription.getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
                GeneralName generalName = accessDescription.getAccessLocation();
                
                // Asegurarse de que el nombre general es una URL
                if (generalName.getTagNo() == GeneralName.uniformResourceIdentifier) {
                	 System.out.println("OCSP URL: " + generalName.getName().toString());
                    return generalName.getName().toString();
                   
                }
            }
        }
        return null;
    }
    
    
   	/*
   	 * Llamamos al servidor OCSP, para validar que el certificado es valido
   	 */
    private boolean validateCertificateOCSP(X509Certificate certificate, X509Certificate issuerCertificate, String ocspUrl) throws Exception {
        // Crear una solicitud OCSP
         OCSPReq ocspReq = generateOCSPRequest(issuerCertificate,certificate.getSerialNumber());

        // Enviar la solicitud OCSP y obtener la respuesta
        OCSPResp ocspResp = sendOcspRequest(ocspUrl, ocspReq);
        
        // Procesar la respuesta OCSP
        if (ocspResp.getStatus() == OCSPResponseStatus.SUCCESSFUL) {
            BasicOCSPResp basicResp = (BasicOCSPResp) ocspResp.getResponseObject();
            SingleResp[] singleResps = basicResp.getResponses();
            for (SingleResp singleResp : singleResps) {
                // Comprobar el estado del certificado
                if (singleResp.getCertStatus() == null) {
                    return true; // El certificado es válido
                }
            }
        }
        return false; // El certificado no es válido o no se encontró
    }

   	/*
   	 * Generamos la request al servidor OCSP, con el certificado del emisor y el serialNumber del certificado del sujeto
   	 */
    private OCSPReq generateOCSPRequest(X509Certificate issuerCertificate, BigInteger serialNumber) throws Exception {
        // Crear el DigestCalculator para SHA-1
        DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().build();
        DigestCalculator digCalc = digCalcProv.get(new DefaultDigestAlgorithmIdentifierFinder().find("SHA-1"));

        // Generar el CertID para el certificado que se va a verificar
        CertificateID certId = new CertificateID(digCalc, new JcaX509CertificateHolder(issuerCertificate), serialNumber);

        // Construir la solicitud OCSP con el CertID
        OCSPReqBuilder ocspReqBuilder = new OCSPReqBuilder();
        ocspReqBuilder.addRequest(certId);

        // Agregar extensiones opcionales (ej. Nonce)
        ExtensionsGenerator extGen = new ExtensionsGenerator();
        extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(BigInteger.valueOf(System.currentTimeMillis()).toByteArray()));
        ocspReqBuilder.setRequestExtensions(extGen.generate());

        return ocspReqBuilder.build();
    }

    /*
     * Hacemos el envio a la url de OCSP y recogemos la respuesta
     */
    private OCSPResp sendOcspRequest(String ocspUrl, OCSPReq ocspReq) throws Exception {
        // Serializar la solicitud OCSP a bytes
        byte[] requestBytes = ocspReq.getEncoded();
        
        // Configurar la conexión HTTP
        HttpURLConnection connection = (HttpURLConnection) new URL(ocspUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/ocsp-request");
        connection.setRequestProperty("Content-Length", String.valueOf(requestBytes.length));
        
        // Enviar la solicitud
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBytes);
        }

        // Leer la respuesta OCSP
        try (InputStream is = connection.getInputStream()) {
            return new OCSPResp(is);
        }
    }
}
