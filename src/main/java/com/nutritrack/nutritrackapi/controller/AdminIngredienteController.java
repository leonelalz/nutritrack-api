package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.CrearIngredienteRequest;
import com.nutritrack.nutritrackapi.dto.response.IngredienteResponse;
import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.service.IngredienteService;
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
@RequestMapping("/admin/ingredientes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Ingredientes (Admin)", description = "Gestión de ingredientes - Solo ADMIN")
public class AdminIngredienteController {
    
    private final IngredienteService ingredienteService;
    
    @PostMapping
    @Operation(summary = "Crear ingrediente", description = "Crea un nuevo ingrediente con información nutricional")
    public ResponseEntity<IngredienteResponse> crear(@Valid @RequestBody CrearIngredienteRequest request) {
        IngredienteResponse response = ingredienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ingrediente", description = "Actualiza un ingrediente existente")
    public ResponseEntity<IngredienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearIngredienteRequest request) {
        IngredienteResponse response = ingredienteService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ingrediente", description = "Elimina un ingrediente del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingredienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener ingrediente por ID", description = "Obtiene los detalles de un ingrediente")
    public ResponseEntity<IngredienteResponse> buscarPorId(@PathVariable Long id) {
        IngredienteResponse response = ingredienteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos los ingredientes", description = "Obtiene todos los ingredientes del sistema")
    public ResponseEntity<List<IngredienteResponse>> listarTodos() {
        List<IngredienteResponse> response = ingredienteService.listarTodos();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar ingredientes por nombre", description = "Busca ingredientes que contengan el texto especificado")
    public ResponseEntity<List<IngredienteResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<IngredienteResponse> response = ingredienteService.buscarPorNombre(nombre);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/grupo/{grupo}")
    @Operation(summary = "Buscar por grupo alimenticio", description = "Obtiene ingredientes de un grupo alimenticio específico")
    public ResponseEntity<List<IngredienteResponse>> buscarPorGrupo(@PathVariable GrupoAlimenticio grupo) {
        List<IngredienteResponse> response = ingredienteService.buscarPorGrupo(grupo);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{ingredienteId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Agregar etiqueta", description = "Asigna una etiqueta al ingrediente")
    public ResponseEntity<IngredienteResponse> agregarEtiqueta(
            @PathVariable Long ingredienteId,
            @PathVariable Long etiquetaId) {
        IngredienteResponse response = ingredienteService.agregarEtiqueta(ingredienteId, etiquetaId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{ingredienteId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Remover etiqueta", description = "Remueve una etiqueta del ingrediente")
    public ResponseEntity<IngredienteResponse> removerEtiqueta(
            @PathVariable Long ingredienteId,
            @PathVariable Long etiquetaId) {
        IngredienteResponse response = ingredienteService.removerEtiqueta(ingredienteId, etiquetaId);
        return ResponseEntity.ok(response);
    }
}
