/**
 * 
 */
package com.ejie.x38.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author surieta
 *
 */
public final class MiscUtilitiesManager {
	
	/**
	 * Reemplaza un string por otro en una cadena reiterativamente
	 * 
	 * @param String cadena de texto fuente
	 * @param String cadena a reemplazar
	 * @param String cadena de reemplazo
	 * @return String cadena resultado
	 */
	public static String replaceString(String src, String old, String newOne) {

		StringBuffer sbuf = new StringBuffer(src);
		int oldPos = 0;
		int fromIndex = 0;
		int offset = 0;

		while (true) {
			oldPos = src.indexOf(old, fromIndex);
			if (oldPos == -1)
				break;

			oldPos += offset;
			fromIndex = oldPos + 1;

			sbuf.replace(oldPos, oldPos + old.length(), newOne);
			offset = newOne.length() - 1;
		}

		String strBuffer = sbuf.toString();
		sbuf = null;

		return strBuffer;

	}
	
	/**
     * Método que comprueba si un object es nula
     * 
     * @param o Object a comprobar
     * @return boolean true si la cadena no es nula y no es la cadena vacía; false en caso contrario, si la cadena tiene blancos
     *         no se considera cadena vacía.
     */
    public static boolean isNotNull(Object o) {
        return (o != null && !o.equals("null"));
    }
    /**
     * Método que comprueba si una colección es nula o es está vacía.
     * 
     * @param c Colección a comprobar
     * @return boolean true si la colección es nula o está vacía; false en caso contrario.
     */
    public static boolean isBlank(Collection<?> c) {
        return (c == null || c.isEmpty());
    }

    /**
     * Método que comprueba si una cadena es nula o es la cadena vacía.
     * 
     * @param s String a comprobar
     * @return boolean true si la cadena es nula o es la cadena vacía; false en caso contrario, si la cadena tiene blancos no se
     *         considera cadena vacía.
     */
    public static boolean isBlank(String s) {
        return (s == null || s.equals(""));
    }

    /**
     * Método que comprueba si una cadena no es nula y no es la cadena vacía.
     * 
     * @param s String a comprobar
     * @return boolean true si la cadena no es nula y no es la cadena vacía; false en caso contrario, si la cadena tiene blancos
     *         no se considera cadena vacía.
     */
    public static boolean isNotBlank(String s) {
        return (s != null && !s.equals(""));
    }
  
    /**
     * Metodo que; pasando un String, le concatena las funciones TRIM(UPPER(param)) para tratamiento Oracle.
     * 
     * @param param String a la que hacer TRIM(UPPER(param))
     * @return devuelve conformado el String
     */
    public static String upperAndTrimOracle(String param) {
        return "TRIM(UPPER(" + param + "))";
    }

    /**
     * Metodo que; pasando un String, le concatena las funciones TRIM(UPPER(String)) para tratamiento JAVA.
     * 
     * @param param String a la que hacer param.trim().toUpperCase()
     * @return devuelve conformado el String
     */
    public static String upperAndTrimJava(String param) {
        return param.trim().toUpperCase();
    }

    /**
     * Método que une los elementos de un array en un String, separando los elementos por comas.
     * 
     * @param a El array del cual se obtendrán los valores de elementos que formarrán el String
     * @return El String con los valores de los elementos separados por comas. En caso de que algún elemento de array sea null se
     *         añadirá la cadena "null" como valor del elemento
     */
    public static String join(Object[] a) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < a.length; ++i) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append((a[i] == null) ? "null" : a[i].toString());
        }
        return s.toString();
    }
    
    /**
     * Método que une los elementos de un array en un String, separando los elementos la cadena de separacion indicada.
     * 
     * @param a El array del cual se obtendrán los valores de elementos que formarrán el String
     * @param seprator Cadena de separacion de los elementos
     * @param withNulls Indica si los elementos nulos se insertará como "null" en la lista de lementos
     * @return El String con los valores de los elementos separados por comas. En caso de que algún elemento de array sea null se
     *         añadirá la cadena "null" como valor del elemento si withNulls es true
     */
    public static String join(Object[] a, String seperator, boolean withNulls) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < a.length; ++i) {
            if (s.length() > 0) {
                s.append(seperator);
            }
            if (a[i] == null) {
                if (withNulls) {
                    s.append("null");
                }
            } else {
                s.append(a[i].toString());
            }
        }
        return s.toString();
    }
    
    /**
     * Método que separa los elementos de una lista en varias listas del tamaño indicado.
     * 
     * @param list La lista de la que se separan los elementos
     * @param size Tamaño de las listas en las cuales son separados los elementos 
     * @return Una lista de listas con los elementos separados
     */
    public static List<List<?>> split(List<?> list, int size) {
        List<List<?>> l = new ArrayList<List<?>>();
        int n = 0;
        int m = (n + size < list.size())?n + size:list.size();
        while (n < list.size()) {
            l.add(list.subList(n, m));
            n = m;
            m = (n + size < list.size())?n + size:list.size();
        }
        return l;
    }
    
    /**
     * Método que reemplaza en una cadena las claves por sus valores indicados en un mapa.
     * 
     * @param format Cadena de plantilla
     * @param data Mapa con las claves y cadenas de reemplazo
     * @return Cadena con los datos reemplazados por los valores indicados en el mapa
     */
    public static String replace(String format, Map<?, ?> data) {
        String s = format;
        for (Iterator<?> it = data.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            String value = (String) data.get(key);
            if (value == null) {
                value = "";
            }
            s = s.replaceAll(key, value);
        }
        return s;
    }
        
    public static String limit(String s, int n) {
        return (s.length() > n)?s.substring(0, n):s;
    }   
    
	/** Lee un fichero a un String
     * @param filePath nombredelfichero a leer 
     * @return String
     * @throws java.io.IOException excepcion
 	**/ 	    
     public static String readFileAsString(String filePath) throws java.io.IOException{
 	    byte[] buffer = new byte[(int) new File(filePath).length()];
 	    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filePath));
 	    f.read(buffer);
 	    return new String(buffer);
 	}
     
     /**
      * CODIFICACION_PLUS_JAVASCRIPT codificacion del escape en javascript para el
      * simbolo +
      */
     public final static String CODIFICACION_PLUS_JAVASCRIPT = "\\+";

       // para la función encodeHTML
       static final String[][] entities = {
           { "&", "amp" }
           
       };
       static String entityMap;
       static String[] quickEntities;
       static {
           int l = entities.length;
           StringBuffer temp = new StringBuffer();
           quickEntities = new String[l];
           for (int i = 0; i < l; i++) {
               temp.append(entities[i][0]);
               quickEntities[i] = "&" + entities[i][1] + ";";
           }
           entityMap = temp.toString();
       }
       
       /**
        * Función para codificar a HTML una cadena
        * @param nonHTMLsrc cadena
        * @return cadena codificada en HTML
        */
       // Función para codificar a HTML una cadena
       public static String encodeHTML(String nonHTMLsrc) {
           if (nonHTMLsrc==null){
               return "";
           }else{
               StringBuffer res = new StringBuffer();
               int l = nonHTMLsrc.length();
               int idx;
               char c;
               for (int i = 0; i < l; i++) {
                   c = nonHTMLsrc.charAt(i);
                   idx = entityMap.indexOf( c);
                   if (idx == -1) res.append( c);
                   else res.append( quickEntities[idx]);
               }
               return(res.toString());
           }
       }
       
   	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
}
