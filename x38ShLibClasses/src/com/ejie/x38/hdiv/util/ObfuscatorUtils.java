package com.ejie.x38.hdiv.util;

public class ObfuscatorUtils {

	private static final String SOURCE="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private static final String TARGET="tQc5nAijb8ZWawShs0XEzDC6RdFoVuT9GeBY4gHpNlU3J2vkxMIm1rKfO7LqPy";
	
	private static final String SPLIT_TOKEN="-:$:-";
	
	private ObfuscatorUtils() {
	}

	public static String obfuscate(String s, Class<?> clazz) {
		
		if(s == null || clazz == null) {
			return null;
		}
		
		return translate(clazz.getName(), SOURCE, TARGET).append(SPLIT_TOKEN).append(translate(s, SOURCE, TARGET)).toString();
		
	}
	
	private static StringBuilder translate(String literal, String source, String target) {
		StringBuilder result = new StringBuilder();
		
		for (int i=0;i<literal.length();i++) {
	        char c=literal.charAt(i);
	        int index=source.indexOf(c);
	        if(index == -1) {
	        	result.append(c);
	        }else {
	        	result.append(target.charAt(index));	
	        }  
	    }
	    
	    return result;
	}

	public static Class<?> getClass(String s) {
		
		if(s == null) {
			return null;
		}
		
		int index = s.indexOf(SPLIT_TOKEN);
		
		if(index == -1) {
			return null;
		}
		
		try {
		    return Class.forName(translate(s.substring(0, index), TARGET, SOURCE).toString());
		 } catch (ClassNotFoundException e) {
		    return null;
		}
		
	}
	
	public static String deobfuscate(String s) {
		
		if(s == null) {
			return null;
		}
		
		int index = s.indexOf(SPLIT_TOKEN);
		
		if(index == -1) {
			return null;
		}
		
		return translate(s.substring(index + SPLIT_TOKEN.length()), TARGET, SOURCE).toString();
	}
	
	public static boolean isObfuscatedId(String s) {
		return s.indexOf(SPLIT_TOKEN) > -1;
	}

}
