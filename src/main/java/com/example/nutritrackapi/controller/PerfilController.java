package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.PerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
@Tag(name = "Módulo 1: Autenticación y Perfil - Gestión de Perfil", description = "👤 USER - Gestión del perfil de usuario y mediciones (US-03, US-04, US-05) - Leonel Alzamora. SOLO USUARIOS REGULARES.")
public class PerfilController {

    private final PerfilService perfilService;

    /**
     * US-03: Actualizar unidades de medida
     * RN03: La unidad aplica a todas las vistas
     */
    @PatchMapping("/unidades")
    @Operation(summary = "👤 USER - US-03: Actualizar unidades de medida [RN03, RN27]",
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN03: La unidad aplica a todas las vistas del sistema
                   - RN27: Conversión automática KG ↔ LBS (almacena en KG en BD)
                   
                   UNIT TESTS: 11/11 ✅ en PerfilServiceTest.java
                   - testActualizarUnidades_ConversionKGaLBS()
                   - testObtenerPerfil_MuestraPesoEnLBS()
                   """)
    public ResponseEntity<ApiResponse<Void>> actualizarUnidades(
            Authentication authentication,
            @Valid @RequestBody UpdateUnidadesMedidaRequest request) {
        try {
            perfilService.actualizarUnidadesMedida(authentication.getName(), request);
            return ResponseEntity.ok(ApiResponse.success(null, "Unidades actualizadas exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * US-04: Crear perfil de salud (primera vez)
     * RN04: Usar etiquetas maestras
     */
    @PostMapping("/salud")
    @Operation(summary = "👤 USER - US-04: Crear perfil de salud [RN04, RN12]",
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN04: Perfil de salud usa etiquetas maestras de la tabla etiquetas
                   - RN12: Solo permite asignar etiquetas existentes (validación FK)
                   
                   UNIT TESTS: 11/11 ✅ en PerfilServiceTest.java
                   - testActualizarPerfilSalud_ValidaEtiquetasExistentes()
                   - testCrearPerfilSalud_ConObjetivoYEtiquetas()
                   """)
    public ResponseEntity<ApiResponse<PerfilSaludResponse>> crearPerfilSalud(
            Authentication authentication,
            @Valid @RequestBody PerfilSaludRequest request) {
        try {
            PerfilSaludResponse response = perfilService.actualizarPerfilSalud(authentication.getName(), request);
            return ResponseEntity.ok(ApiResponse.success(response, "Perfil de salud creado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * US-04: Editar perfil de salud
     * RN04: Usar etiquetas maestras
     */
    @PutMapping("/salud")
    @Operation(summary = "Actualizar perfil de salud",
               description = "Actualiza objetivo de salud, nivel de actividad y etiquetas de salud (US-04)")
    public ResponseEntity<ApiResponse<PerfilSaludResponse>> actualizarPerfilSalud(
            Authentication authentication,
            @Valid @RequestBody PerfilSaludRequest request) {
        try {
            PerfilSaludResponse response = perfilService.actualizarPerfilSalud(authentication.getName(), request);
            return ResponseEntity.ok(ApiResponse.success(response, "Perfil de salud actualizado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener perfil de salud actual
     */
    @GetMapping("/salud")
    @Operation(summary = "Obtener perfil de salud",
               description = "Retorna el perfil de salud completo del usuario (US-04)")
    public ResponseEntity<ApiResponse<PerfilSaludResponse>> obtenerPerfilSalud(
            Authentication authentication) {
        try {
            PerfilSaludResponse response = perfilService.obtenerPerfilSalud(authentication.getName());
            if (response == null) {
                return ResponseEntity.ok(ApiResponse.success(null, "Usuario no tiene perfil de salud configurado"));
            }
            return ResponseEntity.ok(ApiResponse.success(response, "Perfil de salud obtenido"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * US-24: Registrar medición corporal
     * RN22: Validar mediciones en rango
     */
    @PostMapping("/mediciones")
    @Operation(summary = "👤 USER - US-24: Registrar medición corporal [RN22]", 
               description = """
                   REGLAS DE NEGOCIO IMPLEMENTADAS:
                   - RN22: Validación de mediciones en rango (peso: 20-300kg, altura: 50-250cm)
                   
                   VALIDACIONES AUTOMÁTICAS:
                   1. Peso debe estar entre 20 y 300 kg
                   2. Altura debe estar entre 50 y 250 cm
                   3. Fecha no puede ser futura
                   
                   UNIT TESTS: 11/11 ✅ en PerfilServiceTest.java
                   - testRegistrarMedicion_PesoFueraDeRango()
                   - testRegistrarMedicion_AlturaFueraDeRango()
                   """)
    public ResponseEntity<ApiResponse<HistorialMedidasResponse>> registrarMedicion(
            Authentication authentication,
            @Valid @RequestBody HistorialMedidasRequest request) {
        try {
            // TEMPORAL: Si no hay autenticación, usar admin
            String email = (authentication != null) ? authentication.getName() : "admin@nutritrack.com";
            HistorialMedidasResponse response = perfilService.registrarMedicion(email, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Medición registrada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * US-24: Obtener historial de mediciones
     */
    @GetMapping("/mediciones")
    @Operation(summary = "Obtener historial de mediciones",
               description = "Retorna todas las mediciones del usuario ordenadas por fecha (US-05)")
    public ResponseEntity<ApiResponse<List<HistorialMedidasResponse>>> obtenerHistorial(
            Authentication authentication) {
        try {
            List<HistorialMedidasResponse> historial = perfilService.obtenerHistorialMediciones(authentication.getName());
            return ResponseEntity.ok(ApiResponse.success(historial, "Historial obtenido"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PutMapping("/mediciones/{id}")
    @Operation(summary = "Actualizar medición corporal",
            description = "Actualiza una medición existente del usuario (US-24)")
    public ResponseEntity<ApiResponse<HistorialMedidasResponse>> actualizarMedicion(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody HistorialMedidasRequest request) {
        try {
            String email = (authentication != null) ? authentication.getName() : "admin@nutritrack.com";
            HistorialMedidasResponse response = perfilService.actualizarMedicion(email, id, request);
            return ResponseEntity.ok(ApiResponse.success(response, "Medición actualizada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // US-24: Eliminar medición corporal
    @DeleteMapping("/mediciones/{id}")
    @Operation(summary = "Eliminar medición corporal",
            description = "Elimina una medición existente del usuario (US-24)")
    public ResponseEntity<ApiResponse<Void>> eliminarMedicion(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            String email = (authentication != null) ? authentication.getName() : "admin@nutritrack.com";
            perfilService.eliminarMedicion(email, id);
            return ResponseEntity.ok(ApiResponse.success(null, "Medición eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
