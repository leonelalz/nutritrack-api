package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.dto.request.AsignarPlanRequest;
import com.nutritrack.nutritrackapi.dto.response.UsuarioPlanResponse;
import com.nutritrack.nutritrackapi.security.UserDetailsServiceImpl;
import com.nutritrack.nutritrackapi.service.UsuarioPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario/planes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UsuarioPlanController {

    private final UsuarioPlanService usuarioPlanService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping
    public ResponseEntity<UsuarioPlanResponse> asignarPlan(
            @Valid @RequestBody AsignarPlanRequest request,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioPlanResponse response = usuarioPlanService.asignarPlan(perfilUsuarioId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioPlanResponse>> listarPlanesActivos(Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        List<UsuarioPlanResponse> planes = usuarioPlanService.listarPlanesActivos(perfilUsuarioId);
        return ResponseEntity.ok(planes);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioPlanResponse>> listarTodosLosPlanes(Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        List<UsuarioPlanResponse> planes = usuarioPlanService.listarTodosLosPlanes(perfilUsuarioId);
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPlanResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioPlanResponse plan = usuarioPlanService.buscarPorId(perfilUsuarioId, id);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<UsuarioPlanResponse> completarPlan(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioPlanResponse response = usuarioPlanService.completarPlan(perfilUsuarioId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<UsuarioPlanResponse> cancelarPlan(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioPlanResponse response = usuarioPlanService.cancelarPlan(perfilUsuarioId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/avanzar-dia")
    public ResponseEntity<UsuarioPlanResponse> avanzarDia(
            @PathVariable Long id,
            Authentication authentication) {
        Long perfilUsuarioId = userDetailsService.getPerfilUsuarioIdFromAuthentication(authentication);
        UsuarioPlanResponse response = usuarioPlanService.avanzarDia(perfilUsuarioId, id);
        return ResponseEntity.ok(response);
    }
}
