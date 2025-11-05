package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.service.PerfilUsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "1. Administración de Perfiles (ADMIN)", description = "Módulo 1 - Gestión de perfiles de usuario - Solo ADMIN")
@RestController
@RequestMapping("/perfiles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    // 🧑‍💻 Actualizar nombre del perfil
    @PutMapping("/{id}")
    public ResponseEntity<PerfilUsuario> actualizarNombre(
            @PathVariable Long id,
            @RequestParam String nuevoNombre) {
        PerfilUsuario actualizado = perfilUsuarioService.actualizarNombre(id, nuevoNombre);
        return ResponseEntity.ok(actualizado);
    }

    // 🔍 Obtener perfil por id de cuenta
    @GetMapping("/cuenta/{idCuenta}")
    public ResponseEntity<PerfilUsuario> obtenerPorCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(perfilUsuarioService.obtenerPorCuenta(idCuenta));
    }

    // 📋 Listar todos los perfiles
    @GetMapping
    public ResponseEntity<List<PerfilUsuario>> listarTodos() {
        return ResponseEntity.ok(perfilUsuarioService.listarPerfiles());
    }

    // ❌ Eliminar perfil
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPerfil(@PathVariable Long id) {
        perfilUsuarioService.eliminarPerfil(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Perfil eliminado correctamente");
    }
}
