package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.AsignarRutinaRequest;
import com.nutritrack.nutritrackapi.dto.response.UsuarioRutinaResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.model.Rutina;
import com.nutritrack.nutritrackapi.model.UsuarioRutina;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.RutinaRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioRutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioRutinaService {

    private final UsuarioRutinaRepository usuarioRutinaRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final RutinaRepository rutinaRepository;

    @Transactional
    public UsuarioRutinaResponse asignarRutina(Long perfilUsuarioId, AsignarRutinaRequest request) {
        // Validar que el usuario exista
        PerfilUsuario perfilUsuario = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + perfilUsuarioId));

        // Validar que la rutina exista
        Rutina rutina = rutinaRepository.findById(request.getRutinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + request.getRutinaId()));

        // Validar que la rutina esté activa
        if (!rutina.getActivo()) {
            throw new BusinessRuleException("No se puede asignar una rutina inactiva");
        }

        // Calcular fecha fin basada en la duración de la rutina (semanas a días)
        LocalDate fechaFin = request.getFechaInicio().plusWeeks(rutina.getDuracionSemanas()).minusDays(1);

        // Validar que no haya solapamiento con otras rutinas activas
        if (usuarioRutinaRepository.existsOverlappingActiveRutina(perfilUsuarioId, request.getFechaInicio(), fechaFin)) {
            throw new BusinessRuleException("Ya existe una rutina activa que se solapa con las fechas especificadas");
        }

        // Crear la asignación
        UsuarioRutina usuarioRutina = UsuarioRutina.builder()
                .perfilUsuario(perfilUsuario)
                .rutina(rutina)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(fechaFin)
                .estado(EstadoAsignacion.ACTIVO)
                .semanaActual(1)
                .notas(request.getNotas())
                .build();

        usuarioRutina = usuarioRutinaRepository.save(usuarioRutina);

        return convertirAResponse(usuarioRutina);
    }

    @Transactional(readOnly = true)
    public List<UsuarioRutinaResponse> listarRutinasActivas(Long perfilUsuarioId) {
        List<UsuarioRutina> rutinas = usuarioRutinaRepository.findByPerfilUsuarioIdAndEstadoWithRutina(
                perfilUsuarioId, EstadoAsignacion.ACTIVO);
        return rutinas.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioRutinaResponse> listarTodasLasRutinas(Long perfilUsuarioId) {
        List<UsuarioRutina> rutinas = usuarioRutinaRepository.findByPerfilUsuarioId(perfilUsuarioId);
        return rutinas.stream()
                .map(ur -> {
                    UsuarioRutina usuarioRutinaConRutina = usuarioRutinaRepository.findByIdWithRutina(ur.getId())
                            .orElse(ur);
                    return convertirAResponse(usuarioRutinaConRutina);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioRutinaResponse buscarPorId(Long perfilUsuarioId, Long usuarioRutinaId) {
        UsuarioRutina usuarioRutina = usuarioRutinaRepository.findByIdWithRutina(usuarioRutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada con ID: " + usuarioRutinaId));

        // Validar que la rutina pertenezca al usuario
        if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de rutina no pertenece al usuario especificado");
        }

        return convertirAResponse(usuarioRutina);
    }

    @Transactional
    public UsuarioRutinaResponse completarRutina(Long perfilUsuarioId, Long usuarioRutinaId) {
        UsuarioRutina usuarioRutina = usuarioRutinaRepository.findById(usuarioRutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada con ID: " + usuarioRutinaId));

        // Validar que la rutina pertenezca al usuario
        if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de rutina no pertenece al usuario especificado");
        }

        // Validar que la rutina esté activa
        if (usuarioRutina.getEstado() != EstadoAsignacion.ACTIVO) {
            throw new BusinessRuleException("Solo se pueden completar rutinas en estado ACTIVO");
        }

        usuarioRutina.completar();
        usuarioRutina = usuarioRutinaRepository.save(usuarioRutina);

        return convertirAResponse(usuarioRutina);
    }

    @Transactional
    public UsuarioRutinaResponse cancelarRutina(Long perfilUsuarioId, Long usuarioRutinaId) {
        UsuarioRutina usuarioRutina = usuarioRutinaRepository.findById(usuarioRutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada con ID: " + usuarioRutinaId));

        // Validar que la rutina pertenezca al usuario
        if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de rutina no pertenece al usuario especificado");
        }

        // Validar que la rutina esté activa
        if (usuarioRutina.getEstado() != EstadoAsignacion.ACTIVO) {
            throw new BusinessRuleException("Solo se pueden cancelar rutinas en estado ACTIVO");
        }

        usuarioRutina.cancelar();
        usuarioRutina = usuarioRutinaRepository.save(usuarioRutina);

        return convertirAResponse(usuarioRutina);
    }

    @Transactional
    public UsuarioRutinaResponse avanzarSemana(Long perfilUsuarioId, Long usuarioRutinaId) {
        UsuarioRutina usuarioRutina = usuarioRutinaRepository.findById(usuarioRutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada con ID: " + usuarioRutinaId));

        // Validar que la rutina pertenezca al usuario
        if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de rutina no pertenece al usuario especificado");
        }

        // Validar que la rutina esté activa
        if (usuarioRutina.getEstado() != EstadoAsignacion.ACTIVO) {
            throw new BusinessRuleException("Solo se puede avanzar semana en rutinas activas");
        }

        usuarioRutina.avanzarSemana();
        
        // Si alcanzó la última semana, completar automáticamente
        if (usuarioRutina.getSemanaActual().equals(usuarioRutina.getRutina().getDuracionSemanas())) {
            usuarioRutina.completar();
        }

        usuarioRutina = usuarioRutinaRepository.save(usuarioRutina);

        return convertirAResponse(usuarioRutina);
    }

    private UsuarioRutinaResponse convertirAResponse(UsuarioRutina usuarioRutina) {
        Rutina rutina = usuarioRutina.getRutina();
        PerfilUsuario perfil = usuarioRutina.getPerfilUsuario();

        // Calcular progreso
        BigDecimal progreso = calcularProgreso(usuarioRutina);

        // Calcular semanas completadas y restantes
        Integer semanasCompletadas = usuarioRutina.getSemanaActual() - 1;
        Integer semanasRestantes = rutina.getDuracionSemanas() - usuarioRutina.getSemanaActual() + 1;

        return UsuarioRutinaResponse.builder()
                .id(usuarioRutina.getId())
                .perfilUsuarioId(perfil.getId())
                .nombreUsuario(perfil.getNombre() + " " + perfil.getApellido())
                .rutinaId(rutina.getId())
                .nombreRutina(rutina.getNombre())
                .duracionSemanasRutina(rutina.getDuracionSemanas())
                .fechaInicio(usuarioRutina.getFechaInicio())
                .fechaFin(usuarioRutina.getFechaFin())
                .estado(usuarioRutina.getEstado())
                .semanaActual(usuarioRutina.getSemanaActual())
                .notas(usuarioRutina.getNotas())
                .createdAt(usuarioRutina.getCreatedAt())
                .progreso(progreso)
                .semanasCompletadas(semanasCompletadas)
                .semanasRestantes(semanasRestantes)
                .build();
    }

    private BigDecimal calcularProgreso(UsuarioRutina usuarioRutina) {
        if (usuarioRutina.getEstado() == EstadoAsignacion.COMPLETADO) {
            return new BigDecimal("100.00");
        }

        Integer duracionTotal = usuarioRutina.getRutina().getDuracionSemanas();
        Integer semanaActual = usuarioRutina.getSemanaActual();

        // Progreso = (semana actual / duración total) * 100
        return BigDecimal.valueOf(semanaActual)
                .divide(BigDecimal.valueOf(duracionTotal), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
