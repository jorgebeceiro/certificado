package com.jorge.prueba.ws.config;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    @Bean
    public Bus cxfBus() {
    	return CXFBusFactory.getDefaultBus();
    }
}
