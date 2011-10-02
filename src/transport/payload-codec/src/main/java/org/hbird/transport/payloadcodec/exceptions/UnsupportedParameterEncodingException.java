package org.hbird.transport.payloadcodec.exceptions;

import org.hbird.transport.spacesystemmodel.parameters.Parameter.Encoding;

public class UnsupportedParameterEncodingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5931397913867898970L;
	
	Encoding encoding;

	public UnsupportedParameterEncodingException(final String msg) {
		super(msg);
	}
	
	public UnsupportedParameterEncodingException(final String msg, Encoding encoding) {
		super(msg + " [ENCODING = " + encoding + "]");
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UnsupportedParameterEncodingException [encoding=");
		builder.append(encoding);
		builder.append("]");
		return builder.toString();
	}
	
	

}
