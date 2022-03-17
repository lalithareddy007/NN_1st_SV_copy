package com.numpyninja.lms.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockTimeoutException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class LmsServiceExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public LmsError handleValidationError(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String defaultMessage = fieldError.getDefaultMessage();
        return new LmsError("VALIDATION_FAILED", defaultMessage);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public LmsError handleValidationError(EntityExistsException ex) {
        return new LmsError("ENTITY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(LockTimeoutException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public LmsError handleValidationError(LockTimeoutException ex) {
        return new LmsError("DB_LOCK_TIMEOUT", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public LmsError handleValidationError(EntityNotFoundException ex) {
        return new LmsError("ENTITY_ERROR", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public String handleValidationError(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("error", new LmsError("DUPLICATE_ERROR", ex.getMessage()));
        return "LmsError";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public String handleValidationError(ConstraintViolationException ex, Model model) {
        model.addAttribute("error", new LmsError("VALIDATION_ERROR", ex.getMessage()));
        return "LmsError";
    }
}
