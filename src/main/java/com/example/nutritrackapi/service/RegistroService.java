package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.exception.BusinessException;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de registros de actividades (comidas y ejercicios).
 * Módulo 5: US-21, US-22, US-23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroService {

    private final RegistroComidaRepository registroComidaRepository;
    private final RegistroEjercicioRepository registroEjercicioRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final ComidaRepository comidaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final UsuarioPlanRepository usuarioPlanRepository;
    private final UsuarioRutinaRepository usuarioRutinaRepository;
    private final PlanDiaRepository planDiaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;

    // ============================================================
    // US-22: Registrar Comida
    // ============================================================

    @Transactional
    public RegistroComidaResponse registrarComida(Long perfilUsuarioId, RegistroComidaRequest request) {
        log.info("Registrando comida {} para usuario {}", request.getComidaId(), perfilUsuarioId);

        // Validar que el perfil exista
        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuario no encontrado"));

        // Validar que la comida exista
        Comida comida = comidaRepository.findById(request.getComidaId())
                .orElseThrow(() -> new EntityNotFoundException("Comida no encontrada"));

        // Si se especifica un plan, validar que esté activo (RN21)
        UsuarioPlan usuarioPlan = null;
        if (request.getUsuarioPlanId() != null) {
            usuarioPlan = usuarioPlanRepository.findById(request.getUsuarioPlanId())
                    .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

            if (usuarioPlan.getEstado() == UsuarioPlan.EstadoAsignacion.PAUSADO) {
                throw new BusinessException("No se puede registrar comida en un plan pausado");
            }

            // Validar que el plan pertenece al usuario
            if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
                throw new BusinessException("El plan no pertenece al usuario");
            }
        }

        // Calcular calorías consumidas (RN25: Cálculo automático)
        // TODO: Implementar cálculo de calorías sumando ingredientes
        BigDecimal porciones = request.getPorciones() != null ? request.getPorciones() : BigDecimal.ONE;
        BigDecimal caloriasConsumidas = BigDecimal.ZERO; // Placeholder

        // Crear registro
        RegistroComida registro = RegistroComida.builder()
                .perfilUsuario(perfil)
                .comida(comida)
                .usuarioPlan(usuarioPlan)
                .fecha(request.getFecha() != null ? request.getFecha() : LocalDate.now())
                .hora(request.getHora() != null ? request.getHora() : LocalTime.now())
                .tipoComida(request.getTipoComida())
                .porciones(porciones)
                .caloriasConsumidas(caloriasConsumidas)
                .notas(request.getNotas())
                .build();

        registro = registroComidaRepository.save(registro);
        log.info("Comida registrada exitosamente con ID {}", registro.getId());

        return RegistroComidaResponse.fromEntity(registro);
    }

    // ============================================================
    // US-22: Registrar Ejercicio
    // ============================================================

    @Transactional
    public RegistroEjercicioResponse registrarEjercicio(Long perfilUsuarioId, RegistroEjercicioRequest request) {
        log.info("Registrando ejercicio {} para usuario {}", request.getEjercicioId(), perfilUsuarioId);

        // Validar que el perfil exista
        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuario no encontrado"));

        // Validar que el ejercicio exista
        Ejercicio ejercicio = ejercicioRepository.findById(request.getEjercicioId())
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado"));

        // Si se especifica una rutina, validar que esté activa (RN21)
        UsuarioRutina usuarioRutina = null;
        if (request.getUsuarioRutinaId() != null) {
            usuarioRutina = usuarioRutinaRepository.findById(request.getUsuarioRutinaId())
                    .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada"));

            if (usuarioRutina.getEstado() == UsuarioPlan.EstadoAsignacion.PAUSADO) {
                throw new BusinessException("No se puede registrar ejercicio en una rutina pausada");
            }

            // Validar que la rutina pertenece al usuario
            if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
                throw new BusinessException("La rutina no pertenece al usuario");
            }
        }

        // Estimar calorías quemadas (simplificado)
        // TODO: Implementar cálculo basado en ejercicio y duración
        BigDecimal caloriasQuemadas = BigDecimal.ZERO;

        // Crear registro
        RegistroEjercicio registro = RegistroEjercicio.builder()
                .perfilUsuario(perfil)
                .ejercicio(ejercicio)
                .usuarioRutina(usuarioRutina)
                .fecha(request.getFecha() != null ? request.getFecha() : LocalDate.now())
                .hora(request.getHora() != null ? request.getHora() : LocalTime.now())
                .seriesRealizadas(request.getSeries())
                .repeticionesRealizadas(request.getRepeticiones())
                .pesoUtilizado(request.getPesoKg())
                .duracionMinutos(request.getDuracionMinutos())
                .caloriasQuemadas(caloriasQuemadas)
                .notas(request.getNotas())
                .build();

        registro = registroEjercicioRepository.save(registro);
        log.info("Ejercicio registrado exitosamente con ID {}", registro.getId());

        return RegistroEjercicioResponse.fromEntity(registro);
    }

    // ============================================================
    // US-21: Ver Actividades del Día del Plan
    // ============================================================

    @Transactional(readOnly = true)
    public ActividadesDiaResponse obtenerActividadesDia(Long perfilUsuarioId, LocalDate fecha) {
        log.info("Obteniendo actividades del día {} para usuario {}", fecha, perfilUsuarioId);

        // Obtener plan activo del usuario
        UsuarioPlan planActivo = usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException("No hay plan activo"));

        // Calcular día actual del plan
        long diasDesdeInicio = java.time.temporal.ChronoUnit.DAYS.between(planActivo.getFechaInicio(), fecha);
        int diaActual = (int) diasDesdeInicio + 1;

        // Obtener comidas programadas para ese día
        List<PlanDia> comidasDelDia = planDiaRepository.findByPlanIdAndNumeroDia(planActivo.getPlan().getId(), diaActual);

        // Obtener registros de comidas del día
        List<RegistroComida> registros = registroComidaRepository.findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);

        // Mapear comidas con su estado de registro
        List<ActividadesDiaResponse.ComidaDiaInfo> comidas = comidasDelDia.stream()
                .map(planDia -> {
                    RegistroComida registro = registros.stream()
                            .filter(r -> r.getComida().getId().equals(planDia.getComida().getId()))
                            .findFirst()
                            .orElse(null);

                    // TODO: Calcular calorías desde ingredientes de la comida
                    BigDecimal caloriasComida = BigDecimal.ZERO;

                    return new ActividadesDiaResponse.ComidaDiaInfo(
                            planDia.getComida().getId(),
                            planDia.getComida().getNombre(),
                            planDia.getTipoComida().name(),
                            caloriasComida,
                            registro != null,
                            registro != null ? registro.getId() : null
                    );
                })
                .collect(Collectors.toList());

        // Calcular calorías consumidas
        BigDecimal caloriasConsumidas = registros.stream()
                .map(RegistroComida::getCaloriasConsumidas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal caloriasObjetivo = planActivo.getPlan().getObjetivo() != null
                ? planActivo.getPlan().getObjetivo().getCaloriasObjetivo()
                : BigDecimal.ZERO;

        return ActividadesDiaResponse.builder()
                .fecha(fecha)
                .diaActual(diaActual)
                .caloriasObjetivo(caloriasObjetivo)
                .caloriasConsumidas(caloriasConsumidas)
                .comidas(comidas)
                .build();
    }

    // ============================================================
    // US-21: Ver Ejercicios del Día de la Rutina
    // ============================================================

    @Transactional(readOnly = true)
    public EjerciciosDiaResponse obtenerEjerciciosDia(Long perfilUsuarioId, LocalDate fecha) {
        log.info("Obteniendo ejercicios del día {} para usuario {}", fecha, perfilUsuarioId);

        // Obtener rutina activa del usuario
        UsuarioRutina rutinaActiva = usuarioRutinaRepository.findRutinaActivaActual(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException("No hay rutina activa"));

        // Calcular semana actual
        long semanasDesdeInicio = java.time.temporal.ChronoUnit.WEEKS.between(rutinaActiva.getFechaInicio(), fecha);
        int semanaActual = (int) semanasDesdeInicio + 1;

        // Obtener ejercicios programados
        List<RutinaEjercicio> ejerciciosRutina = rutinaEjercicioRepository
                .findByRutinaIdOrderByOrdenAsc(rutinaActiva.getRutina().getId());

        // Obtener registros del día
        List<RegistroEjercicio> registros = registroEjercicioRepository
                .findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);

        // Mapear ejercicios con su estado de registro
        List<EjerciciosDiaResponse.EjercicioDiaInfo> ejercicios = ejerciciosRutina.stream()
                .map(re -> {
                    RegistroEjercicio registro = registros.stream()
                            .filter(r -> r.getEjercicio().getId().equals(re.getEjercicio().getId()))
                            .findFirst()
                            .orElse(null);

                    return EjerciciosDiaResponse.EjercicioDiaInfo.builder()
                            .ejercicioId(re.getEjercicio().getId())
                            .nombre(re.getEjercicio().getNombre())
                            .seriesObjetivo(re.getSeries())
                            .repeticionesObjetivo(re.getRepeticiones())
                            .pesoSugerido(re.getPeso())
                            .duracionMinutos(re.getDuracionMinutos())
                            .registrado(registro != null)
                            .registroId(registro != null ? registro.getId() : null)
                            .build();
                })
                .collect(Collectors.toList());

        return EjerciciosDiaResponse.builder()
                .fecha(fecha)
                .semanaActual(semanaActual)
                .ejercicios(ejercicios)
                .build();
    }

    // ============================================================
    // US-23: Eliminar Registro de Comida
    // ============================================================

    @Transactional
    public void eliminarRegistroComida(Long perfilUsuarioId, Long registroId) {
        log.info("Eliminando registro de comida {} para usuario {}", registroId, perfilUsuarioId);

        RegistroComida registro = registroComidaRepository.findById(registroId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de comida no encontrado"));

        // Validar que el registro pertenece al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessException("El registro no pertenece al usuario");
        }

        registroComidaRepository.delete(registro);
        log.info("Registro de comida {} eliminado exitosamente", registroId);
    }

    // ============================================================
    // US-23: Eliminar Registro de Ejercicio
    // ============================================================

    @Transactional
    public void eliminarRegistroEjercicio(Long perfilUsuarioId, Long registroId) {
        log.info("Eliminando registro de ejercicio {} para usuario {}", registroId, perfilUsuarioId);

        RegistroEjercicio registro = registroEjercicioRepository.findById(registroId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de ejercicio no encontrado"));

        // Validar que el registro pertenece al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessException("El registro no pertenece al usuario");
        }

        registroEjercicioRepository.delete(registro);
        log.info("Registro de ejercicio {} eliminado exitosamente", registroId);
    }

    // ============================================================
    // Métodos auxiliares para consultas
    // ============================================================

    @Transactional(readOnly = true)
    public List<RegistroComidaResponse> obtenerHistorialComidas(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        return registroComidaRepository.findByPerfilUsuarioIdAndFechaBetween(perfilUsuarioId, fechaInicio, fechaFin)
                .stream()
                .map(RegistroComidaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroEjercicioResponse> obtenerHistorialEjercicios(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        return registroEjercicioRepository.findByPerfilUsuarioIdAndFechaBetween(perfilUsuarioId, fechaInicio, fechaFin)
                .stream()
                .map(RegistroEjercicioResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
