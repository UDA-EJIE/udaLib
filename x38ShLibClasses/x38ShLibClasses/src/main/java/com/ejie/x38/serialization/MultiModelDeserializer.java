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
package com.ejie.x38.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.util.StackTraceManager;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

/**
 * Deserializador de Jackson que permite la deserializacion de multiples
 * entidades enviadas en un mismo JSON.<br>
 * 
 * El deserializador recibe un JSON que contiene la representacion de las
 * entidades que se deben deserializar junto a una propiedad que contiene la
 * informacion necesaria para llevar a cabo el mapeo. <br>
 * 
 * Esta informacion consiste en la correlacion entre las propiedades del JSON y
 * de las clases Java. El nombre de esta propiedad sera rupEntityMapping.<br>
 * 
 * Este es un ejemplo de un JSON que contiene varias entidades:<br>
 * <br>
 * 
 * <pre>
 * 
 *  {
 * 	usuario:{
 *  		id:"1",nombre:"nombre_usuario",apellido1:"apellido_usuario"
 * 	},
 * 	localidad:{
 * 		code:"1",descEs:"descripcion_cast",descEu:"descripcion_eusk"
 * 	},
 * 	rupEntityMapping:{
 * 		"usuario":"com.ejie.x21a.model.Usuario",
 * 		"localidad":"com.ejie.x21a.model.Localidad"
 *  }
 * }
 * 
 * </pre>
 * 
 * @author UDA
 * 
 */
public class MultiModelDeserializer extends
		JsonDeserializer<Map<String, Object>> {

	protected final Logger logger = LoggerFactory
			.getLogger(MultiModelDeserializer.class);

	/**
	 * Realiza la deserializacion del JSON
	 * 
	 * @param jsonParser
	 *            JsonParser que contiene el objeto JSON a deserializar y
	 *            proporciona los metodos necesarios para su tratamiento.
	 * @param deserializationContext
	 *            DeserializationContext.
	 * @return Map<String, Object> Mapa que contiene los model deserializados.
	 * @throws IOException
	 *             Al producirse un error en el acceso a los datos del JSON.
	 * @throws JsonProcessingException
	 *             Al producirse un error al procesar el JSON.
	 */
	@Override
	public Map<String, Object> deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException {

		// Creamos una JonFactory que nos permitira el procesado de los objetos JSON 
		JsonFactory jsonFactory = new MappingJsonFactory();
		// Mapa de retorno en el que se van a almacenar los beans
		Map<String, Object> mapaRetorno = new HashMap<String, Object>();
		// Se obtiene una representacion en arbol del json a procesar  
		JsonNode jsonNodeTree = jsonParser.readValueAsTree();
		// Recuperamos el objeto rupMultiModelMappings que define el mapeo de las entidades del json
		JsonNode rupMultiModelMappingsNode = jsonNodeTree.get("rupEntityMapping");
		// Recorremos todas las propiedades del json
		Iterator<Entry<String, JsonNode>> fields = jsonNodeTree.fields();
		while(fields.hasNext()){
			// Procesamos cada propiedad del json
			Entry<String, JsonNode> next = fields.next();
			// Recuperamos el nombre de la propiedad
			String propertyName = next.getKey();
			// Comprobamos que la propiedad corresponde con una de las entidades para las que se ha definido un mapeo
			if (rupMultiModelMappingsNode != null && rupMultiModelMappingsNode.has(propertyName)){
				// Obtenemos el nombre de la clase Java a la que se debe de mapear la propiedad
				String beanType = rupMultiModelMappingsNode.get(propertyName).asText();
				// Creamos un nuevo jsonParser para procesar el json correspondiente a la entidad que se debe mapear
				try (JsonParser entityJsonParser = jsonFactory.createParser(next.getValue().toString());) {
					// Se procesa el objeto json y se obtiene la instancia de la entidad correspondiente.
					Object	obj = entityJsonParser.readValueAs(Class.forName(beanType));
					// Se anyade la entidad en el mapa de retorno
					mapaRetorno.put(propertyName, obj);
				} catch (ClassNotFoundException cnfe) {
					// En caso de producirse un error en el procesado del json se lanza una excepcion
					logger.error(StackTraceManager.getStackTrace(cnfe));
				}
				
			}
		}
		// Se retorna el mapa de retorno que contiene las entidades mapeadas a partir del json procesado.
		return mapaRetorno;
	}
}