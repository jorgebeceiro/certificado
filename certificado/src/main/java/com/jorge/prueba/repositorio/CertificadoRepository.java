package com.jorge.prueba.repositorio;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jorge.prueba.mapping.Certificado;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
}
