/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ejie.x38.webdav;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.ejie.x38.webdav.exceptions.WebdavException;
import com.ejie.x38.webdav.locking.IResourceLocks;

/**
 * Servlet which provides support for WebDAV level 2.
 * 
 * the original class is org.apache.catalina.servlets.WebdavServlet by Remy
 * Maucherat, which was heavily changed
 * 
 * @author Remy Maucherat
 */
public class WebdavSpringServlet implements HttpRequestHandler {

	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
     .getLogger(WebDavServletBean.class);
	
    private Boolean lazyFolderCreationOnPut;
    
    private String defaultIndexFile;
    
    private String insteadOf404;
    
    private Integer noContentLengthHeaders;
    
    private IWebdavStore webDavStore;
    
    private IResourceLocks resourceLocks;
    
    private HashMap<String, IMethodExecutor> _methodMap = new HashMap<String, IMethodExecutor>();
    
    protected static MessageDigest MD5_HELPER;
    
    @PostConstruct
    public void init() throws ServletException {

    	try {
            MD5_HELPER = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
        
        if (this.noContentLengthHeaders==null){
        	this.noContentLengthHeaders = new Integer(0);
        }
    	
        if (this.lazyFolderCreationOnPut==null){
        	this.lazyFolderCreationOnPut = Boolean.FALSE;
        }

        // Inicialización del componente de locking
        if (this.resourceLocks == null){
        	LOG.error("No se ha configurado correctamente el gestor de locking de webDav");
        	throw new WebdavException("No se ha configurado correctamente el gestor de locking de webDav");
        }
        
        WebDavServletHelper.registerWebDavMethods(webDavStore, _methodMap, defaultIndexFile, insteadOf404, resourceLocks, noContentLengthHeaders, lazyFolderCreationOnPut, null);
    	
    }

    
    @Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
    	
    	WebDavServletHelper.processWebDav(request, response, this.webDavStore, this._methodMap);
	}
    

	public void setLazyFolderCreationOnPut(Boolean lazyFolderCreationOnPut) {
		this.lazyFolderCreationOnPut = lazyFolderCreationOnPut;
	}

	public void setDefaultIndexFile(String defaultIndexFile) {
		this.defaultIndexFile = defaultIndexFile;
	}

	public void setInsteadOf404(String insteadOf404) {
		this.insteadOf404 = insteadOf404;
	}

	public void setNoContentLengthHeaders(Integer noContentLengthHeaders) {
		this.noContentLengthHeaders = noContentLengthHeaders;
	}

	public void setWebDavStore(IWebdavStore webDavStore) {
		this.webDavStore = webDavStore;
	}

	public void setResourceLocks(IResourceLocks resourceLocks) {
		this.resourceLocks = resourceLocks;
	}
	
}
