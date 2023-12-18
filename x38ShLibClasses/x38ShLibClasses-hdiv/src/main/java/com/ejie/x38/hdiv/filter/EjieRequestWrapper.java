package com.ejie.x38.hdiv.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ejie.x38.hdiv.filter.CacheInputStream.EmptyCacheInputStream;

public class EjieRequestWrapper extends HttpServletRequestWrapper {
	
	private static final String ENCODING = "UTF-8";

	private CacheInputStream cached;

	public EjieRequestWrapper(final HttpServletRequest request) {
		super(request);
		if (getContentType() != null && getContentType().contains("json")) {
			initCacheInputStream();
		}
	}

	public void cleanup() {
		if (cached != null) {
			cached.close();
		}
	}

	public InputStream getCacheInputStream() throws IOException {
		initCacheInputStream();
		return cached.getInputStream();
	}

	void initCacheInputStream() {
		if (cached == null) {
			try {
				// Create a temporary file to hold the contents of the request's input stream
				cached = new CacheInputStream();
				cached.write(super.getInputStream());
			}
			catch (Exception e) {
				cached = new EmptyCacheInputStream();
				// prevent NoClassDefFoundError on sta tic initialization
			}
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (cached != null) {
			return new CachedServletInputStream(cached);
		}
		else {
			return super.getInputStream();
		}
	}

	@Override
	public BufferedReader getReader() throws IOException {
		String enc = getCharacterEncoding();
		if (enc == null) {
			enc = ENCODING;
		}
		return new BufferedReader(new InputStreamReader(getInputStream(), enc));
	}
	
	class CachedServletInputStream extends ServletInputStream {
		private final CacheInputStream cached;
		
		private InputStream cachedInputStream;
		
		public CachedServletInputStream(CacheInputStream cached) {
			this.cached = cached;
		}

		@Override
		public int read() throws IOException {
			return getInputStream().read();
		}
		
		
		private InputStream getInputStream() throws IOException {
			if (cachedInputStream == null) {
				cachedInputStream = cached.getInputStream();
			}
			return cachedInputStream;
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			try {
				if (cachedInputStream != null) {
					cachedInputStream.close();
				}
			}
			finally {
				cachedInputStream = null;
			}
		}

	}

}
