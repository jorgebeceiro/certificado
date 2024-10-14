package com.jorge.prueba.ws.service;

import jakarta.jws.WebService;

@WebService(endpointInterface = "com.jorge.prueba.ws.service.PruebaService")
public class PruebaServiceImpl implements PruebaService {

	public PruebaServiceImpl() {
	}
    @Override
    public int add(int a, int b) {
        return a + b;
    }

}
