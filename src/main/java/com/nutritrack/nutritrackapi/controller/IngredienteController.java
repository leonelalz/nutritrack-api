package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.IngredienteRequest;
import com.nutritrack.nutritrackapi.dto.response.IngredienteResponse;
import com.nutritrack.nutritrackapi.service.IngredienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @PostMapping
    public ResponseEntity<IngredienteResponse> crearIngrediente(
            @Valid @RequestBody IngredienteRequest request) {
        IngredienteResponse response = ingredienteService.crearIngrediente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IngredienteResponse>> obtenerTodosIngredientes() {
        List<IngredienteResponse> response = ingredienteService.obtenerTodosIngredientes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponse> obtenerIngredientePorId(@PathVariable Long id) {
        IngredienteResponse response = ingredienteService.obtenerIngredientePorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredienteResponse> actualizarIngrediente(
            @PathVariable Long id,
            @Valid @RequestBody IngredienteRequest request) {
        IngredienteResponse response = ingredienteService.actualizarIngrediente(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIngrediente(@PathVariable Long id) {
        ingredienteService.eliminarIngrediente(id);
        return ResponseEntity.noContent().build();
    }
}
