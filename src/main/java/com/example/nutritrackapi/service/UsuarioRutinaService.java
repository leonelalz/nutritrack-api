package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.ActivarRutinaRequest;
import com.example.nutritrackapi.dto.UsuarioRutinaResponse;
import com.example.nutritrackapi.exception.BusinessException;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar asignaciones de rutinas de ejercicio a usuarios.
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * User Stories: US-18, US-19, US-20
 * Reglas de Negocio: RN17, RN18, RN19, RN26, RN33
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioRutinaService {

    private final UsuarioRutinaRepository usuarioRutinaRepository;
    private final RutinaRepository rutinaRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;

    /**
     * US-18: Activa una rutina de ejercicio para un usuario.
     * RN17: No permite duplicar la misma rutina activa.
     * RN18: Propone reemplazo si ya existe activa.
     * RN33: Valida contraindicaciones médicas (lesiones/condiciones del usuario vs ejercicios).
     */
    @Transactional
    public UsuarioRutinaResponse activarRutina(Long perfilUsuarioId, ActivarRutinaRequest request) {
        log.info("Activando rutina {} para usuario {}", request.getRutinaId(), perfilUsuarioId);

        // Verificar que el perfil existe
        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil de usuario no encontrado con ID: " + perfilUsuarioId));

        // Verificar que la rutina existe y está activa
        Rutina rutina = rutinaRepository.findById(request.getRutinaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rutina no encontrada con ID: " + request.getRutinaId()));

        if (!rutina.getActivo()) {
            throw new BusinessException("La rutina seleccionada no está disponible");
        }

        // RN33: Validar contraindicaciones médicas
        validarContraindicacionesUsuario(perfilUsuarioId, request.getRutinaId());

        // RN17: Verificar que no existe la MISMA rutina activa
        boolean mismaRutinaActiva = usuarioRutinaRepository.existsByPerfilUsuarioIdAndRutinaIdAndEstado(
                perfilUsuarioId,
                request.getRutinaId(),
                UsuarioPlan.EstadoAsignacion.ACTIVO
        );

        if (mismaRutinaActiva) {
            // RN18: Proponer reemplazo
            throw new BusinessException(
                    "Ya tienes esta rutina activa. Debes pausarla o cancelarla para reiniciarla."
            );
        }

        // Crear asignación
        UsuarioRutina usuarioRutina = new UsuarioRutina();
        usuarioRutina.setPerfilUsuario(perfil);
        usuarioRutina.setRutina(rutina);
        usuarioRutina.setFechaInicio(request.getFechaInicio() != null ? 
                request.getFechaInicio() : LocalDate.now());
        usuarioRutina.setFechaFin(usuarioRutina.getFechaInicio()
                .plusWeeks(rutina.getDuracionSemanas()).minusDays(1));
        usuarioRutina.setSemanaActual(1);
        usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
        usuarioRutina.setNotas(request.getNotas());

        UsuarioRutina saved = usuarioRutinaRepository.save(usuarioRutina);
        log.info("Rutina {} activada exitosamente para usuario {}", rutina.getId(), perfilUsuarioId);

        return UsuarioRutinaResponse.fromEntity(saved);
    }

    /**
     * US-19: Pausa una rutina activa.
     * RN19: No permite pausar si está en estado final.
     */
    @Transactional
    public UsuarioRutinaResponse pausarRutina(Long perfilUsuarioId, Long usuarioRutinaId) {
        log.info("Pausando rutina {} del usuario {}", usuarioRutinaId, perfilUsuarioId);

        UsuarioRutina usuarioRutina = obtenerRutinaDeUsuario(perfilUsuarioId, usuarioRutinaId);

        // RN19: Validar que se puede pausar
        if (!usuarioRutina.puedeSerPausada()) {
            if (usuarioRutina.esEstadoFinal()) {
                throw new BusinessException(
                        "No puedes pausar una rutina que ya está completada o cancelada"
                );
            }
            throw new BusinessException(
                    "Solo puedes pausar una rutina que esté activa. Estado actual: " + 
                    usuarioRutina.getEstado()
            );
        }

        usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
        UsuarioRutina saved = usuarioRutinaRepository.save(usuarioRutina);

        log.info("Rutina {} pausada exitosamente", usuarioRutinaId);
        return UsuarioRutinaResponse.fromEntity(saved);
    }

    /**
     * US-19: Reanuda una rutina pausada.
     * RN19: No permite reanudar si está en estado final.
     */
    @Transactional
    public UsuarioRutinaResponse reanudarRutina(Long perfilUsuarioId, Long usuarioRutinaId) {
        log.info("Reanudando rutina {} del usuario {}", usuarioRutinaId, perfilUsuarioId);

        UsuarioRutina usuarioRutina = obtenerRutinaDeUsuario(perfilUsuarioId, usuarioRutinaId);

        // RN19: Validar que se puede reanudar
        if (!usuarioRutina.puedeSerReanudada()) {
            if (usuarioRutina.esEstadoFinal()) {
                throw new BusinessException(
                        "No puedes reanudar una rutina que ya está completada o cancelada"
                );
            }
            throw new BusinessException(
                    "Solo puedes reanudar una rutina que esté pausada. Estado actual: " + 
                    usuarioRutina.getEstado()
            );
        }

        usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
        // Recalcular fecha fin considerando semanas pausadas
        LocalDate hoy = LocalDate.now();
        long semanasTranscurridas = java.time.temporal.ChronoUnit.WEEKS.between(
                usuarioRutina.getFechaInicio(), hoy);
        long semanasPausadas = semanasTranscurridas - (usuarioRutina.getSemanaActual() - 1);
        
        if (semanasPausadas > 0) {
            usuarioRutina.setFechaFin(usuarioRutina.getFechaFin().plusWeeks(semanasPausadas));
        }

        UsuarioRutina saved = usuarioRutinaRepository.save(usuarioRutina);

        log.info("Rutina {} reanudada exitosamente", usuarioRutinaId);
        return UsuarioRutinaResponse.fromEntity(saved);
    }

    /**
     * US-20: Marca una rutina como completada.
     * RN26: Validación de transiciones de estado.
     */
    @Transactional
    public UsuarioRutinaResponse completarRutina(Long perfilUsuarioId, Long usuarioRutinaId) {
        log.info("Completando rutina {} del usuario {}", usuarioRutinaId, perfilUsuarioId);

        UsuarioRutina usuarioRutina = obtenerRutinaDeUsuario(perfilUsuarioId, usuarioRutinaId);

        if (usuarioRutina.esEstadoFinal()) {
            throw new BusinessException(
                    "La rutina ya está en estado final: " + usuarioRutina.getEstado()
            );
        }

        usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
        usuarioRutina.setFechaFin(LocalDate.now());

        UsuarioRutina saved = usuarioRutinaRepository.save(usuarioRutina);

        log.info("Rutina {} completada exitosamente", usuarioRutinaId);
        return UsuarioRutinaResponse.fromEntity(saved);
    }

    /**
     * US-20: Cancela una rutina.
     * RN26: Validación de transiciones de estado.
     */
    @Transactional
    public UsuarioRutinaResponse cancelarRutina(Long perfilUsuarioId, Long usuarioRutinaId) {
        log.info("Cancelando rutina {} del usuario {}", usuarioRutinaId, perfilUsuarioId);

        UsuarioRutina usuarioRutina = obtenerRutinaDeUsuario(perfilUsuarioId, usuarioRutinaId);

        if (usuarioRutina.esEstadoFinal()) {
            throw new BusinessException(
                    "La rutina ya está en estado final: " + usuarioRutina.getEstado()
            );
        }

        usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
        usuarioRutina.setFechaFin(LocalDate.now());

        UsuarioRutina saved = usuarioRutinaRepository.save(usuarioRutina);

        log.info("Rutina {} cancelada exitosamente", usuarioRutinaId);
        return UsuarioRutinaResponse.fromEntity(saved);
    }

    /**
     * Obtiene la rutina activa actual de un usuario.
     */
    public UsuarioRutinaResponse obtenerRutinaActiva(Long perfilUsuarioId) {
        return usuarioRutinaRepository.findRutinaActivaActual(perfilUsuarioId)
                .map(UsuarioRutinaResponse::fromEntity)
                .orElse(null);
    }

    /**
     * Obtiene todas las rutinas de un usuario.
     */
    public List<UsuarioRutinaResponse> obtenerRutinas(Long perfilUsuarioId) {
        return usuarioRutinaRepository.findByPerfilUsuarioId(perfilUsuarioId)
                .stream()
                .map(UsuarioRutinaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las rutinas activas de un usuario.
     */
    public List<UsuarioRutinaResponse> obtenerRutinasActivas(Long perfilUsuarioId) {
        return usuarioRutinaRepository.findAllRutinasActivas(perfilUsuarioId)
                .stream()
                .map(UsuarioRutinaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para obtener una rutina y verificar que pertenece al usuario.
     */
    private UsuarioRutina obtenerRutinaDeUsuario(Long perfilUsuarioId, Long usuarioRutinaId) {
        UsuarioRutina usuarioRutina = usuarioRutinaRepository.findById(usuarioRutinaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rutina de usuario no encontrada con ID: " + usuarioRutinaId));

        if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessException("Esta rutina no pertenece al usuario especificado");
        }

        return usuarioRutina;
    }

    /**
     * Actualiza la semana actual de todas las rutinas activas.
     * Este método puede ser llamado por un scheduler semanal.
     */
    @Transactional
    public void actualizarSemanasActuales() {
        List<UsuarioRutina> rutinasActivas = usuarioRutinaRepository
                .findByPerfilUsuarioIdAndEstado(null, UsuarioPlan.EstadoAsignacion.ACTIVO);

        for (UsuarioRutina rutina : rutinasActivas) {
            Integer semanaCalculada = rutina.calcularSemanaActual();
            if (semanaCalculada != null && !semanaCalculada.equals(rutina.getSemanaActual())) {
                rutina.setSemanaActual(semanaCalculada);
                
                // Auto-completar si llegó a la última semana
                if (semanaCalculada > rutina.getRutina().getDuracionSemanas()) {
                    rutina.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
                    rutina.setFechaFin(LocalDate.now());
                    log.info("Rutina {} auto-completada al llegar a la semana {}",
                            rutina.getId(), rutina.getRutina().getDuracionSemanas());
                }
            }
        }

        usuarioRutinaRepository.saveAll(rutinasActivas);
    }

    /**
     * RN33: Valida que la rutina no contenga ejercicios contraindicados para el usuario.
     * Compara las condiciones médicas/lesiones del usuario con las etiquetas de los ejercicios.
     * 
     * @param perfilUsuarioId ID del perfil del usuario
     * @param rutinaId ID de la rutina a validar
     * @throws BusinessException si hay contraindicaciones
     */
    private void validarContraindicacionesUsuario(Long perfilUsuarioId, Long rutinaId) {
        List<String> contraindicaciones = rutinaRepository.findContraindicacionesUsuarioRutina(
                perfilUsuarioId, rutinaId);

        if (!contraindicaciones.isEmpty()) {
            String listaContraindicaciones = String.join(", ", contraindicaciones);
            log.warn("RN33: Rutina {} rechazada para usuario {} por contraindicaciones: {}",
                    rutinaId, perfilUsuarioId, listaContraindicaciones);
            throw new BusinessException(
                    "Esta rutina contiene ejercicios que podrían afectar tu condición médica: " +
                    listaContraindicaciones + 
                    ". Por tu seguridad, te recomendamos elegir otra rutina o consultar con un profesional de la salud."
            );
        }
    }
}
