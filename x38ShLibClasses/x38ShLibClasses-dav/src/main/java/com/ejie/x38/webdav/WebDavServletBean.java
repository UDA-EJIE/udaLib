package com.ejie.x38.webdav;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ejie.x38.webdav.fromcatalina.MD5Encoder;
import com.ejie.x38.webdav.locking.IResourceLocks;

public class WebDavServletBean extends HttpServlet {

    /**
     * MD5 message digest provider.
     */
    protected static MessageDigest MD5_HELPER;

    /**
     * The MD5 helper object for this class.
     */
    protected static final MD5Encoder MD5_ENCODER = new MD5Encoder();

    protected IResourceLocks _resLocks;
    private IWebdavStore _store;
    private HashMap<String, IMethodExecutor> _methodMap = new HashMap<String, IMethodExecutor>();

    public WebDavServletBean() {
//        _resLocks = new ResourceLocks();

        try {
            MD5_HELPER = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    public void init(IWebdavStore store, String dftIndexFile,
            String insteadOf404, int nocontentLenghHeaders,
            boolean lazyFolderCreationOnPut) throws ServletException {

        _store = store;

        IMimeTyper mimeTyper = new IMimeTyper() {
            public String getMimeType(String path) {
                return getServletContext().getMimeType(path);
            }
        };
        
        WebDavServletHelper.registerWebDavMethods(_store, _methodMap, dftIndexFile, insteadOf404, _resLocks, nocontentLenghHeaders, lazyFolderCreationOnPut, mimeTyper);
    }

    /**
     * Handles the special WebDAV methods.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	WebDavServletHelper.processWebDav(request, response, this._store, this._methodMap);
    }
}
