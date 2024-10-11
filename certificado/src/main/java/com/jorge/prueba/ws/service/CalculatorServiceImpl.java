package com.jorge.prueba.ws.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.MTOM;

@WebService(endpointInterface = "com.jorge.prueba.ws.service.CalculatorService")
@MTOM(enabled=true)
public class CalculatorServiceImpl implements CalculatorService {

	public CalculatorServiceImpl() {
		try {
            // Intenta cargar el archivo handlers.xml desde el classpath
//            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//            if (classLoader == null) {
//                classLoader = getClass().getClassLoader();
//            }
//			ClassLoader classLoader = getClass().getClassLoader();
            URL handlerChainURL = getClass().getResource("handler.xml");
 //          URL handlerChainURL = classLoader.getResource("handler.xml");
            if (handlerChainURL != null) {
                System.out.println("Handler file found: " + handlerChainURL.getPath());
            } else {
                System.err.println("Handler file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int subtract(int a, int b) {
        return a - b;
    }
    

    @Override
    public void uploadFile(DataHandler fileData) {
        try {
            // Guardar el archivo en el servidor
            InputStream inputStream = fileData.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(new File("C:\\Users\\dorobejo\\workspace\\prueba.txt"));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public DataHandler downloadFile(String fileName) {
        File file = new File("C:\\workspace" + fileName);
        return new DataHandler(new FileDataSource(file));
    }
}
