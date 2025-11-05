package com.nutritrack.nutritrackapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroRequestDTO(
        // Datos de User (autenticación)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        // Datos de Customer (información personal)
        String nombre,
        String apellido,
        Float weight,
        Float height,
        String phone,
        String dni,
        String address,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        String nationality,
        String occupation
) {
    // Constructor adicional con solo email, password y nombre
    public RegistroRequestDTO(String email, String password, String nombre, String apellido) {
        this(email, password, nombre, apellido, null, null, null, null, null, null, null, null);
    }
}
