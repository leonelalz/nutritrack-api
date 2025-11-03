package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.ActualizarPerfilRequest;
import com.nutritrack.nutritrackapi.dto.response.ApiResponse;
import com.nutritrack.nutritrackapi.dto.response.PerfilUsuarioResponse;
import com.nutritrack.nutritrackapi.security.JwtUtil;
import com.nutritrack.nutritrackapi.service.PerfilUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Perfil de Usuario", description = "Endpoints para gestión del perfil y preferencias del usuario")
@RestController
@RequestMapping("/app/profile")
@RequiredArgsConstructor
public class AppProfileController {

    private final PerfilUsuarioService perfilUsuarioService;
    private final JwtUtil jwtUtil;

    @Operation(
        summary = "Obtener mi perfil",
        description = "Retorna el perfil completo del usuario autenticado, incluyendo preferencias de salud y etiquetas. TEMPORAL: acepta email como parámetro para testing sin JWT.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado",
            content = @Content)
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PerfilUsuarioResponse>> obtenerMiPerfil(
            HttpServletRequest request,
            @Parameter(description = "Email del usuario (solo para testing, será removido en v0.2.0)", example = "user@nutritrack.com")
            @RequestParam(required = false) String email) {
        
        Long perfilId;
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

    @Operation(
        summary = "Actualizar mi perfil",
        description = "Actualiza la información del perfil, preferencias de unidades, objetivo de salud, nivel de actividad y etiquetas de salud.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado",
            content = @Content)
    })
    @PutMapping
    public ResponseEntity<ApiResponse<PerfilUsuarioResponse>> actualizarMiPerfil(
            HttpServletRequest request,
            @Parameter(description = "Email del usuario (solo para testing)")
            @RequestParam(required = false) String email,
            @Valid @RequestBody ActualizarPerfilRequest updateRequest) {
        
        Long perfilId;
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

    @Operation(
        summary = "Eliminar mi cuenta",
        description = "Desactiva la cuenta del usuario (eliminación lógica). El usuario no podrá iniciar sesión después de esta acción.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cuenta eliminada exitosamente",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado",
            content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> eliminarMiCuenta(
            HttpServletRequest request,
            @Parameter(description = "Email del usuario (solo para testing)")
            @RequestParam(required = false) String email) {
        
        Long perfilId;
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
    private Long extraerPerfilIdDelToken(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Token no encontrado");
        }
        return jwtUtil.getPerfilIdFromToken(token);
    }
}
