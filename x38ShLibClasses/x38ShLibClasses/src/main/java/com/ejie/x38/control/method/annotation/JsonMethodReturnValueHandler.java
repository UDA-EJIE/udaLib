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
package com.ejie.x38.control.method.annotation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ejie.x38.control.bind.annotation.Json;
import com.ejie.x38.json.JsonMixin;

/**
 *
 * Clase que permite la resolución de vistas en UDA sobreescribiendo la clase de Spring UrlBasedViewResolver
 * para incluir las siguientes propiedades para el acceso a beans mediante Expression Language ${...}.
 * - exposeContextBeansAsAttributes : Permite exponer todos los beans del contexto de Spring 
 * - exposedContextBeanNames : Permite exponer los beans del contexto de Spring definidos como parámetros
 * 
 * @author UDA
 * 
 */
public class JsonMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("application", "json", DEFAULT_CHARSET);

    private boolean prefixJson = false;

    public void setPrefixJson(boolean prefixJson) {
        this.prefixJson = prefixJson;
    }

    /**
     * Converts Json.mixins() to a Map<Class, Class>
     *
     * @param jsonFilter Json annotation
     * @return Map of Target -> Mixin classes
     */
    protected Map<Class<?>, Class<?>> getMixins(Json jsonFilter) {

        Map<Class<?>, Class<?>> mixins = new HashMap<Class<?>, Class<?>>();

        if(jsonFilter != null) {
            for(JsonMixin jsonMixin : jsonFilter.mixins()) {
                mixins.put(jsonMixin.target(), jsonMixin.mixin());
            }
        }

        return mixins;
    }

    
    
    @Override
	public void handleReturnValue(Object arg0, MethodParameter methodParameter,
			ModelAndViewContainer mavContainer, NativeWebRequest nativeWebRequest) throws Exception {
		// TODO Auto-generated method stub
    	if (methodParameter.getMethod().getAnnotation(Json.class) != null ) {
    		mavContainer.setRequestHandled(true);
    		HttpOutputMessage httpRequest = this.getResponse(nativeWebRequest);
    		
    		ObjectMapper objectMapper = new ObjectMapper();
    		
    	
    		objectMapper.setMixIns(getMixins(methodParameter.getMethod().getAnnotation(Json.class)));
    		ServletOutputStream outputStream = ((ServletServerHttpResponse)httpRequest).getServletResponse().getOutputStream();
    		
    		 JsonGenerator jsonGenerator =
             objectMapper.getFactory().createGenerator(outputStream, JsonEncoding.UTF8);

           if (this.prefixJson) {
               jsonGenerator.writeRaw("{} && ");
           }

           objectMapper.writeValue(jsonGenerator, arg0);
           outputStream.flush();
           outputStream.close();

//    		this.writeInternal(objectMapper, arg0, httpRequest);
    	}
	}
    
    private void writeInternal(ObjectMapper objectMapper, Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException{
        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        try
        {
            if(prefixJson)
                jsonGenerator.writeRaw("{} && ");
            objectMapper.writeValue(jsonGenerator, object);
        }
        catch(JsonProcessingException ex)
        {
            throw new HttpMessageNotWritableException((new StringBuilder("Could not write JSON: ")).append(ex.getMessage()).toString(), ex);
        }
    }
    
    private JsonEncoding getJsonEncoding(MediaType contentType)
    {
        if(contentType != null && contentType.getCharSet() != null)
        {
            Charset charset = contentType.getCharSet();
            JsonEncoding ajsonencoding[];
            int j = (ajsonencoding = JsonEncoding.values()).length;
            for(int i = 0; i < j; i++)
            {
                JsonEncoding encoding = ajsonencoding[i];
                if(charset.name().equals(encoding.getJavaName()))
                    return encoding;
            }

        }
        return JsonEncoding.UTF8;
    }

	@Override
	public boolean supportsReturnType(MethodParameter methodParameter) {
		return methodParameter.getMethod().getAnnotation(Json.class) != null;
	}
	
	
//	private  ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest)
//    {
//        return (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
//        return new ServletServerHttpRequest(servletRequest);
//    }
	
	private HttpOutputMessage getResponse(NativeWebRequest webRequest)
    {
		HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        return new ServletServerHttpResponse(servletResponse);
    }

//	@Override
//    public ModelAndView resolveModelAndView(Method handlerMethod, Class handlerType, Object returnValue, ExtendedModelMap implicitModel, NativeWebRequest webRequest) {
//
//        if(handlerMethod.getAnnotation(Json.class) != null) {
//
//            try {
//
//                HttpServletResponse httpResponse = webRequest.getNativeResponse(HttpServletResponse.class);
//
//                httpResponse.setContentType(DEFAULT_MEDIA_TYPE.toString());
//
//                OutputStream out = httpResponse.getOutputStream();
//
//                ObjectMapper objectMapper = new ObjectMapper();
//
//                objectMapper.getSerializationConfig().setMixInAnnotations(getMixins(handlerMethod.getAnnotation(Json.class)));
//
//                JsonGenerator jsonGenerator =
//                        objectMapper.getJsonFactory().createJsonGenerator(out, JsonEncoding.UTF8);
//
//                if (this.prefixJson) {
//                    jsonGenerator.writeRaw("{} && ");
//                }
//
//                objectMapper.writeValue(jsonGenerator, returnValue);
//
//                out.flush();
//                out.close();
//
//                return null;
//
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return UNRESOLVED;
//    }

}