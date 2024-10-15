package com.jorge.prueba.callback;

import java.io.IOException;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class ClientPasswordCallback implements CallbackHandler {
    @Override
    public void handle(javax.security.auth.callback.Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (javax.security.auth.callback.Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                // Aquí puedes validar el usuario y la contraseña
                if ("jorge".equals(pc.getIdentifier())) {
                    pc.setPassword("passKeyStore");
                }else if("server".equals(pc.getIdentifier())) {
                	pc.setPassword("passAlias");
                }else if("prueba".equals(pc.getIdentifier())) {
                	pc.setPassword("passAlias");
                
                }else if("pepe".equals(pc.getIdentifier())) {
                	pc.setPassword("passAlias");
                }
            }
        }
    }
}
