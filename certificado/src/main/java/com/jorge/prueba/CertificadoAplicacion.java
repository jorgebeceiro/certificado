package com.jorge.prueba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jorge.prueba", "com.jorge.prueba.ws.config"})
public class CertificadoAplicacion {
    public static void main(String[] args) {
        SpringApplication.run(CertificadoAplicacion.class, args);
    }
}
