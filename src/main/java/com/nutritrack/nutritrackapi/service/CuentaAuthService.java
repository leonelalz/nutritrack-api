package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.Rol;
import com.nutritrack.nutritrackapi.model.enums.TipoRol;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaAuthService {

    private final CuentaAuthRepository cuentaRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cambiar la contraseña de una cuenta.
     */
    public void cambiarPassword(UUID idCuenta, String nuevaPassword) {
        CuentaAuth cuenta = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + idCuenta));

        cuenta.setPassword(passwordEncoder.encode(nuevaPassword));
        cuentaRepo.save(cuenta);
    }

    /**
     * Desactivar una cuenta (soft delete).
     */
    public void desactivarCuenta(UUID idCuenta) {
        CuentaAuth cuenta = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + idCuenta));
        cuenta.setActive(false);
        cuentaRepo.save(cuenta);
    }

    /**
     * Cambiar el rol de una cuenta.
     */
    public void cambiarRol(UUID idCuenta, Rol nuevoRol) {
        CuentaAuth cuenta = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + idCuenta));
        cuenta.setRol(nuevoRol);
        cuentaRepo.save(cuenta);
    }

    /**
     * Buscar cuenta por email.
     */
    public Optional<CuentaAuth> buscarPorEmail(String email) {
        return cuentaRepo.findByEmail(email);
    }

    /**
     * Reactivar una cuenta previamente desactivada.
     */
    public void reactivarCuenta(UUID idCuenta) {
        CuentaAuth cuenta = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + idCuenta));
        cuenta.setActive(true);
        cuentaRepo.save(cuenta);
    }
}
