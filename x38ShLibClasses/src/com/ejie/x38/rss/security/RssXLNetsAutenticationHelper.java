package com.ejie.x38.rss.security;

import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.ejie.x38.rss.exception.RssAuthenticationException;
import com.ejie.x38.util.XmlManager;

public class RssXLNetsAutenticationHelper {

	private static final String ERROR_TYPE_INFO = "informacion";
	private static final String ERROR_TYPE_WARNING = "warning";
	private static final String ERROR_TYPE_ERROR = "error";
	
	private static final String XPATH_N38_ROOT = "/n38";
	
	public static void checkSecuritySesionValid(Node nodo){

		if (null == nodo)
			return;
		try {
			
			Node nodeN38 = XmlManager.searchDomNode(nodo, RssXLNetsAutenticationHelper.XPATH_N38_ROOT);
			Node node = nodeN38.getFirstChild();

			if (RssXLNetsAutenticationHelper.ERROR_TYPE_INFO.equalsIgnoreCase(node.getNodeName())) {
				throw new RssAuthenticationException(RssXLNetsAutenticationHelper.getErrorString(node, RssXLNetsAutenticationHelper.ERROR_TYPE_INFO));
			}
			if (RssXLNetsAutenticationHelper.ERROR_TYPE_WARNING.equalsIgnoreCase(node.getNodeName())) {
				throw new RssAuthenticationException(RssXLNetsAutenticationHelper.getErrorString(node, RssXLNetsAutenticationHelper.ERROR_TYPE_WARNING));
			}
			if (RssXLNetsAutenticationHelper.ERROR_TYPE_ERROR.equalsIgnoreCase(node.getNodeName())) {
				throw new RssAuthenticationException(RssXLNetsAutenticationHelper.getErrorString(node, RssXLNetsAutenticationHelper.ERROR_TYPE_ERROR));
			}
		} catch (TransformerException e) {
			throw new RssAuthenticationException(e);
		}
	}
	
	private static String getErrorString(Node node, String type) throws DOMException, TransformerException{
		
		String codigo = XmlManager.searchDomNode(node,"/n38/"+type+"/@codigo").getNodeValue();
		String descripcion = XmlManager.searchDomNode(node,"/n38/"+type+"/descripcionCA/text()").getNodeValue();
		String motivo = XmlManager.searchDomNode(node,"/n38/"+type+"/motivo/text()").getNodeValue();
		return "XLNets Error Code '"+ codigo + "': " + descripcion + " -- " + motivo;
		
	}
	
}
