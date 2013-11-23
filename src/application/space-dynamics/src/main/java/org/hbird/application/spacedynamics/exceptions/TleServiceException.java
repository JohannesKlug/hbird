package org.hbird.application.spacedynamics.exceptions;

public class TleServiceException extends Exception {

	private static final long serialVersionUID = 44531705792895709L;

	public TleServiceException(String message, Throwable e) {
		super(message, e);
	}
}
