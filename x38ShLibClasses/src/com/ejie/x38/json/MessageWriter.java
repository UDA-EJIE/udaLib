package com.ejie.x38.json;

import java.io.StringWriter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageWriter extends JSONWriter {

	
	
	public MessageWriter() {
        super(new StringWriter());
    }
	
	
	public MessageWriter startMessageList(){
		this.array();
		
		return this;
	}
	
	public MessageWriter endMessageList(){
		this.endArray();
		
		return this;
	}
	
	/*
	 * ESCRITURA DE STRINGS
	 */
	/**
	 * 
	 * @param messageList
	 * @return
	 */
	public MessageWriter addMessage(String ... messageList ){
		
		for (String message : messageList) {
			this.value(message);
		}
		
		return this;
	}
	
	public MessageWriter addMessage(MessageSource messageSource, String ... messageList ){
		
		Locale locale = LocaleContextHolder.getLocale();
		
		for (String message : messageList) {
			this.value(messageSource.getMessage(message, null, locale));
		}
		
		return this;
	}
	
	public MessageWriter addMessage(MessageSource messageSource, String message, Object[] messageParams ){
		
		Locale locale = LocaleContextHolder.getLocale();
		
		this.value(messageSource.getMessage(message, messageParams, locale));
		
		return this;
	}
	
	/*
	 * ESCRITURA DE OBJETOS JSON
	 */
	
	public MessageWriter addComplexMessage(String message){
		return this.addComplexMessage(null, message, null, null, false);
	}
	
	public MessageWriter addComplexMessage(String message, boolean i18nRupResource){
		return this.addComplexMessage(null, message, null, null, i18nRupResource);
	}
	
	public MessageWriter addComplexMessage(String message, String style){
		return this.addComplexMessage(null, message, null, style, false);
	}
	
	public MessageWriter addComplexMessage(MessageSource messageSource, String message){
		return this.addComplexMessage(messageSource, message, null, null, false);
	}
	
//	public MessageWriter addComplexMessage(MessageSource messageSource, String message, boolean i18nRupResource){
//		return this.addComplexMessage(messageSource, message, null, null, i18nRupResource);
//	}
	
	public MessageWriter addComplexMessage(MessageSource messageSource, String message, String style){
		return this.addComplexMessage(messageSource, message, null, style, false);
	}
	
	public MessageWriter addComplexMessage(MessageSource messageSource, Object[] messageParams, String message){
		return this.addComplexMessage(messageSource, message, messageParams, null, false);
	}
	
//	public MessageWriter addComplexMessage(MessageSource messageSource, String message, Object[] messageParams, boolean i18nRupResource){
//		return this.addComplexMessage(messageSource, message, messageParams, null, i18nRupResource);
//	}
	
	public MessageWriter addComplexMessage(MessageSource messageSource, String message, Object[] messageParams, String style){
		return this.addComplexMessage(messageSource, message, messageParams, style, false);
	}
	
	public MessageWriter addComplexMessage(MessageSource messageSource, String message, Object[] messageParams, String style, boolean i18nRupResource){
		
		this.object();
		
		this.key((i18nRupResource ? "i18nCaption" : "label"));
		if (messageSource!=null){
			Locale locale = LocaleContextHolder.getLocale();
			this.value(messageSource.getMessage(message, messageParams,  locale));
		}else{
			this.value(message);
		}
		
		if (style!=null){
			this.key("style").value(style);
		}
		
		this.endObject();
		
		return this;
	}
	
	/*
	 * FUNCIONES DE ANIDACION
	 */
	public MessageWriter startSubLevel(){
		this.array();
		return this;
	}
	
	public MessageWriter endSubLevel(){
		this.endArray();
		return this;
	}
	
	
	public String toString() {
//		return this.writer.toString();
        return this.mode == 'd' ? this.writer.toString() : null;
    }
	
	/*
	 * Retorno de 
	 */
	public JSONArray getJSONArray(){
		
		return new JSONArray(this.toString());
		
	}
	
	public JSONObject getJSONObject(){
		
		return new JSONObject(this.toString());
		
	}
	
	
}
