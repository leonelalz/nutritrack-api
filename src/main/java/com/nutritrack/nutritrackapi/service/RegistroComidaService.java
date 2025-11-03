package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.RegistrarComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.EstadisticasNutricionResponse;
import com.nutritrack.nutritrackapi.dto.response.RegistroComidaResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistroComidaService {

    private final RegistroComidaRepository registroComidaRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final ComidaRepository comidaRepository;
    private final UsuarioPlanRepository usuarioPlanRepository;
    private final RecetaRepository recetaRepository;

    @Transactional
    public RegistroComidaResponse registrarComida(Long perfilUsuarioId, RegistrarComidaRequest request) {
        // Validar que el usuario exista
        PerfilUsuario perfilUsuario = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + perfilUsuarioId));

        // Validar que la comida exista
        Comida comida = comidaRepository.findById(request.getComidaId())
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con ID: " + request.getComidaId()));

        // Validar el plan si se proporcionó
        UsuarioPlan usuarioPlan = null;
        if (request.getUsuarioPlanId() != null) {
            usuarioPlan = usuarioPlanRepository.findById(request.getUsuarioPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asignación de plan no encontrada con ID: " + request.getUsuarioPlanId()));

            // Validar que el plan pertenezca al usuario
            if (!usuarioPlan.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
                throw new BusinessRuleException("El plan especificado no pertenece al usuario");
            }
        }

        // Calcular calorías consumidas basadas en porciones
        BigDecimal caloriasConsumidas = calcularCaloriasComida(comida.getId())
                .multiply(request.getPorciones())
                .setScale(2, RoundingMode.HALF_UP);

        // Crear el registro
        RegistroComida registro = RegistroComida.builder()
                .perfilUsuario(perfilUsuario)
                .comida(comida)
                .usuarioPlan(usuarioPlan)
                .fecha(request.getFecha())
                .hora(request.getHora())
                .tipoComida(request.getTipoComida())
                .porciones(request.getPorciones())
                .notas(request.getNotas())
                .caloriasConsumidas(caloriasConsumidas)
                .build();

        registro = registroComidaRepository.save(registro);

        return convertirAResponse(registro);
    }

    @Transactional(readOnly = true)
    public List<RegistroComidaResponse> listarRegistrosPorFecha(Long perfilUsuarioId, LocalDate fecha) {
        List<RegistroComida> registros = registroComidaRepository.findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);
        return registros.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroComidaResponse> listarRegistrosPorPeriodo(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<RegistroComida> registros = registroComidaRepository.findByPerfilUsuarioIdAndFechaBetween(
                perfilUsuarioId, fechaInicio, fechaFin);
        return registros.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistroComidaResponse buscarPorId(Long perfilUsuarioId, Long registroId) {
        RegistroComida registro = registroComidaRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de comida no encontrado con ID: " + registroId));

        // Validar que el registro pertenezca al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Este registro no pertenece al usuario especificado");
        }

        return convertirAResponse(registro);
    }

    @Transactional
    public void eliminarRegistro(Long perfilUsuarioId, Long registroId) {
        RegistroComida registro = registroComidaRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de comida no encontrado con ID: " + registroId));

        // Validar que el registro pertenezca al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessRuleException("Este registro no pertenece al usuario especificado");
        }

        registroComidaRepository.delete(registro);
    }

    @Transactional(readOnly = true)
    public EstadisticasNutricionResponse obtenerEstadisticas(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener calorías totales
        BigDecimal caloriasTotales = registroComidaRepository.sumCaloriasConsumidasByPeriodo(
                perfilUsuarioId, fechaInicio, fechaFin);

        // Obtener estadísticas por tipo de comida
        List<Object[]> estadisticasPorTipo = registroComidaRepository.getEstadisticasPorTipoComida(
                perfilUsuarioId, fechaInicio, fechaFin);

        // Procesar estadísticas por tipo
        Map<String, EstadisticasNutricionResponse.EstadisticaTipoComida> estadisticasMap = new HashMap<>();
        int totalRegistros = 0;

        for (Object[] row : estadisticasPorTipo) {
            TipoComida tipo = (TipoComida) row[0];
            Long cantidad = (Long) row[1];
            BigDecimal calorias = (BigDecimal) row[2];

            totalRegistros += cantidad.intValue();

            BigDecimal caloriasPromedio = cantidad > 0
                    ? calorias.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            EstadisticasNutricionResponse.EstadisticaTipoComida estadistica =
                    EstadisticasNutricionResponse.EstadisticaTipoComida.builder()
                            .cantidad(cantidad.intValue())
                            .caloriasTotales(calorias)
                            .caloriasPromedio(caloriasPromedio)
                            .build();

            estadisticasMap.put(tipo.name(), estadistica);
        }

        // Calcular promedio diario
        long diasEnPeriodo = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        BigDecimal caloriasPromedioDiario = diasEnPeriodo > 0
                ? caloriasTotales.divide(BigDecimal.valueOf(diasEnPeriodo), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return EstadisticasNutricionResponse.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .totalRegistros(totalRegistros)
                .caloriasTotales(caloriasTotales)
                .caloriasPromedioDiario(caloriasPromedioDiario)
                .estadisticasPorTipo(estadisticasMap)
                .build();
    }

    private RegistroComidaResponse convertirAResponse(RegistroComida registro) {
        return RegistroComidaResponse.builder()
                .id(registro.getId())
                .perfilUsuarioId(registro.getPerfilUsuario().getId())
                .nombreUsuario(registro.getPerfilUsuario().getNombre() + " " + registro.getPerfilUsuario().getApellido())
                .comidaId(registro.getComida().getId())
                .nombreComida(registro.getComida().getNombre())
                .usuarioPlanId(registro.getUsuarioPlan() != null ? registro.getUsuarioPlan().getId() : null)
                .nombrePlan(registro.getUsuarioPlan() != null ? registro.getUsuarioPlan().getPlan().getNombre() : null)
                .fecha(registro.getFecha())
                .hora(registro.getHora())
                .tipoComida(registro.getTipoComida())
                .porciones(registro.getPorciones())
                .notas(registro.getNotas())
                .caloriasConsumidas(registro.getCaloriasConsumidas())
                .createdAt(registro.getCreatedAt())
                .build();
    }

    private BigDecimal calcularCaloriasComida(Long comidaId) {
        List<Receta> recetas = recetaRepository.findByIdComida(comidaId);
        BigDecimal caloriasTotales = BigDecimal.ZERO;

        for (Receta receta : recetas) {
            Ingrediente ingrediente = receta.getIngrediente();
            if (ingrediente != null && ingrediente.getEnergia() != null) {
                // Calorías = (energía por 100g * cantidad en gramos) / 100
                BigDecimal calorias = ingrediente.getEnergia()
                        .multiply(receta.getCantidadIngrediente())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                caloriasTotales = caloriasTotales.add(calorias);
            }
        }

        return caloriasTotales;
    }
}
