package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.AsignarPlanRequest;
import com.nutritrack.nutritrackapi.dto.response.UsuarioPlanResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.model.Plan;
import com.nutritrack.nutritrackapi.model.UsuarioPlan;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.PlanRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioPlanService {

    private final UsuarioPlanRepository usuarioPlanRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final PlanRepository planRepository;

    @Transactional
    public UsuarioPlanResponse asignarPlan(Long perfilUsuarioId, AsignarPlanRequest request) {
        // Validar que el usuario exista
        PerfilUsuario perfilUsuario = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + perfilUsuarioId));

        // Validar que el plan exista
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + request.getPlanId()));

        // Validar que el plan esté activo
        if (!plan.getActivo()) {
            throw new BusinessRuleException("No se puede asignar un plan inactivo");
        }

        // Calcular fecha fin basada en la duración del plan
        LocalDate fechaFin = request.getFechaInicio().plusDays(plan.getDuracionDias() - 1);

        // Validar que no haya solapamiento con otros planes activos
        if (usuarioPlanRepository.existsOverlappingActivePlan(perfilUsuarioId, request.getFechaInicio(), fechaFin)) {
            throw new BusinessRuleException("Ya existe un plan activo que se solapa con las fechas especificadas");
        }

        // Crear la asignación
        UsuarioPlan usuarioPlan = UsuarioPlan.builder()
                .perfilUsuario(perfilUsuario)
                .plan(plan)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(fechaFin)
                .estado(EstadoAsignacion.ACTIVO)
                .diaActual(1)
                .notas(request.getNotas())
                .build();

        usuarioPlan = usuarioPlanRepository.save(usuarioPlan);

        return convertirAResponse(usuarioPlan);
    }

    @Transactional(readOnly = true)
    public List<UsuarioPlanResponse> listarPlanesActivos(Long perfilUsuarioId) {
        List<UsuarioPlan> planes = usuarioPlanRepository.findByPerfilUsuarioIdAndEstadoWithPlan(
                perfilUsuarioId, EstadoAsignacion.ACTIVO);
        return planes.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioPlanResponse> listarTodosLosPlanes(Long perfilUsuarioId) {
        List<UsuarioPlan> planes = usuarioPlanRepository.findByPerfilUsuarioId(perfilUsuarioId);
        return planes.stream()
                .map(up -> {
                    UsuarioPlan usuarioPlanConPlan = usuarioPlanRepository.findByIdWithPlan(up.getId())
                            .orElse(up);
                    return convertirAResponse(usuarioPlanConPlan);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioPlanResponse buscarPorId(Long perfilUsuarioId, Long usuarioPlanId) {
        UsuarioPlan usuarioPlan = usuarioPlanRepository.findByIdWithPlan(usuarioPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de plan no encontrada con ID: " + usuarioPlanId));

        // Validar que el plan pertenezca al usuario
        if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de plan no pertenece al usuario especificado");
        }

        return convertirAResponse(usuarioPlan);
    }

    @Transactional
    public UsuarioPlanResponse completarPlan(Long perfilUsuarioId, Long usuarioPlanId) {
        UsuarioPlan usuarioPlan = usuarioPlanRepository.findById(usuarioPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de plan no encontrada con ID: " + usuarioPlanId));

        // Validar que el plan pertenezca al usuario
        if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de plan no pertenece al usuario especificado");
        }

        // Validar que el plan esté activo
        if (usuarioPlan.getEstado() != EstadoAsignacion.ACTIVO) {
            throw new BusinessRuleException("Solo se pueden completar planes en estado ACTIVO");
        }

        usuarioPlan.completar();
        usuarioPlan = usuarioPlanRepository.save(usuarioPlan);

        return convertirAResponse(usuarioPlan);
    }

    @Transactional
    public UsuarioPlanResponse cancelarPlan(Long perfilUsuarioId, Long usuarioPlanId) {
        UsuarioPlan usuarioPlan = usuarioPlanRepository.findById(usuarioPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de plan no encontrada con ID: " + usuarioPlanId));

        // Validar que el plan pertenezca al usuario
        if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de plan no pertenece al usuario especificado");
        }

        // Validar que el plan esté activo
        if (usuarioPlan.getEstado() != EstadoAsignacion.ACTIVO) {
            throw new BusinessRuleException("Solo se pueden cancelar planes en estado ACTIVO");
        }

        usuarioPlan.cancelar();
        usuarioPlan = usuarioPlanRepository.save(usuarioPlan);

        return convertirAResponse(usuarioPlan);
    }

    @Transactional
    public UsuarioPlanResponse avanzarDia(Long perfilUsuarioId, Long usuarioPlanId) {
        UsuarioPlan usuarioPlan = usuarioPlanRepository.findById(usuarioPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de plan no encontrada con ID: " + usuarioPlanId));

        // Validar que el plan pertenezca al usuario
        if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Esta asignación de plan no pertenece al usuario especificado");
        }

        // Validar que el plan esté activo
        if (usuarioPlan.getEstado() != EstadoAsignacion.ACTIVO) {
            throw new BusinessRuleException("Solo se puede avanzar día en planes activos");
        }

        usuarioPlan.avanzarDia();
        
        // Si alcanzó el último día, completar automáticamente
        if (usuarioPlan.getDiaActual().equals(usuarioPlan.getPlan().getDuracionDias())) {
            usuarioPlan.completar();
        }

        usuarioPlan = usuarioPlanRepository.save(usuarioPlan);

        return convertirAResponse(usuarioPlan);
    }

    private UsuarioPlanResponse convertirAResponse(UsuarioPlan usuarioPlan) {
        Plan plan = usuarioPlan.getPlan();
        PerfilUsuario perfil = usuarioPlan.getPerfilUsuario();

        // Calcular progreso
        BigDecimal progreso = calcularProgreso(usuarioPlan);

        // Calcular días completados y restantes
        Integer diasCompletados = usuarioPlan.getDiaActual() - 1;
        Integer diasRestantes = plan.getDuracionDias() - usuarioPlan.getDiaActual() + 1;

        return UsuarioPlanResponse.builder()
                .id(usuarioPlan.getId())
                .perfilUsuarioId(perfil.getId())
                .nombreUsuario(perfil.getNombre() + " " + perfil.getApellido())
                .planId(plan.getId())
                .nombrePlan(plan.getNombre())
                .duracionDiasPlan(plan.getDuracionDias())
                .fechaInicio(usuarioPlan.getFechaInicio())
                .fechaFin(usuarioPlan.getFechaFin())
                .estado(usuarioPlan.getEstado())
                .diaActual(usuarioPlan.getDiaActual())
                .notas(usuarioPlan.getNotas())
                .createdAt(usuarioPlan.getCreatedAt())
                .progreso(progreso)
                .diasCompletados(diasCompletados)
                .diasRestantes(diasRestantes)
                .build();
    }

    private BigDecimal calcularProgreso(UsuarioPlan usuarioPlan) {
        if (usuarioPlan.getEstado() == EstadoAsignacion.COMPLETADO) {
            return new BigDecimal("100.00");
        }

        Integer duracionTotal = usuarioPlan.getPlan().getDuracionDias();
        Integer diaActual = usuarioPlan.getDiaActual();

        // Progreso = (día actual / duración total) * 100
        return BigDecimal.valueOf(diaActual)
                .divide(BigDecimal.valueOf(duracionTotal), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
