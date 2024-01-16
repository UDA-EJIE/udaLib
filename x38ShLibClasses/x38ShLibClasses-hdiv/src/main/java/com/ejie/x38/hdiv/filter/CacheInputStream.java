package com.ejie.x38.hdiv.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class CacheInputStream implements Closeable {

	public static final int MAX_SIZE = 1024 * 1024;

	volatile File temporaryFile;

	public static final String TEMPORARY_FILENAME_PREFIX = "MyPrefix";

	public static final String TEMPORARY_FILENAME_SUFFIX = ".cache";

	public static final int LEN_BUFFER = 32768; // 32 KB

	private byte[] data;

	OutputStream out = new ByteArrayOutputStream() {
		@Override
		public void close() throws IOException {
			super.close();
			data = toByteArray();
		}
	};

	public void write(final InputStream in) throws IOException {
		try {
			try {
				byte[] buffer = new byte[LEN_BUFFER];
				int bytesRead = in.read(buffer);
				while (bytesRead != -1) {
					write(buffer, 0, bytesRead);
					bytesRead = in.read(buffer);
				}
			}
			finally {
				out.close();
			}
		}
		finally {
			in.close();
		}
	}

	void write(final byte[] data, final int offset, final int length) throws IOException {
		if (inMemory() && ((ByteArrayOutputStream) out).size() + length >= MAX_SIZE) {
			// Create a temporary file to hold the contents of the request's input stream
			temporaryFile = createTempFile(TEMPORARY_FILENAME_PREFIX, null);
			byte[] previous = ((ByteArrayOutputStream) out).toByteArray();
			out = new FileOutputStream(temporaryFile);
			out.write(previous);
		}
		out.write(data, offset, length);
	}

	private File createTempFile(final String prefix, final String suffix) throws IOException {
		File tmpDir = new File("tmp");
		tmpDir.mkdirs();
		return File.createTempFile(prefix, suffix, tmpDir);
	}
	
	public InputStream getInputStream() throws FileNotFoundException {
		if (inMemory()) {
			return new ByteArrayInputStream(data != null ? data : new byte[0]);
		}
		else {
			return new FileInputStream(temporaryFile);
		}
	}

	@Override
	public void close() {
		IOUtils.closeQuietly(out);
		if (!inMemory()) {
			temporaryFile.delete();
		}
	}

	private boolean inMemory() {
		return temporaryFile == null;
	}

	public static class EmptyCacheInputStream extends CacheInputStream {
		@Override
		public void write(final InputStream in) throws IOException {
			// ignore it
		}

		@Override
		void write(final byte[] data, final int offset, final int length) throws IOException {
			// ignore it
		}

		@Override
		public InputStream getInputStream() throws FileNotFoundException {
			return new ByteArrayInputStream(new byte[0]);
		}

		@Override
		public void close() {
			// ignore it
		}
	}

}
