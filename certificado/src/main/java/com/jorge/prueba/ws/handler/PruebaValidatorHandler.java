package com.jorge.prueba.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;

import jakarta.mail.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

public class PruebaValidatorHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		System.out.println("entra en el handle Message");
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		System.out.println("entra en el handle Fault");
		return false;
	}


	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		System.out.println("entra en el getHeaders");
		return null;
	}

	@Override
	public void close(jakarta.xml.ws.handler.MessageContext context) {
		// TODO Auto-generated method stub
		
	}

}
