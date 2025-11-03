package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.ActualizarPlanRequest;
import com.nutritrack.nutritrackapi.dto.request.AgregarComidaPlanRequest;
import com.nutritrack.nutritrackapi.dto.request.CrearPlanRequest;
import com.nutritrack.nutritrackapi.dto.response.PlanDetalleResponse;
import com.nutritrack.nutritrackapi.dto.response.PlanResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanDiaRepository planDiaRepository;
    private final PlanObjetivoRepository planObjetivoRepository;
    private final ComidaRepository comidaRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final RecetaRepository recetaRepository;

    @Transactional
    public PlanResponse crear(CrearPlanRequest request) {
        if (planRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new DuplicateResourceException("Ya existe un plan con el nombre: " + request.getNombre());
        }

        Plan plan = Plan.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionDias(request.getDuracionDias())
                .activo(true)
                .build();

        if (request.getEtiquetasIds() != null && !request.getEtiquetasIds().isEmpty()) {
            List<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.getEtiquetasIds());
            if (etiquetas.size() != request.getEtiquetasIds().size()) {
                throw new ResourceNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            plan.setEtiquetas(etiquetas);
        }

        Plan planGuardado = planRepository.save(plan);

        if (request.getObjetivo() != null) {
            PlanObjetivo objetivo = PlanObjetivo.builder()
                    .plan(planGuardado)
                    .caloriasObjetivo(request.getObjetivo().getCaloriasObjetivo())
                    .proteinasObjetivo(request.getObjetivo().getProteinasObjetivo())
                    .grasasObjetivo(request.getObjetivo().getGrasasObjetivo())
                    .carbohidratosObjetivo(request.getObjetivo().getCarbohidratosObjetivo())
                    .descripcion(request.getObjetivo().getDescripcion())
                    .build();
            planObjetivoRepository.save(objetivo);
            planGuardado.setObjetivo(objetivo);
        }

        return convertirAResponse(planGuardado);
    }

    @Transactional
    public PlanResponse actualizar(Long id, ActualizarPlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));

        if (request.getNombre() != null) {
            if (planRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
                throw new DuplicateResourceException("Ya existe otro plan con el nombre: " + request.getNombre());
            }
            plan.setNombre(request.getNombre());
        }

        if (request.getDescripcion() != null) {
            plan.setDescripcion(request.getDescripcion());
        }

        if (request.getDuracionDias() != null) {
            plan.setDuracionDias(request.getDuracionDias());
        }

        if (request.getActivo() != null) {
            plan.setActivo(request.getActivo());
        }

        if (request.getEtiquetasIds() != null) {
            List<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.getEtiquetasIds());
            if (etiquetas.size() != request.getEtiquetasIds().size()) {
                throw new ResourceNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            plan.setEtiquetas(etiquetas);
        }

        if (request.getObjetivo() != null) {
            PlanObjetivo objetivo = planObjetivoRepository.findByPlanId(id)
                    .orElse(PlanObjetivo.builder().plan(plan).build());

            if (request.getObjetivo().getCaloriasObjetivo() != null) {
                objetivo.setCaloriasObjetivo(request.getObjetivo().getCaloriasObjetivo());
            }
            if (request.getObjetivo().getProteinasObjetivo() != null) {
                objetivo.setProteinasObjetivo(request.getObjetivo().getProteinasObjetivo());
            }
            if (request.getObjetivo().getGrasasObjetivo() != null) {
                objetivo.setGrasasObjetivo(request.getObjetivo().getGrasasObjetivo());
            }
            if (request.getObjetivo().getCarbohidratosObjetivo() != null) {
                objetivo.setCarbohidratosObjetivo(request.getObjetivo().getCarbohidratosObjetivo());
            }
            if (request.getObjetivo().getDescripcion() != null) {
                objetivo.setDescripcion(request.getObjetivo().getDescripcion());
            }

            planObjetivoRepository.save(objetivo);
            plan.setObjetivo(objetivo);
        }

        Plan planActualizado = planRepository.save(plan);
        return convertirAResponse(planActualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan no encontrado con ID: " + id);
        }
        planRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PlanResponse buscarPorId(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));
        return convertirAResponse(plan);
    }

    @Transactional(readOnly = true)
    public PlanDetalleResponse buscarDetallePorId(Long id) {
        Plan plan = planRepository.findByIdWithDias(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));

        PlanObjetivo objetivo = planObjetivoRepository.findByPlanId(id).orElse(null);
        plan.setObjetivo(objetivo);

        List<PlanDia> diasConComidas = planDiaRepository.findByPlanIdWithComida(id);

        return convertirADetalleResponse(plan, diasConComidas);
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listarTodos() {
        return planRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listarActivos() {
        return planRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> buscarPorNombre(String nombre) {
        return planRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> buscarPorEtiqueta(Long etiquetaId) {
        if (!etiquetaRepository.existsById(etiquetaId)) {
            throw new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId);
        }
        return planRepository.findByEtiquetaId(etiquetaId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanDetalleResponse agregarComida(Long planId, AgregarComidaPlanRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + planId));

        if (request.getNumeroDia() > plan.getDuracionDias()) {
            throw new BusinessRuleException("El día " + request.getNumeroDia() + " excede la duración del plan (" + plan.getDuracionDias() + " días)");
        }

        Comida comida = comidaRepository.findById(request.getComidaId())
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con ID: " + request.getComidaId()));

        if (planDiaRepository.existsByPlanIdAndNumeroDiaAndTipoComida(planId, request.getNumeroDia(), request.getTipoComida())) {
            throw new BusinessRuleException("Ya existe una comida para el día " + request.getNumeroDia() + " tipo " + request.getTipoComida());
        }

        PlanDia planDia = PlanDia.builder()
                .plan(plan)
                .numeroDia(request.getNumeroDia())
                .tipoComida(request.getTipoComida())
                .comida(comida)
                .notas(request.getNotas())
                .build();

        planDiaRepository.save(planDia);

        return buscarDetallePorId(planId);
    }

    @Transactional
    public PlanDetalleResponse removerComida(Long planId, Integer numeroDia, TipoComida tipoComida) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Plan no encontrado con ID: " + planId);
        }

        PlanDia planDia = planDiaRepository.findByPlanIdAndNumeroDiaAndTipoComida(planId, numeroDia, tipoComida)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró comida para el día " + numeroDia + " tipo " + tipoComida));

        planDiaRepository.delete(planDia);

        return buscarDetallePorId(planId);
    }

    @Transactional
    public PlanResponse agregarEtiqueta(Long planId, Long etiquetaId) {
        Plan plan = planRepository.findByIdWithEtiquetas(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + planId));

        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        if (plan.getEtiquetas().contains(etiqueta)) {
            throw new BusinessRuleException("El plan ya tiene esta etiqueta asignada");
        }

        plan.agregarEtiqueta(etiqueta);
        planRepository.save(plan);

        return convertirAResponse(plan);
    }

    @Transactional
    public PlanResponse removerEtiqueta(Long planId, Long etiquetaId) {
        Plan plan = planRepository.findByIdWithEtiquetas(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + planId));

        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));

        plan.removerEtiqueta(etiqueta);
        planRepository.save(plan);

        return convertirAResponse(plan);
    }

    private PlanResponse convertirAResponse(Plan plan) {
        PlanResponse.PlanResponseBuilder builder = PlanResponse.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .descripcion(plan.getDescripcion())
                .duracionDias(plan.getDuracionDias())
                .activo(plan.getActivo())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt());

        if (plan.getEtiquetas() != null) {
            builder.etiquetas(plan.getEtiquetas().stream()
                    .map(e -> PlanResponse.EtiquetaSimpleResponse.builder()
                            .id(e.getId())
                            .nombre(e.getNombre())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (plan.getObjetivo() != null) {
            builder.objetivo(PlanResponse.ObjetivoResponse.builder()
                    .caloriasObjetivo(plan.getObjetivo().getCaloriasObjetivo())
                    .proteinasObjetivo(plan.getObjetivo().getProteinasObjetivo())
                    .grasasObjetivo(plan.getObjetivo().getGrasasObjetivo())
                    .carbohidratosObjetivo(plan.getObjetivo().getCarbohidratosObjetivo())
                    .descripcion(plan.getObjetivo().getDescripcion())
                    .build());
        }

        List<PlanDia> dias = planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(plan.getId());
        builder.totalComidas(dias.size());

        if (!dias.isEmpty()) {
            PlanResponse.NutricionPromedioResponse nutricion = calcularNutricionPromedio(plan.getId(), dias);
            builder.nutricionPromedio(nutricion);
        }

        return builder.build();
    }

    private PlanDetalleResponse convertirADetalleResponse(Plan plan, List<PlanDia> diasConComidas) {
        Map<Integer, List<PlanDetalleResponse.DiaComidaResponse>> diasAgrupados = diasConComidas.stream()
                .map(pd -> {
                    PlanDetalleResponse.NutricionComidaResponse nutricion = calcularNutricionComida(pd.getComida().getId());

                    return PlanDetalleResponse.DiaComidaResponse.builder()
                            .id(pd.getId())
                            .numeroDia(pd.getNumeroDia())
                            .tipoComida(pd.getTipoComida())
                            .comida(PlanDetalleResponse.ComidaSimpleResponse.builder()
                                    .id(pd.getComida().getId())
                                    .nombre(pd.getComida().getNombre())
                                    .tipoComida(pd.getComida().getTipoComida())
                                    .nutricion(PlanDetalleResponse.NutricionComidaResponse.builder()
                                            .energiaTotal(nutricion.getEnergiaTotal())
                                            .proteinasTotal(nutricion.getProteinasTotal())
                                            .grasasTotal(nutricion.getGrasasTotal())
                                            .carbohidratosTotal(nutricion.getCarbohidratosTotal())
                                            .build())
                                    .build())
                            .notas(pd.getNotas())
                            .createdAt(pd.getCreatedAt())
                            .build();
                })
                .collect(Collectors.groupingBy(PlanDetalleResponse.DiaComidaResponse::getNumeroDia));

        PlanDetalleResponse.NutricionPorDiaResponse nutricionPorDia = calcularNutricionPorDia(diasConComidas);

        PlanDetalleResponse.PlanDetalleResponseBuilder builder = PlanDetalleResponse.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .descripcion(plan.getDescripcion())
                .duracionDias(plan.getDuracionDias())
                .activo(plan.getActivo())
                .diasPorNumero(diasAgrupados)
                .nutricionPorDia(nutricionPorDia)
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt());

        if (plan.getEtiquetas() != null) {
            builder.etiquetas(plan.getEtiquetas().stream()
                    .map(e -> PlanResponse.EtiquetaSimpleResponse.builder()
                            .id(e.getId())
                            .nombre(e.getNombre())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (plan.getObjetivo() != null) {
            builder.objetivo(PlanResponse.ObjetivoResponse.builder()
                    .caloriasObjetivo(plan.getObjetivo().getCaloriasObjetivo())
                    .proteinasObjetivo(plan.getObjetivo().getProteinasObjetivo())
                    .grasasObjetivo(plan.getObjetivo().getGrasasObjetivo())
                    .carbohidratosObjetivo(plan.getObjetivo().getCarbohidratosObjetivo())
                    .descripcion(plan.getObjetivo().getDescripcion())
                    .build());
        }

        return builder.build();
    }

    private PlanDetalleResponse.NutricionComidaResponse calcularNutricionComida(Long comidaId) {
        List<Receta> recetas = recetaRepository.findByIdComida(comidaId);

        BigDecimal energiaTotal = BigDecimal.ZERO;
        BigDecimal proteinasTotal = BigDecimal.ZERO;
        BigDecimal grasasTotal = BigDecimal.ZERO;
        BigDecimal carbohidratosTotal = BigDecimal.ZERO;

        for (Receta receta : recetas) {
            Ingrediente ingrediente = receta.getIngrediente();
            BigDecimal cantidad = receta.getCantidadIngrediente();

            BigDecimal factor = cantidad.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

            energiaTotal = energiaTotal.add(ingrediente.getEnergia().multiply(factor));
            proteinasTotal = proteinasTotal.add(ingrediente.getProteinas().multiply(factor));
            grasasTotal = grasasTotal.add(ingrediente.getGrasas().multiply(factor));
            carbohidratosTotal = carbohidratosTotal.add(ingrediente.getCarbohidratos().multiply(factor));
        }

        return PlanDetalleResponse.NutricionComidaResponse.builder()
                .energiaTotal(energiaTotal.setScale(2, RoundingMode.HALF_UP))
                .proteinasTotal(proteinasTotal.setScale(2, RoundingMode.HALF_UP))
                .grasasTotal(grasasTotal.setScale(2, RoundingMode.HALF_UP))
                .carbohidratosTotal(carbohidratosTotal.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private PlanResponse.NutricionPromedioResponse calcularNutricionPromedio(Long planId, List<PlanDia> dias) {
        Map<Integer, BigDecimal> caloriasPorDia = new HashMap<>();
        Map<Integer, BigDecimal> proteinasPorDia = new HashMap<>();
        Map<Integer, BigDecimal> grasasPorDia = new HashMap<>();
        Map<Integer, BigDecimal> carbohidratosPorDia = new HashMap<>();

        for (PlanDia dia : dias) {
            PlanDetalleResponse.NutricionComidaResponse nutricion = calcularNutricionComida(dia.getComida().getId());

            caloriasPorDia.merge(dia.getNumeroDia(), nutricion.getEnergiaTotal(), BigDecimal::add);
            proteinasPorDia.merge(dia.getNumeroDia(), nutricion.getProteinasTotal(), BigDecimal::add);
            grasasPorDia.merge(dia.getNumeroDia(), nutricion.getGrasasTotal(), BigDecimal::add);
            carbohidratosPorDia.merge(dia.getNumeroDia(), nutricion.getCarbohidratosTotal(), BigDecimal::add);
        }

        int diasConComidas = caloriasPorDia.size();

        if (diasConComidas == 0) {
            return PlanResponse.NutricionPromedioResponse.builder()
                    .caloriasDiarias(BigDecimal.ZERO)
                    .proteinasDiarias(BigDecimal.ZERO)
                    .grasasDiarias(BigDecimal.ZERO)
                    .carbohidratosDiarios(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal totalCalorias = caloriasPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProteinas = proteinasPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGrasas = grasasPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCarbohidratos = carbohidratosPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal divisor = new BigDecimal(diasConComidas);

        return PlanResponse.NutricionPromedioResponse.builder()
                .caloriasDiarias(totalCalorias.divide(divisor, 2, RoundingMode.HALF_UP))
                .proteinasDiarias(totalProteinas.divide(divisor, 2, RoundingMode.HALF_UP))
                .grasasDiarias(totalGrasas.divide(divisor, 2, RoundingMode.HALF_UP))
                .carbohidratosDiarios(totalCarbohidratos.divide(divisor, 2, RoundingMode.HALF_UP))
                .build();
    }

    private PlanDetalleResponse.NutricionPorDiaResponse calcularNutricionPorDia(List<PlanDia> dias) {
        Map<Integer, BigDecimal> caloriasPorDia = new HashMap<>();
        Map<Integer, BigDecimal> proteinasPorDia = new HashMap<>();
        Map<Integer, BigDecimal> grasasPorDia = new HashMap<>();
        Map<Integer, BigDecimal> carbohidratosPorDia = new HashMap<>();

        for (PlanDia dia : dias) {
            PlanDetalleResponse.NutricionComidaResponse nutricion = calcularNutricionComida(dia.getComida().getId());

            caloriasPorDia.merge(dia.getNumeroDia(), nutricion.getEnergiaTotal(), BigDecimal::add);
            proteinasPorDia.merge(dia.getNumeroDia(), nutricion.getProteinasTotal(), BigDecimal::add);
            grasasPorDia.merge(dia.getNumeroDia(), nutricion.getGrasasTotal(), BigDecimal::add);
            carbohidratosPorDia.merge(dia.getNumeroDia(), nutricion.getCarbohidratosTotal(), BigDecimal::add);
        }

        int diasConComidas = caloriasPorDia.size();

        if (diasConComidas == 0) {
            return PlanDetalleResponse.NutricionPorDiaResponse.builder()
                    .caloriasPorDia(BigDecimal.ZERO)
                    .proteinasPorDia(BigDecimal.ZERO)
                    .grasasPorDia(BigDecimal.ZERO)
                    .carbohidratosPorDia(BigDecimal.ZERO)
                    .diasConComidas(0)
                    .build();
        }

        BigDecimal totalCalorias = caloriasPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProteinas = proteinasPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGrasas = grasasPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCarbohidratos = carbohidratosPorDia.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal divisor = new BigDecimal(diasConComidas);

        return PlanDetalleResponse.NutricionPorDiaResponse.builder()
                .caloriasPorDia(totalCalorias.divide(divisor, 2, RoundingMode.HALF_UP))
                .proteinasPorDia(totalProteinas.divide(divisor, 2, RoundingMode.HALF_UP))
                .grasasPorDia(totalGrasas.divide(divisor, 2, RoundingMode.HALF_UP))
                .carbohidratosPorDia(totalCarbohidratos.divide(divisor, 2, RoundingMode.HALF_UP))
                .diasConComidas(diasConComidas)
                .build();
    }
}
