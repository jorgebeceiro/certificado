package com.jorge.prueba.controlador;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jorge.prueba.mapping.Certificado;
import com.jorge.prueba.servicio.CertificadoService;

@Controller
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @GetMapping("/cargar-certificado")
    public String formularioCarga() {
        return "cargar-certificado";
    }

    @PostMapping("/cargar-certificado")
    public String cargarCertificado(@RequestParam("archivo") MultipartFile archivo, Model model) {
        try {
        	System.out.println("ha llegado aqui");
            Certificado certificado = certificadoService.guardarCertificado(archivo);
            model.addAttribute("mensaje", "Certificado cargado exitosamente: " + certificado.getNombre());
        } catch (IOException e) {
            model.addAttribute("mensaje", "Error al cargar el certificado: " + e.getMessage());
        }
        return "cargar-certificado";
    }
    public static void main(String[] args) {
		SpringApplication.run(CertificadoController.class, args);
	}

}
