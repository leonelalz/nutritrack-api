package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.ActualizarPerfilRequest;
import com.nutritrack.nutritrackapi.dto.response.ApiResponse;
import com.nutritrack.nutritrackapi.dto.response.PerfilUsuarioResponse;
import com.nutritrack.nutritrackapi.security.JwtUtil;
import com.nutritrack.nutritrackapi.service.PerfilUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/app/profile")
@RequiredArgsConstructor
public class AppProfileController {

    private final PerfilUsuarioService perfilUsuarioService;
    private final JwtUtil jwtUtil;

    /**
     * US-04: Obtener mi perfil
     * GET /api/v1/app/profile
     * 
     * TEMPORAL: Acepta email como query param para testing sin JWT
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PerfilUsuarioResponse>> obtenerMiPerfil(
            HttpServletRequest request,
            @RequestParam(required = false) String email) {
        
        UUID perfilId;
        if (email != null) {
            // Modo testing: buscar perfil por email
            perfilId = perfilUsuarioService.obtenerPerfilIdPorEmail(email);
        } else {
            // Modo producción: extraer del token JWT
            perfilId = extraerPerfilIdDelToken(request);
        }
        
        PerfilUsuarioResponse perfil = perfilUsuarioService.obtenerPerfilCompleto(perfilId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Perfil obtenido exitosamente", perfil)
        );
    }

    /**
     * US-03, US-04: Actualizar mi perfil
     * PUT /api/v1/app/profile
     * 
     * TEMPORAL: Acepta email como query param para testing sin JWT
     */
    @PutMapping
    public ResponseEntity<ApiResponse<PerfilUsuarioResponse>> actualizarMiPerfil(
            HttpServletRequest request,
            @RequestParam(required = false) String email,
            @Valid @RequestBody ActualizarPerfilRequest updateRequest) {
        
        UUID perfilId;
        if (email != null) {
            perfilId = perfilUsuarioService.obtenerPerfilIdPorEmail(email);
        } else {
            perfilId = extraerPerfilIdDelToken(request);
        }
        
        PerfilUsuarioResponse perfilActualizado = perfilUsuarioService.actualizarPerfil(perfilId, updateRequest);
        
        return ResponseEntity.ok(
            ApiResponse.success("Perfil actualizado exitosamente", perfilActualizado)
        );
    }

    /**
     * US-05: Eliminar mi cuenta
     * DELETE /api/v1/app/profile
     * 
     * TEMPORAL: Acepta email como query param para testing sin JWT
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> eliminarMiCuenta(
            HttpServletRequest request,
            @RequestParam(required = false) String email) {
        
        UUID perfilId;
        if (email != null) {
            perfilId = perfilUsuarioService.obtenerPerfilIdPorEmail(email);
        } else {
            perfilId = extraerPerfilIdDelToken(request);
        }
        
        perfilUsuarioService.eliminarCuenta(perfilId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Tu cuenta ha sido eliminada permanentemente", null)
        );
    }

    /**
     * Helper: Extrae el ID del perfil desde el token JWT
     */
    private UUID extraerPerfilIdDelToken(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Token no encontrado");
        }
        return jwtUtil.getPerfilIdFromToken(token);
    }
}
