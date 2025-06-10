package com.ejie.x38.pif;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import n38a.exe.N38APISesion;
import n38c.exe.N38API;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.Document;

import com.ejie.x38.json.JSONArray;
import com.ejie.x38.json.JSONObject;
import com.ejie.x38.security.XlnetCore;
import com.ejie.y31.exception.Y31JanoServiceAccesDeniedException;
import com.ejie.y31.exception.Y31JanoServiceEventJmsException;
import com.ejie.y31.exception.Y31JanoServiceFileNameFormatException;
import com.ejie.y31.exception.Y31JanoServiceFileNotFoundException;
import com.ejie.y31.exception.Y31JanoServiceGenericException;
import com.ejie.y31.exception.Y31JanoServiceMongoDbGenericException;
import com.ejie.y31.exception.Y31JanoServiceOracleGenericException;
import com.ejie.y31.factory.Y31JanoServiceAbstractFactory;
import com.ejie.y31.service.Y31JanoService;
import com.ejie.y31.vo.Y31AttachmentBean;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;

public class PifServletHelper {

	private static final Logger logger = LoggerFactory.getLogger(PifServletHelper.class);
	
	private static final String HTTP_BASE_URL_PARAM_NAME = "base_url";
	private static final String HTTP_SECURITY_TOKEN_PARAM_NAME = "securityToken";
	
