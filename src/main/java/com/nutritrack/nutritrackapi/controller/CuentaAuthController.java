package com.nutritrack.nutritrackapi.controller;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import com.nutritrack.nutritrackapi.service.CuentaAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "1. Administración de Cuentas (ADMIN)", description = "Módulo 1 - Gestión de cuentas de usuario - Solo ADMIN")
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaAuthController {

    private final CuentaAuthService cuentaAuthService;

    // 🔐 Cambiar contraseña
    @PutMapping("/{id}/password")
    public ResponseEntity<String> cambiarPassword(
            @PathVariable Long id,
            @RequestParam String nuevaPassword) {
        cuentaAuthService.cambiarPassword(id, nuevaPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // 🧩 Cambiar rol (ej. USER -> ADMIN)
    @PutMapping("/{id}/rol")
    public ResponseEntity<String> cambiarRol(
            @PathVariable Long id,
            @RequestParam Rol nuevoRol) {
        cuentaAuthService.cambiarRol(id, nuevoRol);
        return ResponseEntity.ok("Rol actualizado a: " + nuevoRol);
    }

    // 🚫 Desactivar cuenta
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarCuenta(@PathVariable Long id) {
        cuentaAuthService.desactivarCuenta(id);
        return ResponseEntity.ok("Cuenta desactivada correctamente");
    }

    // ✅ Reactivar cuenta
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<String> reactivarCuenta(@PathVariable Long id) {
        cuentaAuthService.reactivarCuenta(id);
        return ResponseEntity.ok("Cuenta reactivada correctamente");
    }

    // 🔎 Buscar cuenta por email
    @GetMapping("/buscar")
    public ResponseEntity<CuentaAuth> buscarPorEmail(@RequestParam String email) {
        Optional<CuentaAuth> cuenta = cuentaAuthService.buscarPorEmail(email);
        return cuenta.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
}
