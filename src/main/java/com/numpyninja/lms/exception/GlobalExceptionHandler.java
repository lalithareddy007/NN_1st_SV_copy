package com.numpyninja.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.numpyninja.lms.config.ApiResponse;

import java.util.Date;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DuplicateResourceFoundException.class)
	public ResponseEntity<ApiResponse> duplicateResourceFoundExceptionHandler(DuplicateResourceFoundException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ApiResponse> invalidDataExceptionHandler(InvalidDataException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		FieldError fieldError = bindingResult.getFieldError();
		String message = fieldError.getDefaultMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse> constraintViolationExceptionHandler(InvalidDataException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse> badCredentialsExceptionExceptionHandler(BadCredentialsException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(CalendarAccessDeniedException.class)
	public ResponseEntity<ApiResponse> calendarAccessDeniedExceptionExceptionHandler(CalendarAccessDeniedException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(GCalendarSecurityException.class)
	public ResponseEntity<ApiResponse> calendarSecurityExceptionExceptionHandler(GCalendarSecurityException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(GCalendarCreateEventException.class)
	public ResponseEntity<ApiResponse> calendarEventCreateExceptionExceptionHandler(GCalendarCreateEventException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(GCalendarGetEventsException.class)
	public ResponseEntity<ApiResponse> calendarEventGetExceptionExceptionHandler(GCalendarGetEventsException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(GCalendarIOException.class)
	public ResponseEntity<ApiResponse> calendarIOExceptionExceptionHandler(GCalendarIOException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		if(message.contains("400 Bad Request")) {
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(GCalendarDeleteEventException.class)
	public ResponseEntity<ApiResponse> calendarDeleteExceptionHandler(GCalendarDeleteEventException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(GCalendarEventNotFoundException.class)
	public ResponseEntity<ApiResponse> calendarEventNotFoundExceptionExceptionHandler(GCalendarEventNotFoundException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
  }
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ApiResponse> InvalidFormatExceptionExceptionHandler(InvalidFormatException ex) {
		String message = ex.getMessage();
		if(ex.getTargetType().equals(Date.class)) //&& message.contains("Cannot deserialize value of type 'java.util.Date'")) 
		{
			message = "Invalid DateTime format";
		}
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);

	}
}
