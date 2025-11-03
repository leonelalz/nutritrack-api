package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.model.UsuarioPerfilSalud;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.repository.UsuarioPerfilSaludRepository;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.model.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsuarioPerfilSaludService {

    private final UsuarioPerfilSaludRepository perfilSaludRepo;
    private final PerfilUsuarioRepository perfilRepo;

    /**
     * Crear o actualizar el perfil de salud de un usuario.
     */
    public UsuarioPerfilSalud actualizarPerfilSalud(Long idCliente,
                                                    ObjetivoGeneral objetivo,
                                                    NivelActividad nivelActividad,
                                                    String alergias,
                                                    String condicionesMedicas) {

        // Buscar perfil de usuario
        PerfilUsuario cliente = perfilRepo.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + idCliente));

        // Buscar si ya tiene perfil de salud
        UsuarioPerfilSalud perfilSalud = perfilSaludRepo.findByIdPerfil(idCliente);

        if (perfilSalud == null) {
            // Crear nuevo perfil
            perfilSalud = UsuarioPerfilSalud.builder()
                    .idPerfil(idCliente)
                    .objetivoActual(objetivo)
                    .nivelActividadActual(nivelActividad)
                    .fechaActualizacion(LocalDate.now())
                    .perfil(cliente)
                    .build();
        } else {
            // Actualizar perfil existente
            perfilSalud.setObjetivoActual(objetivo);
            perfilSalud.setNivelActividadActual(nivelActividad);
            perfilSalud.setFechaActualizacion(LocalDate.now());
        }

        return perfilSaludRepo.save(perfilSalud);
    }

    /**
     * Obtener el perfil de salud de un usuario.
     */
    public UsuarioPerfilSalud obtenerPerfilSalud(Long idPerfil) {
        UsuarioPerfilSalud perfil = perfilSaludRepo.findByIdPerfil(idPerfil);
        if (perfil == null) {
            throw new RuntimeException("No existe un perfil de salud para el cliente con id: " + idPerfil);
        }
        return perfil;
    }
}
