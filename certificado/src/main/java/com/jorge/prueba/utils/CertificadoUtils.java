package com.jorge.prueba.utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificadoUtils {

    public static String obtenerSerialNumber(String archivoCertificado) throws Exception {
        // Cargar el certificado desde un archivo
//        try (PEMParser pemParser = new PEMParser(new FileReader(archivoCertificado))) {
//            Object objeto = pemParser.readObject();
//            if (objeto instanceof X509CertificateHolder) {
//                X509CertificateHolder certHolder = (X509CertificateHolder) objeto;
//                return certHolder.getSerialNumber().toString();
//            } else {
//                throw new IllegalArgumentException("El archivo no contiene un certificado válido.");
//            }
        try {    
            // Crear un FileInputStream para leer el archivo DER
            FileInputStream fis = new FileInputStream(archivoCertificado);

            // Crear un CertificateFactory para leer certificados X.509
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // Leer el certificado desde el archivo
            Certificate certificado = cf.generateCertificate(fis);
            X509Certificate x509Certificado = (X509Certificate) certificado;
            return x509Certificado.getSerialNumber().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el certificado: " + e.getMessage(), e);
        }
    }
}
