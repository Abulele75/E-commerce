package cput.ac.za.ecommerce.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;



@RestControllerAdvice
public class ApiExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<?> validation(MethodArgumentNotValidException ex)
    {

        Map<String,String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity
                .badRequest()
                .body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> notFound(EntityNotFoundException ex)
    {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }


    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})

    public ResponseEntity<?> badRequest(RuntimeException ex)
    {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)

    public ResponseEntity<?> accessDenied(AccessDeniedException ex)
    {
        return response(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)

    public ResponseEntity<?> unreadable(HttpMessageNotReadableException ex)
    {
        return response(HttpStatus.BAD_REQUEST, "The request contains an invalid or unsupported value");
    }


    @ExceptionHandler(DataIntegrityViolationException.class)

    public ResponseEntity<?> database(DataIntegrityViolationException ex)
    {
        ex.printStackTrace();
        Throwable cause = ex.getMostSpecificCause();
        String detail = cause != null
                        && cause.getMessage() != null
                        && !cause.getMessage().isBlank()
                        ? cause.getMessage()
                        : "A record with these details already exists";

        return response(HttpStatus.CONFLICT, "Database constraint violation: " + detail);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> general(Exception ex)
    {
        ex.printStackTrace();
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<?> response(HttpStatus status, String message)
    {
        Map<String,Object> body =
                new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}