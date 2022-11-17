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

/**
 * Serializador que permite serializar unicamente determinadas propiedades del objeto a procesar. Las propiedades a serializar se
 * especifican enviandose en un mapa de propiedades del thread.
 * 
 * @author UDA
 * 
 */
public class CustomizedCustomSerializer extends CustomSerializer {

	public CustomizedCustomSerializer() {
		setDelegatedSerializer(new EjieSecureModule.SecureIdSerializer());
	}
}