package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.LoginRequestDTO;
import com.nutritrack.nutritrackapi.dto.request.RegistroRequestDTO;
import com.nutritrack.nutritrackapi.dto.response.AuthResponse;
import com.nutritrack.nutritrackapi.service.AuthService;
import com.nutritrack.nutritrackapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 🧾 Registro de nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistroRequestDTO request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔐 Login
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
        UUID perfilId = jwtUtil.getPerfilIdFromToken(oldToken);

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
