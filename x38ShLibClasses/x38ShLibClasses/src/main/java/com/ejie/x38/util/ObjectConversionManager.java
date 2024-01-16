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
package com.ejie.x38.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Generic object converter.
 * <p>
 * <h3>Use examples</h3>
 * 
 * <pre>
 * Object o1 = Boolean.TRUE;
 * Integer i = ObjectConverter.convert(o1, Integer.class);
 * System.out.println(i); // 1
 * 
 * Object o2 = "false";
 * Boolean b = ObjectConverter.convert(o2, Boolean.class);
 * System.out.println(b); // false
 * 
 * Object o3 = new Integer(123);
 * String s = ObjectConverter.convert(o3, String.class);
 * System.out.println(s); // 123
 * </pre>
 * 
 * Not all possible conversions are implemented. You can extend the <tt>ObjectConverter</tt>
 * easily by just adding a new method to it, with the appropriate logic. For example:
 * 
 * <pre>
 * public static ToObject fromObjectToObject(FromObject fromObject) {
 *     // Implement.
 * }
 * </pre>
 * 
 * The method name doesn't matter. It's all about the parameter type and the return type.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/08/generic-object-converter.html
 */
public final class ObjectConversionManager {

    // Init ---------------------------------------------------------------------------------------

    private static final Map<String, Method> CONVERTERS = new HashMap<String, Method>();

    static {
        // Preload converters.
        Method[] methods = ObjectConversionManager.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1) {
                // Converter should accept 1 argument. This skips the convert() method.
                CONVERTERS.put(method.getParameterTypes()[0].getName() + "_"
                    + method.getReturnType().getName(), method);
            }
        }
    }

    private ObjectConversionManager() {
        // Utility class, hide the constructor.
    }

    // Action -------------------------------------------------------------------------------------

    /**
     * Convert the given object value to the given class.
     * @param from The object value to be converted.
     * @param to The type class which the given object should be converted to.
     * @return The converted object value.
     * @throws NullPointerException If 'to' is null.
     * @throws UnsupportedOperationException If no suitable converter can be found.
     * @throws RuntimeException If conversion failed somehow. This can be caused by at least
     * an ExceptionInInitializerError, IllegalAccessException or InvocationTargetException.
     */
    public static <T> T convert(Object from, Class<T> to) {

        // Null is just null.
        if (from == null) {
            return null;
        }

        // Can we cast? Then just do it.
        if (to.isAssignableFrom(from.getClass())) {
            return to.cast(from);
        }

        // Lookup the suitable converter.
        String converterId = from.getClass().getName() + "_" + to.getName();
        Method converter = CONVERTERS.get(converterId);
        if (converter == null) {
            throw new UnsupportedOperationException("Cannot convert from " 
                + from.getClass().getName() + " to " + to.getName()
                + ". Requested converter does not exist.");
        }

        // Convert the value.
        try {
            return to.cast(converter.invoke(to, from));
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert from " 
                + from.getClass().getName() + " to " + to.getName()
                + ". Conversion failed with " + e.getMessage(), e);
        }
    }

    // Converters ---------------------------------------------------------------------------------

    /**
     * Converts Integer to Boolean. If integer value is 0, then return FALSE, else return TRUE.
     * @param value The Integer to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean integerToBoolean(Integer value) {
        return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Converts Boolean to Integer. If boolean value is TRUE, then return 1, else return 0.
     * @param value The Boolean to be converted.
     * @return The converted Integer value.
     */
    public static Integer booleanToInteger(Boolean value) {
        return value.booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
    }

    /**
     * Converts Double to BigDecimal.
     * @param value The Double to be converted.
     * @return The converted BigDecimal value.
     */
    public static BigDecimal doubleToBigDecimal(Double value) {
        return new BigDecimal(value.doubleValue());
    }

    /**
     * Converts BigDecimal to Double.
     * @param value The BigDecimal to be converted.
     * @return The converted Double value.
     */
    public static Double bigDecimalToDouble(BigDecimal value) {
        return Double.valueOf(value.doubleValue());
    }

    /**
     * Converts Integer to String.
     * @param value The Integer to be converted.
     * @return The converted String value.
     */
    public static String integerToString(Integer value) {
        return value.toString();
    }

    /**
     * Converts String to Integer.
     * @param value The String to be converted.
     * @return The converted Integer value.
     */
    public static Integer stringToInteger(String value) {
        return Integer.valueOf(value);
    }

    /**
     * Converts Boolean to String.
     * @param value The Boolean to be converted.
     * @return The converted String value.
     */
    public static String booleanToString(Boolean value) {
        return value.toString();
    }

    /**
     * Converts String to Boolean.
     * @param value The String to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean stringToBoolean(String value) {
        return Boolean.valueOf(value);
    }

    /**
     * Converts Long to String.
     * @param value The Integer to be converted.
     * @return The converted String value.
     */
    public static String longToString(Long value) {
        return value.toString();
    }

    /**
     * Converts String to Long.
     * @param value The String to be converted.
     * @return The converted Long value.
     */
    public static Long stringToLong(String value) {
    	if (value.equals("")){
    		return null;
    	}else{
    		return Long.valueOf(value);
    	}
    }
    
    /**
     * Converts BigDecimal to String.
     * @param value The Integer to be converted.
     * @return The converted String value.
     */
    public static String bigDecimalToString(BigDecimal value) {
        return value.toString();
    }
    
    /**
     * Converts BigDecimal to String.
     * @param value The Integer to be converted.
     * @param numDecimales Number of decimals
     * @return The converted String value.
     */
    public static String bigDecimalToString(BigDecimal number, int numDecimales) {
		return ObjectConversionManager.bigDecimalToString(number, numDecimales, LocaleContextHolder.getLocale());
	}
    
    /**
     * Converts BigDecimal to String.
     * @param value The Integer to be converted.
     * @param numDecimales Number of decimals
     * @param locale Locale to use in the conversion.
     * @return The converted String value.
     */
    public static String bigDecimalToString(BigDecimal number, int numDecimales, Locale locale) {
		String res = "";
		try {
			if (number != null) {
				DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(locale));
				df.setMinimumFractionDigits(numDecimales);
				df.setMaximumFractionDigits(numDecimales);
				res = df.format(number);
			}
		} catch (IllegalArgumentException e) {
			res = "";
		}
		return res;
	}
    
    
    public static BigDecimal stringToBigDecimal(String strNumber, Locale locale) {
		BigDecimal res = null;
		try {
			if (strNumber != null) {
				DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(locale));
				df.setParseBigDecimal(true);
				res = new BigDecimal(df.parse(strNumber).toString());
			}
		} catch (IllegalArgumentException e) {
			res = null;
		} catch (ParseException e) {
			res = null;
		}
		return res;
	}
    
   
    /**
     * Converts String to BigDecimal.
     * @param value The String to be converted.
     * @return The converted Long value.
     */
    public static BigDecimal stringToBigDecimal(String value) {
    	if (value.equals("")){
    		return null;
    	}else{
    		return BigDecimal.valueOf(Long.valueOf(value).longValue());
    	}        
    }
    
    // You can implement more converter methods here.

}