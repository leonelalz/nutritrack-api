package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.ActualizarPerfilRequest;
import com.nutritrack.nutritrackapi.dto.response.PerfilUsuarioResponse;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.CuentaAuth;
import com.nutritrack.nutritrackapi.model.Etiqueta;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.model.UsuarioPerfilSalud;
import com.nutritrack.nutritrackapi.repository.CuentaAuthRepository;
import com.nutritrack.nutritrackapi.repository.EtiquetaRepository;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioPerfilSaludRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PerfilUsuarioService {

    private final PerfilUsuarioRepository perfilRepo;
    private final CuentaAuthRepository cuentaRepo;
    private final UsuarioPerfilSaludRepository perfilSaludRepo;
    private final EtiquetaRepository etiquetaRepo;

    /**
     * US-04: Obtener perfil completo del usuario
     */
    public PerfilUsuarioResponse obtenerPerfilCompleto(UUID perfilId) {
        PerfilUsuario perfil = perfilRepo.findById(perfilId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

        UsuarioPerfilSalud perfilSalud = perfilSaludRepo.findById(perfilId).orElse(null);

        return mapearAPerfilResponse(perfil, perfilSalud);
    }

    /**
     * Obtener ID de perfil por email de cuenta (para testing sin JWT)
     */
    public UUID obtenerPerfilIdPorEmail(String email) {
        CuentaAuth cuenta = cuentaRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con email: " + email));
        
        PerfilUsuario perfil = perfilRepo.findByCuenta_Id(cuenta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para la cuenta"));
        
        return perfil.getId();
    }

    /**
     * US-03, US-04: Actualizar perfil del usuario
     */
    public PerfilUsuarioResponse actualizarPerfil(UUID perfilId, ActualizarPerfilRequest request) {
        PerfilUsuario perfil = perfilRepo.findById(perfilId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

        // Actualizar datos básicos del perfil
        if (request.getNombre() != null) {
            perfil.setNombre(request.getNombre());
        }
        if (request.getUnidadesMedida() != null) {
            perfil.setUnidadesMedida(request.getUnidadesMedida());
        }

        perfil = perfilRepo.save(perfil);

        // Actualizar perfil de salud si existen datos
        if (request.getObjetivoActual() != null || request.getNivelActividadActual() != null 
            || request.getEtiquetasSaludIds() != null) {
            
            UsuarioPerfilSalud perfilSalud = perfilSaludRepo.findById(perfilId)
                    .orElse(UsuarioPerfilSalud.builder()
                            .idPerfil(perfilId)
                            .build());

            if (request.getObjetivoActual() != null) {
                perfilSalud.setObjetivoActual(request.getObjetivoActual());
            }
            if (request.getNivelActividadActual() != null) {
                perfilSalud.setNivelActividadActual(request.getNivelActividadActual());
            }
            if (request.getEtiquetasSaludIds() != null) {
                Set<Etiqueta> etiquetas = new HashSet<>(etiquetaRepo.findAllById(request.getEtiquetasSaludIds()));
                perfilSalud.setEtiquetasSalud(etiquetas);
            }

            perfilSaludRepo.save(perfilSalud);
        }

        return obtenerPerfilCompleto(perfilId);
    }

    /**
     * US-05: Eliminar cuenta del usuario (eliminación lógica)
     */
    public void eliminarCuenta(UUID perfilId) {
        PerfilUsuario perfil = perfilRepo.findById(perfilId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

        CuentaAuth cuenta = perfil.getCuenta();
        if (cuenta != null) {
            cuenta.setActive(false); // Desactivar cuenta en lugar de borrar
            cuentaRepo.save(cuenta);
        }
    }

    /**
     * Mapear entidades a DTO de respuesta
     */
    private PerfilUsuarioResponse mapearAPerfilResponse(PerfilUsuario perfil, UsuarioPerfilSalud perfilSalud) {
        PerfilUsuarioResponse.PerfilSaludDTO perfilSaludDTO = null;

        if (perfilSalud != null) {
            Set<PerfilUsuarioResponse.EtiquetaSaludDTO> etiquetasDTO = perfilSalud.getEtiquetasSalud() != null 
                ? perfilSalud.getEtiquetasSalud().stream()
                    .map(e -> PerfilUsuarioResponse.EtiquetaSaludDTO.builder()
                            .id(e.getId())
                            .nombre(e.getNombre())
                            .tipoEtiqueta(e.getTipoEtiqueta().name())
                            .build())
                    .collect(Collectors.toSet())
                : new HashSet<>();

            perfilSaludDTO = PerfilUsuarioResponse.PerfilSaludDTO.builder()
                    .objetivoActual(perfilSalud.getObjetivoActual())
                    .nivelActividadActual(perfilSalud.getNivelActividadActual())
                    .etiquetasSalud(etiquetasDTO)
                    .fechaActualizacion(perfilSalud.getFechaActualizacion())
                    .build();
        }

        return PerfilUsuarioResponse.builder()
                .profileId(perfil.getId())
                .nombre(perfil.getNombre())
                .unidadesMedida(perfil.getUnidadesMedida())
                .fechaInicioApp(perfil.getFechaInicioApp())
                .perfilSalud(perfilSaludDTO)
                .build();
    }

    // ==========================================
    // Métodos legacy (mantener compatibilidad)
    // ==========================================

    public PerfilUsuario actualizarNombre(UUID idPerfil, String nuevoNombre) {
        PerfilUsuario perfil = perfilRepo.findById(idPerfil)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con id: " + idPerfil));

        perfil.setNombre(nuevoNombre);
        return perfilRepo.save(perfil);
    }

    public PerfilUsuario obtenerPorCuenta(UUID idCuenta) {
        CuentaAuth cuenta = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + idCuenta));

        return perfilRepo.findByCuenta_Id(cuenta.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe perfil asociado a esta cuenta"));
    }

    public List<PerfilUsuario> listarPerfiles() {
        return perfilRepo.findAll();
    }

    public void eliminarPerfil(UUID idPerfil) {
        if (!perfilRepo.existsById(idPerfil)) {
            throw new ResourceNotFoundException("Perfil no encontrado");
        }
        perfilRepo.deleteById(idPerfil);
    }
}
