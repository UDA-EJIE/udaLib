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
package com.ejie.x38.log;

//##|Fecha y hora         ~~ Sesion XLNetS ~~ IP           ~~ Usuario ~~ Puesto  ~~ Codigo de aplicacion ~~ Instancia Weblogic       ~~ Subsistema funcional    ~~ Criticidad ~~ Mensaje                     ~~ Información adicional |##
//##|2010/06/22 09:21:52  ~~ 1268767190369 ~~ 10.170.9.246 ~~ SOPDESA ~~ SOPDESA ~~ s73b                 ~~ intra_apps_dpto2_apps112 ~~ Subsistema Presentacion ~~ DEBUG      ~~ Mensaje de traza de ejemplo ~~ xxx                   |##

/**
 * 
 * @author UDA
 *
 */
public class LogConstants {

	protected static final String INITSEPARATOR = "##| ";
	protected static final String ENDSEPARATOR = " |##";
	protected static final String FIELDSEPARATOR = " ~~ ";
	protected static final String EMPTYFIELD = " ";
	
	protected static final String DATETIME = "datetime";
	protected static final String IPADDRESS = "ipAddress";
	protected static final String APPCODE = "appCode";
	protected static final String SERVERINSTANCE = "serverInstance";
	protected static final String FUNCTIONALSUBSYSTEM = "functionalSubsystem";
	protected static final String INTERFUNCTIONALSUBSYSTEM = "interFunctionalSubsystem";
	protected static final String LOGGERCLASS = "loggerClass";
	protected static final String THREAD = "thread";
	protected static final String CRITICALITY = "criticality";
	protected static final String MESSAGE = "message";
	protected static final String ADITIONALINFO = "aditionalInfo";
	protected static final String INTERADITIONALINFO = "interAditionalInfo";
	
	public static final String NOINTERNALACCES = "noInternalAcces";
	public static final String ACCESSTYPEHTTP = "httpAcces";
	public static final String ACCESSTYPEEJB = "ejbAcces";
	
	public static final String USER = "user";
	public static final String POSITION = "position";
	public static final String SESSION = "session";
	public static final String[] parameters = {DATETIME,SESSION,IPADDRESS,USER,POSITION,APPCODE,SERVERINSTANCE,FUNCTIONALSUBSYSTEM,THREAD,LOGGERCLASS,CRITICALITY,MESSAGE,ADITIONALINFO};
	
	public static final String DATETIMEFORMAT = "yyyy/MM/dd HH:mm:ss:SSS";
	
	protected static final String DATASUBSYSTEM = "Data Subsystem";
	protected static final String LOGICSUBSYSTEM = "Logic Subsystem";
	protected static final String WEBSUBSYSTEM = "Web Subsystem";
	protected static final String INCIDENCESUBSYSTEM = "Incidence Subsystem";
	protected static final String TRACESUBSYSTEM = "Trace Subsystem";
}