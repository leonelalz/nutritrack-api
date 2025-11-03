package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.CrearEjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EjercicioResponse;
import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import com.nutritrack.nutritrackapi.service.EjercicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. Ejercicios (ADMIN)", description = "Módulo 2 - Gestión de ejercicios con calorías estimadas - Solo ADMIN")
@RestController
@RequestMapping("/admin/ejercicios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Ejercicios (Admin)", description = "Gestión de ejercicios - Solo ADMIN")
public class AdminEjercicioController {
    
    private final EjercicioService ejercicioService;
    
    @PostMapping
    @Operation(summary = "Crear ejercicio", description = "Crea un nuevo ejercicio con tipo, dificultad y calorías")
    public ResponseEntity<EjercicioResponse> crear(@Valid @RequestBody CrearEjercicioRequest request) {
        EjercicioResponse response = ejercicioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ejercicio", description = "Actualiza un ejercicio existente")
    public ResponseEntity<EjercicioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearEjercicioRequest request) {
        EjercicioResponse response = ejercicioService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ejercicio", description = "Elimina un ejercicio del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ejercicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener ejercicio por ID", description = "Obtiene los detalles de un ejercicio")
    public ResponseEntity<EjercicioResponse> buscarPorId(@PathVariable Long id) {
        EjercicioResponse response = ejercicioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos los ejercicios", description = "Obtiene todos los ejercicios del sistema")
    public ResponseEntity<List<EjercicioResponse>> listarTodos() {
        List<EjercicioResponse> response = ejercicioService.listarTodos();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar ejercicios por nombre", description = "Busca ejercicios que contengan el texto especificado")
    public ResponseEntity<List<EjercicioResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<EjercicioResponse> response = ejercicioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar por tipo de ejercicio", description = "Obtiene ejercicios de un tipo específico")
    public ResponseEntity<List<EjercicioResponse>> buscarPorTipo(@PathVariable TipoEjercicio tipo) {
        List<EjercicioResponse> response = ejercicioService.buscarPorTipo(tipo);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/musculo/{musculo}")
    @Operation(summary = "Buscar por músculo principal", description = "Obtiene ejercicios que trabajan un músculo específico")
    public ResponseEntity<List<EjercicioResponse>> buscarPorMusculo(@PathVariable MusculoPrincipal musculo) {
        List<EjercicioResponse> response = ejercicioService.buscarPorMusculo(musculo);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dificultad/{dificultad}")
    @Operation(summary = "Buscar por dificultad", description = "Obtiene ejercicios de una dificultad específica")
    public ResponseEntity<List<EjercicioResponse>> buscarPorDificultad(@PathVariable Dificultad dificultad) {
        List<EjercicioResponse> response = ejercicioService.buscarPorDificultad(dificultad);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{ejercicioId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Agregar etiqueta", description = "Asigna una etiqueta al ejercicio")
    public ResponseEntity<EjercicioResponse> agregarEtiqueta(
            @PathVariable Long ejercicioId,
            @PathVariable Long etiquetaId) {
        EjercicioResponse response = ejercicioService.agregarEtiqueta(ejercicioId, etiquetaId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{ejercicioId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Remover etiqueta", description = "Remueve una etiqueta del ejercicio")
    public ResponseEntity<EjercicioResponse> removerEtiqueta(
            @PathVariable Long ejercicioId,
            @PathVariable Long etiquetaId) {
        EjercicioResponse response = ejercicioService.removerEtiqueta(ejercicioId, etiquetaId);
        return ResponseEntity.ok(response);
    }
}
