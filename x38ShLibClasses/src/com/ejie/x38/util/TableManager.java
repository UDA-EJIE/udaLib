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
package com.ejie.x38.util;

import java.util.Hashtable;

import com.ejie.x38.log.LogConstants;

/**
 * 
 * @author UDA
 *
 */
public class TableManager {

	public static Hashtable<String, String> initTable (){
		Hashtable<String, String> table = new Hashtable<String, String>(11);
		for(String param:LogConstants.parameters){
			table.put(param, "");
		}
		return table;
	}
}