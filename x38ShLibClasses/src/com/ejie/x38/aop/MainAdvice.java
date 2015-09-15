package com.ejie.x38.aop;

import org.apache.log4j.Logger;

import org.aspectj.lang.ProceedingJoinPoint;

import com.ejie.x38.log.IncidenceLoggingAdvice;
import com.ejie.x38.log.LoggingAdvice;

/**
 * @author UDA
 * 
 * Advice principal que se encarga de gestionar toda la informacion interceptada por los PointCuts.
 * Actualmente solo se usa para el aspecto de Logging.
 */
public class MainAdvice {
			
		@SuppressWarnings("unused")
		private static Logger logger = Logger.getLogger("com.ejie.x38.aop.MainAdvice");
		
		private LoggingAdvice filterLoggingAdvice;
		private LoggingAdvice serviceLoggingAdvice;
		private LoggingAdvice daoLoggingAdvice;
		private IncidenceLoggingAdvice incidenceLoggingAdvice;

		/**
		 * MainAdvice que se ejecuta alrededor de llamadas a un filtro, de tal manera que loguea 
		 * la peticion y la respuesta de este.
		 * @param call Llamada interceptada.
		 * @return Retorno de la llamada interceptada.
		 * @throws Throwable Throwable Excepcion que se deja pasar, en caso de que el Target la genere.
		 */
		public Object filterCall(ProceedingJoinPoint call) throws Throwable {
			filterLoggingAdvice.preLogging(call);
			Object ret = call.proceed();	
			filterLoggingAdvice.postLogging(call, ret);			
			return ret;
		}
		
		/**
		 * MainAdvice que se ejecuta alrededor de llamadas a servicios, de tal manera que loguea 
		 * la peticion y la respuesta de estas.
		 * 
		 * @param call Llamada interceptada.
		 * @return Retorno de la llamada interceptada.
		 * @throws Throwable Excepcion que se deja pasar, en caso de que el Target la genere.
		 */
		public Object serviceLogCall(ProceedingJoinPoint call) throws Throwable {
			serviceLoggingAdvice.preLogging(call);
			Object ret = call.proceed();	
			serviceLoggingAdvice.postLogging(call, ret);
			return ret;
		}			
		
		/**
		 * MainAdvice que loguea las llamadas a los DAOs, registrando los valores de entrada y salida.
		 * 
		 * @param call Llamada interceptada.
		 * @return Retorno de la llamada interceptada.
		 * @throws Throwable Throwable Excepcion que se deja pasar, en caso de que el Target la genere.
		 */
		public Object daoLogCall(ProceedingJoinPoint call) throws Throwable {
			daoLoggingAdvice.preLogging(call);
			Object ret = call.proceed();	
			daoLoggingAdvice.postLogging(call, ret);
			return ret;
		}
		
		/**
		 * Advice que se encarga de loguear las excepciones no controladas.
		 * 
		 * @param target Clase que lanza la excepcion no controlada.
		 * @param exception Excepcion capturada.
		 */
		public void logIncidence (Object target, Exception exception){
			incidenceLoggingAdvice.logIncidence(target, exception);
		}
		
		//Getters & Setters

		public void setFilterLoggingAdvice(LoggingAdvice filterLoggingAdvice) {
			this.filterLoggingAdvice = filterLoggingAdvice;
		}

		public LoggingAdvice getServiceLoggingAdvice() {
			return serviceLoggingAdvice;
		}

		public void setServiceLoggingAdvice(LoggingAdvice serviceLoggingAdvice) {
			this.serviceLoggingAdvice = serviceLoggingAdvice;
		}

		public LoggingAdvice getDaoLoggingAdvice() {
			return daoLoggingAdvice;
		}

		public void setDaoLoggingAdvice(LoggingAdvice daoLoggingAdvice) {
			this.daoLoggingAdvice = daoLoggingAdvice;
		}

		public IncidenceLoggingAdvice getIncidenceLoggingAdvice() {
			return incidenceLoggingAdvice;
		}

		public void setIncidenceLoggingAdvice(IncidenceLoggingAdvice incidenceLoggingAdvice) {
			this.incidenceLoggingAdvice = incidenceLoggingAdvice;
		}
}