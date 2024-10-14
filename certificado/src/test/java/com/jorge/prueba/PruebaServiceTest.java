package com.jorge.prueba;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jorge.prueba.ws.service.CalculatorService;
import com.jorge.prueba.ws.service.PruebaService;
import com.jorge.prueba.ws.service.PruebaServiceImpl;

public class PruebaServiceTest {
	
    private  org.eclipse.jetty.server.Server jettyServer;

    private PruebaService calculatorServiceProxy;
    @BeforeEach
    public void setUp() throws Exception {
        // Crear el servidor Jetty
        jettyServer = new Server(8080);

        // Crear el contexto de servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Añadir el CXFServlet al contexto
        ServletHolder cxfServletHolder = new ServletHolder(new CXFServlet());
        context.addServlet(cxfServletHolder, "/services/*"); // Cambiar la ruta según lo necesites
       
        jettyServer.setHandler(context);
        // Iniciar el servidor Jetty
        jettyServer.start();

        // Crear el bus de CXF
        Bus bus = BusFactory.getDefaultBus();

        // Configurar el servicio con CXF
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setBus(bus);
        factory.setServiceClass(PruebaService.class);
        factory.setServiceBean(new PruebaServiceImpl());
        factory.setAddress("/PruebaService");

        // Crear el servidor CXF
        factory.create(); // Solo creamos el servidor, no lo iniciamos aquí.

        System.out.println("Servidor Jetty iniciado en http://localhost:8080/services/PruebaService");

    }

    @AfterEach
    public void tearDown() {
        if (jettyServer != null) {
            try {
            	jettyServer.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
    }

//    private void waitForService(String serviceUrl) {
//        int retries = 5;
//        while (retries > 0) {
//            try {
//                HttpURLConnection connection = (HttpURLConnection) new URL(serviceUrl).openConnection();
//                connection.setRequestMethod("GET");
//                connection.setConnectTimeout(1000); // Timeout de 1 segundo
//                connection.connect();
//                
//                if (connection.getResponseCode() == 200) {
//                    return; // El servicio está disponible
//                }
//            } catch (Exception e) {
//                // Ignorar y esperar
//            }
//            retries--;
//            try {
//                Thread.sleep(1000); // Esperar 1 segundo antes de reintentar
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
//            }
//        }
//        throw new RuntimeException("El servicio no está disponible después de varios intentos.");
//    }

    @Test
    public void testGetCustomerDetails() throws IOException {
        // Realizar la llamada al servicio SOAP y verificar la respuesta

        // Test GET
        HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8080/services").openConnection();
        http.connect();
        System.out.println(http.getResponseCode());
        assertEquals( HttpStatus.OK_200, http.getResponseCode() );
        JaxWsProxyFactoryBean factory2 = new JaxWsProxyFactoryBean();
        factory2.setServiceClass(PruebaService.class);
        factory2.setAddress("http://localhost:8080/services/PruebaService");
        calculatorServiceProxy = (PruebaService) factory2.create();
        
    	int suma = calculatorServiceProxy.add(123,245);
//        
//        assertNotNull(suma);
        assertEquals("368",String.valueOf(suma));
    }
}
