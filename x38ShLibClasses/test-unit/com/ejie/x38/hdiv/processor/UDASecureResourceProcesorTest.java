package com.ejie.x38.hdiv.processor;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.hdiv.services.SecureIdentifiable;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.controller.model.MethodMappingInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkMappingInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkResources;
import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;
import com.ejie.x38.hdiv.controller.utils.MethodLinkDiscoverer;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UDASecureResourceProcesorTest {

	private DinamicLinkProvider dinamicLinkProvider;
	
	@Before
	public void init() {
		dinamicLinkProvider = new DinamicLinkProvider(null);
		
		HttpServletRequest request = new HttpServletRequest() {

			@Override
			public AsyncContext getAsyncContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration<String> getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getCharacterEncoding() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public DispatcherType getDispatcherType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletInputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getLocalAddr() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getLocalName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getLocalPort() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Locale getLocale() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration<Locale> getLocales() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, String[]> getParameterMap() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration<String> getParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String[] getParameterValues(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BufferedReader getReader() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRealPath(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRemoteAddr() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRemoteHost() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getRemotePort() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getScheme() {
				return "http";
			}

			@Override
			public String getServerName() {
				return "domain";
			}

			@Override
			public int getServerPort() {
				return 7001;
			}

			@Override
			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isAsyncStarted() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isAsyncSupported() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSecure() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public AsyncContext startAsync() throws IllegalStateException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getAuthType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContextPath() {
				return "/testingWar";
			}

			@Override
			public Cookie[] getCookies() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getDateHeader(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getHeader(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration<String> getHeaderNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration<String> getHeaders(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getIntHeader(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getMethod() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Part getPart(String arg0) throws IOException, ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Collection<Part> getParts() throws IOException, ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPathInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPathTranslated() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getQueryString() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRemoteUser() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRequestURI() {
				return "/test/unit";
			}

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer("http://domain/").append(getContextPath()).append(getRequestURI());
			}

			@Override
			public String getRequestedSessionId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServletPath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HttpSession getSession() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HttpSession getSession(boolean arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Principal getUserPrincipal() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isRequestedSessionIdFromCookie() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromURL() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isRequestedSessionIdFromUrl() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isRequestedSessionIdValid() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isUserInRole(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void login(String arg0, String arg1) throws ServletException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logout() throws ServletException {
				// TODO Auto-generated method stub
				
			}}
		;
		RequestAttributes attr = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(attr);
		
	}
	
	private MethodLinkDiscoverer getMethodLinkDiscoverer(final String allowerName, final boolean allowSubEntities,  final Class<?> allower, final MethodMappingInfo mappings) {
		return new MethodLinkDiscoverer() {
			@Override
			public List<UDALinkMappingInfo> getMethodLinkInfo(final HttpServletRequest request) {
				List<UDALinkMappingInfo> allowInfoList = new ArrayList<UDALinkMappingInfo>();
				allowInfoList.add(new UDALinkMappingInfo(allowerName, allowSubEntities ,allower, mappings));
				return allowInfoList;
			}
		};
	}
	
	@Test
	public void testEntityLink() throws JsonProcessingException, IOException {
		UDALinkResources resources = new UDALinkResources();
		Entity entity = new Entity(1l, "entity1");
		resources.getEntities().add(entity);
		
		MethodMappingInfo mappings = new MethodMappingInfo(new HashSet<String>(Arrays.asList("/test/{name}")), null, null);
		
		String relName = "allower";
		UDASecureResourceProcesor.registerMethodLinkDiscoverer(getMethodLinkDiscoverer(relName, false, Void.class, mappings));
		
		List<Resource<Object>> resorces = UDASecureResourceProcesor.processLinks(resources, getClass(), dinamicLinkProvider);
		
		assertEquals(1, resorces.size());
		assertEquals(1, resorces.get(0).getLinks().size());
		assertEquals("<http://domain:7001/testingWar/test/"+entity.getName()+">;rel=\""+relName+"\"", resorces.get(0).getLinks().get(0).toString());	
	}
	
	@Test
	public void testResourceEntityLink() throws JsonProcessingException, IOException {
		UDALinkResources resources = new UDALinkResources();
		Entity entity = new Entity(1l, "entity1");
		Resource<Entity> resource = new Resource<Entity>(entity);
		resources.getEntities().add(resource);
		
		MethodMappingInfo mappings = new MethodMappingInfo(new HashSet<String>(Arrays.asList("/test/{name}")), null, null);
		
		String relName = "allower";
		UDASecureResourceProcesor.registerMethodLinkDiscoverer(getMethodLinkDiscoverer(relName, false, Void.class, mappings));
		
		List<Resource<Object>> resorces = UDASecureResourceProcesor.processLinks(resources, getClass(), dinamicLinkProvider);
		
		assertEquals(0, resorces.size());
		assertEquals(1, resource.getLinks().size());
		assertEquals("<http://domain:7001/testingWar/test/"+entity.getName()+">;rel=\""+relName+"\"", resource.getLinks().get(0).toString());	
	}
	
	public class Entity implements SecureIdentifiable<Long> {
		
		private Long id;
		private String name;
		
		public Entity( Long id, String name) {
			this.id = id;
			this.name = name;
		}
		
		@Override
		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		
	}
}
