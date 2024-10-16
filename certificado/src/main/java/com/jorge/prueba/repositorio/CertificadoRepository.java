package com.jorge.prueba.repositorio;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jorge.prueba.mapping.Certificado;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
	
	   // Método para buscar por serialNumber
    Optional<Certificado> findBySerialNumber(String serialNumber);

}
