package com.jorge.prueba.ws.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jorge.prueba.ws.service.CalculatorServiceImpl;

import jakarta.xml.ws.Endpoint;

@Configuration
public class WebServiceConfig {
	@Autowired
    private Bus bus;
 
    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, new CalculatorServiceImpl());
        
       
        endpoint.publish("/Hello");
        return endpoint;
    }
}
