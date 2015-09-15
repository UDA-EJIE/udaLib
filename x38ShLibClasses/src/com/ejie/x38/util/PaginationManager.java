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

import com.ejie.x38.dto.Pagination;

/**
 * 
 * @author UDA
 *
 */
public class PaginationManager {
	public static String getQueryLimits(Pagination pagination , String query)
    {
		String queryAux="";
		if (pagination.getPage()!=null && pagination.getRows()!=null){
			Long paginationRows = pagination.getRows();	
			queryAux= "SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > " + (paginationRows*(pagination.getPage()-1)) +" and rnum < " +(paginationRows*(pagination.getPage())+1);
			return queryAux;
		}else{
			Long paginationRows = pagination.getRows();	
			queryAux= "SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 1 and rnum < " +(paginationRows+1);
			return queryAux;
		}
		
        
    }
}
