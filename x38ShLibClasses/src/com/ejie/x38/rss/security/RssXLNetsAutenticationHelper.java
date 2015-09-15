package com.ejie.x38.rss.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import n38a.exe.N38APISesion;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ejie.x38.rss.exception.RssAuthenticationException;
import com.ejie.x38.util.XmlManager;
import com.ejie.x38.webdav.security.WebDAVHttpServletRequestWrapper;

/**
 * Clase de utilidades para facilitar el proceso de autenticación de un usuario
 * contra XLNets que vaya aceder al contenido RSS.
 * 
 * @author UDA
 *
 */
public class RssXLNetsAutenticationHelper {

	private static final String ERROR_TYPE_INFO = "informacion";
	private static final String ERROR_TYPE_WARNING = "warning";
	private static final String ERROR_TYPE_ERROR = "error";

	private static final String XPATH_N38_ROOT = "/n38";
	private static final String XPATH_N38UIDSESION = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='n38uidsesion']/valor";
	private static final String XPATH_N38DOMINIOUID = "/n38/elementos/elemento[@subtipo='N38Sesion']/parametro[@id='n38dominiouid']/valor";

	/**
	 * Comprueba que el documento generado desde XLNets es válido.
	 * 
	 * @param nodo
	 *            Documento de sesión de XLNets.
	 */
	public static void checkSecuritySesionValid(Node nodo) {

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
			throw new RssAuthenticationException("Se ha producido un error a la hora de validar el xml de sesión de XLNets", e);
		}
	}

	/**
	 * Genera una sesión de XLNets realizando un login mediante usuario y
	 * contraseña.
	 * 
	 * @param username
	 *            Nombre de usuario de XLNets.
	 * @param password
	 *            Password del usuario en XLNets.
	 * @param request
	 *            HTTP Request.
	 * @param response
	 *            HTTP Response.
	 * @return Request generada a partir de la indicada por parámetro en la que
	 *         se añaden las cookies correspondientes a la sesión de XLNets
	 *         generada.
	 */
	public static HttpServletRequest createXLNetsSessionUP(String username, String password, HttpServletRequest request, HttpServletResponse response) {
		N38APISesion n38APISession = new N38APISesion(request);
		Document n38apiSesionCrearUP = n38APISession.n38APISesionCrearUP(username, password);

		RssXLNetsAutenticationHelper.checkSecuritySesionValid(n38apiSesionCrearUP);
		
		WebDAVHttpServletRequestWrapper wrapedRequest = new WebDAVHttpServletRequestWrapper(request);
		wrapedRequest.getSession(true);

		String searchDomNodeUidSesion;
		String searchDomNodeDominioUid;
		try {
			searchDomNodeUidSesion = XmlManager.searchDomNode(n38apiSesionCrearUP, RssXLNetsAutenticationHelper.XPATH_N38UIDSESION).getFirstChild().getNodeValue();
			searchDomNodeDominioUid = XmlManager.searchDomNode(n38apiSesionCrearUP, RssXLNetsAutenticationHelper.XPATH_N38DOMINIOUID).getFirstChild().getNodeValue();

		} catch (TransformerException e) {
			throw new RssAuthenticationException("Se ha producido un error a la hora de validar el xml de sesión de XLNets", e);
		}

		wrapedRequest.addCookie(new Cookie("n38UidSesion", searchDomNodeUidSesion));
		wrapedRequest.addCookie(new Cookie("n38DominioUid", searchDomNodeDominioUid));

		return wrapedRequest;
	}

	/**
	 * Genera un código de error a partir de un xml de error devuelto por
	 * XLNets.
	 * 
	 * @param node
	 *            Documento de error de xlnets.
	 * @param type
	 *            Tipo de error.
	 * @return Mensaje de error correspondiente al error de XLNets.
	 * @throws DOMException
	 *             Excepción DOMException.
	 * @throws TransformerException
	 *             Excepción TransformerException.
	 */
	private static String getErrorString(Node node, String type) throws DOMException, TransformerException {

		String codigo = XmlManager.searchDomNode(node, "/n38/" + type + "/@codigo").getNodeValue();
		String descripcion = XmlManager.searchDomNode(node, "/n38/" + type + "/descripcionCA/text()").getNodeValue();
		String motivo = XmlManager.searchDomNode(node, "/n38/" + type + "/motivo/text()").getNodeValue();
		return "XLNets Error Code '" + codigo + "': " + descripcion + " -- " + motivo;

	}

}
