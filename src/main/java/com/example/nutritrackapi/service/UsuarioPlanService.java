package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.ActivarPlanRequest;
import com.example.nutritrackapi.dto.UsuarioPlanResponse;
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
 * Servicio para gestionar asignaciones de planes nutricionales a usuarios.
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * User Stories: US-18, US-19, US-20
 * Reglas de Negocio: RN17, RN18, RN19, RN26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioPlanService {

    private final UsuarioPlanRepository usuarioPlanRepository;
    private final PlanRepository planRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final UsuarioEtiquetasSaludRepository etiquetasSaludRepository;
    private final EtiquetaRepository etiquetaRepository;

    /**
     * US-18: Activa un plan nutricional para un usuario.
     * RN17: No permite duplicar el mismo plan activo.
     * RN18: Propone reemplazo si ya existe activo.
     */
    @Transactional
    public UsuarioPlanResponse activarPlan(Long perfilUsuarioId, ActivarPlanRequest request) {
        log.info("Activando plan {} para usuario {}", request.getPlanId(), perfilUsuarioId);

        // Verificar que el perfil existe
        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil de usuario no encontrado con ID: " + perfilUsuarioId));

        // Verificar que el plan existe y está activo
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Plan no encontrado con ID: " + request.getPlanId()));

        if (!plan.getActivo()) {
            throw new BusinessException("El plan seleccionado no está disponible");
        }

        // RN32: Validar que el plan no contenga alérgenos del usuario
        validarAlergenosUsuario(perfilUsuarioId, request.getPlanId());

        // RN17: Verificar que no existe el MISMO plan activo
        boolean mismoPlanActivo = usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                perfilUsuarioId,
                request.getPlanId(),
                UsuarioPlan.EstadoAsignacion.ACTIVO
        );

        if (mismoPlanActivo) {
            // RN18: Proponer reemplazo
            throw new BusinessException(
                    "Ya tienes este plan activo. Debes pausarlo o cancelarlo para reiniciarlo."
            );
        }

        // Crear asignación
        UsuarioPlan usuarioPlan = new UsuarioPlan();
        usuarioPlan.setPerfilUsuario(perfil);
        usuarioPlan.setPlan(plan);
        usuarioPlan.setFechaInicio(request.getFechaInicio() != null ? 
                request.getFechaInicio() : LocalDate.now());
        usuarioPlan.setFechaFin(usuarioPlan.getFechaInicio().plusDays(plan.getDuracionDias() - 1));
        usuarioPlan.setDiaActual(1);
        usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
        usuarioPlan.setNotas(request.getNotas());

        UsuarioPlan saved = usuarioPlanRepository.save(usuarioPlan);
        log.info("Plan {} activado exitosamente para usuario {}", plan.getId(), perfilUsuarioId);

        return UsuarioPlanResponse.fromEntity(saved);
    }

    /**
     * US-19: Pausa un plan activo.
     * RN19: No permite pausar si está en estado final.
     */
    @Transactional
    public UsuarioPlanResponse pausarPlan(Long perfilUsuarioId, Long usuarioPlanId) {
        log.info("Pausando plan {} del usuario {}", usuarioPlanId, perfilUsuarioId);

        UsuarioPlan usuarioPlan = obtenerPlanDeUsuario(perfilUsuarioId, usuarioPlanId);

        // RN19: Validar que se puede pausar
        if (!usuarioPlan.puedeSerPausado()) {
            if (usuarioPlan.esEstadoFinal()) {
                throw new BusinessException(
                        "No puedes pausar un plan que ya está completado o cancelado"
                );
            }
            throw new BusinessException(
                    "Solo puedes pausar un plan que esté activo. Estado actual: " + 
                    usuarioPlan.getEstado()
            );
        }

        usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
        UsuarioPlan saved = usuarioPlanRepository.save(usuarioPlan);

        log.info("Plan {} pausado exitosamente", usuarioPlanId);
        return UsuarioPlanResponse.fromEntity(saved);
    }

    /**
     * US-19: Reanuda un plan pausado.
     * RN19: No permite reanudar si está en estado final.
     */
    @Transactional
    public UsuarioPlanResponse reanudarPlan(Long perfilUsuarioId, Long usuarioPlanId) {
        log.info("Reanudando plan {} del usuario {}", usuarioPlanId, perfilUsuarioId);

        UsuarioPlan usuarioPlan = obtenerPlanDeUsuario(perfilUsuarioId, usuarioPlanId);

        // RN19: Validar que se puede reanudar
        if (!usuarioPlan.puedeSerReanudado()) {
            if (usuarioPlan.esEstadoFinal()) {
                throw new BusinessException(
                        "No puedes reanudar un plan que ya está completado o cancelado"
                );
            }
            throw new BusinessException(
                    "Solo puedes reanudar un plan que esté pausado. Estado actual: " + 
                    usuarioPlan.getEstado()
            );
        }

        usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
        // Recalcular fecha fin considerando días pausados
        LocalDate hoy = LocalDate.now();
        long diasPausados = java.time.temporal.ChronoUnit.DAYS.between(
                usuarioPlan.getFechaInicio(), hoy) - (usuarioPlan.getDiaActual() - 1);
        
        if (diasPausados > 0) {
            usuarioPlan.setFechaFin(usuarioPlan.getFechaFin().plusDays(diasPausados));
        }

        UsuarioPlan saved = usuarioPlanRepository.save(usuarioPlan);

        log.info("Plan {} reanudado exitosamente", usuarioPlanId);
        return UsuarioPlanResponse.fromEntity(saved);
    }

    /**
     * US-20: Marca un plan como completado.
     * RN26: Validación de transiciones de estado.
     */
    @Transactional
    public UsuarioPlanResponse completarPlan(Long perfilUsuarioId, Long usuarioPlanId) {
        log.info("Completando plan {} del usuario {}", usuarioPlanId, perfilUsuarioId);

        UsuarioPlan usuarioPlan = obtenerPlanDeUsuario(perfilUsuarioId, usuarioPlanId);

        if (usuarioPlan.esEstadoFinal()) {
            throw new BusinessException(
                    "El plan ya está en estado final: " + usuarioPlan.getEstado()
            );
        }

        usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
        usuarioPlan.setFechaFin(LocalDate.now());

        UsuarioPlan saved = usuarioPlanRepository.save(usuarioPlan);

        log.info("Plan {} completado exitosamente", usuarioPlanId);
        return UsuarioPlanResponse.fromEntity(saved);
    }

    /**
     * US-20: Cancela un plan.
     * RN26: Validación de transiciones de estado.
     */
    @Transactional
    public UsuarioPlanResponse cancelarPlan(Long perfilUsuarioId, Long usuarioPlanId) {
        log.info("Cancelando plan {} del usuario {}", usuarioPlanId, perfilUsuarioId);

        UsuarioPlan usuarioPlan = obtenerPlanDeUsuario(perfilUsuarioId, usuarioPlanId);

        if (usuarioPlan.esEstadoFinal()) {
            throw new BusinessException(
                    "El plan ya está en estado final: " + usuarioPlan.getEstado()
            );
        }

        usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
        usuarioPlan.setFechaFin(LocalDate.now());

        UsuarioPlan saved = usuarioPlanRepository.save(usuarioPlan);

        log.info("Plan {} cancelado exitosamente", usuarioPlanId);
        return UsuarioPlanResponse.fromEntity(saved);
    }

    /**
     * Obtiene el plan activo actual de un usuario.
     */
    public UsuarioPlanResponse obtenerPlanActivo(Long perfilUsuarioId) {
        return usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId)
                .map(UsuarioPlanResponse::fromEntity)
                .orElse(null);
    }

    /**
     * Obtiene todos los planes de un usuario.
     */
    public List<UsuarioPlanResponse> obtenerPlanes(Long perfilUsuarioId) {
        return usuarioPlanRepository.findByPerfilUsuarioId(perfilUsuarioId)
                .stream()
                .map(UsuarioPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los planes activos de un usuario.
     */
    public List<UsuarioPlanResponse> obtenerPlanesActivos(Long perfilUsuarioId) {
        return usuarioPlanRepository.findAllPlanesActivos(perfilUsuarioId)
                .stream()
                .map(UsuarioPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para obtener un plan y verificar que pertenece al usuario.
     */
    private UsuarioPlan obtenerPlanDeUsuario(Long perfilUsuarioId, Long usuarioPlanId) {
        UsuarioPlan usuarioPlan = usuarioPlanRepository.findById(usuarioPlanId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Plan de usuario no encontrado con ID: " + usuarioPlanId));

        if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessException("Este plan no pertenece al usuario especificado");
        }

        return usuarioPlan;
    }

    /**
     * Actualiza el día actual de todos los planes activos.
     * Este método puede ser llamado por un scheduler diario.
     */
    @Transactional
    public void actualizarDiasActuales() {
        List<UsuarioPlan> planesActivos = usuarioPlanRepository
                .findByPerfilUsuarioIdAndEstado(null, UsuarioPlan.EstadoAsignacion.ACTIVO);

        for (UsuarioPlan plan : planesActivos) {
            Integer diaCalculado = plan.calcularDiaActual();
            if (diaCalculado != null && !diaCalculado.equals(plan.getDiaActual())) {
                plan.setDiaActual(diaCalculado);
                
                // Auto-completar si llegó al último día
                if (diaCalculado > plan.getPlan().getDuracionDias()) {
                    plan.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
                    plan.setFechaFin(LocalDate.now());
                    log.info("Plan {} auto-completado al llegar al día {}",
                            plan.getId(), plan.getPlan().getDuracionDias());
                }
            }
        }

        usuarioPlanRepository.saveAll(planesActivos);
    }

    /**
     * RN32: Validar que el plan no contenga alérgenos del usuario
     * Método privado para validación cruzada de ingredientes con alergias
     */
    private void validarAlergenosUsuario(Long perfilUsuarioId, Long planId) {
        // 1. Obtener alérgenos del usuario (solo tipo ALERGIA)
        List<Long> alergenosUsuario = etiquetasSaludRepository
                .findEtiquetasAlergenosByPerfilUsuarioId(perfilUsuarioId);

        // Si el usuario no tiene alergias registradas, no hay problema
        if (alergenosUsuario == null || alergenosUsuario.isEmpty()) {
            return;
        }

        // 2. Obtener etiquetas de ingredientes del plan
        List<Long> etiquetasIngredientesPlan = planRepository
                .findEtiquetasIngredientesByPlanId(planId);

        // 3. Buscar intersección (alérgenos presentes en el plan)
        java.util.Set<Long> alergenosEnPlan = alergenosUsuario.stream()
                .filter(etiquetasIngredientesPlan::contains)
                .collect(java.util.stream.Collectors.toSet());

        // 4. Si hay coincidencias, RECHAZAR asignación
        if (!alergenosEnPlan.isEmpty()) {
            List<String> nombresAlergenos = etiquetaRepository
                    .findNombresByIds(alergenosEnPlan);

            throw new BusinessException(
                    "❌ No puedes activar este plan. Contiene ingredientes con alérgenos que tienes registrados: "
                            + String.join(", ", nombresAlergenos)
                            + ". Por tu seguridad, elige otro plan."
            );
        }

        log.info("✅ Validación de alérgenos exitosa para plan {} y usuario {}", planId, perfilUsuarioId);
    }
}
