package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.RegistrarEjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EstadisticasEjercicioResponse;
import com.nutritrack.nutritrackapi.dto.response.RegistroEjercicioResponse;
import com.nutritrack.nutritrackapi.security.UserDetailsServiceImpl;
import com.nutritrack.nutritrackapi.service.RegistroEjercicioService;
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
@RequestMapping("/api/usuario/registros/ejercicios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class RegistroEjercicioController {

    private final RegistroEjercicioService registroEjercicioService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping
    public ResponseEntity<RegistroEjercicioResponse> registrarEjercicio(
            @Valid @RequestBody RegistrarEjercicioRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        RegistroEjercicioResponse response = registroEjercicioService.registrarEjercicio(perfilUsuarioId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RegistroEjercicioResponse>> listarRegistros(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        
        List<RegistroEjercicioResponse> registros;
        if (fecha != null) {
            registros = registroEjercicioService.listarRegistrosPorFecha(perfilUsuarioId, fecha);
        } else if (fechaInicio != null && fechaFin != null) {
            registros = registroEjercicioService.listarRegistrosPorPeriodo(perfilUsuarioId, fechaInicio, fechaFin);
        } else {
            // Por defecto, listar registros de hoy
            registros = registroEjercicioService.listarRegistrosPorFecha(perfilUsuarioId, LocalDate.now());
        }
        
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroEjercicioResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        RegistroEjercicioResponse registro = registroEjercicioService.buscarPorId(perfilUsuarioId, id);
        return ResponseEntity.ok(registro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistro(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        registroEjercicioService.eliminarRegistro(perfilUsuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasEjercicioResponse> obtenerEstadisticas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        EstadisticasEjercicioResponse estadisticas = registroEjercicioService.obtenerEstadisticas(
                perfilUsuarioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }
}
