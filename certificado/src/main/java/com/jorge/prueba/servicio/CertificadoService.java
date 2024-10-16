package com.jorge.prueba.servicio;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jorge.prueba.mapping.Certificado;
import com.jorge.prueba.repositorio.CertificadoRepository;
import com.jorge.prueba.utils.CertificadoUtils;

@Service
public class CertificadoService {
    private final CertificadoRepository certificadoRepository;

    public CertificadoService(CertificadoRepository certificadoRepository) {
        this.certificadoRepository = certificadoRepository;
    }

    public Certificado guardarCertificado(MultipartFile archivo) throws IOException {
        Certificado certificado = new Certificado();
        certificado.setContenido(archivo.getBytes());
  	 	File tempFile = File.createTempFile("certificado", ".der"); // Cambia la extensión según tu archivo
    	 archivo.transferTo(tempFile); // Guardar el MultipartFile en el archivo temporal
    	 String serialNumber ="";
    	try {
			serialNumber = CertificadoUtils.obtenerSerialNumber(tempFile.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        certificado.setNombre(archivo.getOriginalFilename());
        certificado.setSerialNumber(serialNumber);
        certificado.setFechaCarga(LocalDateTime.now());
        return certificadoRepository.save(certificado);
    }
    
    
}
