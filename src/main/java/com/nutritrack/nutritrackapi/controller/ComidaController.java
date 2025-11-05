package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.ComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.ComidaResponse;
import com.nutritrack.nutritrackapi.service.ComidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comidas")
@RequiredArgsConstructor
public class ComidaController {

    private final ComidaService comidaService;

    @PostMapping
    public ResponseEntity<ComidaResponse> crearComida(
            @Valid @RequestBody ComidaRequest request) {
        ComidaResponse response = comidaService.crearComida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ComidaResponse>> obtenerTodasComidas() {
        List<ComidaResponse> response = comidaService.obtenerTodasComidas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComidaResponse> obtenerComidaPorId(@PathVariable Long id) {
        ComidaResponse response = comidaService.obtenerComidaPorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComidaResponse> actualizarComida(
            @PathVariable Long id,
            @Valid @RequestBody ComidaRequest request) {
        ComidaResponse response = comidaService.actualizarComida(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComida(@PathVariable Long id) {
        comidaService.eliminarComida(id);
        return ResponseEntity.noContent().build();
    }
}