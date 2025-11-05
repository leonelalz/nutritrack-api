package com.example.nutritrackapi.exception;

/**
 * Excepción para validaciones de lógica de negocio.
 * Se lanza cuando se violan reglas de negocio (RN).
 * Módulo 4: Exploración y Activación (Cliente)
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
