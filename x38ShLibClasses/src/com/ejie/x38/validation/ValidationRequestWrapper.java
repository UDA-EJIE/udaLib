/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class which is used to wrap a request in order that the wrapped request's input stream can be 
 * read once and later be read again in a pseudo fashion by virtue of keeping the original payload
 * as a string which is actually what is returned by subsequent calls to getInputStream().
 * 
 * @author UDA
 * 
 */
public class ValidationRequestWrapper extends HttpServletRequestWrapper {
    
    private static Logger log =  LoggerFactory.getLogger("com.ejie.x38.validation.ValidationRequestWrapper");
 
    private final String jsonPayload;
    
    public ValidationRequestWrapper (HttpServletRequest request) throws Exception {
        
        super(request);
        
        // read the original payload into the xmlPayload variable
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        
        try {
            // read the payload into the StringBuilder
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                // make an empty string since there is no payload
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            log.error("Error reading the request payload", ex);
            throw new Exception("Error reading the request payload", ex);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iox) {
                    // ignore
                }
            }
        }
        jsonPayload = stringBuilder.toString();
    }
 
    /**
     * Override of the getInputStream() method which returns an InputStream that reads from the
     * stored JSON payload string instead of from the request's actual InputStream.
     */
    @Override
    public ServletInputStream getInputStream ()
        throws IOException {
        
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonPayload.getBytes());
        ServletInputStream inputStream = new ServletInputStream() {
            public int read () 
                throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return inputStream;
    }
    
}
