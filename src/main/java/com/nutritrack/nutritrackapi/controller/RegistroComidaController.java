package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.RegistrarComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.EstadisticasNutricionResponse;
import com.nutritrack.nutritrackapi.dto.response.RegistroComidaResponse;
import com.nutritrack.nutritrackapi.security.UserDetailsServiceImpl;
import com.nutritrack.nutritrackapi.service.RegistroComidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/usuario/registros/comidas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class RegistroComidaController {

    private final RegistroComidaService registroComidaService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping
    public ResponseEntity<RegistroComidaResponse> registrarComida(
            @Valid @RequestBody RegistrarComidaRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        RegistroComidaResponse response = registroComidaService.registrarComida(perfilUsuarioId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RegistroComidaResponse>> listarRegistros(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        
        List<RegistroComidaResponse> registros;
        if (fecha != null) {
            registros = registroComidaService.listarRegistrosPorFecha(perfilUsuarioId, fecha);
        } else if (fechaInicio != null && fechaFin != null) {
            registros = registroComidaService.listarRegistrosPorPeriodo(perfilUsuarioId, fechaInicio, fechaFin);
        } else {
            // Por defecto, listar registros de hoy
            registros = registroComidaService.listarRegistrosPorFecha(perfilUsuarioId, LocalDate.now());
        }
        
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroComidaResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        RegistroComidaResponse registro = registroComidaService.buscarPorId(perfilUsuarioId, id);
        return ResponseEntity.ok(registro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistro(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        registroComidaService.eliminarRegistro(perfilUsuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasNutricionResponse> obtenerEstadisticas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        EstadisticasNutricionResponse estadisticas = registroComidaService.obtenerEstadisticas(
                perfilUsuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }
}
