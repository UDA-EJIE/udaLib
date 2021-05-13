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
package com.ejie.x38.security;

import java.util.HashMap;
import java.util.Vector;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.XmlManager;

import n38c.exe.N38API;
import n38i.exe.N38DocumentPrinter;
import n38i.exe.N38Excepcion;
import n38i.exe.N38ParameterException;

/**
 * 
 * @author UDA
 *
 */
public class XlnetCore {

	private static final Logger logger =  LoggerFactory.getLogger(XlnetCore.class);

	public static final String PATH_SUBTIPO_N38INSTANCIA = "/n38/elementos/elemento/elemento/elemento/parametro[@id='n38uidobjseguridad']/valor";
	public static final String PATH_SUBTIPO_N38SESION = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='?']/valor";
	public static final String PATH_SUBTIPO_N38DOMINIOCOMUNCOOKIE = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='n38dominiocomuncookie']/valor";
	public static final String PATH_SUBTIPO_N38SUBJECTCERT = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='n38subjectcert']/valor";
	public static final String PATH_SUBTIPO_DNI = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='dni']/valor";
	public static final String PATH_SUBTIPO_N38PERSONAUID = "n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='n38personauid']/valor";
	public static final String PATH_SUBTIPO_ORGANIZATIONALUNIT = "/n38/elementos/elemento[@subtipo='OrganizationalUnit']/parametro[@id='ou']/valor[text()='?']/../../elemento[@subtipo=\"n38itemSeguridad\"]/parametro[@id=\"n38uidobjseguridad\"]/valor";
	public static final String PATH_CHECK_ERROR = "/n38/error";
	public static final String PATH_CHECK_WARNING = "/n38/warning";
	public static final String FILTRO_LDAP_UID = "uid=";
	public static final String PATH_PUESTOUID_SUBTIPO_SN = "/n38/elementos/elemento[@subtipo='n38persona']/parametro[@id='sn']/valor";
	public static final String PATH_PUESTOUID_SUBTIPO_CN = "/n38/elementos/elemento[@subtipo='n38persona']/parametro[@id='cn']/valor";
	public static final String PATH_PUESTOUID_SUBTIPO_GIVENNAME = "/n38/elementos/elemento[@subtipo='n38persona']/parametro[@id='givenname']/valor";
	public static final String PATH_XMLSESION_N38PERFILES = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='n38perfiles']/valor";

	/**
	 * Devuelve un objeto N38API a partir del contexto de una petición Request.
	 * Si la request es inválida se propaga una excepción.
	 * 
	 * @param httpRequest
	 *            La request que lleva la validación XLNET.
	 * @return Un objeto N38API con información sobre la sesión XLNET.
	 */
	public static N38API getN38API(HttpServletRequest httpRequest) {
		N38API n38apiRetorno = null;
		if (httpRequest == null)
			throw new IllegalArgumentException(
					"getN38API(): The HttpServletRequest input parameter can't be NULL.");
		n38apiRetorno = new N38API(httpRequest);
		return n38apiRetorno;
	}

	public static Document getN38ItemSesion(N38API n38api) {
		Document documentReturn = null;
		if (n38api == null)
			throw new IllegalArgumentException(
					"getN38ItemSesion(): The N38API input parameter can't be NULL.");

		documentReturn = n38api.n38ItemSesion();
		if (documentReturn != null) {
			logger.trace("N38ItemSesion is: "+ N38DocumentPrinter.print(documentReturn));
		}

		return documentReturn;
	}
	
	public static String getN38ItemSesion(N38API n38api, String parametro) {
		if (n38api == null)
			throw new IllegalArgumentException(
					"getN38ItemSesion(): The N38API input parameter can't be NULL.");
		
		try {
			String[] n38UidSesion = n38api.n38ItemSesion(parametro);
			if (n38UidSesion != null && n38UidSesion.length > 0) {
				logger.trace("N38ItemSesion is: "+ n38UidSesion[0]);
				return n38UidSesion[0];
			}
		} catch (N38ParameterException e) {
			logger.error(StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.error(StackTraceManager.getStackTrace(e));
		}
		
		return null;
	}

	public static Document getN38ItemSeguridad(N38API n38api, String idItemSeguridad) {
		if (n38api != null) logger.trace("N38API is: "+n38api);
		logger.trace("idItemSeguridad is: "+idItemSeguridad);
		
		Document documentReturn = null;
		if (n38api == null)
			throw new IllegalArgumentException(
					"getN38ItemSeguridad(): The N38API input parameter can't be NULL.");

		documentReturn = n38api.n38ItemSeguridad(idItemSeguridad);

		if (documentReturn != null) {
			logger.trace("N38ItemSeguridad is: "+ N38DocumentPrinter.print(documentReturn));
		}

		return documentReturn;
	}

	public static boolean isXlnetSessionContainingErrors(Document xmlSesion) {
		if (xmlSesion != null)
			logger.trace("XmlSesion is: "+xmlSesion.getTextContent());

		if (xmlSesion == null)
			throw new IllegalArgumentException(
					"isXlnetSessionContainingErrors(): The Document input parameter can't be NULL.");

		boolean bResultado = false;
		try {
			if (XmlManager.searchDomNode(xmlSesion, PATH_CHECK_ERROR) != null) {
				bResultado = true;
				logger.debug("XmlSesion contains errors: "+xmlSesion.getTextContent());
			}else{
				logger.debug("XmlSesion does not contain errors: "+xmlSesion.getTextContent());
			}
		} catch (TransformerException e) {
			logger.error("isXlnetSessionContainingErrors(): XML searching error: "+ StackTraceManager.getStackTrace(e));
			bResultado = true;
		}
		return bResultado;
	}

	public static boolean isXlnetSessionContainingWarnings(Document xmlSesion) {
		if (xmlSesion == null)
			throw new IllegalArgumentException(
					"isXlnetSessionContainingErrors(): The Document input parameter can't be NULL.");

		boolean bResultado = false;
		try {
			if (XmlManager.searchDomNode(xmlSesion, PATH_CHECK_WARNING) != null) {
				bResultado = true;
				logger.debug("XmlSesion contains warnings: "+xmlSesion.getTextContent());
			}else{
				logger.debug("XmlSesion is not containing warnings: "+xmlSesion.getTextContent());
			}
		} catch (TransformerException e) {
			logger.error("isXlnetSessionContainingWarnings(): XML searching error: "+ e.getMessage());
			bResultado = true;
		}

		return bResultado;
	}
	
	public static String getN38DominioComunCookie(Document xmlSesion) {
		
		Node n38DominioComunCookieNode; 
		
		if (xmlSesion != null)
			logger.trace("XmlSesion is: "+xmlSesion.getTextContent());

		if (xmlSesion == null)
			throw new IllegalArgumentException(
					"isXlnetSessionContainingErrors(): The Document input parameter can't be NULL.");

		try {
			
			n38DominioComunCookieNode = XmlManager.searchDomNode(xmlSesion, PATH_SUBTIPO_N38DOMINIOCOMUNCOOKIE);
			return n38DominioComunCookieNode.getFirstChild().getNodeValue();
			
		} catch (TransformerException e) {
			logger.error("isXlnetSessionContainingErrors(): XML searching error: "+ StackTraceManager.getStackTrace(e));
			return null; 
		}
	}
	
	/**
	 * Método para tratar el atributo n38SubjectCert del xml de sesión
	 * Cuando la autenticación se hace con certificado el atributo se informa con el subject del mismo
	 * Si la autenticación se hace con juego de barcos de izenpe -no hay certificado- 
	 * el atributo tiene un literal informativo   
	 * 
	 *  @return Mapa de los atributos del certificado. Si no hay, el CN se informa con DNI de sesión o literal informativo
	 *  
	 * */
	public static HashMap<String, String> getN38SubjectCert(Document xmlSesion) {

		HashMap<String, String> certinfo = null;
		String n38SubjectCert;
		String dni;

		if (xmlSesion != null) {
			logger.trace("XmlSesion is: " + xmlSesion.getTextContent());
		} else {
			throw new IllegalArgumentException(
					"isXlnetSessionContainingErrors(): The Document input parameter can't be NULL.");
		}

		certinfo = new HashMap<String, String>();
		try {
			n38SubjectCert = (XmlManager.searchDomNode(xmlSesion, PATH_SUBTIPO_N38SUBJECTCERT)).getFirstChild()
					.getNodeValue();
			dni = (XmlManager.searchDomNode(xmlSesion, XlnetCore.PATH_SUBTIPO_DNI)).getFirstChild().getNodeValue();
		} catch (TransformerException transfEx) {
			logger.error("getN38SubjectCert(): XML searching error: " + StackTraceManager.getStackTrace(transfEx));
			return null;
		}

		try {
			LdapName ln;
			ln = new LdapName(n38SubjectCert);
			for (Rdn rdn : ln.getRdns()) {
				certinfo.put(rdn.getType(), rdn.getValue().toString());
			}
		} catch (InvalidNameException e) {
			if (dni != null) {
				certinfo.put("CN", dni);
			} else {
				certinfo.put("CN", n38SubjectCert);
			}

		} catch (Exception e) {
			logger.error("getN38SubjectCert(): XML Read and Parser error: " + StackTraceManager.getStackTrace(e));
			return null;
		}
		return certinfo;
	}

	public static Vector<String> searchParameterIntoXlnetSesion(Document xmlSesion, String searchUrl) {
		if (xmlSesion == null)
			throw new IllegalArgumentException(
					"isXlnetSessionContainingErrors(): The Document input parameter can't be NULL.");

		if (searchUrl == null || searchUrl.equals(""))
			throw new IllegalArgumentException(
					"isXlnetSessionContainingErrors(): The String searchUrl input parameter can't be NULL.");

		Vector<String> resultVector = null;

		try {
			resultVector = XmlManager.searchDomVector(xmlSesion, searchUrl);
		} catch (TransformerException e) {
			logger.error("Could not find ["+searchUrl+"] in Document ["+xmlSesion.getTextContent()+"]. Error is: "+ e.getMessage());
			resultVector = null;
		}

		if (resultVector != null) logger.trace("Search ["+searchUrl+"] in Document ["+xmlSesion.getTextContent()+"] obtained results: ["+ resultVector.toString() + "]");
		else logger.trace("Search ["+searchUrl+"] in Document ["+xmlSesion.getTextContent()+"] obtained NO results!");

		return resultVector;
	}
	
	public static String getParameterSession(N38API n38Api, String param){
		String[] result = null;
		
		if (n38Api == null)
			throw new IllegalArgumentException(
					"getN38ItemSeguridad(): The N38API input parameter can't be NULL.");
		
		try {
			result = n38Api.n38ItemSesion(param);
		} catch (N38ParameterException e) {
			logger.error(StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.error(StackTraceManager.getStackTrace(e));
		}	
		if(result!=null && result.length>0){
			return result[0];
		}else{
			return null;
		}
	}
	
	public static HashMap<String, String> getUserDataInfo(N38API n38Api){
		HashMap<String, String> result = new HashMap<String, String>();
		
		String n38personauidString;
		Document xmlPersona;
		Document n38ItemSesion;
		
		if (n38Api == null)
			throw new IllegalArgumentException(
					"getN38ItemSeguridad(): The N38API input parameter can't be NULL.");
		
		n38ItemSesion = n38Api.n38ItemSesion();
		
		try {
			n38personauidString = XmlManager.searchDomNode(n38ItemSesion, XlnetCore.PATH_SUBTIPO_N38PERSONAUID).getFirstChild().getNodeValue();
			
		} catch (TransformerException e) {
			logger.error("isXlnetSessionContainingErrors(): XML searching error: "+ StackTraceManager.getStackTrace(e));
			return null; 
		}	
		
		xmlPersona = n38Api.n38ItemObtenerPersonas(FILTRO_LDAP_UID+n38personauidString);
		
		try {
			result.put("name",XmlManager.searchDomNode(xmlPersona, PATH_PUESTOUID_SUBTIPO_GIVENNAME).getFirstChild().getNodeValue());
			result.put("surname",XmlManager.searchDomNode(xmlPersona, PATH_PUESTOUID_SUBTIPO_SN).getFirstChild().getNodeValue());
			result.put("fullName", XmlManager.searchDomNode(xmlPersona, PATH_PUESTOUID_SUBTIPO_CN).getFirstChild().getNodeValue()); 
			
		} catch (TransformerException e) {
			logger.error("isXlnetSessionContainingErrors(): XML searching error: "+ StackTraceManager.getStackTrace(e));
			return null; 
		}			

		return result;
	}
	
	@Deprecated
	public static String getUidSesion(N38API n38Api){
		String[] uidSesions = null;
		try {
			uidSesions = n38Api.n38ItemSesion(N38API.NOMBRE_N38UIDSESION);
		} catch (N38ParameterException e) {
			logger.error(StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.error(StackTraceManager.getStackTrace(e));
		}	
		if(uidSesions!=null && uidSesions.length>0){
			return uidSesions[0];
		}else{
			return null;
		}
	}
	
	@Deprecated
	public static String getLogin(N38API n38Api){
		String[] personaUids = null;
		try {
			personaUids = n38Api.n38ItemSesion("n38personasuid");
		} catch (N38ParameterException e) {
			logger.error(StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.error(StackTraceManager.getStackTrace(e));
		}	
		if(personaUids!=null && personaUids.length>0){
			return personaUids[0];
		}else{
			return null;
		}
	}
	
	@Deprecated
	public static String getPuesto(N38API n38Api){
		String[] personaPuestoUids = null;
		try {
			personaPuestoUids = n38Api.n38ItemSesion(N38API.NOMBRE_N38PUESTOUID);
		} catch (N38ParameterException e) {
			logger.error(StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.error(StackTraceManager.getStackTrace(e));
		}	
		if(personaPuestoUids!=null && personaPuestoUids.length>0){
			return personaPuestoUids[0];
		}else{
			return null;
		}
	}	
	
}