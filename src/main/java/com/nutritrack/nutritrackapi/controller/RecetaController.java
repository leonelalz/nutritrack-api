package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.RecetaRequest;
import com.nutritrack.nutritrackapi.dto.response.RecetaResponse;
import com.nutritrack.nutritrackapi.service.RecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comidas/{idComida}/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    //Agregar ingrediente a comida
    @PostMapping
    public ResponseEntity<RecetaResponse> agregarIngredienteAComida(
            @PathVariable Long idComida,
            @Valid @RequestBody RecetaRequest request) {
        RecetaResponse response = recetaService.agregarIngredienteAComida(idComida, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Obtener todos los ingredientes de una comida
    @GetMapping
    public ResponseEntity<List<RecetaResponse>> obtenerRecetasPorComida(
            @PathVariable Long idComida) {
        List<RecetaResponse> response = recetaService.obtenerRecetasPorComida(idComida);
        return ResponseEntity.ok(response);
    }

    //Actualizar cantidad/ingrediente
    @PatchMapping("/{idReceta}")
    public ResponseEntity<RecetaResponse> actualizarReceta(
            @PathVariable Long idComida,
            @PathVariable Long idReceta,
            @Valid @RequestBody RecetaRequest request) {
        RecetaResponse response = recetaService.actualizarReceta(idReceta, request);
        return ResponseEntity.ok(response);
    }

    //Eliminar receta por ID
    @DeleteMapping("/{idReceta}")
    public ResponseEntity<Void> eliminarReceta(
            @PathVariable Long idComida,
            @PathVariable Long idReceta) {
        recetaService.eliminarReceta(idReceta);
        return ResponseEntity.noContent().build();
    }

    //Quitar ingrediente específico
    @DeleteMapping("/ingrediente/{idIngrediente}")
    public ResponseEntity<Void> eliminarIngredienteDeComida(
            @PathVariable Long idComida,
            @PathVariable Long idIngrediente) {
        recetaService.eliminarIngredienteDeComida(idComida, idIngrediente);
        return ResponseEntity.noContent().build();
    }
}