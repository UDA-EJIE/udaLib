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
package com.ejie.x38.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import com.ejie.x38.log.IncidenceLoggingAdvice;
import com.ejie.x38.log.LoggingAdvice;

/**
 * 
 * Advice principal que se encarga de gestionar toda la informacion interceptada por los PointCuts.
 * Actualmente solo se usa para el aspecto de Logging.
 * 
 * @author UDA
 * 
 */
public class MainAdvice {
		
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