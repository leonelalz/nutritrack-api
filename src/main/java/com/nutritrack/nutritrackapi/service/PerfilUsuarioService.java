package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PerfilUsuarioService {

    private final PerfilUsuarioRepository perfilRepo;
    private final CuentaAuthRepository cuentaRepo;

    /**
     * Actualizar el nombre del perfil de un usuario.
     */
    public PerfilUsuario actualizarNombre(UUID idPerfil, String nuevoNombre) {
        PerfilUsuario perfil = perfilRepo.findById(idPerfil)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado con id: " + idPerfil));

        perfil.setName(nuevoNombre);
        return perfilRepo.save(perfil);
    }

    /**
     * Actualizar la fecha de inicio de la app (si es necesario reiniciar o resetear el progreso).
     */
    public PerfilUsuario reiniciarFechaInicio(UUID idPerfil) {
        PerfilUsuario perfil = perfilRepo.findById(idPerfil)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado con id: " + idPerfil));

        perfil.setFecha_inicio_app(LocalDate.now());
        return perfilRepo.save(perfil);
    }

    /**
     * Obtener perfil por ID de cuenta.
     */
    public PerfilUsuario obtenerPorCuenta(UUID idCuenta) {
        CuentaAuth cuenta = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con id: " + idCuenta));

        return perfilRepo.findByCuenta_Id(cuenta.getId())
                .orElseThrow(() -> new RuntimeException("No existe perfil asociado a esta cuenta"));
    }

    /**
     * Listar todos los perfiles de usuarios.
     */
    public List<PerfilUsuario> listarPerfiles() {
        return perfilRepo.findAll();
    }

    /**
     * Eliminar perfil de usuario (borrado físico).
     */
    public void eliminarPerfil(UUID idPerfil) {
        if (!perfilRepo.existsById(idPerfil)) {
            throw new RuntimeException("Perfil no encontrado");
        }
        perfilRepo.deleteById(idPerfil);
    }
}
