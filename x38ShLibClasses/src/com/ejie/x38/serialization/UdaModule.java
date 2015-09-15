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
package com.ejie.x38.serialization;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * Modulo de Jackson para extender las funcionalidades que proporciona por defecto.
 * 
 * @author UDA
 *
 */
public class UdaModule extends SimpleModule {

	/**
	 * Nombre identificador del modulo.
	 */
	private static final String NAME = "UdaModule";
	
	/**
	 * Version del modulo.
	 */
	private static final Version VERSION = new Version(1, 0, 0, null);
	
	/**
	 * Conjunto de serializadores a aplicar.
	 */
	private Map<Class<? extends Object>,JsonSerializer<Object>> serializers;
	
	/**
	 * Conjunto de deserializadores a aplicar.
	 */
	private Map<Class<Object>,JsonDeserializer<? extends Object>> deserializers;
	
	/**
	 * Propiedades de configuracion de la serializacion
	 */
	private Map<SerializationConfig.Feature,Boolean> serializationConfigFeatures;
	
	/**
	 * Propiedades de configuracion de la deserializacion
	 */
	private Map<DeserializationConfig.Feature,Boolean> deserializationConfigFeatures;
	
	/**
	 * Propiedades de inclusion de la serializacion.
	 */
	private List<JsonSerialize.Inclusion> serializationInclusions;
	
	/**
	 * Constructor por defecto.
	 */
	public UdaModule() {
		super(NAME, VERSION);
	}
	
	/**
	 * Metodo que se ejecuta despues de finalizar el contructor. Se inicializa
	 * el modulo configurando los serializadores y deserializadores indicados.
	 * Se anyade por defecto el deserializador MultiBeanDeserializer para
	 * permitir la deserializacion de multiples entidades no anidadas en la
	 * misma peticion.
	 */
	@PostConstruct
	public void initialize() {
		
		// Se registran los serializadores indicados.
		if(this.serializers!=null){
			Set<Entry<Class<? extends Object>,JsonSerializer<Object>>> serializerEntrySet = this.serializers.entrySet();
			
			for (Iterator<Entry<Class<? extends Object>, JsonSerializer<Object>>> iterator = serializerEntrySet.iterator(); iterator.hasNext();) {
				Entry<Class<? extends Object>, JsonSerializer<Object>> entry = (Entry<Class<? extends Object>, JsonSerializer<Object>>) iterator
						.next();
				this.addSerializer(entry.getKey(), entry.getValue());
			}
		}
		
		// Se registran los deserializadores indicados.
		if(this.deserializers!=null){
			Set<Entry<Class<Object>, JsonDeserializer<? extends Object>>> deserializerEntrySet = this.deserializers.entrySet();
	
			for (Iterator<Entry<Class<Object>, JsonDeserializer<? extends Object>>> iterator = deserializerEntrySet.iterator(); iterator
					.hasNext();) {
				Entry<Class<Object>, JsonDeserializer<? extends Object>> entry = (Entry<Class<Object>, JsonDeserializer<? extends Object>>) iterator
						.next();
				this.addDeserializer(entry.getKey(), entry.getValue());
			}
		}
		
		// Se registra el deserializador MultiBeanDeserializer. Este bean
		// permite la deserializacion de multiples entidades no anidadas en la
		// misma peticion.
		this.addDeserializer(Map.class, new MultiModelDeserializer());
	}
	
	/**
	 * Setter para asignar el conjunto de serializadores a aplicar.
	 * 
	 * @param serializers Conjunto de serializadores.
	 */
	public void setSerializers(Map<Class<? extends Object>,JsonSerializer<Object>> serializers) {
		this.serializers = serializers;
	}
	
	/**
	 * Setter para asignar el conjunto de deserializadores a aplicar.
	 * 
	 * @param serializers Conjunto de deserializadores.
	 */
	public void setDeserializers(Map<Class<Object>,JsonDeserializer<? extends Object>> deserializers) {
		this.deserializers = deserializers;
	}

	/**
	 * Getter para obtener las propiedades de configruacion de serializacion realizadas.
	 * 
	 * @return Map<SerializationConfig.Feature, Boolean> Mapa de propiedades de
	 *         configuracion y su estado.
	 */
	public Map<SerializationConfig.Feature, Boolean> getSerializationConfigFeatures() {
		return serializationConfigFeatures;
	}

	/**
	 * Setter para asignar las propiedades de configruacion de serializacion realizadas.
	 * 
	 * @param serializationConfigFeatures Mapa de propiedades de
	 *         configuracion y su estado.
	 */
	public void setSerializationConfigFeatures(
			Map<SerializationConfig.Feature, Boolean> serializationConfigFeatures) {
		this.serializationConfigFeatures = serializationConfigFeatures;
	}

	/**
	 * Getter para obtener las propiedades de configruacion de deserializacion
	 * realizadas.
	 * 
	 * @return Map<SerializationConfig.Feature, Boolean> Mapa de propiedades de
	 *         configuracio su estado.
	 */
	public Map<DeserializationConfig.Feature, Boolean> getDeserializationConfigFeatures() {
		return deserializationConfigFeatures;
	}

	/**
	 * Setter para asignar las propiedades de configruacion de deserializacion
	 * realizadas.
	 * 
	 * @param deserializationConfigFeatures
	 *            Mapa de propiedades de configuracion y su estado.
	 */
	public void setDeserializationConfigFeatures(
			Map<DeserializationConfig.Feature, Boolean> deserializationConfigFeatures) {
		this.deserializationConfigFeatures = deserializationConfigFeatures;
	}

	/**
	 * Getter para obtener las inclusiones de serialización de Jackson.
	 * 
	 * @return List<JsonSerialize.Inclusion> Inclusiones de serialización de
	 *         Jackson.
	 */
	public List<JsonSerialize.Inclusion> getSerializationInclusions() {
		return serializationInclusions;
	}

	/**
	 * Getter para asignar las inclusiones de serialización de Jackson.
	 * 
	 * @param serializationInclusions
	 *            Inclusiones de serialización de Jackson.
	 */
	public void setSerializationInclusions(
			List<JsonSerialize.Inclusion> serializationInclusions) {
		this.serializationInclusions = serializationInclusions;
	}
	
	
}
