package com.gba.ws.exception;
//helloworld
/**
 * Extends {@link Exception} class, provides occured exception details
 * 
 * @author Mohan
 * @createdOn Jan 8, 2018 5:37:52 PM
 */
@SuppressWarnings("serial")
public class CustomException extends Exception {

	/**
	 * 
	 */
	public CustomException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CustomException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CustomException(Throwable cause) {
		super(cause);
	}

}
