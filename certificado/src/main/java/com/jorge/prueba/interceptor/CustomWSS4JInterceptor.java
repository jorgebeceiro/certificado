package com.jorge.prueba.interceptor;

import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.apache.wss4j.dom.handler.WSHandlerResult;

public class CustomWSS4JInterceptor extends WSS4JInInterceptor {


	    @Override
	    public void handleMessage(SoapMessage msg) {
	    	System.out.println("Entra en CustomWSS4JInterceptor " );

	        // Llamada al interceptor original para validar la seguridad
	        super.handleMessage(msg);
	        
	        // Obtener los resultados de la validación WS-Security
	        List<WSHandlerResult> results = (List<WSHandlerResult>) msg.getContextualProperty(WSHandlerConstants.RECV_RESULTS);
	        if (results != null) {
	            for (WSHandlerResult result : results) {
	                List<WSSecurityEngineResult> securityResults = result.getResults();
	                for (WSSecurityEngineResult securityResult : securityResults) {
	                    // Obtener el certificado X.509 usado en la firma
	                    X509Certificate cert = (X509Certificate) securityResult.get(WSSecurityEngineResult.TAG_X509_CERTIFICATE);
	                    if (cert != null) {
	                        // Extraer el "Subject" del certificado
	                        String subject = cert.getSubjectX500Principal().getName();
	                        System.out.println("Certificado del remitente: " + subject);

	                        // Aquí puedes agregar lógica adicional para verificar el certificado o autorizar al usuario
	                    }
	                }
	            }
	        }
	    }
	}

