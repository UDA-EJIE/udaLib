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

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author UDA
 *
 */
public class XmlManager {

	private static final Logger logger = LoggerFactory.getLogger(XmlManager.class);

	private static final XPath xPath = XPathFactory.newInstance().newXPath();
	
	private static final String DISALLOW_INLINE_DTD = "http://apache.org/xml/features/disallow-doctype-decl";
	private static final String DISALLOW_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
	private static final String DISALLOW_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
	private static final String DISALLOW_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
	private static final String PARSER_CONFIGURATION_EXCEPTION_MESSAGE = "ParserConfigurationException was thrown. The feature '{}' is not supported by your XML processor.";
	
	private static DocumentBuilderFactory securizeFactory(DocumentBuilderFactory dbFactory) {
		// ATENCIÓN: cada llamada a setFeature() debe estar en su propio try/catch, de lo contrario, las llamadas subsecuentes son omitidas.
		try {
			// Esta es la defensa principal. Si se desautorizan los DTD (DOCTYPE), se evitan casi todos los ataques a entidades XML.
		    // https://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl (sólo Xerces 2)
			dbFactory.setFeature(DISALLOW_INLINE_DTD, true);
		} catch (ParserConfigurationException e) {
		    logger.info(PARSER_CONFIGURATION_EXCEPTION_MESSAGE, DISALLOW_INLINE_DTD);
		}
		
		try {
			// No incluye DTD externos.
		    // https://xerces.apache.org/xerces-j/features.html#load-external-dtd
			dbFactory.setFeature(DISALLOW_EXTERNAL_DTD, false);
		} catch (ParserConfigurationException e) {
		    logger.info(PARSER_CONFIGURATION_EXCEPTION_MESSAGE, DISALLOW_EXTERNAL_DTD);
		}
		
		try {
			// No incluye entidades externas.
		    // https://xerces.apache.org/xerces2-j/features.html#external-general-entities
			dbFactory.setFeature(DISALLOW_EXTERNAL_GENERAL_ENTITIES, false);
		} catch (ParserConfigurationException e) {
		    logger.info(PARSER_CONFIGURATION_EXCEPTION_MESSAGE, DISALLOW_EXTERNAL_GENERAL_ENTITIES);
		}
		
		try {
			// No incluye parámetros externos.
		    // https://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
			dbFactory.setFeature(DISALLOW_EXTERNAL_PARAMETER_ENTITIES, false);
		} catch (ParserConfigurationException e) {
		    logger.info(PARSER_CONFIGURATION_EXCEPTION_MESSAGE, DISALLOW_EXTERNAL_PARAMETER_ENTITIES);
		}
		
		try {
			// Incluído por recomendación del artículo de Timothy Morgan de 2014: "XML Schema, DTD, and Entity Attacks".
		    dbFactory.setXIncludeAware(false);
		} catch (UnsupportedOperationException e) {
			logger.info("Call to method setXIncludeAware() not available");
		}
		
		return dbFactory;
	}

	/**
	 * Función que devuelve el documento a procesar.
	 * 
	 * @param String codigo de error de N38
	 * @return Document Documento de error que incluye una descripción del problema
	 * @throws NullPointerException
	 */
	public static Document getErrorsDocument(String codigo) throws NullPointerException {
    	File xlnetsErrorMessageFile = new File(StaticsContainer.getN38ErrorMessagesPath() + codigo + "ERROR.xml");
    	
    	DocumentBuilderFactory dbFactory = securizeFactory(DocumentBuilderFactory.newInstance());
    	
		Document doc = null;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xlnetsErrorMessageFile);
		    doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// No se puede crear un DocumentBuilder con la configuración solicitada.
		    logger.info(DISALLOW_EXTERNAL_PARAMETER_ENTITIES, DISALLOW_EXTERNAL_PARAMETER_ENTITIES);
		} catch (SAXException e) {
		    // En Apache, esto debería ser lanzado al no permitir DOCTYPE.
		    logger.warn("A DOCTYPE was passed into the XML document");
		} catch (IOException e) {
		    // Fichero inexistente. Todavía es posible un ataque por XXE.
		    logger.debug("N38 {} error XML not found", codigo);
		}
	    
	    return doc;
	}

	/**
	 * Función que devuelve un Nodo dado un XPath.
	 * 
	 * @param Node XML
	 * @param Path XPath
	 * @return Node Nodo seleccionado con el Path
	 * @throws TransformerException
	 */
	public static Node searchDomNode(Node docDom, String strPath) throws TransformerException {
		if (docDom != null && docDom.getTextContent() != null) {
			logger.debug("Searching node [{}] in document {}", strPath, docDom.getTextContent());
		} else {
			logger.debug("Can't find node [{}] in empty document", strPath);
		}

		try {
			return (Node) xPath.evaluate(strPath, docDom, XPathConstants.NODE);
		} catch (XPathExpressionException | DOMException e) {
			throw new TransformerException(e);
		} 
	}

	/**
	 * Función que devuelve un Vector de Strings dado un XPath.
	 * 
	 * @param Node XML
	 * @param Path XPath
	 * @return Vector vector de valores seleccionados con el Path
	 * @throws TransformerException
	 * @throws SecurityException    Excepción de seguridad
	 */
	public static Vector<String> searchDomVector(Node docDom, String strPath) throws TransformerException {
		NodeList nodeLiResultado;
		Vector<String> vecValores = null;

		try {
			nodeLiResultado = (NodeList) xPath.evaluate(strPath, docDom, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new TransformerException(e);
		}

		if (nodeLiResultado.getLength() != 0) {
			int x = 0;
			vecValores = new Vector<String>();
			for (x = 0; x < nodeLiResultado.getLength(); x++) {
				if (nodeLiResultado.item(x).hasChildNodes()) {
					// arrayValores.add(NodeLiResultado.item(x).getFirstChild().getNodeValue());
					vecValores.add(nodeLiResultado.item(x).getFirstChild().getNodeValue());
				} else {
					vecValores.add("");
				}
			}
		} else {
			vecValores = new Vector<String>();
		}
		return vecValores;
	}

	/**
	 * Función que devuelve un texto dado un XPath.
	 * 
	 * @param Node XML
	 * @param String XPath
	 * @return String Texto del nodo seleccionado con la ruta
	 * @throws TransformerException
	 */
	public static String searchDomText(Node docDom, String strPath) throws TransformerException {
		if (docDom != null && docDom.getTextContent() != null) {
			logger.debug("Searching node [{}] in document {}", strPath, docDom.getTextContent());
		} else {
			logger.debug("Can't find node [{}] in empty document", strPath);
		}

		try {
			return xPath.evaluate(strPath, docDom, XPathConstants.STRING).toString();
		} catch (XPathExpressionException | DOMException e) {
			throw new TransformerException(e);
		} 
	}

}