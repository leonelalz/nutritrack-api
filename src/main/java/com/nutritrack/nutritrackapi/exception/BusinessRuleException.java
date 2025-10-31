package com.nutritrack.nutritrackapi.exception;

// Cuando hay una regla de negocio rota (ej. duplicado, no se puede eliminar, etc.)
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}