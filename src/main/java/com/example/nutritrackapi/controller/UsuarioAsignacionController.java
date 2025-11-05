package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.UsuarioPlanService;
import com.example.nutritrackapi.service.UsuarioRutinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar asignaciones de planes y rutinas a usuarios.
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * Endpoints para US-18, US-19, US-20
 */
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
@Tag(name = "Módulo 4: Asignación de Metas", description = "Activar, pausar, reanudar y completar planes/rutinas")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioAsignacionController {

    private final UsuarioPlanService usuarioPlanService;
    private final UsuarioRutinaService usuarioRutinaService;

    // ============================================================================
    // PLANES NUTRICIONALES
    // ============================================================================

    @PostMapping("/planes/activar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-18: Activar plan nutricional",
        description = "Activa un plan nutricional para el usuario. RN17: No permite duplicados activos. RN18: Propone reemplazo si existe."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> activarPlan(
            @RequestParam Long perfilUsuarioId,
            @Valid @RequestBody ActivarPlanRequest request) {
        
        UsuarioPlanResponse response = usuarioPlanService.activarPlan(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Plan activado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/pausar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-19: Pausar plan nutricional",
        description = "Pausa un plan activo. RN19: No permite pausar si está completado/cancelado."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> pausarPlan(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioPlanId) {
        
        UsuarioPlanResponse response = usuarioPlanService.pausarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan pausado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/reanudar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-19: Reanudar plan nutricional",
        description = "Reanuda un plan pausado. RN19: Solo permite reanudar planes pausados."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> reanudarPlan(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioPlanId) {
        
        UsuarioPlanResponse response = usuarioPlanService.reanudarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan reanudado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/completar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Completar plan nutricional",
        description = "Marca el plan como completado. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> completarPlan(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioPlanId) {
        
        UsuarioPlanResponse response = usuarioPlanService.completarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan completado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/cancelar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Cancelar plan nutricional",
        description = "Cancela el plan. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> cancelarPlan(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioPlanId) {
        
        UsuarioPlanResponse response = usuarioPlanService.cancelarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan cancelado")
        );
    }

    @GetMapping("/planes/activo")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obtener plan activo",
        description = "Obtiene el plan activo actual del usuario"
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> obtenerPlanActivo(
            @RequestParam Long perfilUsuarioId) {
        
        UsuarioPlanResponse response = usuarioPlanService.obtenerPlanActivo(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan activo obtenido")
        );
    }

    @GetMapping("/planes")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar todos los planes del usuario",
        description = "Obtiene historial completo de planes (activos, pausados, completados, cancelados)"
    )
    public ResponseEntity<ApiResponse<List<UsuarioPlanResponse>>> obtenerPlanes(
            @RequestParam Long perfilUsuarioId) {
        
        List<UsuarioPlanResponse> response = usuarioPlanService.obtenerPlanes(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Planes obtenidos")
        );
    }

    @GetMapping("/planes/activos")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar planes activos",
        description = "Obtiene todos los planes activos del usuario"
    )
    public ResponseEntity<ApiResponse<List<UsuarioPlanResponse>>> obtenerPlanesActivos(
            @RequestParam Long perfilUsuarioId) {
        
        List<UsuarioPlanResponse> response = usuarioPlanService.obtenerPlanesActivos(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Planes activos obtenidos")
        );
    }

    // ============================================================================
    // RUTINAS DE EJERCICIO
    // ============================================================================

    @PostMapping("/rutinas/activar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-18: Activar rutina de ejercicio",
        description = "Activa una rutina de ejercicio para el usuario. RN17: No permite duplicados activos."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> activarRutina(
            @RequestParam Long perfilUsuarioId,
            @Valid @RequestBody ActivarRutinaRequest request) {
        
        UsuarioRutinaResponse response = usuarioRutinaService.activarRutina(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Rutina activada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/pausar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-19: Pausar rutina de ejercicio",
        description = "Pausa una rutina activa. RN19: No permite pausar si está completada/cancelada."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> pausarRutina(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioRutinaId) {
        
        UsuarioRutinaResponse response = usuarioRutinaService.pausarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina pausada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/reanudar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-19: Reanudar rutina de ejercicio",
        description = "Reanuda una rutina pausada. RN19: Solo permite reanudar rutinas pausadas."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> reanudarRutina(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioRutinaId) {
        
        UsuarioRutinaResponse response = usuarioRutinaService.reanudarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina reanudada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/completar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Completar rutina de ejercicio",
        description = "Marca la rutina como completada. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> completarRutina(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioRutinaId) {
        
        UsuarioRutinaResponse response = usuarioRutinaService.completarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina completada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/cancelar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Cancelar rutina de ejercicio",
        description = "Cancela la rutina. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> cancelarRutina(
            @RequestParam Long perfilUsuarioId,
            @PathVariable Long usuarioRutinaId) {
        
        UsuarioRutinaResponse response = usuarioRutinaService.cancelarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina cancelada")
        );
    }

    @GetMapping("/rutinas/activa")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obtener rutina activa",
        description = "Obtiene la rutina activa actual del usuario"
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> obtenerRutinaActiva(
            @RequestParam Long perfilUsuarioId) {
        
        UsuarioRutinaResponse response = usuarioRutinaService.obtenerRutinaActiva(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina activa obtenida")
        );
    }

    @GetMapping("/rutinas")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar todas las rutinas del usuario",
        description = "Obtiene historial completo de rutinas (activas, pausadas, completadas, canceladas)"
    )
    public ResponseEntity<ApiResponse<List<UsuarioRutinaResponse>>> obtenerRutinas(
            @RequestParam Long perfilUsuarioId) {
        
        List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinas(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutinas obtenidas")
        );
    }

    @GetMapping("/rutinas/activas")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar rutinas activas",
        description = "Obtiene todas las rutinas activas del usuario"
    )
    public ResponseEntity<ApiResponse<List<UsuarioRutinaResponse>>> obtenerRutinasActivas(
            @RequestParam Long perfilUsuarioId) {
        
        List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinasActivas(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutinas activas obtenidas")
        );
    }
}
