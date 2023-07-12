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

import java.text.SimpleDateFormat;

/**
 * 
 * @author UDA
 *
 */
public class Constants {

	public static final String EUSKARA = "eu";
	public static final String FRANCAIS = "fr";
	public static final String CASTELLANO = "es";
	public static final String ENGLISH = "en";
	
	public static final String X38_VERSION = "4.5.0-RELEASE";
	
	public static final String PROPS_MOMO_SERVICIO = "momo.servicio";
	public static final String PROPS_MOMO_SALIDA_ESTANDAR = "momo.doMomo.salidaEstandar";
	public static final String PROPS_MOMO_PUERTO_ENDPOINT = "momo.w43taEndpointPort";
	public static final String PROPS_MOMO_URI_ENDPOINT ="momo.w43taEndpointUri";
	
	public static final String MOMO_APP = "x38";
	public static final String MOMO_SEC_TOKEN = "PIB-3123456786-0";
	
	public static final String MOMO_LABEL_URL = "url_lt";
	public static final String MOMO_LABEL_COD_APP = "codApp_lt";
	public static final String MOMO_LABEL_VERSION = "versionRup_lt";
	public static final String MOMO_LABEL_COMPONENTE = "nombreComponente_lt";
	public static final String MOMO_LABEL_AUDITING = "auditing_lt";
	public static final String MOMO_LABEL_TIMESTAMP = "timestamp_ts";
	public static final String MOMO_LABEL_SERVICIO = "servicio_lt";
	
	public static final SimpleDateFormat DDMMYYYY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat YYYYMMDD_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	
	public static final SimpleDateFormat DDMMYYYY_HHMMSS_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	public static final SimpleDateFormat YYYYMMDD_HHMMSS_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static final SimpleDateFormat HHMMSS_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	
	//security
	public static final String dataNotApplicable = "N/A";
}