package com.promocedes.api.exception.advice;

import com.promocedes.api.exception.DuplicateUniqueValueException;
import com.promocedes.api.exception.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidArgument(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getFieldError().getDefaultMessage()
        );
        problemDetail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return problemDetail;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ProblemDetail handleObjectNotFound(ObjectNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage()
        );
        problemDetail.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        return problemDetail;
    }

    @ExceptionHandler(DuplicateUniqueValueException.class)
    public ProblemDetail handleDuplicateUniqueValue(DuplicateUniqueValueException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
        );
        problemDetail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        return problemDetail;
    }
}
