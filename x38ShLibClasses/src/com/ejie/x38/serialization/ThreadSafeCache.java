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
package com.ejie.x38.serialization;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author UDA
 *
 */
public class ThreadSafeCache {
	
    private static ThreadLocal<Map<String, String>> map = 
        new ThreadLocal<Map<String, String>> () {
            @Override protected Map<String, String> initialValue() {
                return new HashMap<String, String>();
        }
    };

    public static void addValue(String k, String v) {
    	map.get().put(k, v);
    }
    
    public static Map<?, ?> getMap(){
    	return (Map<?, ?>) map.get();
    }
    
    public static void clearCurrentThreadCache(){
    	map.remove();
    }
}