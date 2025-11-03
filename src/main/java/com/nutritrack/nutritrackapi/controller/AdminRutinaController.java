package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.ActualizarRutinaRequest;
import com.nutritrack.nutritrackapi.dto.request.AgregarEjercicioRutinaRequest;
import com.nutritrack.nutritrackapi.dto.request.CrearRutinaRequest;
import com.nutritrack.nutritrackapi.dto.response.RutinaDetalleResponse;
import com.nutritrack.nutritrackapi.dto.response.RutinaResponse;
import com.nutritrack.nutritrackapi.service.RutinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rutinas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Rutinas de Ejercicio (Admin)", description = "Gestión de rutinas de ejercicio - Solo ADMIN")
public class AdminRutinaController {

    private final RutinaService rutinaService;

    @PostMapping
    @Operation(summary = "Crear una nueva rutina", description = "Crea una rutina de ejercicio con duración y descripción")
    public ResponseEntity<RutinaResponse> crear(@Valid @RequestBody CrearRutinaRequest request) {
        RutinaResponse response = rutinaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas las rutinas", description = "Obtiene todas las rutinas de ejercicio")
    public ResponseEntity<List<RutinaResponse>> listarTodos() {
        List<RutinaResponse> rutinas = rutinaService.listarTodos();
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar rutinas activas", description = "Obtiene solo las rutinas marcadas como activas")
    public ResponseEntity<List<RutinaResponse>> listarActivos() {
        List<RutinaResponse> rutinas = rutinaService.listarActivos();
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rutina por ID", description = "Obtiene una rutina específica con calorías totales")
    public ResponseEntity<RutinaResponse> buscarPorId(@PathVariable Long id) {
        RutinaResponse response = rutinaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener detalle completo de la rutina", description = "Incluye todos los ejercicios con series, repeticiones y estadísticas")
    public ResponseEntity<RutinaDetalleResponse> buscarDetallePorId(@PathVariable Long id) {
        RutinaDetalleResponse response = rutinaService.buscarDetallePorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar rutinas por nombre", description = "Búsqueda parcial case-insensitive")
    public ResponseEntity<List<RutinaResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<RutinaResponse> rutinas = rutinaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    @Operation(summary = "Buscar rutinas por etiqueta", description = "Obtiene todas las rutinas activas con una etiqueta específica")
    public ResponseEntity<List<RutinaResponse>> buscarPorEtiqueta(@PathVariable Long etiquetaId) {
        List<RutinaResponse> rutinas = rutinaService.buscarPorEtiqueta(etiquetaId);
        return ResponseEntity.ok(rutinas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una rutina", description = "Actualiza nombre, descripción, duración o estado activo")
    public ResponseEntity<RutinaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRutinaRequest request) {
        RutinaResponse response = rutinaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una rutina", description = "Elimina la rutina y todos sus ejercicios asociados (cascade)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rutinaId}/ejercicios")
    @Operation(summary = "Agregar ejercicio a la rutina", description = "Asocia un ejercicio con series, repeticiones y duración")
    public ResponseEntity<RutinaDetalleResponse> agregarEjercicio(
            @PathVariable Long rutinaId,
            @Valid @RequestBody AgregarEjercicioRutinaRequest request) {
        RutinaDetalleResponse response = rutinaService.agregarEjercicio(rutinaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{rutinaId}/ejercicios/{ejercicioId}")
    @Operation(summary = "Remover ejercicio de la rutina", description = "Elimina un ejercicio específico y reordena los restantes")
    public ResponseEntity<RutinaDetalleResponse> removerEjercicio(
            @PathVariable Long rutinaId,
            @PathVariable Long ejercicioId) {
        RutinaDetalleResponse response = rutinaService.removerEjercicio(rutinaId, ejercicioId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{rutinaId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Agregar etiqueta a la rutina", description = "Asocia una etiqueta existente a la rutina")
    public ResponseEntity<RutinaResponse> agregarEtiqueta(
            @PathVariable Long rutinaId,
            @PathVariable Long etiquetaId) {
        RutinaResponse response = rutinaService.agregarEtiqueta(rutinaId, etiquetaId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{rutinaId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Remover etiqueta de la rutina", description = "Desasocia una etiqueta de la rutina")
    public ResponseEntity<RutinaResponse> removerEtiqueta(
            @PathVariable Long rutinaId,
            @PathVariable Long etiquetaId) {
        RutinaResponse response = rutinaService.removerEtiqueta(rutinaId, etiquetaId);
        return ResponseEntity.ok(response);
    }
}
