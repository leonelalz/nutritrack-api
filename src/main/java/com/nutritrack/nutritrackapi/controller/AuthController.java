package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.LoginRequestDTO;
import com.nutritrack.nutritrackapi.dto.request.RegistroRequestDTO;
import com.nutritrack.nutritrackapi.dto.response.AuthResponse;
import com.nutritrack.nutritrackapi.service.AuthService;
import com.nutritrack.nutritrackapi.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.UUID;

@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de sesiones")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario con email, contraseña y nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Email ya registrado o datos inválidos",
            content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistroRequestDTO request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Usuario deshabilitado",
            content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 🔁 Refresh token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        String oldToken = jwtUtil.extractTokenFromRequest(request);

        if (oldToken == null || !jwtUtil.validateToken(oldToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message("Token inválido o expirado")
                            .build());
        }

        String email = jwtUtil.getEmailFromToken(oldToken);
        String name = jwtUtil.getNameFromToken(oldToken);
        Long perfilId = jwtUtil.getPerfilIdFromToken(oldToken);

        String newToken = jwtUtil.generateToken(email, name, perfilId);

        AuthResponse response = AuthResponse.builder()
                .token(newToken)
                .message("Token renovado exitosamente")
                .build();

        return ResponseEntity.ok(response);
    }

    // 🚪 Logout seguro (invalidar token)
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);

        if (token != null) {
            jwtUtil.invalidateToken(token);
        }

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .message("Sesión cerrada correctamente")
                        .build()
        );
    }
}
