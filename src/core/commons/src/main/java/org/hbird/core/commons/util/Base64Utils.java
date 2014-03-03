package org.hbird.core.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

public class Base64Utils {

	public static String createBase64EncodedStringFromURL(URL url) throws IOException {
		final InputStream inputStream = url.openStream();
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		final byte[] buffer = new byte[4096];

		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		outputStream.close();
		inputStream.close();

		final byte[] bytes = outputStream.toByteArray();
		return "data:image/png;base64," + DatatypeConverter.printBase64Binary(bytes);
	}
}