/**
 * 
 */
package com.gba.ws.exception;

import org.apache.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.gba.ws.bean.ErrorBean;
import com.gba.ws.bean.ErrorResponse;
//helloworld
/**
 * Provides application exception handling
 * 
 * @author Mohan
 * @createdOn Nov 10, 2017 7:29:22 PM
 */
@ControllerAdvice
public class ApplicationExceptionHandler {

	private static final Logger LOGGER = Logger.getLogger(ApplicationExceptionHandler.class);

	/**
	 * This method throws HttpRequestMethodNotSupportedException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link HttpRequestMethodNotSupportedException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - httpRequestMethodNotSupportedException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.METHOD_NOT_ALLOWED.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - httpRequestMethodNotSupportedException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * This method throws HttpMediaTypeNotSupportedException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link HttpMediaTypeNotSupportedException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Object> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - httpMediaTypeNotSupportedException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse().setError(
				new ErrorBean().setCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - httpMediaTypeNotSupportedException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	/**
	 * This method throws MissingServletRequestParameterException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link MissingServletRequestParameterException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Object> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - missingServletRequestParameterException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - missingServletRequestParameterException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method throws ServletRequestBindingException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link ServletRequestBindingException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(ServletRequestBindingException.class)
	public ResponseEntity<Object> servletRequestBindingException(ServletRequestBindingException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - servletRequestBindingException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - servletRequestBindingException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method throws ConversionNotSupportedException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link ConversionNotSupportedException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(ConversionNotSupportedException.class)
	public ResponseEntity<Object> conversionNotSupportedException(ConversionNotSupportedException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - conversionNotSupportedException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse().setError(
				new ErrorBean().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - conversionNotSupportedException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method throws TypeMismatchException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link TypeMismatchException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(TypeMismatchException.class)
	public ResponseEntity<Object> typeMismatchException(TypeMismatchException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - typeMismatchException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - typeMismatchException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method throws HttpMessageNotReadableException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link HttpMessageNotReadableException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - httpMessageNotReadableException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - httpMessageNotReadableException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method throws HttpMessageNotWritableException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link HttpMessageNotWritableException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(HttpMessageNotWritableException.class)
	public ResponseEntity<Object> httpMessageNotWritableException(HttpMessageNotWritableException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - httpMessageNotWritableException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse().setError(
				new ErrorBean().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - httpMessageNotWritableException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method throws MethodArgumentNotValidException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link MethodArgumentNotValidException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - methodArgumentNotValidException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - methodArgumentNotValidException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * This method throws MissingServletRequestPartException
	 * 
	 * @author Mohan
	 * @param ex
	 *            {@link MissingServletRequestPartException}
	 * @return ErrorResponse the {@link ErrorResponse} details
	 */
	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<Object> missingServletRequestPartException(MissingServletRequestPartException ex) {
		LOGGER.info("INFO: ApplicationExceptionHandler - missingServletRequestPartException() :: starts");
		ErrorResponse errorResponse = new ErrorResponse()
				.setError(new ErrorBean().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(ex.getMessage()));
		LOGGER.info("INFO: ApplicationExceptionHandler - missingServletRequestPartException() :: ends");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

}
