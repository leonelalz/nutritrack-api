package com.nutritrack.nutritrackapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

//import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail(ex.getMessage());
        //problem.setType(URI.create("https://api.upc.com/errors/not-found"));
        return problem;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Violación de regla de negocio");
        problem.setDetail(ex.getMessage());
        //problem.setType(URI.create("https://api.upc.com/errors/business-rule"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Error interno");
        problem.setDetail("Ha ocurrido un error inesperado: " + ex.getMessage());
        //problem.setType(URI.create("https://api.upc.com/errors/internal-error"));
        return problem;
    }
    //Para nombres duplicados (RN06)
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Recurso duplicado");
        problem.setDetail(ex.getMessage());
        //problem.setType(URI.create("https://api.upc.com/errors/duplicate"));
        return problem;
    }

    //Para etiquetas en uso (RN08)
    @ExceptionHandler(ResourceInUseException.class)
    public ProblemDetail handleResourceInUse(ResourceInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Recurso en uso");
        problem.setDetail(ex.getMessage());
        //problem.setType(URI.create("https://api.upc.com/errors/in-use"));
        return problem;
    }

    //errores de validación (@Valid en Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Error de validación");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        problem.setDetail("Se encontraron " + errors.size() + " error(es) de validación");
        problem.setProperty("errors", errors);
        //problem.setType(URI.create("https://api.upc.com/errors/validation"));
        return problem;
    }

}