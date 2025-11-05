package com.example.nutritrackapi.controller;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para gestión de registros de actividades.
 * Módulo 5: US-21, US-22, US-23
 */
@RestController
@RequestMapping("/api/v1/usuario/registros")
@RequiredArgsConstructor
@Tag(name = "Registros", description = "Gestión de registros de comidas y ejercicios")
public class RegistroController {

    private final RegistroService registroService;

    // ============================================================
    // US-22: Registrar Comidas y Ejercicios
    // ============================================================

    @PostMapping("/comidas")
    @Operation(summary = "Registrar comida consumida", description = "US-22: Marcar comida como completada")
    public ResponseEntity<RegistroComidaResponse> registrarComida(
            @Valid @RequestBody RegistroComidaRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroComidaResponse response = registroService.registrarComida(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ejercicios")
    @Operation(summary = "Registrar ejercicio realizado", description = "US-22: Marcar ejercicio como completado")
    public ResponseEntity<RegistroEjercicioResponse> registrarEjercicio(
            @Valid @RequestBody RegistroEjercicioRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        RegistroEjercicioResponse response = registroService.registrarEjercicio(perfilUsuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // US-21: Ver Actividades del Día
    // ============================================================

    @GetMapping("/plan/hoy")
    @Operation(summary = "Ver actividades del plan de hoy", description = "US-21: Obtener comidas programadas y su estado")
    public ResponseEntity<ActividadesDiaResponse> obtenerActividadesHoy(Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        ActividadesDiaResponse response = registroService.obtenerActividadesDia(perfilUsuarioId, LocalDate.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan/dia")
    @Operation(summary = "Ver actividades del plan en una fecha", description = "US-21: Obtener comidas de un día específico")
    public ResponseEntity<ActividadesDiaResponse> obtenerActividadesDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        ActividadesDiaResponse response = registroService.obtenerActividadesDia(perfilUsuarioId, fecha);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rutina/hoy")
    @Operation(summary = "Ver ejercicios de la rutina de hoy", description = "US-21: Obtener ejercicios programados y su estado")
    public ResponseEntity<EjerciciosDiaResponse> obtenerEjerciciosHoy(Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        EjerciciosDiaResponse response = registroService.obtenerEjerciciosDia(perfilUsuarioId, LocalDate.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rutina/dia")
    @Operation(summary = "Ver ejercicios de la rutina en una fecha", description = "US-21: Obtener ejercicios de un día específico")
    public ResponseEntity<EjerciciosDiaResponse> obtenerEjerciciosDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        EjerciciosDiaResponse response = registroService.obtenerEjerciciosDia(perfilUsuarioId, fecha);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // US-23: Eliminar Registros
    // ============================================================

    @DeleteMapping("/comidas/{registroId}")
    @Operation(summary = "Eliminar registro de comida", description = "US-23: Desmarcar comida completada")
    public ResponseEntity<Void> eliminarRegistroComida(
            @PathVariable Long registroId,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        registroService.eliminarRegistroComida(perfilUsuarioId, registroId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ejercicios/{registroId}")
    @Operation(summary = "Eliminar registro de ejercicio", description = "US-23: Desmarcar ejercicio completado")
    public ResponseEntity<Void> eliminarRegistroEjercicio(
            @PathVariable Long registroId,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        registroService.eliminarRegistroEjercicio(perfilUsuarioId, registroId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // Consultas Históricas
    // ============================================================

    @GetMapping("/comidas/historial")
    @Operation(summary = "Obtener historial de comidas", description = "Consultar registros de comidas en un rango de fechas")
    public ResponseEntity<List<RegistroComidaResponse>> obtenerHistorialComidas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        List<RegistroComidaResponse> historial = registroService.obtenerHistorialComidas(
                perfilUsuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/ejercicios/historial")
    @Operation(summary = "Obtener historial de ejercicios", description = "Consultar registros de ejercicios en un rango de fechas")
    public ResponseEntity<List<RegistroEjercicioResponse>> obtenerHistorialEjercicios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = obtenerPerfilUsuarioId(authentication);
        List<RegistroEjercicioResponse> historial = registroService.obtenerHistorialEjercicios(
                perfilUsuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(historial);
    }

    // ============================================================
    // Utilidades
    // ============================================================

    private Long obtenerPerfilUsuarioId(Authentication authentication) {
        // TODO: Implementar obtención del perfilUsuarioId desde el token JWT
        // Por ahora retornamos un ID de ejemplo
        return 1L;
    }
}
