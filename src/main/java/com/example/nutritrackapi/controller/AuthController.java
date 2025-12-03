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
@Tag(name = "Módulo 1: Autenticación y Perfil", description = "🔓 PÚBLICO - Endpoints para registro, login y gestión de cuentas (US-01 a US-05) - Leonel Alzamora. ACCESO PÚBLICO (sin autenticación).")
public class AuthController {

    private final AuthService authService;

    /**
     * US-01: Crear cuenta
     * RN01: Email único
     * RN30: Validación Email RFC 5322 + DNS
     * RN31: Política de contraseñas robusta (12+ caracteres)
     * 
     * UNIT TESTS (13 tests en AuthServiceTest.java):
     * ✅ testRegistro_EmailFormatoInvalido() - RN30
     * ✅ testRegistro_EmailDominioInexistente() - RN30
     * ✅ testRegistro_PasswordCorta() - RN31
     * ✅ testRegistro_PasswordSinComplejidad() - RN31
     * ✅ testRegistro_PasswordComun() - RN31
     * ✅ testRegistro_PasswordContieneEmail() - RN31
     * ✅ testRegistro_EmailDuplicado() - RN01
     */
    @PostMapping("/registro")
    @Operation(
        summary = "🔓 PÚBLICO - Registrar nuevo usuario [RN01, RN30, RN31]", 
        description = """
            Crea una nueva cuenta de usuario con su perfil básico. ACCESO PÚBLICO.
            
            **REGLAS DE NEGOCIO IMPLEMENTADAS:**
            - RN01: Email único en la base de datos
            - RN30: Validación formato email RFC 5322 + verificación DNS
            - RN31: Contraseña mínimo 12 caracteres con complejidad (mayúscula, minúscula, número, símbolo)
            
            **VALIDACIONES AUTOMÁTICAS:**
            1. Email con formato válido y dominio existente (DNS lookup)
            2. Contraseña no puede ser común (blacklist)
            3. Contraseña no puede contener el email del usuario
            4. Email no puede estar registrado previamente
            
            **UNIT TESTS:** 13/13 ✅ en AuthServiceTest.java
            - Ejecutar: ./mvnw test -Dtest=AuthServiceTest
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "✅ Usuario registrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Registro Exitoso",
                    value = """
                        {
                          "success": true,
                          "message": "Usuario registrado exitosamente",
                          "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "email": "nuevo@ejemplo.com",
                            "nombre": "Juan",
                            "apellido": "Pérez",
                            "role": "ROLE_USER"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "❌ Error de validación (RN01, RN30, RN31)",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "RN01: Email Duplicado",
                        summary = "Email ya registrado",
                        description = "Test: testRegistro_EmailDuplicado()",
                        value = """
                            {
                              "success": false,
                              "message": "El email ya está registrado",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "RN30: Email Inválido",
                        summary = "Formato de email inválido",
                        description = "Test: testRegistro_EmailFormatoInvalido()",
                        value = """
                            {
                              "success": false,
                              "message": "Email con formato inválido o dominio inexistente",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "RN31: Contraseña Corta",
                        summary = "Contraseña < 12 caracteres",
                        description = "Test: testRegistro_PasswordCorta()",
                        value = """
                            {
                              "success": false,
                              "message": "La contraseña debe tener mínimo 12 caracteres",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "RN31: Contraseña Sin Complejidad",
                        summary = "Falta mayúscula/número/símbolo",
                        description = "Test: testRegistro_PasswordSinComplejidad()",
                        value = """
                            {
                              "success": false,
                              "message": "La contraseña debe contener al menos una mayúscula, un número y un carácter especial",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "RN31: Contraseña Común",
                        summary = "Contraseña en blacklist",
                        description = "Test: testRegistro_PasswordComun()",
                        value = """
                            {
                              "success": false,
                              "message": "Contraseña demasiado común, elige una más segura",
                              "data": null
                            }
                            """
                    )
                }
            )
        )
    })
    @RequestBody(
        description = "Datos del nuevo usuario",
        required = true,
        content = @Content(
            examples = {
                @ExampleObject(
                    name = "✅ Registro Válido",
                    summary = "Ejemplo cumple RN30 y RN31",
                    description = "Email válido RFC 5322 + contraseña 12+ chars con complejidad",
                    value = """
                        {
                          "email": "nuevo@ejemplo.com",
                          "password": "SecurePass2024!",
                          "nombre": "Juan",
                          "apellido": "Pérez",
                          "fechaNacimiento": "1990-05-15"
                        }
                        """
                ),
                @ExampleObject(
                    name = "❌ Email Inválido (RN30)",
                    summary = "Email sin formato válido",
                    description = "Rechaza email sin @ o con dominio inexistente",
                    value = """
                        {
                          "email": "emailinvalido",
                          "password": "SecurePass2024!",
                          "nombre": "Test",
                          "apellido": "Error"
                        }
                        """
                ),
                @ExampleObject(
                    name = "❌ Contraseña Débil (RN31)",
                    summary = "Contraseña < 12 caracteres",
                    description = "Rechaza contraseñas cortas o sin complejidad",
                    value = """
                        {
                          "email": "test@ejemplo.com",
                          "password": "Pass1!",
                          "nombre": "Test",
                          "apellido": "Error"
                        }
                        """
                ),
                @ExampleObject(
                    name = "❌ Contraseña Común (RN31)",
                    summary = "Contraseña en blacklist",
                    description = "Rechaza contraseñas comunes como 'password1234'",
                    value = """
                        {
                          "email": "test@ejemplo.com",
                          "password": "password1234",
                          "nombre": "Test",
                          "apellido": "Error"
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
        summary = "🔓 PÚBLICO - Iniciar sesión [RN03]", 
        description = """
            Autentica un usuario y retorna un token JWT. ACCESO PÚBLICO.
            
            **REGLAS DE NEGOCIO:**
            - RN03: Login falla si credenciales incorrectas o cuenta inactiva
            
            **UNIT TESTS:** testLogin_PasswordIncorrecto(), testLogin_CuentaInactiva()
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "✅ Login exitoso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Login Exitoso",
                    value = """
                        {
                          "success": true,
                          "message": "Login exitoso",
                          "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "email": "demo@nutritrack.com",
                            "nombre": "Demo",
                            "apellido": "Usuario",
                            "role": "ROLE_USER"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "❌ Credenciales inválidas o cuenta inactiva (RN03)",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "RN03: Email No Registrado",
                        summary = "Usuario no existe",
                        description = "Test: testLogin_EmailNoRegistrado()",
                        value = """
                            {
                              "success": false,
                              "message": "Credenciales inválidas",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "RN03: Contraseña Incorrecta",
                        summary = "Password no coincide",
                        description = "Test: testLogin_PasswordIncorrecto()",
                        value = """
                            {
                              "success": false,
                              "message": "Credenciales inválidas",
                              "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "RN03: Cuenta Inactiva",
                        summary = "Cuenta desactivada",
                        description = "Test: testLogin_CuentaInactiva()",
                        value = """
                            {
                              "success": false,
                              "message": "Cuenta inactiva o desactivada",
                              "data": null
                            }
                            """
                    )
                }
            )
        )
    })
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
