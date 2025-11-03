package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.CrearComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.ComidaResponse;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.service.ComidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "2. Comidas (ADMIN)", description = "Módulo 2 - Gestión de comidas con recetas e ingredientes - Solo ADMIN")
@RestController
@RequestMapping("/admin/comidas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Comidas (Admin)", description = "Gestión de comidas y recetas - Solo ADMIN")
public class AdminComidaController {
    
    private final ComidaService comidaService;
    
    @PostMapping
    @Operation(summary = "Crear comida", description = "Crea una nueva comida con su receta de ingredientes")
    public ResponseEntity<ComidaResponse> crear(@Valid @RequestBody CrearComidaRequest request) {
        ComidaResponse response = comidaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar comida", description = "Actualiza una comida y su receta")
    public ResponseEntity<ComidaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearComidaRequest request) {
        ComidaResponse response = comidaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar comida", description = "Elimina una comida y su receta del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener comida por ID", description = "Obtiene los detalles de una comida con valores nutricionales totales")
    public ResponseEntity<ComidaResponse> buscarPorId(@PathVariable Long id) {
        ComidaResponse response = comidaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar todas las comidas", description = "Obtiene todas las comidas del sistema")
    public ResponseEntity<List<ComidaResponse>> listarTodos() {
        List<ComidaResponse> response = comidaService.listarTodos();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar comidas por nombre", description = "Busca comidas que contengan el texto especificado")
    public ResponseEntity<List<ComidaResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<ComidaResponse> response = comidaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar por tipo de comida", description = "Obtiene comidas de un tipo específico")
    public ResponseEntity<List<ComidaResponse>> buscarPorTipo(@PathVariable TipoComida tipo) {
        List<ComidaResponse> response = comidaService.buscarPorTipo(tipo);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{comidaId}/ingredientes/{ingredienteId}")
    @Operation(summary = "Agregar ingrediente a receta", description = "Agrega un ingrediente con su cantidad a la receta de la comida")
    public ResponseEntity<ComidaResponse> agregarIngrediente(
            @PathVariable Long comidaId,
            @PathVariable Long ingredienteId,
            @RequestParam BigDecimal cantidad) {
        ComidaResponse response = comidaService.agregarIngrediente(comidaId, ingredienteId, cantidad);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{comidaId}/ingredientes/{ingredienteId}")
    @Operation(summary = "Remover ingrediente de receta", description = "Remueve un ingrediente de la receta de la comida")
    public ResponseEntity<ComidaResponse> removerIngrediente(
            @PathVariable Long comidaId,
            @PathVariable Long ingredienteId) {
        ComidaResponse response = comidaService.removerIngrediente(comidaId, ingredienteId);
        return ResponseEntity.ok(response);
    }
}
