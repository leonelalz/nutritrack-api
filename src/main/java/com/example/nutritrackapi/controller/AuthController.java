package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Módulo 1: Autenticación y Perfil", description = "Endpoints para registro, login y gestión de cuentas (US-01 a US-05) - Leonel Alzamora")
public class AuthController {

    private final AuthService authService;

    /**
     * US-01: Crear cuenta
     * RN01: Email único
     * RN02: Validación de credenciales
     */
    @PostMapping("/registro")
    @Operation(
        summary = "Registrar nuevo usuario", 
        description = "Crea una nueva cuenta de usuario con su perfil básico"
    )
    @RequestBody(
        description = "Datos del nuevo usuario",
        required = true,
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Usuario Regular",
                    summary = "Ejemplo de registro de usuario estándar",
                    description = "Registro con datos completos de un usuario regular",
                    value = """
                        {
                          "email": "nuevo@ejemplo.com",
                          "password": "Pass123!",
                          "nombre": "Juan",
                          "apellido": "Pérez",
                          "fechaNacimiento": "1990-05-15"
                        }
                        """
                ),
                @ExampleObject(
                    name = "Usuario Joven",
                    summary = "Ejemplo de registro de usuario joven",
                    description = "Registro de usuario de menor edad",
                    value = """
                        {
                          "email": "maria@ejemplo.com",
                          "password": "Maria123!",
                          "nombre": "María",
                          "apellido": "González",
                          "fechaNacimiento": "2000-08-20"
                        }
                        """
                )
            }
        )
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * US-02: Iniciar sesión
     * RN02: Login falla si credenciales incorrectas o cuenta inactiva
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión", 
        description = "Autentica un usuario y retorna un token JWT"
    )
    @RequestBody(
        description = "Credenciales de acceso",
        required = true,
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "Usuario Demo",
                    summary = "Usuario regular para pruebas",
                    description = "Cuenta de usuario regular con objetivo de perder peso",
                    value = """
                        {
                          "email": "demo@nutritrack.com",
                          "password": "Demo123!"
                        }
                        """
                ),
                @ExampleObject(
                    name = "Usuario Admin",
                    summary = "Administrador del sistema",
                    description = "Cuenta de administrador con permisos completos",
                    value = """
                        {
                          "email": "admin@nutritrack.com",
                          "password": "Admin123!"
                        }
                        """
                )
            }
        )
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Login exitoso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * US-05: Eliminar cuenta
     * RN05: Requiere confirmación explícita escribiendo "ELIMINAR"
     */
    @DeleteMapping("/cuenta")
    @Operation(summary = "Eliminar cuenta", 
               description = "Elimina permanentemente la cuenta del usuario. Requiere escribir 'ELIMINAR' para confirmar")
    public ResponseEntity<ApiResponse<Void>> eliminarCuenta(
            Authentication authentication,
            @Valid @RequestBody DeleteAccountRequest request) {
        try {
            authService.eliminarCuenta(authentication.getName(), request);
            return ResponseEntity.ok(ApiResponse.success(null, "Cuenta eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
