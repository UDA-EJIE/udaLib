package com.ejie.x38.webdav;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ejie.x38.webdav.exceptions.UnauthenticatedException;
import com.ejie.x38.webdav.exceptions.WebdavException;
import com.ejie.x38.webdav.locking.IResourceLocks;
import com.ejie.x38.webdav.methods.DoCopy;
import com.ejie.x38.webdav.methods.DoDelete;
import com.ejie.x38.webdav.methods.DoGet;
import com.ejie.x38.webdav.methods.DoHead;
import com.ejie.x38.webdav.methods.DoLock;
import com.ejie.x38.webdav.methods.DoMkcol;
import com.ejie.x38.webdav.methods.DoMove;
import com.ejie.x38.webdav.methods.DoNotImplemented;
import com.ejie.x38.webdav.methods.DoOptions;
import com.ejie.x38.webdav.methods.DoPropfind;
import com.ejie.x38.webdav.methods.DoProppatch;
import com.ejie.x38.webdav.methods.DoPut;
import com.ejie.x38.webdav.methods.DoUnlock;

public class WebDavServletHelper {

	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
    .getLogger(WebDavServletBean.class);
	
	private static final boolean READ_ONLY = false;
	
	public static void processWebDav(HttpServletRequest request,
			HttpServletResponse response, IWebdavStore webDavStore, HashMap<String, IMethodExecutor> _methodMap) throws ServletException, IOException {
		String methodName = request.getMethod();
		ITransaction transaction = null;
		boolean needRollback = false;

		if (LOG.isTraceEnabled()){
			WebDavServletHelper.debugRequest(methodName, request);
		}

		try {
			Principal userPrincipal = request.getUserPrincipal();
			transaction = webDavStore.begin(userPrincipal);
			needRollback = true;
			webDavStore.checkAuthentication(transaction);
			response.setStatus(WebdavStatus.SC_OK);

			try {
				IMethodExecutor methodExecutor = (IMethodExecutor) _methodMap
						.get(methodName);
				if (methodExecutor == null) {
					methodExecutor = (IMethodExecutor) _methodMap
							.get("*NO*IMPL*");
				}

				methodExecutor.execute(transaction, request, response);

				webDavStore.commit(transaction);
				needRollback = false;
			} catch (IOException e) {
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				e.printStackTrace(pw);
				LOG.error("IOException: " + sw.toString());
				response.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
				webDavStore.rollback(transaction);
				throw new ServletException(e);
			}

		} catch (UnauthenticatedException e) {
			response.sendError(WebdavStatus.SC_FORBIDDEN);
		} catch (WebdavException e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			LOG.error("WebdavException: " + sw.toString());
			throw new ServletException(e);
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			LOG.error("Exception: " + sw.toString());
		} finally {
			if (needRollback)
				webDavStore.rollback(transaction);
		}

	}

	public static void registerWebDavMethods(IWebdavStore webDavStore, HashMap<String, IMethodExecutor> _methodMap, String dftIndexFile, String insteadOf404, IResourceLocks _resLocks, int nocontentLenghHeaders, boolean lazyFolderCreationOnPut, IMimeTyper mimeTyper){
		WebDavServletHelper.register("GET", _methodMap, new DoGet(webDavStore, dftIndexFile, insteadOf404, _resLocks,
				mimeTyper, nocontentLenghHeaders));
		WebDavServletHelper.register("HEAD", _methodMap, new DoHead(webDavStore, dftIndexFile, insteadOf404,
				_resLocks, mimeTyper, nocontentLenghHeaders));

		DoDelete doDelete = (DoDelete) WebDavServletHelper.register("DELETE",  _methodMap,new DoDelete(webDavStore,
				_resLocks, READ_ONLY));
		
		DoCopy doCopy = (DoCopy) WebDavServletHelper.register("COPY",  _methodMap, new DoCopy(webDavStore, _resLocks,
				doDelete, READ_ONLY));
		
		WebDavServletHelper.register("LOCK", _methodMap, new DoLock(webDavStore, _resLocks, READ_ONLY));
		WebDavServletHelper.register("UNLOCK", _methodMap, new DoUnlock(webDavStore, _resLocks, READ_ONLY));
		WebDavServletHelper.register("MOVE", _methodMap, new DoMove(_resLocks, doDelete, doCopy, READ_ONLY));
		WebDavServletHelper.register("MKCOL", _methodMap, new DoMkcol(webDavStore, _resLocks, READ_ONLY));
		WebDavServletHelper.register("OPTIONS", _methodMap, new DoOptions(webDavStore, _resLocks));
		WebDavServletHelper.register("PUT", _methodMap, new DoPut(webDavStore, _resLocks, READ_ONLY,
				lazyFolderCreationOnPut));
		WebDavServletHelper.register("PROPFIND", _methodMap, new DoPropfind(webDavStore, _resLocks, mimeTyper));
		WebDavServletHelper.register("PROPPATCH", _methodMap, new DoProppatch(webDavStore, _resLocks, READ_ONLY));
		WebDavServletHelper.register("*NO*IMPL*", _methodMap, new DoNotImplemented(READ_ONLY));
	}
	
	public static IWebdavStore constructStore(String clazzName, File root) {
        IWebdavStore webdavStore;
        try {
        	Class<?> clazz = Class.forName(clazzName);

            Constructor<?> ctor = clazz
                    .getConstructor(new Class[] { File.class });

            webdavStore = (IWebdavStore) ctor
                    .newInstance(new Object[] { root });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("some problem making store component", e);
        }
        return webdavStore;
    }
    
	public static IResourceLocks constructResourceLocks(String clazzName){
    	IResourceLocks resourceLocks;
        try {
        	Class<?> clazz = Class.forName(clazzName);

            Constructor<?> ctor = clazz
                    .getConstructor();

            resourceLocks = (IResourceLocks) ctor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("some problem making store component", e);
        }
        return resourceLocks;
    }
	
	private static IMethodExecutor register(String methodName, HashMap<String, IMethodExecutor> _methodMap, IMethodExecutor method) {
        _methodMap.put(methodName, method);
        return method;
    }
	
	private static void debugRequest(String methodName, HttpServletRequest req) {
        LOG.trace("-----------");
        LOG.trace("WebdavServlet\n request: methodName = " + methodName);
        LOG.trace("time: " + System.currentTimeMillis());
        LOG.trace("path: " + req.getRequestURI());
        LOG.trace("-----------");
        Enumeration<?> e = req.getHeaderNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            LOG.trace("header: " + s + " " + req.getHeader(s));
        }
        e = req.getAttributeNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            LOG.trace("attribute: " + s + " " + req.getAttribute(s));
        }
        e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            LOG.trace("parameter: " + s + " " + req.getParameter(s));
        }
    }
	
}
