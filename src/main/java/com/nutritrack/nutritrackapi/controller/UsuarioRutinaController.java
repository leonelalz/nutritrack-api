package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.AsignarRutinaRequest;
import com.nutritrack.nutritrackapi.dto.response.UsuarioRutinaResponse;
import com.nutritrack.nutritrackapi.security.UserDetailsServiceImpl;
import com.nutritrack.nutritrackapi.service.UsuarioRutinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario/rutinas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UsuarioRutinaController {

    private final UsuarioRutinaService usuarioRutinaService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping
    public ResponseEntity<UsuarioRutinaResponse> asignarRutina(
            @Valid @RequestBody AsignarRutinaRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioRutinaResponse response = usuarioRutinaService.asignarRutina(perfilUsuarioId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioRutinaResponse>> listarRutinasActivas(Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        List<UsuarioRutinaResponse> rutinas = usuarioRutinaService.listarRutinasActivas(perfilUsuarioId);
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRutinaResponse>> listarTodasLasRutinas(Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        List<UsuarioRutinaResponse> rutinas = usuarioRutinaService.listarTodasLasRutinas(perfilUsuarioId);
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRutinaResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioRutinaResponse rutina = usuarioRutinaService.buscarPorId(perfilUsuarioId, id);
        return ResponseEntity.ok(rutina);
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<UsuarioRutinaResponse> completarRutina(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioRutinaResponse response = usuarioRutinaService.completarRutina(perfilUsuarioId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<UsuarioRutinaResponse> cancelarRutina(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioRutinaResponse response = usuarioRutinaService.cancelarRutina(perfilUsuarioId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/avanzar-semana")
    public ResponseEntity<UsuarioRutinaResponse> avanzarSemana(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioRutinaResponse response = usuarioRutinaService.avanzarSemana(perfilUsuarioId, id);
        return ResponseEntity.ok(response);
    }
}
