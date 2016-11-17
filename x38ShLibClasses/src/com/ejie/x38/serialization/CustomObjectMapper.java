/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, VersiÃ³n 1.1 exclusivamente (la Â«LicenciaÂ»);
* Solo podrÃ¡ usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislaciÃ³n aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye Â«TAL CUALÂ»,
* SIN GARANTÃ�AS NI CONDICIONES DE NINGÃšN TIPO, ni expresas ni implÃ­citas.
* VÃ©ase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.serialization;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

/**
 * Object mapper propio de UDA en el que se realiza la configuraciÃ³n necesaria.
 * 
 * @author UDA
 *
 */
@Deprecated
public class CustomObjectMapper extends ObjectMapper{

	protected final Logger logger =  LoggerFactory.getLogger(CustomObjectMapper.class);

	public void setCustomSerializerFactory(SerializerFactory factory) {
		setSerializerFactory(factory);
		logger.info("Using [" + factory + "] as the custom Jackson JSON serializer factory.");
	}
}