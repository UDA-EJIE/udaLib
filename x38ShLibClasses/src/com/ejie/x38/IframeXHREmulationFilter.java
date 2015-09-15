/*
* Copyright 2012 E.J.I.E., S.A.
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
package com.ejie.x38;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.filter.DelegatingFilterProxy;

import com.ejie.x38.util.IframeXHREmulationUtils;

/**
 * Filtro encargado de modificar la request en caso de que sea necesaria emular
 * el comportamiento de la gestión de errores HTTP al utilizar iframes.
 * 
 * @author UDA
 * 
 */
public class IframeXHREmulationFilter extends DelegatingFilterProxy  {


	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		
		// Se comprueba si es necesario realizar la emulación.
		if (IframeXHREmulationUtils.isIframeEmulationRequired(request)){
				
			HttpServletResponse httpServletResponse = (HttpServletResponse)response;
			// Se genera un wrapper de la response para poder insertar la respuesta indicada en la response dentro de la estructura necesaria.
			IframeXHREmulationFilter.GenericResponseWrapper wrapper = new IframeXHREmulationFilter.GenericResponseWrapper(httpServletResponse); 
			// Se continúa con la ejecución de la petición.
			filterChain.doFilter(request, wrapper);
			// Se escribe la respuesta correspondiente en la response.
			IframeXHREmulationUtils.writeIframeHttpStatus(httpServletResponse, wrapper.getData(), wrapper.getStatus());
			
		}else{
			// En caso de no ser necesaria la emulación se continua con la ejecución.
			filterChain.doFilter(request, response);
		}
	}
	
	/**
	 * Wrapper de la response http para permitir su modificación en el proceso
	 * de emular la gestión de errores, del mismo modo que se realiza en
	 * peticiones XHR, en las realizadas mediante iframes.
	 * 
	 * @author UDA
	 * 
	 */
	private class GenericResponseWrapper extends HttpServletResponseWrapper {

		/**
		 * OutputStream utilizado para almacenar el contenido que se va a
		 * incluir en la request.
		 */
		private ByteArrayOutputStream output;
		/**
		 * ContentType de la request
		 */
		private String contentType;
		/**
		 * Coódigo de error HTTP que se debe de incluir en el contenido de la respuesta final.
		 */
		private int httpStatus;

		/**
		 * Constructor. Recibe como parámetro la response que se va a arropar.
		 * 
		 * @param response
		 *            Response http.
		 */
		public GenericResponseWrapper(HttpServletResponse response) {
			super(response);
			output = new ByteArrayOutputStream();
		}

		/**
		 * Devuelve el contenido de la response escrito hasta el momento.
		 * 
		 * @return Representación mediante un array de bytes del contenido de la
		 *         response.
		 */
		public byte[] getData() {
			return output.toByteArray();
		}

		/**
		 * Devuelve un outputStream para poder escribir el contenido de la
		 * response.
		 */
		public ServletOutputStream getOutputStream() {
			return new FilterServletOutputStream(output);
		}

		/**
		 * Devuelve un PrintWriter para poder escribir el contenido de la
		 * response.
		 */
		public PrintWriter getWriter() {
			return new PrintWriter(getOutputStream(), true);
		}		
		
		@Override
		public void setContentType(String type) {
			this.contentType = type;
			super.setContentType(type);
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public void sendError(int sc) throws IOException {
			httpStatus = sc;
			super.sendError(sc);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			httpStatus = sc;
			super.sendError(sc, msg);
		}

		@Override
		public void setStatus(int sc) {
			httpStatus = sc;
		}
		
		/**
		 * Getter de la propiedad httpStatus.
		 * 
		 * @return Código de estado http.
		 */
		public int getStatus() {
			return httpStatus;
		}

	}
	
	/**
	 * OutputStream utilizado en el wrapper de la request utilizado en la emulación.
	 * 
	 * @author UDA
	 *
	 */
	private class FilterServletOutputStream extends ServletOutputStream {
		
		/**
		 * DataOutputStream 
		 */
		private DataOutputStream stream;

		/**
		 * Constructor. Genera un nuevo OutputStream a partir del indicado como
		 * parámetro
		 * 
		 * @param output
		 *            OutputStream utilizado para inicializar el nuevo.
		 */
		public FilterServletOutputStream(OutputStream output) {
			stream = new DataOutputStream(output);
		}
		
		@Override
		public void write(int b) throws IOException {
			stream.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			stream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			stream.write(b, off, len);
		}
	}
	
}
