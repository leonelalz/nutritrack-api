package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.ActualizarPlanRequest;
import com.nutritrack.nutritrackapi.dto.request.AgregarComidaPlanRequest;
import com.nutritrack.nutritrackapi.dto.request.CrearPlanRequest;
import com.nutritrack.nutritrackapi.dto.response.PlanDetalleResponse;
import com.nutritrack.nutritrackapi.dto.response.PlanResponse;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.service.PlanService;
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
@RequestMapping("/admin/planes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Planes Nutricionales (Admin)", description = "Gestión de planes nutricionales - Solo ADMIN")
public class AdminPlanController {

    private final PlanService planService;

    @PostMapping
    @Operation(summary = "Crear un nuevo plan nutricional", description = "Crea un plan con duración, descripción y objetivos nutricionales")
    public ResponseEntity<PlanResponse> crear(@Valid @RequestBody CrearPlanRequest request) {
        PlanResponse response = planService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos los planes", description = "Obtiene todos los planes nutricionales")
    public ResponseEntity<List<PlanResponse>> listarTodos() {
        List<PlanResponse> planes = planService.listarTodos();
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar planes activos", description = "Obtiene solo los planes marcados como activos")
    public ResponseEntity<List<PlanResponse>> listarActivos() {
        List<PlanResponse> planes = planService.listarActivos();
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plan por ID", description = "Obtiene un plan específico con nutrición promedio")
    public ResponseEntity<PlanResponse> buscarPorId(@PathVariable Long id) {
        PlanResponse response = planService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener detalle completo del plan", description = "Incluye todos los días, comidas y nutrición calculada por día")
    public ResponseEntity<PlanDetalleResponse> buscarDetallePorId(@PathVariable Long id) {
        PlanDetalleResponse response = planService.buscarDetallePorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar planes por nombre", description = "Búsqueda parcial case-insensitive")
    public ResponseEntity<List<PlanResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<PlanResponse> planes = planService.buscarPorNombre(nombre);
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    @Operation(summary = "Buscar planes por etiqueta", description = "Obtiene todos los planes activos con una etiqueta específica")
    public ResponseEntity<List<PlanResponse>> buscarPorEtiqueta(@PathVariable Long etiquetaId) {
        List<PlanResponse> planes = planService.buscarPorEtiqueta(etiquetaId);
        return ResponseEntity.ok(planes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un plan", description = "Actualiza nombre, descripción, duración, objetivos o estado activo")
    public ResponseEntity<PlanResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPlanRequest request) {
        PlanResponse response = planService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un plan", description = "Elimina el plan y todos sus días asociados (cascade)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        planService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{planId}/comidas")
    @Operation(summary = "Agregar comida al plan", description = "Asocia una comida a un día y tipo específico del plan")
    public ResponseEntity<PlanDetalleResponse> agregarComida(
            @PathVariable Long planId,
            @Valid @RequestBody AgregarComidaPlanRequest request) {
        PlanDetalleResponse response = planService.agregarComida(planId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{planId}/comidas")
    @Operation(summary = "Remover comida del plan", description = "Elimina una comida específica de un día del plan")
    public ResponseEntity<PlanDetalleResponse> removerComida(
            @PathVariable Long planId,
            @RequestParam Integer numeroDia,
            @RequestParam TipoComida tipoComida) {
        PlanDetalleResponse response = planService.removerComida(planId, numeroDia, tipoComida);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{planId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Agregar etiqueta al plan", description = "Asocia una etiqueta existente al plan")
    public ResponseEntity<PlanResponse> agregarEtiqueta(
            @PathVariable Long planId,
            @PathVariable Long etiquetaId) {
        PlanResponse response = planService.agregarEtiqueta(planId, etiquetaId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{planId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Remover etiqueta del plan", description = "Desasocia una etiqueta del plan")
    public ResponseEntity<PlanResponse> removerEtiqueta(
            @PathVariable Long planId,
            @PathVariable Long etiquetaId) {
        PlanResponse response = planService.removerEtiqueta(planId, etiquetaId);
        return ResponseEntity.ok(response);
    }
}
