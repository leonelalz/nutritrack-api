package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.RegistrarEjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EstadisticasEjercicioResponse;
import com.nutritrack.nutritrackapi.dto.response.RegistroEjercicioResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistroEjercicioService {

    private final RegistroEjercicioRepository registroEjercicioRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRutinaRepository usuarioRutinaRepository;

    @Transactional
    public RegistroEjercicioResponse registrarEjercicio(Long perfilUsuarioId, RegistrarEjercicioRequest request) {
        // Validar que el usuario exista
        PerfilUsuario perfilUsuario = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + perfilUsuarioId));

        // Validar que el ejercicio exista
        Ejercicio ejercicio = ejercicioRepository.findById(request.getEjercicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + request.getEjercicioId()));

        // Validar la rutina si se proporcionó
        UsuarioRutina usuarioRutina = null;
        if (request.getUsuarioRutinaId() != null) {
            usuarioRutina = usuarioRutinaRepository.findById(request.getUsuarioRutinaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada con ID: " + request.getUsuarioRutinaId()));

            // Validar que la rutina pertenezca al usuario
            if (!usuarioRutina.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
                throw new BusinessRuleException("La rutina especificada no pertenece al usuario");
            }
        }

        // Calcular calorías quemadas basadas en duración
        BigDecimal caloriasQuemadas = calcularCaloriasQuemadas(ejercicio, request.getDuracionMinutos());

        // Crear el registro
        RegistroEjercicio registro = RegistroEjercicio.builder()
                .perfilUsuario(perfilUsuario)
                .ejercicio(ejercicio)
                .usuarioRutina(usuarioRutina)
                .fecha(request.getFecha())
                .hora(request.getHora())
                .seriesRealizadas(request.getSeriesRealizadas())
                .repeticionesRealizadas(request.getRepeticionesRealizadas())
                .pesoUtilizado(request.getPesoUtilizado())
                .duracionMinutos(request.getDuracionMinutos())
                .notas(request.getNotas())
                .caloriasQuemadas(caloriasQuemadas)
                .build();

        registro = registroEjercicioRepository.save(registro);

        return convertirAResponse(registro);
    }

    @Transactional(readOnly = true)
    public List<RegistroEjercicioResponse> listarRegistrosPorFecha(Long perfilUsuarioId, LocalDate fecha) {
        List<RegistroEjercicio> registros = registroEjercicioRepository.findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);
        return registros.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroEjercicioResponse> listarRegistrosPorPeriodo(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<RegistroEjercicio> registros = registroEjercicioRepository.findByPerfilUsuarioIdAndFechaBetween(
                perfilUsuarioId, fechaInicio, fechaFin);
        return registros.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistroEjercicioResponse buscarPorId(Long perfilUsuarioId, Long registroId) {
        RegistroEjercicio registro = registroEjercicioRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de ejercicio no encontrado con ID: " + registroId));

        // Validar que el registro pertenezca al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Este registro no pertenece al usuario especificado");
        }

        return convertirAResponse(registro);
    }

    @Transactional
    public void eliminarRegistro(Long perfilUsuarioId, Long registroId) {
        RegistroEjercicio registro = registroEjercicioRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de ejercicio no encontrado con ID: " + registroId));

        // Validar que el registro pertenezca al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Este registro no pertenece al usuario especificado");
        }

        registroEjercicioRepository.delete(registro);
    }

    @Transactional(readOnly = true)
    public EstadisticasEjercicioResponse obtenerEstadisticas(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener calorías totales quemadas
        BigDecimal caloriasTotales = registroEjercicioRepository.sumCaloriasQuemadasByPeriodo(
                perfilUsuarioId, fechaInicio, fechaFin);

        // Obtener duración total
        Integer duracionTotal = registroEjercicioRepository.sumDuracionMinutosByPeriodo(
                perfilUsuarioId, fechaInicio, fechaFin);

        // Obtener estadísticas por ejercicio
        List<Object[]> estadisticasPorEjercicio = registroEjercicioRepository.getEstadisticasPorEjercicio(
                perfilUsuarioId, fechaInicio, fechaFin);

        // Procesar top ejercicios
        List<EstadisticasEjercicioResponse.EstadisticaEjercicio> topEjercicios = new ArrayList<>();
        int totalRegistros = 0;

        for (Object[] row : estadisticasPorEjercicio) {
            String nombreEjercicio = (String) row[0];
            Long cantidad = (Long) row[1];
            BigDecimal calorias = (BigDecimal) row[2];
            Long duracion = (Long) row[3];

            totalRegistros += cantidad.intValue();

            EstadisticasEjercicioResponse.EstadisticaEjercicio estadistica =
                    EstadisticasEjercicioResponse.EstadisticaEjercicio.builder()
                            .nombreEjercicio(nombreEjercicio)
                            .vecesRealizado(cantidad.intValue())
                            .caloriasTotales(calorias)
                            .duracionTotal(duracion.intValue())
                            .build();

            topEjercicios.add(estadistica);
        }

        // Calcular promedio diario
        long diasEnPeriodo = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        BigDecimal caloriasPromedioDiario = diasEnPeriodo > 0
                ? caloriasTotales.divide(BigDecimal.valueOf(diasEnPeriodo), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return EstadisticasEjercicioResponse.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .totalRegistros(totalRegistros)
                .duracionTotalMinutos(duracionTotal)
                .caloriasTotalesQuemadas(caloriasTotales)
                .caloriasPromedioDiario(caloriasPromedioDiario)
                .ejerciciosMasRealizados(topEjercicios)
                .build();
    }

    private BigDecimal calcularCaloriasQuemadas(Ejercicio ejercicio, Integer duracionMinutos) {
        // Calorías por minuto = calorías estimadas / duración estándar
        BigDecimal caloriasPorMinuto = ejercicio.getCaloriasEstimadas()
                .divide(BigDecimal.valueOf(ejercicio.getDuracion()), 4, RoundingMode.HALF_UP);

        // Calorías quemadas = calorías por minuto × duración real
        return caloriasPorMinuto
                .multiply(BigDecimal.valueOf(duracionMinutos))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private RegistroEjercicioResponse convertirAResponse(RegistroEjercicio registro) {
        return RegistroEjercicioResponse.builder()
                .id(registro.getId())
                .perfilUsuarioId(registro.getPerfilUsuario().getId())
                .nombreUsuario(registro.getPerfilUsuario().getNombre() + " " + registro.getPerfilUsuario().getApellido())
                .ejercicioId(registro.getEjercicio().getId())
                .nombreEjercicio(registro.getEjercicio().getNombre())
                .usuarioRutinaId(registro.getUsuarioRutina() != null ? registro.getUsuarioRutina().getId() : null)
                .nombreRutina(registro.getUsuarioRutina() != null ? registro.getUsuarioRutina().getRutina().getNombre() : null)
                .fecha(registro.getFecha())
                .hora(registro.getHora())
                .seriesRealizadas(registro.getSeriesRealizadas())
                .repeticionesRealizadas(registro.getRepeticionesRealizadas())
                .pesoUtilizado(registro.getPesoUtilizado())
                .duracionMinutos(registro.getDuracionMinutos())
                .notas(registro.getNotas())
                .caloriasQuemadas(registro.getCaloriasQuemadas())
                .createdAt(registro.getCreatedAt())
                .build();
    }
}
