package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.service.PerfilUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/perfiles")
@RequiredArgsConstructor
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    // 🧑‍💻 Actualizar nombre del perfil
    @PutMapping("/{id}/nombre")
    public ResponseEntity<PerfilUsuario> actualizarNombre(
            @PathVariable UUID id,
            @RequestParam String nuevoNombre) {
        PerfilUsuario actualizado = perfilUsuarioService.actualizarNombre(id, nuevoNombre);
        return ResponseEntity.ok(actualizado);
    }

    // 🔁 Reiniciar fecha de inicio de la app
    @PatchMapping("/{id}/reiniciar-fecha")
    public ResponseEntity<PerfilUsuario> reiniciarFecha(@PathVariable UUID id) {
        PerfilUsuario actualizado = perfilUsuarioService.reiniciarFechaInicio(id);
        return ResponseEntity.ok(actualizado);
    }

    // 🔍 Obtener perfil por id de cuenta
    @GetMapping("/cuenta/{idCuenta}")
    public ResponseEntity<PerfilUsuario> obtenerPorCuenta(@PathVariable UUID idCuenta) {
        PerfilUsuario perfil = perfilUsuarioService.obtenerPorCuenta(idCuenta);
        return ResponseEntity.ok(perfil);
    }

    // 📋 Listar todos los perfiles
    @GetMapping
    public ResponseEntity<List<PerfilUsuario>> listarTodos() {
        return ResponseEntity.ok(perfilUsuarioService.listarPerfiles());
    }

    // ❌ Eliminar perfil
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPerfil(@PathVariable UUID id) {
        perfilUsuarioService.eliminarPerfil(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Perfil eliminado correctamente");
    }
}
