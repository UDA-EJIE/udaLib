package com.ejie.x38.security;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import n38c.exe.N38API;
import n38i.exe.N38DocumentPrinter;
import n38i.exe.N38Excepcion;
import n38i.exe.N38ParameterException;

import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.XmlManager;

public class XlnetCore {

	private static final Logger logger = Logger.getLogger(XlnetCore.class);

	public static final String PATH_SUBTIPO_N38INSTANCIA = "/n38/elementos/elemento/elemento/elemento/parametro[@id='n38uidobjseguridad']/valor";
	public static final String PATH_SUBTIPO_N38SESION = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='?']/valor";
	public static final String PATH_SUBTIPO_ORGANIZATIONALUNIT = "/n38/elementos/elemento[@subtipo='OrganizationalUnit']/parametro[@id='ou']/valor[text()='?']/../../elemento[@subtipo=\"n38itemSeguridad\"]/parametro[@id=\"n38uidobjseguridad\"]/valor";
	public static final String PATH_CHECK_ERROR = "/n38/error";
	public static final String PATH_CHECK_WARNING = "/n38/warning";

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
				logger.log(Level.DEBUG, "XmlSesion contains errors: "+xmlSesion.getTextContent());
			}else{
				logger.log(Level.DEBUG, "XmlSesion does not contain errors: "+xmlSesion.getTextContent());
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
				logger.log(Level.DEBUG, "XmlSesion contains warnings: "+xmlSesion.getTextContent());
			}else{
				logger.log(Level.DEBUG, "XmlSesion is not containing warnings: "+xmlSesion.getTextContent());
			}
		} catch (TransformerException e) {
			logger.error("isXlnetSessionContainingWarnings(): XML searching error: "+ e.getMessage());
			bResultado = true;
		}

		return bResultado;
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
	
	public static String getUidSesion(N38API n38Api){
		String[] uidSesions = null;
		try {
			uidSesions = n38Api.n38ItemSesion(N38API.NOMBRE_N38UIDSESION);
		} catch (N38ParameterException e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		}	
		if(uidSesions!=null && uidSesions.length>0){
			return uidSesions[0];
		}else{
			return null;
		}
	}

	public static String getLogin(N38API n38Api){
		String[] personaUids = null;
		try {
			personaUids = n38Api.n38ItemSesion("n38personasuid");
		} catch (N38ParameterException e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		}	
		if(personaUids!=null && personaUids.length>0){
			return personaUids[0];
		}else{
			return null;
		}
	}
	
	public static String getPuesto(N38API n38Api){
		String[] personaPuestoUids = null;
		try {
			personaPuestoUids = n38Api.n38ItemSesion(N38API.NOMBRE_N38PUESTOUID);
		} catch (N38ParameterException e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		} catch (N38Excepcion e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		}	
		if(personaPuestoUids!=null && personaPuestoUids.length>0){
			return personaPuestoUids[0];
		}else{
			return null;
		}
	}	
}