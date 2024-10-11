package com.jorge.prueba.ws.service;

import org.apache.cxf.feature.Features;

import jakarta.activation.DataHandler;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import jakarta.jws.soap.SOAPBinding.Use;

@WebService
@HandlerChain(file="handler.xml")
@SOAPBinding(style=Style.RPC,use=Use.LITERAL)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
public interface CalculatorService {
    @WebMethod
    int add(@WebParam(name="valorUno") int a, @WebParam(name="valorDos")int b);

    @WebMethod
    int subtract(@WebParam(name="valorUno") int a, @WebParam(name="valorDos")int b);
    
    @WebMethod
    public void uploadFile(@WebParam(name="fichero") DataHandler fileData);

    @WebMethod
    public DataHandler downloadFile(@WebParam(name="nombreFichero")  String fileName);

}
