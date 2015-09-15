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

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author UDA
 *
 */
public class XmlManager {
	
	private static final Logger logger =  LoggerFactory.getLogger(XmlManager.class);

	/**
	 * Funcion que devuelve un Nodo dado un XPath.
	 * 
	 * @param Node XML
	 * @param Path XPath
	 * @return Node Nodo seleccionado con el Path
	 * @throws TransformerException 
	 */
	public static Node searchDomNode(Node docDom, String strPath) throws TransformerException {
		if (docDom != null && docDom.getTextContent()!=null)
			logger.debug("Searching node "+ "["+strPath+"] in document " + docDom.getTextContent());
		else 
			logger.debug("Can't find node "+ "["+strPath+"] in empty document");
			
		return XPathAPI.selectSingleNode(docDom, strPath);
	}	

	/**
	 * Funcion que devuelve un Vector de Strings dado un XPath.
	 * 
	 * @param Node XML
	 * @param Path XPath
	 * @return Vector vector de valores seleccionados con el Path
	 * @throws TransformerException 
	 * @throws SecurityException Excepción de seguridad
	 */
	public static Vector<String> searchDomVector(Node docDom, String strPath) throws TransformerException {
		NodeList NodeLiResultado = null;
		Vector<String> vecValores = null ;
		
		NodeLiResultado = XPathAPI.selectNodeList(docDom, strPath);
		if (NodeLiResultado.getLength() != 0) {
			int x = 0;
			vecValores = new Vector<String>();
			for (x = 0; x < NodeLiResultado.getLength(); x++) {
				if (NodeLiResultado.item(x).hasChildNodes()) {
					//arrayValores.add(NodeLiResultado.item(x).getFirstChild().getNodeValue());
					vecValores.add(NodeLiResultado.item(x).getFirstChild().getNodeValue());
				} else {
					vecValores.add("");
				}
			}
		} else {
			vecValores = new Vector<String>();
		}
		return vecValores;
		
	}	

}