package com.cooksys.social_network_api.advice;

import com.cooksys.social_network_api.dtos.ErrorResponse;
import com.cooksys.social_network_api.exceptions.BadRequestException;
import com.cooksys.social_network_api.exceptions.NotAuthorizedException;
import com.cooksys.social_network_api.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST) //sets response status to 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleInvalidArgument(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error->
            errors.add(error.getDefaultMessage())
        );
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), String.join("\n", errors));
    }

    //Generic 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleBadRequestException(Throwable t) {
        String message = t.getMessage();
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.join("\n", listOfErrorMessages)
        );
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class) 
    public ErrorResponse handleDataIntegrityViolationException(Throwable t) {
    	String message = t.getCause().getMessage();
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.join("\n", listOfErrorMessages)
        );
    }

    // Specific 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleHttpMessageNotReadableException(Throwable t) {
        String message = HttpStatus.BAD_REQUEST.getReasonPhrase();
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.join("\n", listOfErrorMessages)
        );
    }

    // Another specific 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = (e.getMessage() == null ? "Bad request." : e.getMessage());
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                String.join("\n", listOfErrorMessages));
    }

    // Generic 404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        String message = (e.getMessage() == null ? "Resource not found." : e.getMessage());
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                String.join("\n", listOfErrorMessages));
    }

    // Generic 401
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedException.class)
    public ErrorResponse handleNotFoundException(NotAuthorizedException e) {
        String message = (e.getMessage() == null ? "Unauthorized." : e.getMessage());
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                String.join("\n", listOfErrorMessages));
    }
    
   

    // Generic 500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleOtherException(Throwable t) {
        String message = "An internal server error occurred.";
        List<String> listOfErrorMessages = new ArrayList<>();
        listOfErrorMessages.add(message);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                String.join("\n", listOfErrorMessages));
    }
}