	private static final String SECURITY_TOKEN_APP = "APP";
	
	
	public void processRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException{
		try{
			String method = httpRequest.getMethod();
			
			if ("POST".equals(method)){
				this.doPost(httpRequest, httpResponse);
			}else if ("GET".equals(method)){
				this.doGet(httpRequest, httpResponse);
			}else if ("DELETE".equals(method)){
				this.doDelete(httpRequest, httpResponse);
			}
		
		} catch (Y31JanoServiceGenericException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceGenericException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		} catch (Y31JanoServiceFileNameFormatException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceFileNameFormatException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		} catch (Y31JanoServiceFileNotFoundException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceFileNotFoundException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		} catch (Y31JanoServiceMongoDbGenericException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceMongoDbGenericException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		} catch (Y31JanoServiceOracleGenericException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceOracleGenericException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		} catch (Y31JanoServiceAccesDeniedException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceAccesDeniedException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		}  catch (IOException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceGenericException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		} catch (Y31JanoServiceEventJmsException e) {
			this.translateY31JanoErrorCode(httpResponse, e, Y31JanoServiceEventJmsException.HTTP_ERROR_CODE_TRANSLATE_BASE);
		}
		finally
		{
			httpResponse.getWriter().flush();
			httpResponse.getWriter().close();
		}
	}
	
	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Y31JanoServiceGenericException, Y31JanoServiceFileNameFormatException, Y31JanoServiceMongoDbGenericException, Y31JanoServiceEventJmsException, Y31JanoServiceAccesDeniedException{
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter;
			iter = upload.getItemIterator(request);
			
			String baseUrl = "";
			String folder = "";
			Long fileTtl = null;
			Boolean preserveName = Boolean.FALSE;
			Y31AttachmentBean result;
			Boolean appSecurityToken = Boolean.TRUE;
			Boolean isIEEmulate = request.getParameter("_emulate_iframe_http_status") != null? Boolean.TRUE:Boolean.FALSE;

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					if (PifServletHelper.HTTP_BASE_URL_PARAM_NAME.equals(name)){
						baseUrl = Streams.asString(stream);
					}else if (Y31JanoService.HTTP_FOLDER_PATH_PARAM_NAME.equals(name)){
						folder = Streams.asString(stream);
					}else if (Y31JanoService.HTTP_PRESERVE_NAME_PARAM_NAME.equals(name)){
						preserveName = Boolean.TRUE.toString().toUpperCase().equals(Streams.asString(stream).toUpperCase())?Boolean.TRUE:Boolean.FALSE;
					}else if (Y31JanoService.Y31_TTL_NAME_PARM.equals(name)){
						fileTtl = Long.valueOf(Streams.asString(stream));
					}else if (PifServletHelper.HTTP_SECURITY_TOKEN_PARAM_NAME.equals(name)){
						appSecurityToken = SECURITY_TOKEN_APP.equals(Streams.asString(stream).toUpperCase())?Boolean.TRUE:Boolean.FALSE;
					}
					
				} else {
					
					Y31JanoService service = null;
					try {
						
						String fileName;
						if (isIEEmulate){
							fileName = item.getName();
							int indexOf = fileName.lastIndexOf("\\");
							if (indexOf != -1){
								fileName = fileName.substring(indexOf+1);
							}
						}else{
							fileName = item.getName();
						}
								
						service = Y31JanoServiceAbstractFactory.getInstance();
						StringBuilder rutaFichTmp = new StringBuilder(folder).append("/").append(fileName);
				
						Document xmlSession = null;
						
						xmlSession = this.getXLNetsDocument(request, appSecurityToken);
						
						result = service.put(xmlSession, stream, rutaFichTmp.toString(),
								preserveName, fileTtl);
					} finally {
						if (stream != null) {
							try {
								stream.close();
							} catch (IOException ioe) {
								logger.warn("Se ha producido un error al cerrar el stream de lectura: "+ ioe.getMessage());
							}
						}
					}
					
					
					if (!isIEEmulate){
						response.setContentType("application/json");
					}
					response.getWriter().write(this.getJsonResponsePut(baseUrl, result));
				}
				
			}
		} catch (FileUploadException fue) {
			logger.error("Se ha producido un error la realizar la subida del fichero: ", fue);
			throw new Y31JanoServiceGenericException(fue); 
		} catch (IOException ioe) {
			logger.error("Se ha producido un error la realizar la subida del fichero: ", ioe);
			throw new Y31JanoServiceGenericException(ioe); 
		}
	}
	
	private void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Y31JanoServiceGenericException, Y31JanoServiceFileNameFormatException, Y31JanoServiceMongoDbGenericException, Y31JanoServiceAccesDeniedException, Y31JanoServiceFileNotFoundException, Y31JanoServiceOracleGenericException, IOException{
			
			String path = httpRequest.getParameter(Y31JanoService.HTTP_PATH_PARAM_NAME);
			
			Document xlnetsToken = this.getXLNetsDocument(httpRequest, Boolean.TRUE);
			
			OutputStream out = null;
			
			// info
			Y31AttachmentBean file = Y31JanoServiceAbstractFactory.getInstance().info(xlnetsToken, path);
			
			if (file != null){
				
				try {
					httpResponse.setContentType(getMimeTypeFromCompletePath(path));
					out = httpResponse.getOutputStream();
				} catch (IOException ioe) {
					logger.error("Se ha producido un error la realizar la subida del fichero: ", ioe);
					httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				
				httpResponse.setHeader("Content-disposition", "attachment; filename=" + file.getFileName());
				httpResponse.setContentLength(file.getSize().intValue());
				
				InputStream is = Y31JanoServiceAbstractFactory.getInstance().get(xlnetsToken, path);
				FileCopyUtils.copy(is, out);
				is.close();
				out.flush();
				out.close();
				httpResponse.setStatus(HttpServletResponse.SC_OK);	
			}else{
				throw new Y31JanoServiceFileNotFoundException("El path " + path + " no existe en remoto");
			}
			
	}
	
	private void doDelete(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Y31JanoServiceFileNameFormatException, Y31JanoServiceGenericException, Y31JanoServiceAccesDeniedException, Y31JanoServiceFileNotFoundException{
		
				
		// Preparando los parámetros
		Document sesion = this.getXLNetsDocument(httpRequest, Boolean.TRUE);
				
		String path = httpRequest.getParameter(Y31JanoService.HTTP_PATH_PARAM_NAME);
		
		// invocando el metodo
		Y31JanoServiceAbstractFactory.getInstance().delete(sesion, path);
		
	}
	
	
	private Document getXLNetsDocument(HttpServletRequest httpRequest, Boolean appToken){
		
		if (Boolean.TRUE.equals(appToken)){
			return this.getTokenAppDocument(httpRequest);
		}else{
			
			N38API n38api = XlnetCore.getN38API(httpRequest);
			return XlnetCore.getN38ItemSesion(n38api);
		}
		
	}
	
	private Document getTokenAppDocument(HttpServletRequest httpRequest) {
		final String webAppName = httpRequest.getSession().getServletContext().getInitParameter("webAppName");
		
		final N38APISesion miAPISesion = new N38APISesion();
		final Document docAPISesionApp = miAPISesion.n38APISesionCrearApp(webAppName);
		
		PifServletHelper.logger.trace("INI - getTokenAppDocument");
		Document doc = null;
			
		PifServletHelper.logger.info(" logueandose en XLNetS ...");

		N38API n38api = new N38API(docAPISesionApp);
		PifServletHelper.logger.info(webAppName + " logueada en XLNetS.");
		
		doc = n38api.n38ItemSesion();

		PifServletHelper.logger.trace("FIN - getTokenAppDocument");
		return doc;
	}

	private static String getMimeTypeFromCompletePath(String path) throws Y31JanoServiceFileNameFormatException {
		/*
		 * File f = new File(path); String m = new
		 * MimetypesFileTypeMap().getContentType(f);
		 */
		try {
			ExtensionMimeDetector emd = new ExtensionMimeDetector();
			@SuppressWarnings("unchecked")
			Collection<MimeType> mimeTypes = emd.getMimeTypesFileName(path);
			MimeType mimeType = MimeUtil.getMostSpecificMimeType(mimeTypes);
			return mimeType.toString();
		} catch (Exception e) {
			return "application/octet-stream";
		}
	}
	
	private void translateY31JanoErrorCode(HttpServletResponse httpResponse, Exception e, int errorCode) throws IOException {
		e.printStackTrace();
		httpResponse.setStatus(errorCode);
		String s = "{\"success\": false, \"statusCode\":"+errorCode+", \"message\":\""+e.getMessage()+"\" }";
		httpResponse.getWriter().print(s);
		httpResponse.addHeader("HTTP_ERROR_CODE_TRANSLATE",s);
		httpResponse.addHeader("HTTP_ERROR_CODE_TRANSLATE_CODE","Y31-"+errorCode);
		httpResponse.addHeader("HTTP_ERROR_CODE_TRANSLATE_MESSAGE",e.getMessage());
	}
	
	
	private String getJsonResponsePut(String baseUrl, Y31AttachmentBean y31AttachmentBean) {

		JSONArray files = new JSONArray();
		JSONObject file;
		file = new JSONObject();
		file.put(
				"url",
				baseUrl+"?hadoop_file_path="
						+ y31AttachmentBean.getFilePath());
		file.put("name", y31AttachmentBean.getFileName());
		file.put("type", y31AttachmentBean.getContentType());
		file.put("size", y31AttachmentBean.getSize());
		file.put("delete_url", baseUrl+"?hadoop_file_path="
				+ y31AttachmentBean.getFilePath());
		file.put("delete_type", "DELETE");
		files.put(file);

		return files.toString();

	}
	
}
