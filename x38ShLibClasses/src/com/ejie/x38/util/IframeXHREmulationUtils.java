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
package com.ejie.x38.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Clase de utilidades para realizar la emulación de llamadas XHR utilizando iframes.
 * 
 * @author UDA
 *
 */
public class IframeXHREmulationUtils {

	public static final String PARAM_EMULATE_IFRAME_HTTP_STATUS = "_emulate_iframe_http_status";
	
	/**
	 * Comprueba si es necesario realizar la emulación. La emulación se indica
	 * mediante la presencia en la request del parámetro
	 * PARAM_EMULATE_IFRAME_HTTP_STATUS.
	 * 
	 * @param request
	 *            Petición HTTP.
	 * @return <b>true</b> o <b>false</b> dependiendo si es necesaria o no
	 *         realizar la emulación.
	 */
	public static boolean isIframeEmulationRequired(ServletRequest request){
		
		String emulate_iframe_http_status = request.getParameter(IframeXHREmulationUtils.PARAM_EMULATE_IFRAME_HTTP_STATUS);
		
		if (emulate_iframe_http_status!=null && Boolean.TRUE.toString().equals(emulate_iframe_http_status.toLowerCase())){
			return true;
		}
		
		return false;
	}
	
	/**
	 * @see IframeXHREmulationUtils.writeIframeHttpStatus
	 */
	public static void writeIframeHttpStatus(HttpServletResponse response, byte[] data, int httpStatusCode) throws IOException{
		IframeXHREmulationUtils.writeIframeHttpStatus(response, data, httpStatusCode, null);
	}
	
	/**
	 * Escribe en la respuesta de la petición el mensaje correspondiente al
	 * contenido y el código de estado http indicados.</br> El contenido enviado
	 * en la request para permitir la emulación se arropa dentro de un textárea
	 * en cuyos atributos <i>status</i> y <i>statusText</i> se incluye la
	 * información de los errores http. La estructura enviada es la siguiente:
	 * 
	 * <pre>
	 * 
	 *  <textarea status="406" statusText="NotAcceptable">
	 *  	["Contenido de la respuesta en formato json"]
	 *  </textarea>
	 * 
	 * </pre>
	 * 
	 * @param response
	 *            Respuesta HTTP.
	 * @param data
	 *            Información que debe ser incluida en la respuesta.
	 * @param httpStatusCode
	 *            Código de error de estado HTTP.
	 * @param httpStatusCodeText
	 *            Textp del código de error de estado HTTP.
	 * @throws IOException
	 *             Excepción producida en operaciones I/O.
	 */
	public static void writeIframeHttpStatus(HttpServletResponse response, byte[] data, int httpStatusCode, String httpStatusCodeText) throws IOException{
		
		ServletOutputStream outputStream = response.getOutputStream();

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
       
		outputStream.write("<textarea ".getBytes());
		outputStream.write("status=\"".getBytes());
		outputStream.write(String.valueOf(httpStatusCode).getBytes());
		outputStream.write("\" ".getBytes());
		outputStream.write("statusText=\"".getBytes());
		if (httpStatusCodeText!=null){
			outputStream.write(httpStatusCodeText.getBytes());
		}
		outputStream.write("\">".getBytes());
		outputStream.write(data);
		outputStream.write("</textarea>".getBytes());
		outputStream.flush();
		outputStream.close();
		
		response.flushBuffer();
	}
}
