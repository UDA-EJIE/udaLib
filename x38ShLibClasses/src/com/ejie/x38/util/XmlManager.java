package com.ejie.x38.util;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlManager {

	private static final Logger logger = Logger.getLogger(XmlManager.class);

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
		Vector<String> vecValores = null;
		
		NodeLiResultado = XPathAPI.selectNodeList(docDom, strPath);
		if (NodeLiResultado.getLength() != 0) {
			int x = 0;
			vecValores = new Vector<String>();
			for (x = 0; x < NodeLiResultado.getLength(); x++) {
				if (NodeLiResultado.item(x).hasChildNodes()) {
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