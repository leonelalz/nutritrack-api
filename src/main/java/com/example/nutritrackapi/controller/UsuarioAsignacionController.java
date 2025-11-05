package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.UsuarioPlanService;
import com.example.nutritrackapi.service.UsuarioRutinaService;
import com.example.nutritrackapi.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar asignaciones de planes y rutinas a usuarios.
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * Endpoints para US-18, US-19, US-20
 * 
 * SEGURIDAD: Todos los endpoints extraen el perfilUsuarioId del token JWT
 * automáticamente, evitando que usuarios accedan a datos de otros.
 */
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
@Tag(name = "Módulo 4: Asignación de Metas", description = "👤 USER - Activar, pausar, reanudar y completar planes/rutinas asignadas. SOLO USUARIOS REGULARES.")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioAsignacionController {

    private final UsuarioPlanService usuarioPlanService;
    private final UsuarioRutinaService usuarioRutinaService;
    private final PlanService planService;

    // ============================================================================
    // PLANES NUTRICIONALES
    // ============================================================================

    @PostMapping("/planes/activar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "👤 USER - US-18: Activar plan nutricional [RN17, RN32]",
        description = """
            Activa un plan nutricional para el usuario autenticado.
            
            **REGLAS DE NEGOCIO IMPLEMENTADAS:**
            - RN17: No permite duplicar el mismo plan si ya está activo
            - RN32: Validación cruzada de alérgenos (bloquea si plan contiene alérgenos del usuario)
            
            **VALIDACIONES AUTOMÁTICAS:**
            1. Query 5-join: Plan → PlanDia → Comida → ComidaIngrediente → Ingrediente → Etiqueta
            2. Intersección de alergias del usuario vs etiquetas de ingredientes del plan
            3. Si hay coincidencia, rechaza activación con mensaje específico de alérgenos
            
            **UNIT TESTS:** 37/37 ✅ en UsuarioPlanServiceTest.java
            - testActivarPlan_ConAlergenosIncompatibles() - RN32
            - testActivarPlan_MismoPlanActivo() - RN17
            - testActivarPlan_ExitoCuandoNoHayAlergias() - RN32
            
            SOLO USUARIOS REGULARES.
            """
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> activarPlan(
            Authentication authentication,
            @Valid @RequestBody ActivarPlanRequest request) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioPlanResponse response = usuarioPlanService.activarPlan(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Plan activado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/pausar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "👤 USER - US-19: Pausar plan nutricional",
        description = "Pausa un plan activo del usuario autenticado. RN19: No permite pausar si está completado/cancelado. SOLO USUARIOS REGULARES."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> pausarPlan(
            Authentication authentication,
            @PathVariable Long usuarioPlanId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioPlanResponse response = usuarioPlanService.pausarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan pausado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/reanudar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "👤 USER - US-19: Reanudar plan nutricional",
        description = "Reanuda un plan pausado del usuario autenticado. RN19: Solo permite reanudar planes pausados. SOLO USUARIOS REGULARES."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> reanudarPlan(
            Authentication authentication,
            @PathVariable Long usuarioPlanId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioPlanResponse response = usuarioPlanService.reanudarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan reanudado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/completar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Completar plan nutricional",
        description = "Marca el plan del usuario autenticado como completado. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> completarPlan(
            Authentication authentication,
            @PathVariable Long usuarioPlanId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioPlanResponse response = usuarioPlanService.completarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan completado exitosamente")
        );
    }

    @PatchMapping("/planes/{usuarioPlanId}/cancelar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Cancelar plan nutricional",
        description = "Cancela el plan del usuario autenticado. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> cancelarPlan(
            Authentication authentication,
            @PathVariable Long usuarioPlanId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioPlanResponse response = usuarioPlanService.cancelarPlan(perfilUsuarioId, usuarioPlanId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan cancelado")
        );
    }

    @GetMapping("/planes/activo")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obtener plan activo",
        description = "Obtiene el plan activo actual del usuario autenticado"
    )
    public ResponseEntity<ApiResponse<UsuarioPlanResponse>> obtenerPlanActivo(
            Authentication authentication) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioPlanResponse response = usuarioPlanService.obtenerPlanActivo(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Plan activo obtenido")
        );
    }

    @GetMapping("/planes")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar todos los planes del usuario",
        description = "Obtiene historial completo de planes del usuario autenticado (activos, pausados, completados, cancelados)"
    )
    public ResponseEntity<ApiResponse<List<UsuarioPlanResponse>>> obtenerPlanes(
            Authentication authentication) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        List<UsuarioPlanResponse> response = usuarioPlanService.obtenerPlanes(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Planes obtenidos")
        );
    }

    @GetMapping("/planes/activos")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar planes activos",
        description = "Obtiene todos los planes activos del usuario autenticado"
    )
    public ResponseEntity<ApiResponse<List<UsuarioPlanResponse>>> obtenerPlanesActivos(
            Authentication authentication) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
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
        description = "Activa una rutina de ejercicio para el usuario autenticado. RN17: No permite duplicados activos."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> activarRutina(
            Authentication authentication,
            @Valid @RequestBody ActivarRutinaRequest request) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioRutinaResponse response = usuarioRutinaService.activarRutina(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Rutina activada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/pausar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-19: Pausar rutina de ejercicio",
        description = "Pausa una rutina activa del usuario autenticado. RN19: No permite pausar si está completada/cancelada."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> pausarRutina(
            Authentication authentication,
            @PathVariable Long usuarioRutinaId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioRutinaResponse response = usuarioRutinaService.pausarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina pausada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/reanudar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-19: Reanudar rutina de ejercicio",
        description = "Reanuda una rutina pausada del usuario autenticado. RN19: Solo permite reanudar rutinas pausadas."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> reanudarRutina(
            Authentication authentication,
            @PathVariable Long usuarioRutinaId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioRutinaResponse response = usuarioRutinaService.reanudarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina reanudada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/completar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Completar rutina de ejercicio",
        description = "Marca la rutina del usuario autenticado como completada. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> completarRutina(
            Authentication authentication,
            @PathVariable Long usuarioRutinaId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioRutinaResponse response = usuarioRutinaService.completarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina completada exitosamente")
        );
    }

    @PatchMapping("/rutinas/{usuarioRutinaId}/cancelar")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "US-20: Cancelar rutina de ejercicio",
        description = "Cancela la rutina del usuario autenticado. RN26: Valida transiciones de estado."
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> cancelarRutina(
            Authentication authentication,
            @PathVariable Long usuarioRutinaId) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioRutinaResponse response = usuarioRutinaService.cancelarRutina(perfilUsuarioId, usuarioRutinaId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina cancelada")
        );
    }

    @GetMapping("/rutinas/activa")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obtener rutina activa",
        description = "Obtiene la rutina activa actual del usuario autenticado"
    )
    public ResponseEntity<ApiResponse<UsuarioRutinaResponse>> obtenerRutinaActiva(
            Authentication authentication) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        UsuarioRutinaResponse response = usuarioRutinaService.obtenerRutinaActiva(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutina activa obtenida")
        );
    }

    @GetMapping("/rutinas")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar todas las rutinas del usuario",
        description = "Obtiene historial completo de rutinas del usuario autenticado (activas, pausadas, completadas, canceladas)"
    )
    public ResponseEntity<ApiResponse<List<UsuarioRutinaResponse>>> obtenerRutinas(
            Authentication authentication) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinas(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutinas obtenidas")
        );
    }

    @GetMapping("/rutinas/activas")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar rutinas activas",
        description = "Obtiene todas las rutinas activas del usuario autenticado"
    )
    public ResponseEntity<ApiResponse<List<UsuarioRutinaResponse>>> obtenerRutinasActivas(
            Authentication authentication) {
        
        Long perfilUsuarioId = planService.obtenerPerfilUsuarioId(authentication.getName());
        List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinasActivas(perfilUsuarioId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Rutinas activas obtenidas")
        );
    }
}
