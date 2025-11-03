package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.ActualizarRutinaRequest;
import com.nutritrack.nutritrackapi.dto.request.AgregarEjercicioRutinaRequest;
import com.nutritrack.nutritrackapi.dto.request.CrearRutinaRequest;
import com.nutritrack.nutritrackapi.dto.response.RutinaDetalleResponse;
import com.nutritrack.nutritrackapi.dto.response.RutinaResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final RutinaEjercicioRepository rutinaEjercicioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final EtiquetaRepository etiquetaRepository;

    @Transactional
    public RutinaResponse crear(CrearRutinaRequest request) {
        if (rutinaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new DuplicateResourceException("Ya existe una rutina con el nombre: " + request.getNombre());
        }

        Rutina rutina = Rutina.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionSemanas(request.getDuracionSemanas())
                .activo(true)
                .build();

        if (request.getEtiquetasIds() != null && !request.getEtiquetasIds().isEmpty()) {
            List<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.getEtiquetasIds());
            if (etiquetas.size() != request.getEtiquetasIds().size()) {
                throw new ResourceNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            rutina.setEtiquetas(etiquetas);
        }

        Rutina rutinaGuardada = rutinaRepository.save(rutina);
        return convertirAResponse(rutinaGuardada);
    }

    @Transactional
    public RutinaResponse actualizar(Long id, ActualizarRutinaRequest request) {
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + id));

        if (request.getNombre() != null) {
            if (rutinaRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
                throw new DuplicateResourceException("Ya existe otra rutina con el nombre: " + request.getNombre());
            }
            rutina.setNombre(request.getNombre());
        }

        if (request.getDescripcion() != null) {
            rutina.setDescripcion(request.getDescripcion());
        }

        if (request.getDuracionSemanas() != null) {
            rutina.setDuracionSemanas(request.getDuracionSemanas());
        }

        if (request.getActivo() != null) {
            rutina.setActivo(request.getActivo());
        }

        if (request.getEtiquetasIds() != null) {
            List<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.getEtiquetasIds());
            if (etiquetas.size() != request.getEtiquetasIds().size()) {
                throw new ResourceNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            rutina.setEtiquetas(etiquetas);
        }

        Rutina rutinaActualizada = rutinaRepository.save(rutina);
        return convertirAResponse(rutinaActualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!rutinaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rutina no encontrada con ID: " + id);
        }
        rutinaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public RutinaResponse buscarPorId(Long id) {
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + id));
        return convertirAResponse(rutina);
    }

    @Transactional(readOnly = true)
    public RutinaDetalleResponse buscarDetallePorId(Long id) {
        Rutina rutina = rutinaRepository.findByIdWithEjercicios(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + id));

        List<RutinaEjercicio> ejercicios = rutinaEjercicioRepository.findByRutinaIdWithEjercicio(id);

        return convertirADetalleResponse(rutina, ejercicios);
    }

    @Transactional(readOnly = true)
    public List<RutinaResponse> listarTodos() {
        return rutinaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RutinaResponse> listarActivos() {
        return rutinaRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RutinaResponse> buscarPorNombre(String nombre) {
        return rutinaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RutinaResponse> buscarPorEtiqueta(Long etiquetaId) {
        if (!etiquetaRepository.existsById(etiquetaId)) {
            throw new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId);
        }
        return rutinaRepository.findByEtiquetaId(etiquetaId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RutinaDetalleResponse agregarEjercicio(Long rutinaId, AgregarEjercicioRutinaRequest request) {
        Rutina rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + rutinaId));

        Ejercicio ejercicio = ejercicioRepository.findById(request.getEjercicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + request.getEjercicioId()));

        if (rutinaEjercicioRepository.existsByRutinaIdAndEjercicioId(rutinaId, request.getEjercicioId())) {
            throw new BusinessRuleException("El ejercicio ya está en la rutina");
        }

        Integer siguienteOrden = rutinaEjercicioRepository.findMaxOrdenByRutinaId(rutinaId) + 1;

        RutinaEjercicio rutinaEjercicio = RutinaEjercicio.builder()
                .rutina(rutina)
                .ejercicio(ejercicio)
                .orden(siguienteOrden)
                .series(request.getSeries())
                .repeticiones(request.getRepeticiones())
                .peso(request.getPeso())
                .duracionMinutos(request.getDuracionMinutos())
                .notas(request.getNotas())
                .build();

        rutinaEjercicioRepository.save(rutinaEjercicio);

        return buscarDetallePorId(rutinaId);
    }

    @Transactional
    public RutinaDetalleResponse removerEjercicio(Long rutinaId, Long ejercicioId) {
        if (!rutinaRepository.existsById(rutinaId)) {
            throw new ResourceNotFoundException("Rutina no encontrada con ID: " + rutinaId);
        }

        RutinaEjercicio rutinaEjercicio = rutinaEjercicioRepository.findByRutinaIdAndEjercicioId(rutinaId, ejercicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado en la rutina"));

        rutinaEjercicioRepository.delete(rutinaEjercicio);

        // Reordenar los ejercicios restantes
        List<RutinaEjercicio> ejerciciosRestantes = rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(rutinaId);
        for (int i = 0; i < ejerciciosRestantes.size(); i++) {
            ejerciciosRestantes.get(i).setOrden(i + 1);
        }
        rutinaEjercicioRepository.saveAll(ejerciciosRestantes);

        return buscarDetallePorId(rutinaId);
    }

    @Transactional
    public RutinaResponse agregarEtiqueta(Long rutinaId, Long etiquetaId) {
        Rutina rutina = rutinaRepository.findByIdWithEtiquetas(rutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + rutinaId));

        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        if (rutina.getEtiquetas().contains(etiqueta)) {
            throw new BusinessRuleException("La rutina ya tiene esta etiqueta asignada");
        }

        rutina.agregarEtiqueta(etiqueta);
        rutinaRepository.save(rutina);

        return convertirAResponse(rutina);
    }

    @Transactional
    public RutinaResponse removerEtiqueta(Long rutinaId, Long etiquetaId) {
        Rutina rutina = rutinaRepository.findByIdWithEtiquetas(rutinaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada con ID: " + rutinaId));

        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        rutina.removerEtiqueta(etiqueta);
        rutinaRepository.save(rutina);

        return convertirAResponse(rutina);
    }

    private RutinaResponse convertirAResponse(Rutina rutina) {
        List<RutinaEjercicio> ejercicios = rutinaEjercicioRepository.findByRutinaIdOrderByOrdenAsc(rutina.getId());

        BigDecimal caloriasTotal = BigDecimal.ZERO;
        Integer duracionTotal = 0;

        for (RutinaEjercicio re : ejercicios) {
            BigDecimal caloriasPorMinuto = re.getEjercicio().getCaloriasEstimadas()
                    .divide(BigDecimal.valueOf(re.getEjercicio().getDuracion()), 2, java.math.RoundingMode.HALF_UP);
            BigDecimal caloriasEjercicio = caloriasPorMinuto.multiply(BigDecimal.valueOf(re.getDuracionMinutos()));
            caloriasTotal = caloriasTotal.add(caloriasEjercicio);
            duracionTotal += re.getDuracionMinutos();
        }

        RutinaResponse.RutinaResponseBuilder builder = RutinaResponse.builder()
                .id(rutina.getId())
                .nombre(rutina.getNombre())
                .descripcion(rutina.getDescripcion())
                .duracionSemanas(rutina.getDuracionSemanas())
                .activo(rutina.getActivo())
                .totalEjercicios(ejercicios.size())
                .caloriasEstimadasTotal(caloriasTotal.setScale(2, java.math.RoundingMode.HALF_UP))
                .duracionTotalMinutos(duracionTotal)
                .createdAt(rutina.getCreatedAt())
                .updatedAt(rutina.getUpdatedAt());

        if (rutina.getEtiquetas() != null) {
            builder.etiquetas(rutina.getEtiquetas().stream()
                    .map(e -> RutinaResponse.EtiquetaSimpleResponse.builder()
                            .id(e.getId())
                            .nombre(e.getNombre())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    private RutinaDetalleResponse convertirADetalleResponse(Rutina rutina, List<RutinaEjercicio> ejercicios) {
        List<RutinaDetalleResponse.EjercicioRutinaResponse> ejerciciosResponse = ejercicios.stream()
                .map(re -> {
                    BigDecimal caloriasPorMinuto = re.getEjercicio().getCaloriasEstimadas()
                            .divide(BigDecimal.valueOf(re.getEjercicio().getDuracion()), 2, java.math.RoundingMode.HALF_UP);
                    BigDecimal caloriasEjercicio = caloriasPorMinuto.multiply(BigDecimal.valueOf(re.getDuracionMinutos()));

                    return RutinaDetalleResponse.EjercicioRutinaResponse.builder()
                            .id(re.getId())
                            .orden(re.getOrden())
                            .ejercicio(RutinaDetalleResponse.EjercicioSimpleResponse.builder()
                                    .id(re.getEjercicio().getId())
                                    .nombre(re.getEjercicio().getNombre())
                                    .tipoEjercicio(re.getEjercicio().getTipoEjercicio())
                                    .musculoPrincipal(re.getEjercicio().getMusculoPrincipal())
                                    .dificultad(re.getEjercicio().getDificultad())
                                    .caloriasEstimadas(re.getEjercicio().getCaloriasEstimadas())
                                    .build())
                            .series(re.getSeries())
                            .repeticiones(re.getRepeticiones())
                            .peso(re.getPeso())
                            .duracionMinutos(re.getDuracionMinutos())
                            .caloriasEstimadas(caloriasEjercicio.setScale(2, java.math.RoundingMode.HALF_UP))
                            .notas(re.getNotas())
                            .createdAt(re.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal caloriasTotal = ejerciciosResponse.stream()
                .map(RutinaDetalleResponse.EjercicioRutinaResponse::getCaloriasEstimadas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer duracionTotal = ejerciciosResponse.stream()
                .map(RutinaDetalleResponse.EjercicioRutinaResponse::getDuracionMinutos)
                .reduce(0, Integer::sum);

        Integer totalSeries = ejerciciosResponse.stream()
                .map(RutinaDetalleResponse.EjercicioRutinaResponse::getSeries)
                .reduce(0, Integer::sum);

        Integer totalRepeticiones = ejerciciosResponse.stream()
                .map(re -> re.getSeries() * re.getRepeticiones())
                .reduce(0, Integer::sum);

        RutinaDetalleResponse.EstadisticasRutinaResponse estadisticas = RutinaDetalleResponse.EstadisticasRutinaResponse.builder()
                .totalEjercicios(ejercicios.size())
                .totalSeries(totalSeries)
                .totalRepeticiones(totalRepeticiones)
                .caloriasEstimadasTotal(caloriasTotal.setScale(2, java.math.RoundingMode.HALF_UP))
                .duracionTotalMinutos(duracionTotal)
                .build();

        RutinaDetalleResponse.RutinaDetalleResponseBuilder builder = RutinaDetalleResponse.builder()
                .id(rutina.getId())
                .nombre(rutina.getNombre())
                .descripcion(rutina.getDescripcion())
                .duracionSemanas(rutina.getDuracionSemanas())
                .activo(rutina.getActivo())
                .ejercicios(ejerciciosResponse)
                .estadisticas(estadisticas)
                .createdAt(rutina.getCreatedAt())
                .updatedAt(rutina.getUpdatedAt());

        if (rutina.getEtiquetas() != null) {
            builder.etiquetas(rutina.getEtiquetas().stream()
                    .map(e -> RutinaResponse.EtiquetaSimpleResponse.builder()
                            .id(e.getId())
                            .nombre(e.getNombre())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
