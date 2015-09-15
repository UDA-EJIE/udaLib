package com.ejie.x38.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Almacena datos en un espacio de memoria independiente por cada hilo.
 */
public class ThreadStorageManager {
	
	//-9223372036854775808
	//9223372036854775807
	private static final AtomicLong uniqueId = new AtomicLong(0);
	
    private static ThreadLocal < Long > uniqueNum = 
        new ThreadLocal < Long > () {
            @Override protected Long initialValue() {
                return uniqueId.getAndIncrement();
        }
    };

    /**
     * Asigna un valor predefinido al Thread Local.
     * 
     * @param uniqueIdentificator
     */
    public static void setCurrentThreadId(Long uniqueIdentificator){
    	uniqueId.lazySet(uniqueIdentificator);
    }
    
    /**
     * Devuelve el valor almacenado por cada hilo. Si dicho valor no se ha inicializado, lo inicializa.
     * 
     * @return valor que se almacenapor cada hilo
     */
    public static long getCurrentThreadId() {
        return uniqueNum.get();
    }
    
    /**
     * Borra el espacio de memoria asociado al hilo.
     */
    public static void clearCurrentThreadId(){
    	uniqueNum.remove();
    }
}