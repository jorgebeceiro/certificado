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
@Features(features = "org.apache.cxf.feature.LoggingFeature")
public interface PruebaService {
    @WebMethod
    int add(@WebParam(name="valorUno") int a, @WebParam(name="valorDos")int b);

}
