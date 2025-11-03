package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.EjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EjercicioResponse;
import com.nutritrack.nutritrackapi.service.EjercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejercicios")
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService ejercicioService;

    @PostMapping
    public ResponseEntity<EjercicioResponse> crearEjercicio(
            @Valid @RequestBody EjercicioRequest request) {
        EjercicioResponse response = ejercicioService.crearEjercicio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EjercicioResponse>> obtenerTodosEjercicios() {
        List<EjercicioResponse> response = ejercicioService.obtenerTodosEjercicios();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjercicioResponse> obtenerEjercicioPorId(@PathVariable Long id) {
        EjercicioResponse response = ejercicioService.obtenerEjercicioPorId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EjercicioResponse> actualizarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request) {
        EjercicioResponse response = ejercicioService.actualizarEjercicio(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEjercicio(@PathVariable Long id) {
        ejercicioService.eliminarEjercicio(id);
        return ResponseEntity.noContent().build();
    }
}