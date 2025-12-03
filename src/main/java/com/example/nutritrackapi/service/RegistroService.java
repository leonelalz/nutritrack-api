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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
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
    private final TipoComidaRepository tipoComidaRepository;

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

        // Resolver tipo de comida (por ID o por nombre)
        TipoComidaEntity tipoComida = resolverTipoComidaParaRegistro(request);

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
                .tipoComida(tipoComida)
                .porciones(porciones)
                .caloriasConsumidas(caloriasConsumidas)
                .notas(request.getNotas())
                .build();

        registro = registroComidaRepository.save(registro);
        log.info("Comida registrada exitosamente con ID {}", registro.getId());

        return RegistroComidaResponse.fromEntity(registro);
    }

    /**
     * Resuelve el tipo de comida para el registro, buscando por ID o por nombre.
     */
    private TipoComidaEntity resolverTipoComidaParaRegistro(RegistroComidaRequest request) {
        if (request.getTipoComidaId() != null) {
            return tipoComidaRepository.findById(request.getTipoComidaId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de comida no encontrado con ID: " + request.getTipoComidaId()));
        }
        
        if (request.getTipoComidaNombre() != null && !request.getTipoComidaNombre().isBlank()) {
            return tipoComidaRepository.findByNombreIgnoreCase(request.getTipoComidaNombre())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de comida no encontrado: " + request.getTipoComidaNombre()));
        }
        
        // Si no se especifica tipo, intentar usar el tipo de la comida del catálogo
        return null; // Permitir null si no se especifica
    }

    public RegistroComidaResponse actualizarRegistroComida(
            Long perfilUsuarioId,
            Long registroId,
            RegistroComidaRequest request
    ) {
        RegistroComida registro = registroComidaRepository.findByIdAndPerfilId(registroId, perfilUsuarioId)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        registro.setFecha(request.getFecha());
        registro.setHora(request.getHora());
        registro.setPorciones(request.getPorciones());
        registro.setNotas(request.getNotas());
        
        // Resolver tipo de comida
        TipoComidaEntity tipoComida = resolverTipoComidaParaRegistro(request);
        if (tipoComida != null) {
            registro.setTipoComida(tipoComida);
        }

        registroComidaRepository.save(registro);

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

        // Validar que no exista un registro duplicado para el mismo ejercicio el mismo día
        LocalDate fechaRegistro = request.getFecha() != null ? request.getFecha() : LocalDate.now();
        if (registroEjercicioRepository.existsByPerfilUsuarioIdAndEjercicioIdAndFecha(
                perfilUsuarioId, request.getEjercicioId(), fechaRegistro)) {
            throw new BusinessException("Ya existe un registro de este ejercicio para el día " + fechaRegistro);
        }

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

        // Calcular calorías quemadas automáticamente
        BigDecimal caloriasQuemadas = calcularCaloriasQuemadas(ejercicio, request);

        // Crear registro
        RegistroEjercicio registro = RegistroEjercicio.builder()
                .perfilUsuario(perfil)
                .ejercicio(ejercicio)
                .usuarioRutina(usuarioRutina)
                .fecha(fechaRegistro)
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

        // Obtener el día de la semana (1=Lunes, 7=Domingo)
        int diaSemana = fecha.getDayOfWeek().getValue();
        String nombreDia = obtenerNombreDia(diaSemana);

        // Intentar obtener plan activo
        var optionalPlan = usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId);

        if (optionalPlan.isEmpty()) {
            // 👉 No hay plan activo: devolvemos respuesta "vacía", SIN lanzar 404
            return ActividadesDiaResponse.builder()
                    .fecha(fecha)
                    .diaSemana(diaSemana)
                    .nombreDia(nombreDia)
                    .diaActual(0)
                    .diaPlan(0)
                    .duracionDias(0)
                    .nombrePlan(null)
                    .caloriasObjetivo(BigDecimal.ZERO)
                    .proteinasObjetivo(BigDecimal.ZERO)
                    .carbohidratosObjetivo(BigDecimal.ZERO)
                    .grasasObjetivo(BigDecimal.ZERO)
                    .caloriasConsumidas(BigDecimal.ZERO)
                    .proteinasConsumidas(BigDecimal.ZERO)
                    .carbohidratosConsumidos(BigDecimal.ZERO)
                    .grasasConsumidas(BigDecimal.ZERO)
                    .caloriasPlanificadas(BigDecimal.ZERO)
                    .proteinasPlanificadas(BigDecimal.ZERO)
                    .carbohidratosPlanificados(BigDecimal.ZERO)
                    .grasasPlanificadas(BigDecimal.ZERO)
                    .comidas(Collections.emptyList())
                    .build();
        }

        UsuarioPlan planActivo = optionalPlan.get();
        Plan plan = planActivo.getPlan();

        // Calcular día actual desde el inicio del plan
        long diasDesdeInicio = java.time.temporal.ChronoUnit.DAYS
                .between(planActivo.getFechaInicio(), fecha);
        int diaActual = (int) diasDesdeInicio + 1;

        // Calcular día del plan (cíclico si excede la duración)
        int duracionDias = plan.getDuracionDias() != null ? plan.getDuracionDias() : 1;
        int diaPlan;
        if (diaActual <= duracionDias) {
            diaPlan = diaActual;
        } else {
            // Plan cíclico: día 31 de plan de 30 días = día 1, día 32 = día 2, etc.
            diaPlan = ((diaActual - 1) % duracionDias) + 1;
        }
        log.info("Día actual: {}, Duración plan: {}, Día del plan (cíclico): {}", diaActual, duracionDias, diaPlan);

        // Obtener comidas programadas para ese día del plan
        List<PlanDia> comidasDelDia = planDiaRepository
                .findByPlanIdAndNumeroDia(plan.getId(), diaPlan);

        log.info("Comidas encontradas para día {} del plan: {}", diaPlan, comidasDelDia.size());

        // Obtener registros de comidas del día
        List<RegistroComida> registros = registroComidaRepository
                .findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);

        // Acumuladores para totales planificados
        BigDecimal totalCaloriasPlan = BigDecimal.ZERO;
        BigDecimal totalProteinasPlan = BigDecimal.ZERO;
        BigDecimal totalCarbosPlan = BigDecimal.ZERO;
        BigDecimal totalGrasasPlan = BigDecimal.ZERO;

        // Mapear comidas con información nutricional completa
        List<ActividadesDiaResponse.ComidaDiaInfo> comidas = new java.util.ArrayList<>();
        
        for (PlanDia planDia : comidasDelDia) {
            Comida comida = planDia.getComida();
            
            // Calcular macros de la comida basándose en sus ingredientes
            NutricionComida nutricion = calcularNutricionComida(comida);
            
            // Acumular totales planificados
            totalCaloriasPlan = totalCaloriasPlan.add(nutricion.calorias);
            totalProteinasPlan = totalProteinasPlan.add(nutricion.proteinas);
            totalCarbosPlan = totalCarbosPlan.add(nutricion.carbohidratos);
            totalGrasasPlan = totalGrasasPlan.add(nutricion.grasas);
            
            // Verificar si está registrada
            RegistroComida registro = registros.stream()
                    .filter(r -> r.getComida().getId().equals(comida.getId()))
                    .findFirst()
                    .orElse(null);
            
            // Mapear ingredientes
            List<ActividadesDiaResponse.IngredienteInfo> ingredientesInfo = 
                    comida.getComidaIngredientes().stream()
                            .map(ci -> {
                                Ingrediente ing = ci.getIngrediente();
                                BigDecimal cantidad = ci.getCantidadGramos();
                                BigDecimal factor = cantidad.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                                
                                return ActividadesDiaResponse.IngredienteInfo.builder()
                                        .ingredienteId(ing.getId())
                                        .nombre(ing.getNombre())
                                        .cantidadGramos(cantidad)
                                        .calorias(ing.getEnergia().multiply(factor).setScale(2, RoundingMode.HALF_UP))
                                        .proteinas(ing.getProteinas().multiply(factor).setScale(2, RoundingMode.HALF_UP))
                                        .carbohidratos(ing.getCarbohidratos().multiply(factor).setScale(2, RoundingMode.HALF_UP))
                                        .grasas(ing.getGrasas().multiply(factor).setScale(2, RoundingMode.HALF_UP))
                                        .notas(ci.getNotas())
                                        .build();
                            })
                            .collect(Collectors.toList());
            
            comidas.add(ActividadesDiaResponse.ComidaDiaInfo.builder()
                    .comidaId(comida.getId())
                    .nombre(comida.getNombre())
                    .tipoComida(planDia.getTipoComida() != null ? planDia.getTipoComida().getNombre() : null)
                    .tipoComidaId(planDia.getTipoComida() != null ? planDia.getTipoComida().getId() : null)
                    .calorias(nutricion.calorias)
                    .proteinas(nutricion.proteinas)
                    .carbohidratos(nutricion.carbohidratos)
                    .grasas(nutricion.grasas)
                    .descripcion(comida.getDescripcion())
                    .tiempoPreparacionMinutos(comida.getTiempoPreparacionMinutos())
                    .porciones(comida.getPorciones())
                    .notas(planDia.getNotas())
                    .registrada(registro != null)
                    .registroId(registro != null ? registro.getId() : null)
                    .ingredientes(ingredientesInfo)
                    .build());
        }

        // Calcular consumo del día (de los registros)
        BigDecimal caloriasConsumidas = BigDecimal.ZERO;
        BigDecimal proteinasConsumidas = BigDecimal.ZERO;
        BigDecimal carbohidratosConsumidos = BigDecimal.ZERO;
        BigDecimal grasasConsumidas = BigDecimal.ZERO;
        
        for (RegistroComida registro : registros) {
            BigDecimal porciones = registro.getPorciones() != null ? registro.getPorciones() : BigDecimal.ONE;
            NutricionComida nutricionRegistro = calcularNutricionComida(registro.getComida());
            
            caloriasConsumidas = caloriasConsumidas.add(nutricionRegistro.calorias.multiply(porciones));
            proteinasConsumidas = proteinasConsumidas.add(nutricionRegistro.proteinas.multiply(porciones));
            carbohidratosConsumidos = carbohidratosConsumidos.add(nutricionRegistro.carbohidratos.multiply(porciones));
            grasasConsumidas = grasasConsumidas.add(nutricionRegistro.grasas.multiply(porciones));
        }

        // Obtener objetivos del plan
        BigDecimal caloriasObjetivo = plan.getObjetivo() != null && plan.getObjetivo().getCaloriasObjetivo() != null
                ? plan.getObjetivo().getCaloriasObjetivo()
                : BigDecimal.ZERO;
        BigDecimal proteinasObjetivo = plan.getObjetivo() != null && plan.getObjetivo().getProteinasObjetivo() != null
                ? plan.getObjetivo().getProteinasObjetivo()
                : BigDecimal.ZERO;
        BigDecimal carbohidratosObjetivo = plan.getObjetivo() != null && plan.getObjetivo().getCarbohidratosObjetivo() != null
                ? plan.getObjetivo().getCarbohidratosObjetivo()
                : BigDecimal.ZERO;
        BigDecimal grasasObjetivo = plan.getObjetivo() != null && plan.getObjetivo().getGrasasObjetivo() != null
                ? plan.getObjetivo().getGrasasObjetivo()
                : BigDecimal.ZERO;

        return ActividadesDiaResponse.builder()
                .fecha(fecha)
                .diaSemana(diaSemana)
                .nombreDia(nombreDia)
                .diaActual(diaActual)
                .diaPlan(diaPlan)
                .duracionDias(duracionDias)
                .nombrePlan(plan.getNombre())
                .caloriasObjetivo(caloriasObjetivo)
                .proteinasObjetivo(proteinasObjetivo)
                .carbohidratosObjetivo(carbohidratosObjetivo)
                .grasasObjetivo(grasasObjetivo)
                .caloriasConsumidas(caloriasConsumidas.setScale(2, RoundingMode.HALF_UP))
                .proteinasConsumidas(proteinasConsumidas.setScale(2, RoundingMode.HALF_UP))
                .carbohidratosConsumidos(carbohidratosConsumidos.setScale(2, RoundingMode.HALF_UP))
                .grasasConsumidas(grasasConsumidas.setScale(2, RoundingMode.HALF_UP))
                .caloriasPlanificadas(totalCaloriasPlan.setScale(2, RoundingMode.HALF_UP))
                .proteinasPlanificadas(totalProteinasPlan.setScale(2, RoundingMode.HALF_UP))
                .carbohidratosPlanificados(totalCarbosPlan.setScale(2, RoundingMode.HALF_UP))
                .grasasPlanificadas(totalGrasasPlan.setScale(2, RoundingMode.HALF_UP))
                .comidas(comidas)
                .build();
    }

    /**
     * Clase interna para encapsular información nutricional de una comida.
     */
    private static class NutricionComida {
        BigDecimal calorias = BigDecimal.ZERO;
        BigDecimal proteinas = BigDecimal.ZERO;
        BigDecimal carbohidratos = BigDecimal.ZERO;
        BigDecimal grasas = BigDecimal.ZERO;
    }

    /**
     * Calcula la información nutricional total de una comida sumando sus ingredientes.
     * Los valores nutricionales de los ingredientes están por 100g, se ajustan según cantidad.
     */
    private NutricionComida calcularNutricionComida(Comida comida) {
        NutricionComida nutricion = new NutricionComida();
        
        if (comida.getComidaIngredientes() == null || comida.getComidaIngredientes().isEmpty()) {
            return nutricion;
        }
        
        for (ComidaIngrediente ci : comida.getComidaIngredientes()) {
            Ingrediente ingrediente = ci.getIngrediente();
            BigDecimal cantidadGramos = ci.getCantidadGramos();
            
            // Valores nutricionales están por 100g, calculamos proporción
            BigDecimal factor = cantidadGramos.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            
            nutricion.calorias = nutricion.calorias.add(
                    ingrediente.getEnergia().multiply(factor));
            nutricion.proteinas = nutricion.proteinas.add(
                    ingrediente.getProteinas().multiply(factor));
            nutricion.carbohidratos = nutricion.carbohidratos.add(
                    ingrediente.getCarbohidratos().multiply(factor));
            nutricion.grasas = nutricion.grasas.add(
                    ingrediente.getGrasas().multiply(factor));
        }
        
        // Redondear a 2 decimales
        nutricion.calorias = nutricion.calorias.setScale(2, RoundingMode.HALF_UP);
        nutricion.proteinas = nutricion.proteinas.setScale(2, RoundingMode.HALF_UP);
        nutricion.carbohidratos = nutricion.carbohidratos.setScale(2, RoundingMode.HALF_UP);
        nutricion.grasas = nutricion.grasas.setScale(2, RoundingMode.HALF_UP);
        
        return nutricion;
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

        // Calcular semana actual desde el inicio de la rutina
        long semanasDesdeInicio = java.time.temporal.ChronoUnit.WEEKS.between(rutinaActiva.getFechaInicio(), fecha);
        int semanaActual = (int) semanasDesdeInicio + 1;

        // Obtener el día de la semana (1=Lunes, 7=Domingo)
        int diaSemana = fecha.getDayOfWeek().getValue();
        log.info("Día de la semana: {} (1=Lunes, 7=Domingo), Semana actual: {}", diaSemana, semanaActual);

        // Calcular la semana base del patrón (si la rutina tiene patrón de semanas)
        Integer patronSemanas = rutinaActiva.getRutina().getPatronSemanas();
        int semanaBase = 1;
        if (patronSemanas != null && patronSemanas > 0) {
            // La semana base es cíclica: semana 1, 2, ..., patronSemanas, 1, 2, ...
            semanaBase = ((semanaActual - 1) % patronSemanas) + 1;
        }
        log.info("Patrón de semanas: {}, Semana base calculada: {}", patronSemanas, semanaBase);

        // Obtener SOLO los ejercicios programados para este día y semana base
        final int finalSemanaBase = semanaBase;
        List<RutinaEjercicio> ejerciciosRutina = rutinaEjercicioRepository
                .findByRutinaIdOrderBySemanaBaseAscDiaSemanaAscOrdenAsc(rutinaActiva.getRutina().getId())
                .stream()
                .filter(re -> re.getDiaSemana().equals(diaSemana))
                .filter(re -> re.getSemanaBase().equals(finalSemanaBase))
                .collect(Collectors.toList());

        log.info("Ejercicios encontrados para día {} semana base {}: {}", diaSemana, semanaBase, ejerciciosRutina.size());
        
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
                            .descansoSegundos(re.getDescansoSegundos())
                            .notas(re.getNotas())
                            .registrado(registro != null)
                            .registroId(registro != null ? registro.getId() : null)
                            .build();
                })
                .collect(Collectors.toList());

        // Obtener nombre del día en español
        String nombreDia = obtenerNombreDia(diaSemana);

        return EjerciciosDiaResponse.builder()
                .fecha(fecha)
                .diaSemana(diaSemana)
                .nombreDia(nombreDia)
                .semanaActual(semanaActual)
                .semanaBase(semanaBase)
                .ejercicios(ejercicios)
                .build();
    }

    /**
     * Obtiene el nombre del día de la semana en español.
     */
    private String obtenerNombreDia(int diaSemana) {
        return switch (diaSemana) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sábado";
            case 7 -> "Domingo";
            default -> "Desconocido";
        };
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

    @Transactional(readOnly = true)
    public RegistroComidaResponse obtenerRegistroComida(Long perfilUsuarioId, Long registroId) {
        log.info("Obteniendo detalle de registro de comida {} para usuario {}", registroId, perfilUsuarioId);

        RegistroComida registro = registroComidaRepository.findById(registroId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de comida no encontrado"));

        // Validar que el registro pertenece al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessException("El registro no pertenece al usuario");
        }

        return RegistroComidaResponse.fromEntity(registro);
    }

    @Transactional(readOnly = true)
    public RegistroEjercicioResponse obtenerRegistroEjercicio(Long perfilUsuarioId, Long registroId) {
        log.info("Obteniendo detalle de registro de ejercicio {} para usuario {}", registroId, perfilUsuarioId);

        RegistroEjercicio registro = registroEjercicioRepository.findById(registroId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de ejercicio no encontrado"));

        // Validar que el registro pertenece al usuario
        if (!registro.getPerfilUsuario().getId().equals(perfilUsuarioId)) {
            throw new BusinessException("El registro no pertenece al usuario");
        }

        return RegistroEjercicioResponse.fromEntity(registro);
    }

    // ============================================================
    // Progreso Semanal de Ejercicios
    // ============================================================

    /**
     * Obtiene el progreso semanal de ejercicios del usuario.
     * Incluye estadísticas de ejercicios completados, calorías quemadas y tiempo total.
     */
    @Transactional(readOnly = true)
    public ProgresoSemanalResponse obtenerProgresoSemanal(Long perfilUsuarioId, LocalDate fechaReferencia) {
        log.info("Obteniendo progreso semanal para usuario {} con fecha de referencia {}", perfilUsuarioId, fechaReferencia);

        // Calcular inicio y fin de la semana (lunes a domingo)
        LocalDate inicioSemana = fechaReferencia.with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = fechaReferencia.with(java.time.DayOfWeek.SUNDAY);

        // Obtener todos los registros de ejercicios de la semana
        List<RegistroEjercicio> registrosSemana = registroEjercicioRepository
                .findByPerfilUsuarioIdAndFechaBetween(perfilUsuarioId, inicioSemana, finSemana);

        // Calcular estadísticas
        int ejerciciosCompletados = registrosSemana.size();
        
        BigDecimal caloriasQuemadasTotal = registrosSemana.stream()
                .map(RegistroEjercicio::getCaloriasQuemadas)
                .filter(cal -> cal != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int tiempoTotalMinutos = registrosSemana.stream()
                .map(RegistroEjercicio::getDuracionMinutos)
                .filter(dur -> dur != null)
                .mapToInt(Integer::intValue)
                .sum();

        // Agrupar por día de la semana
        List<ProgresoSemanalResponse.DiaSemanaInfo> diasSemana = java.util.stream.Stream
                .iterate(inicioSemana, d -> d.plusDays(1))
                .limit(7)
                .map(dia -> {
                    List<RegistroEjercicio> registrosDia = registrosSemana.stream()
                            .filter(r -> r.getFecha().equals(dia))
                            .collect(Collectors.toList());

                    int ejerciciosDia = registrosDia.size();
                    BigDecimal caloriasDia = registrosDia.stream()
                            .map(RegistroEjercicio::getCaloriasQuemadas)
                            .filter(cal -> cal != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    int tiempoDia = registrosDia.stream()
                            .map(RegistroEjercicio::getDuracionMinutos)
                            .filter(dur -> dur != null)
                            .mapToInt(Integer::intValue)
                            .sum();

                    return ProgresoSemanalResponse.DiaSemanaInfo.builder()
                            .fecha(dia)
                            .diaSemana(dia.getDayOfWeek().toString())
                            .ejerciciosCompletados(ejerciciosDia)
                            .caloriasQuemadas(caloriasDia)
                            .tiempoMinutos(tiempoDia)
                            .build();
                })
                .collect(Collectors.toList());

        // Obtener rutina activa para calcular porcentaje de cumplimiento
        int ejerciciosProgramados = 0;
        var rutinaActiva = usuarioRutinaRepository.findRutinaActivaActual(perfilUsuarioId);
        if (rutinaActiva.isPresent()) {
            // Contar ejercicios programados para la semana
            ejerciciosProgramados = rutinaEjercicioRepository
                    .findByRutinaIdOrderBySemanaBaseAscDiaSemanaAscOrdenAsc(rutinaActiva.get().getRutina().getId())
                    .size();
        }

        double porcentajeCumplimiento = ejerciciosProgramados > 0 
                ? (double) ejerciciosCompletados / ejerciciosProgramados * 100 
                : 0;

        return ProgresoSemanalResponse.builder()
                .inicioSemana(inicioSemana)
                .finSemana(finSemana)
                .ejerciciosCompletados(ejerciciosCompletados)
                .ejerciciosProgramados(ejerciciosProgramados)
                .porcentajeCumplimiento(Math.min(porcentajeCumplimiento, 100)) // Máximo 100%
                .caloriasQuemadasTotal(caloriasQuemadasTotal)
                .tiempoTotalMinutos(tiempoTotalMinutos)
                .diasSemana(diasSemana)
                .build();
    }

    // ============================================================
    // Métodos auxiliares privados
    // ============================================================

    /**
     * Calcula las calorías quemadas basándose en el trabajo real realizado.
     * 
     * Para ejercicios de FUERZA (con series, repeticiones y peso):
     * - Usa la fórmula basada en trabajo mecánico y MET
     * - Calorías = (Series × Repeticiones × Peso × Factor) / Eficiencia
     * - Factor varía según grupo muscular (piernas queman más que brazos)
     * 
     * Para ejercicios de CARDIO/otros (con duración):
     * - Usa calorías por minuto × duración
     * 
     * Si el frontend envía calorías precalculadas, las usa directamente.
     */
    private BigDecimal calcularCaloriasQuemadas(Ejercicio ejercicio, RegistroEjercicioRequest request) {
        // Si el frontend envía calorías calculadas, usarlas directamente
        if (request.getCaloriasQuemadas() != null && request.getCaloriasQuemadas().compareTo(BigDecimal.ZERO) > 0) {
            log.debug("Usando calorías enviadas por frontend: {}", request.getCaloriasQuemadas());
            return request.getCaloriasQuemadas();
        }

        // Determinar si es ejercicio de fuerza (tiene series/reps/peso) o cardio (tiene duración)
        boolean tieneSeriesReps = (request.getSeries() != null && request.getSeries() > 0) 
                               || (request.getRepeticiones() != null && request.getRepeticiones() > 0);
        boolean tienePeso = request.getPesoKg() != null && request.getPesoKg().compareTo(BigDecimal.ZERO) > 0;
        boolean tieneDuracion = request.getDuracionMinutos() != null && request.getDuracionMinutos() > 0;

        // Ejercicios de FUERZA: calcular basado en trabajo mecánico
        if (tieneSeriesReps && tienePeso) {
            return calcularCaloriasFuerza(ejercicio, request);
        }
        
        // Ejercicios de FUERZA sin peso (peso corporal): usar factor reducido
        if (tieneSeriesReps && !tienePeso) {
            return calcularCaloriasPesoCorporal(ejercicio, request);
        }

        // Ejercicios de CARDIO o con duración: usar calorías por minuto
        if (tieneDuracion) {
            return calcularCaloriasCardio(ejercicio, request);
        }

        // Fallback: estimar basado en tipo de ejercicio
        return estimarCaloriasMinimas(ejercicio);
    }

    /**
     * Calcula calorías para ejercicios de fuerza con peso.
     * 
     * Fórmula basada en investigación de ejercicio:
     * Trabajo (Joules) = Peso (kg) × Repeticiones × Distancia movimiento (m) × 9.81
     * Calorías = Trabajo / 4184 × Factor de eficiencia muscular (≈0.25)
     * 
     * Simplificado: Calorías ≈ Series × Repeticiones × Peso × Factor grupo muscular / 200
     * 
     * Factores por grupo muscular (basados en masa muscular involucrada):
     * - Piernas (sentadillas, peso muerto): 0.08-0.10 cal por rep×kg
     * - Espalda (remo, jalones): 0.06-0.08 cal por rep×kg
     * - Pecho (press banca): 0.05-0.07 cal por rep×kg
     * - Hombros: 0.04-0.06 cal por rep×kg
     * - Brazos: 0.03-0.05 cal por rep×kg
     */
    private BigDecimal calcularCaloriasFuerza(Ejercicio ejercicio, RegistroEjercicioRequest request) {
        int series = request.getSeries() != null ? request.getSeries() : 1;
        int repeticiones = request.getRepeticiones() != null ? request.getRepeticiones() : 10;
        BigDecimal peso = request.getPesoKg() != null ? request.getPesoKg() : BigDecimal.ZERO;

        // Factor según grupo muscular
        BigDecimal factorGrupoMuscular = obtenerFactorGrupoMuscular(ejercicio.getGrupoMuscular());
        
        // Fórmula: Series × Repeticiones × Peso × Factor
        // Ejemplo: 3 series × 10 reps × 20kg × 0.05 = 30 calorías
        BigDecimal totalReps = BigDecimal.valueOf((long) series * repeticiones);
        BigDecimal calorias = totalReps.multiply(peso).multiply(factorGrupoMuscular);
        
        // Añadir calorías base por el esfuerzo (incluso sin peso, hay gasto energético)
        // Aproximadamente 0.5-1 caloría por repetición por el esfuerzo cardiovascular
        BigDecimal caloriasBase = totalReps.multiply(new BigDecimal("0.5"));
        calorias = calorias.add(caloriasBase);

        log.debug("Calorías fuerza: {} series × {} reps × {} kg × factor {} = {} cal", 
                series, repeticiones, peso, factorGrupoMuscular, calorias);
        
        return calorias.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula calorías para ejercicios de peso corporal (sin pesas).
     * Ejemplo: flexiones, dominadas, sentadillas sin peso
     */
    private BigDecimal calcularCaloriasPesoCorporal(Ejercicio ejercicio, RegistroEjercicioRequest request) {
        int series = request.getSeries() != null ? request.getSeries() : 1;
        int repeticiones = request.getRepeticiones() != null ? request.getRepeticiones() : 10;

        // Factor por tipo de ejercicio de peso corporal
        BigDecimal factorPorRepeticion = obtenerFactorPesoCorporal(ejercicio);
        
        // Fórmula: Series × Repeticiones × Factor
        // Ejemplo: 3 series × 15 flexiones × 0.8 = 36 calorías
        BigDecimal totalReps = BigDecimal.valueOf((long) series * repeticiones);
        BigDecimal calorias = totalReps.multiply(factorPorRepeticion);

        log.debug("Calorías peso corporal: {} series × {} reps × factor {} = {} cal", 
                series, repeticiones, factorPorRepeticion, calorias);
        
        return calorias.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula calorías para ejercicios de cardio basado en duración.
     */
    private BigDecimal calcularCaloriasCardio(Ejercicio ejercicio, RegistroEjercicioRequest request) {
        int duracion = request.getDuracionMinutos();
        
        BigDecimal caloriasPorMinuto = ejercicio.getCaloriasQuemadasPorMinuto();
        if (caloriasPorMinuto == null || caloriasPorMinuto.compareTo(BigDecimal.ZERO) == 0) {
            caloriasPorMinuto = estimarCaloriasPorMinuto(ejercicio.getTipoEjercicio());
        }

        BigDecimal calorias = caloriasPorMinuto.multiply(BigDecimal.valueOf(duracion));
        
        log.debug("Calorías cardio: {} min × {} cal/min = {} cal", 
                duracion, caloriasPorMinuto, calorias);
        
        return calorias.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Obtiene el factor de calorías por repetición×kg según el grupo muscular.
     */
    private BigDecimal obtenerFactorGrupoMuscular(Ejercicio.GrupoMuscular grupo) {
        if (grupo == null) {
            return new BigDecimal("0.05"); // Factor medio por defecto
        }
        
        return switch (grupo) {
            // Grupos grandes (más masa muscular = más calorías)
            case PIERNAS, GLUTEOS, CUADRICEPS, ISQUIOTIBIALES, GEMELOS -> new BigDecimal("0.08");
            case ESPALDA -> new BigDecimal("0.07");
            case PECHO -> new BigDecimal("0.06");
            // Grupos medianos
            case HOMBROS, CORE, ABDOMINALES -> new BigDecimal("0.05");
            // Grupos pequeños
            case BICEPS, TRICEPS, BRAZOS -> new BigDecimal("0.04");
            // Cuerpo completo (promedio alto)
            case CUERPO_COMPLETO -> new BigDecimal("0.09");
            // Cardio y otros
            case CARDIO, OTRO -> new BigDecimal("0.05");
        };
    }

    /**
     * Obtiene el factor de calorías por repetición para ejercicios de peso corporal.
     */
    private BigDecimal obtenerFactorPesoCorporal(Ejercicio ejercicio) {
        // Basado en el tipo de ejercicio y grupo muscular
        Ejercicio.GrupoMuscular grupo = ejercicio.getGrupoMuscular();
        
        if (grupo == null) {
            return new BigDecimal("0.5"); // Por defecto
        }
        
        return switch (grupo) {
            // Ejercicios que usan grandes grupos musculares
            case PIERNAS, GLUTEOS, CUADRICEPS, ISQUIOTIBIALES, GEMELOS -> new BigDecimal("1.0"); // Sentadillas, lunges
            case CUERPO_COMPLETO -> new BigDecimal("1.2"); // Burpees, mountain climbers
            case ESPALDA -> new BigDecimal("0.9"); // Dominadas, remo invertido
            case PECHO -> new BigDecimal("0.8"); // Flexiones
            case CORE, ABDOMINALES -> new BigDecimal("0.6"); // Abdominales, plancha
            case HOMBROS -> new BigDecimal("0.7"); // Pike push-ups
            // Grupos pequeños
            case BICEPS, TRICEPS, BRAZOS -> new BigDecimal("0.5"); // Dips, chin-ups
            // Cardio y otros
            case CARDIO, OTRO -> new BigDecimal("0.5");
        };
    }

    /**
     * Estima calorías mínimas cuando no hay suficientes datos.
     */
    private BigDecimal estimarCaloriasMinimas(Ejercicio ejercicio) {
        // Estimación conservadora basada en tipo de ejercicio
        Ejercicio.TipoEjercicio tipo = ejercicio.getTipoEjercicio();
        
        if (tipo == null) {
            return new BigDecimal("20"); // Mínimo 20 calorías
        }
        
        return switch (tipo) {
            case HIIT -> new BigDecimal("50");
            case CARDIO -> new BigDecimal("40");
            case FUERZA, FUNCIONAL -> new BigDecimal("30");
            case DEPORTIVO -> new BigDecimal("35");
            case YOGA, PILATES -> new BigDecimal("15");
            case FLEXIBILIDAD, EQUILIBRIO -> new BigDecimal("10");
            case REHABILITACION -> new BigDecimal("8");
            default -> new BigDecimal("20");
        };
    }

    /**
     * Estima calorías por minuto según el tipo de ejercicio.
     */
    private BigDecimal estimarCaloriasPorMinuto(Ejercicio.TipoEjercicio tipo) {
        if (tipo == null) {
            return new BigDecimal("5.0"); // Valor por defecto
        }
        
        return switch (tipo) {
            case CARDIO -> new BigDecimal("10.0");
            case HIIT -> new BigDecimal("12.0");
            case FUERZA -> new BigDecimal("6.0");
            case FUNCIONAL -> new BigDecimal("8.0");
            case YOGA, PILATES -> new BigDecimal("3.0");
            case FLEXIBILIDAD -> new BigDecimal("2.5");
            case EQUILIBRIO -> new BigDecimal("3.0");
            case DEPORTIVO -> new BigDecimal("9.0");
            case REHABILITACION -> new BigDecimal("2.0");
            case OTRO -> new BigDecimal("5.0");
        };
    }

    // ============================================================
    // Calendario de Comidas
    // ============================================================

    /**
     * Obtiene el calendario de comidas para un rango de fechas.
     * Ideal para vistas semanales o mensuales.
     */
    @Transactional(readOnly = true)
    public CalendarioComidaResponse obtenerCalendarioComidas(Long perfilUsuarioId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo calendario de comidas para usuario {} desde {} hasta {}", 
                perfilUsuarioId, fechaInicio, fechaFin);

        // Verificar si hay plan activo
        var optionalPlan = usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId);
        
        if (optionalPlan.isEmpty()) {
            return CalendarioComidaResponse.builder()
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .nombrePlan(null)
                    .duracionPlanDias(0)
                    .diasConRegistros(0)
                    .totalComidasProgramadas(0)
                    .totalComidasCompletadas(0)
                    .porcentajeCumplimiento(BigDecimal.ZERO)
                    .caloriasConsumidasTotal(BigDecimal.ZERO)
                    .proteinasConsumidasTotal(BigDecimal.ZERO)
                    .carbohidratosConsumidosTotal(BigDecimal.ZERO)
                    .grasasConsumidasTotal(BigDecimal.ZERO)
                    .dias(Collections.emptyList())
                    .build();
        }

        UsuarioPlan planActivo = optionalPlan.get();
        Plan plan = planActivo.getPlan();
        int duracionDias = plan.getDuracionDias() != null ? plan.getDuracionDias() : 1;

        // Acumuladores
        int totalComidasProgramadas = 0;
        int totalComidasCompletadas = 0;
        int diasConRegistros = 0;
        BigDecimal totalCalorias = BigDecimal.ZERO;
        BigDecimal totalProteinas = BigDecimal.ZERO;
        BigDecimal totalCarbos = BigDecimal.ZERO;
        BigDecimal totalGrasas = BigDecimal.ZERO;

        // Generar lista de días
        List<CalendarioComidaResponse.DiaCalendario> dias = new java.util.ArrayList<>();
        
        LocalDate fechaActual = fechaInicio;
        while (!fechaActual.isAfter(fechaFin)) {
            final LocalDate fecha = fechaActual;
            
            // Calcular día del plan
            long diasDesdeInicio = java.time.temporal.ChronoUnit.DAYS.between(planActivo.getFechaInicio(), fecha);
            int diaActual = (int) diasDesdeInicio + 1;
            int diaPlan = diaActual <= duracionDias ? diaActual : ((diaActual - 1) % duracionDias) + 1;
            
            // Obtener comidas del día
            List<PlanDia> comidasDelDia = planDiaRepository.findByPlanIdAndNumeroDia(plan.getId(), diaPlan);
            List<RegistroComida> registrosDelDia = registroComidaRepository
                    .findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);
            
            int comidasProgramadasDia = comidasDelDia.size();
            int comidasCompletadasDia = 0;
            BigDecimal caloriasDia = BigDecimal.ZERO;
            BigDecimal proteinasDia = BigDecimal.ZERO;
            BigDecimal carbosDia = BigDecimal.ZERO;
            BigDecimal grasasDia = BigDecimal.ZERO;
            
            // Mapear comidas
            List<CalendarioComidaResponse.ComidaResumen> comidasResumen = new java.util.ArrayList<>();
            for (PlanDia planDia : comidasDelDia) {
                Comida comida = planDia.getComida();
                NutricionComida nutricion = calcularNutricionComida(comida);
                
                RegistroComida registro = registrosDelDia.stream()
                        .filter(r -> r.getComida().getId().equals(comida.getId()))
                        .findFirst()
                        .orElse(null);
                
                if (registro != null) {
                    comidasCompletadasDia++;
                    BigDecimal porciones = registro.getPorciones() != null ? registro.getPorciones() : BigDecimal.ONE;
                    caloriasDia = caloriasDia.add(nutricion.calorias.multiply(porciones));
                    proteinasDia = proteinasDia.add(nutricion.proteinas.multiply(porciones));
                    carbosDia = carbosDia.add(nutricion.carbohidratos.multiply(porciones));
                    grasasDia = grasasDia.add(nutricion.grasas.multiply(porciones));
                }
                
                comidasResumen.add(CalendarioComidaResponse.ComidaResumen.builder()
                        .comidaId(comida.getId())
                        .nombre(comida.getNombre())
                        .tipoComida(planDia.getTipoComida() != null ? planDia.getTipoComida().getNombre() : null)
                        .tipoComidaId(planDia.getTipoComida() != null ? planDia.getTipoComida().getId() : null)
                        .calorias(nutricion.calorias)
                        .registrada(registro != null)
                        .registroId(registro != null ? registro.getId() : null)
                        .build());
            }
            
            // Actualizar totales
            totalComidasProgramadas += comidasProgramadasDia;
            totalComidasCompletadas += comidasCompletadasDia;
            if (comidasCompletadasDia > 0) diasConRegistros++;
            totalCalorias = totalCalorias.add(caloriasDia);
            totalProteinas = totalProteinas.add(proteinasDia);
            totalCarbos = totalCarbos.add(carbosDia);
            totalGrasas = totalGrasas.add(grasasDia);
            
            // Obtener objetivo de calorías
            BigDecimal caloriasObjetivo = plan.getObjetivo() != null && plan.getObjetivo().getCaloriasObjetivo() != null
                    ? plan.getObjetivo().getCaloriasObjetivo() : BigDecimal.ZERO;
            
            dias.add(CalendarioComidaResponse.DiaCalendario.builder()
                    .fecha(fecha)
                    .diaSemana(fecha.getDayOfWeek().getValue())
                    .nombreDia(obtenerNombreDia(fecha.getDayOfWeek().getValue()))
                    .diaPlan(diaPlan)
                    .comidasProgramadas(comidasProgramadasDia)
                    .comidasCompletadas(comidasCompletadasDia)
                    .diaCompleto(comidasProgramadasDia > 0 && comidasCompletadasDia == comidasProgramadasDia)
                    .caloriasObjetivo(caloriasObjetivo)
                    .caloriasConsumidas(caloriasDia.setScale(2, RoundingMode.HALF_UP))
                    .proteinasConsumidas(proteinasDia.setScale(2, RoundingMode.HALF_UP))
                    .carbohidratosConsumidos(carbosDia.setScale(2, RoundingMode.HALF_UP))
                    .grasasConsumidas(grasasDia.setScale(2, RoundingMode.HALF_UP))
                    .comidas(comidasResumen)
                    .build());
            
            fechaActual = fechaActual.plusDays(1);
        }

        BigDecimal porcentajeCumplimiento = totalComidasProgramadas > 0
                ? BigDecimal.valueOf((double) totalComidasCompletadas / totalComidasProgramadas * 100)
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return CalendarioComidaResponse.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .nombrePlan(plan.getNombre())
                .duracionPlanDias(duracionDias)
                .diasConRegistros(diasConRegistros)
                .totalComidasProgramadas(totalComidasProgramadas)
                .totalComidasCompletadas(totalComidasCompletadas)
                .porcentajeCumplimiento(porcentajeCumplimiento)
                .caloriasConsumidasTotal(totalCalorias.setScale(2, RoundingMode.HALF_UP))
                .proteinasConsumidasTotal(totalProteinas.setScale(2, RoundingMode.HALF_UP))
                .carbohidratosConsumidosTotal(totalCarbos.setScale(2, RoundingMode.HALF_UP))
                .grasasConsumidasTotal(totalGrasas.setScale(2, RoundingMode.HALF_UP))
                .dias(dias)
                .build();
    }

    // ============================================================
    // Progreso Nutricional Semanal
    // ============================================================

    /**
     * Obtiene el progreso nutricional semanal del usuario.
     */
    @Transactional(readOnly = true)
    public ProgresoNutricionalResponse obtenerProgresoNutricional(Long perfilUsuarioId, LocalDate fechaReferencia) {
        log.info("Obteniendo progreso nutricional para usuario {} en semana de {}", perfilUsuarioId, fechaReferencia);

        // Calcular inicio y fin de la semana (lunes a domingo)
        LocalDate inicioSemana = fechaReferencia.with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = fechaReferencia.with(java.time.DayOfWeek.SUNDAY);

        // Verificar si hay plan activo
        var optionalPlan = usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId);
        
        if (optionalPlan.isEmpty()) {
            return ProgresoNutricionalResponse.builder()
                    .inicioSemana(inicioSemana)
                    .finSemana(finSemana)
                    .nombrePlan(null)
                    .caloriasObjetivoSemanal(BigDecimal.ZERO)
                    .proteinasObjetivoSemanal(BigDecimal.ZERO)
                    .carbohidratosObjetivoSemanal(BigDecimal.ZERO)
                    .grasasObjetivoSemanal(BigDecimal.ZERO)
                    .caloriasConsumidasSemanal(BigDecimal.ZERO)
                    .proteinasConsumidasSemanal(BigDecimal.ZERO)
                    .carbohidratosConsumidosSemanal(BigDecimal.ZERO)
                    .grasasConsumidasSemanal(BigDecimal.ZERO)
                    .porcentajeCaloriasCumplido(BigDecimal.ZERO)
                    .porcentajeProteinasCumplido(BigDecimal.ZERO)
                    .porcentajeCarbohidratosCumplido(BigDecimal.ZERO)
                    .porcentajeGrasasCumplido(BigDecimal.ZERO)
                    .caloriasPromedioDiario(BigDecimal.ZERO)
                    .proteinasPromedioDiario(BigDecimal.ZERO)
                    .carbohidratosPromedioDiario(BigDecimal.ZERO)
                    .grasasPromedioDiario(BigDecimal.ZERO)
                    .diasConRegistro(0)
                    .comidasRegistradas(0)
                    .comidasProgramadas(0)
                    .porcentajeComidasCumplido(BigDecimal.ZERO)
                    .diasSemana(Collections.emptyList())
                    .build();
        }

        UsuarioPlan planActivo = optionalPlan.get();
        Plan plan = planActivo.getPlan();
        int duracionDias = plan.getDuracionDias() != null ? plan.getDuracionDias() : 1;

        // Obtener objetivos del plan
        BigDecimal calObjetivoDia = plan.getObjetivo() != null && plan.getObjetivo().getCaloriasObjetivo() != null
                ? plan.getObjetivo().getCaloriasObjetivo() : BigDecimal.ZERO;
        BigDecimal protObjetivoDia = plan.getObjetivo() != null && plan.getObjetivo().getProteinasObjetivo() != null
                ? plan.getObjetivo().getProteinasObjetivo() : BigDecimal.ZERO;
        BigDecimal carbObjetivoDia = plan.getObjetivo() != null && plan.getObjetivo().getCarbohidratosObjetivo() != null
                ? plan.getObjetivo().getCarbohidratosObjetivo() : BigDecimal.ZERO;
        BigDecimal grasObjetivoDia = plan.getObjetivo() != null && plan.getObjetivo().getGrasasObjetivo() != null
                ? plan.getObjetivo().getGrasasObjetivo() : BigDecimal.ZERO;

        // Objetivos semanales (x7)
        BigDecimal calObjetivoSemanal = calObjetivoDia.multiply(BigDecimal.valueOf(7));
        BigDecimal protObjetivoSemanal = protObjetivoDia.multiply(BigDecimal.valueOf(7));
        BigDecimal carbObjetivoSemanal = carbObjetivoDia.multiply(BigDecimal.valueOf(7));
        BigDecimal grasObjetivoSemanal = grasObjetivoDia.multiply(BigDecimal.valueOf(7));

        // Acumuladores
        BigDecimal totalCalorias = BigDecimal.ZERO;
        BigDecimal totalProteinas = BigDecimal.ZERO;
        BigDecimal totalCarbos = BigDecimal.ZERO;
        BigDecimal totalGrasas = BigDecimal.ZERO;
        int diasConRegistro = 0;
        int totalComidasRegistradas = 0;
        int totalComidasProgramadas = 0;

        // Generar lista de días
        List<ProgresoNutricionalResponse.DiaNutricional> diasSemana = new java.util.ArrayList<>();
        
        LocalDate fechaActual = inicioSemana;
        while (!fechaActual.isAfter(finSemana)) {
            final LocalDate fecha = fechaActual;
            
            // Calcular día del plan
            long diasDesdeInicio = java.time.temporal.ChronoUnit.DAYS.between(planActivo.getFechaInicio(), fecha);
            int diaActual = (int) diasDesdeInicio + 1;
            int diaPlan = diaActual <= duracionDias ? diaActual : ((diaActual - 1) % duracionDias) + 1;
            
            // Obtener comidas programadas y registradas
            List<PlanDia> comidasDelDia = planDiaRepository.findByPlanIdAndNumeroDia(plan.getId(), diaPlan);
            List<RegistroComida> registrosDelDia = registroComidaRepository
                    .findByPerfilUsuarioIdAndFecha(perfilUsuarioId, fecha);
            
            int comidasProgramadasDia = comidasDelDia.size();
            int comidasRegistradasDia = 0;
            BigDecimal caloriasDia = BigDecimal.ZERO;
            BigDecimal proteinasDia = BigDecimal.ZERO;
            BigDecimal carbosDia = BigDecimal.ZERO;
            BigDecimal grasasDia = BigDecimal.ZERO;
            
            // Calcular nutrición del día
            for (RegistroComida registro : registrosDelDia) {
                comidasRegistradasDia++;
                BigDecimal porciones = registro.getPorciones() != null ? registro.getPorciones() : BigDecimal.ONE;
                NutricionComida nutricion = calcularNutricionComida(registro.getComida());
                caloriasDia = caloriasDia.add(nutricion.calorias.multiply(porciones));
                proteinasDia = proteinasDia.add(nutricion.proteinas.multiply(porciones));
                carbosDia = carbosDia.add(nutricion.carbohidratos.multiply(porciones));
                grasasDia = grasasDia.add(nutricion.grasas.multiply(porciones));
            }
            
            // Actualizar totales
            totalComidasProgramadas += comidasProgramadasDia;
            totalComidasRegistradas += comidasRegistradasDia;
            if (comidasRegistradasDia > 0) diasConRegistro++;
            totalCalorias = totalCalorias.add(caloriasDia);
            totalProteinas = totalProteinas.add(proteinasDia);
            totalCarbos = totalCarbos.add(carbosDia);
            totalGrasas = totalGrasas.add(grasasDia);
            
            diasSemana.add(ProgresoNutricionalResponse.DiaNutricional.builder()
                    .fecha(fecha)
                    .nombreDia(obtenerNombreDia(fecha.getDayOfWeek().getValue()))
                    .diaSemana(fecha.getDayOfWeek().getValue())
                    .caloriasObjetivo(calObjetivoDia)
                    .proteinasObjetivo(protObjetivoDia)
                    .carbohidratosObjetivo(carbObjetivoDia)
                    .grasasObjetivo(grasObjetivoDia)
                    .caloriasConsumidas(caloriasDia.setScale(2, RoundingMode.HALF_UP))
                    .proteinasConsumidas(proteinasDia.setScale(2, RoundingMode.HALF_UP))
                    .carbohidratosConsumidos(carbosDia.setScale(2, RoundingMode.HALF_UP))
                    .grasasConsumidas(grasasDia.setScale(2, RoundingMode.HALF_UP))
                    .comidasRegistradas(comidasRegistradasDia)
                    .comidasProgramadas(comidasProgramadasDia)
                    .diaCompleto(comidasProgramadasDia > 0 && comidasRegistradasDia == comidasProgramadasDia)
                    .build());
            
            fechaActual = fechaActual.plusDays(1);
        }

        // Calcular porcentajes de cumplimiento
        BigDecimal pctCalorias = calObjetivoSemanal.compareTo(BigDecimal.ZERO) > 0
                ? totalCalorias.divide(calObjetivoSemanal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal pctProteinas = protObjetivoSemanal.compareTo(BigDecimal.ZERO) > 0
                ? totalProteinas.divide(protObjetivoSemanal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal pctCarbos = carbObjetivoSemanal.compareTo(BigDecimal.ZERO) > 0
                ? totalCarbos.divide(carbObjetivoSemanal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal pctGrasas = grasObjetivoSemanal.compareTo(BigDecimal.ZERO) > 0
                ? totalGrasas.divide(grasObjetivoSemanal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal pctComidas = totalComidasProgramadas > 0
                ? BigDecimal.valueOf((double) totalComidasRegistradas / totalComidasProgramadas * 100)
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Promedios diarios (sobre días con registro)
        int diasParaPromedio = diasConRegistro > 0 ? diasConRegistro : 1;
        BigDecimal calPromedio = totalCalorias.divide(BigDecimal.valueOf(diasParaPromedio), 2, RoundingMode.HALF_UP);
        BigDecimal protPromedio = totalProteinas.divide(BigDecimal.valueOf(diasParaPromedio), 2, RoundingMode.HALF_UP);
        BigDecimal carbPromedio = totalCarbos.divide(BigDecimal.valueOf(diasParaPromedio), 2, RoundingMode.HALF_UP);
        BigDecimal grasPromedio = totalGrasas.divide(BigDecimal.valueOf(diasParaPromedio), 2, RoundingMode.HALF_UP);

        return ProgresoNutricionalResponse.builder()
                .inicioSemana(inicioSemana)
                .finSemana(finSemana)
                .nombrePlan(plan.getNombre())
                .caloriasObjetivoSemanal(calObjetivoSemanal)
                .proteinasObjetivoSemanal(protObjetivoSemanal)
                .carbohidratosObjetivoSemanal(carbObjetivoSemanal)
                .grasasObjetivoSemanal(grasObjetivoSemanal)
                .caloriasConsumidasSemanal(totalCalorias.setScale(2, RoundingMode.HALF_UP))
                .proteinasConsumidasSemanal(totalProteinas.setScale(2, RoundingMode.HALF_UP))
                .carbohidratosConsumidosSemanal(totalCarbos.setScale(2, RoundingMode.HALF_UP))
                .grasasConsumidasSemanal(totalGrasas.setScale(2, RoundingMode.HALF_UP))
                .porcentajeCaloriasCumplido(pctCalorias)
                .porcentajeProteinasCumplido(pctProteinas)
                .porcentajeCarbohidratosCumplido(pctCarbos)
                .porcentajeGrasasCumplido(pctGrasas)
                .caloriasPromedioDiario(calPromedio)
                .proteinasPromedioDiario(protPromedio)
                .carbohidratosPromedioDiario(carbPromedio)
                .grasasPromedioDiario(grasPromedio)
                .diasConRegistro(diasConRegistro)
                .comidasRegistradas(totalComidasRegistradas)
                .comidasProgramadas(totalComidasProgramadas)
                .porcentajeComidasCumplido(pctComidas)
                .diasSemana(diasSemana)
                .build();
    }

    // ============================================================
    // Registrar Comida Extra (no del plan)
    // ============================================================

    /**
     * Registra una comida extra que no estaba en el plan.
     * Puede ser una comida del catálogo o una comida manual.
     */
    @Transactional
    public RegistroComidaResponse registrarComidaExtra(Long perfilUsuarioId, RegistroComidaExtraRequest request) {
        log.info("Registrando comida extra para usuario {}", perfilUsuarioId);

        PerfilUsuario perfil = perfilUsuarioRepository.findById(perfilUsuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuario no encontrado"));

        // Obtener plan activo (si existe, para asociar el registro)
        UsuarioPlan usuarioPlan = usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId).orElse(null);

        // Resolver comida
        Comida comida;
        if (request.getComidaId() != null) {
            // Comida del catálogo
            comida = comidaRepository.findById(request.getComidaId())
                    .orElseThrow(() -> new EntityNotFoundException("Comida no encontrada"));
        } else if (request.getNombreComida() != null && !request.getNombreComida().isBlank()) {
            // Comida manual - crear una comida temporal o buscar por nombre
            comida = comidaRepository.findByNombreIgnoreCase(request.getNombreComida())
                    .orElseGet(() -> {
                        // Si no existe, crear una comida nueva (simple, sin ingredientes)
                        Comida nuevaComida = Comida.builder()
                                .nombre(request.getNombreComida())
                                .descripcion(request.getDescripcion())
                                .build();
                        return comidaRepository.save(nuevaComida);
                    });
        } else {
            throw new BusinessException("Debe especificar una comida del catálogo (comidaId) o un nombre de comida");
        }

        // Resolver tipo de comida
        TipoComidaEntity tipoComida = null;
        if (request.getTipoComidaId() != null) {
            tipoComida = tipoComidaRepository.findById(request.getTipoComidaId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de comida no encontrado"));
        } else if (request.getTipoComidaNombre() != null) {
            tipoComida = tipoComidaRepository.findByNombreIgnoreCase(request.getTipoComidaNombre())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de comida no encontrado: " + request.getTipoComidaNombre()));
        }

        // Calcular calorías
        BigDecimal calorias;
        if (request.getCalorias() != null && request.getCalorias().compareTo(BigDecimal.ZERO) > 0) {
            calorias = request.getCalorias();
        } else {
            NutricionComida nutricion = calcularNutricionComida(comida);
            calorias = nutricion.calorias;
        }

        BigDecimal porciones = request.getPorciones() != null ? request.getPorciones() : BigDecimal.ONE;

        RegistroComida registro = RegistroComida.builder()
                .perfilUsuario(perfil)
                .comida(comida)
                .usuarioPlan(usuarioPlan)
                .fecha(request.getFecha() != null ? request.getFecha() : LocalDate.now())
                .hora(request.getHora() != null ? request.getHora() : LocalTime.now())
                .tipoComida(tipoComida)
                .porciones(porciones)
                .caloriasConsumidas(calorias.multiply(porciones))
                .notas(request.getNotas())
                .build();

        registro = registroComidaRepository.save(registro);
        log.info("Comida extra registrada exitosamente con ID {}", registro.getId());

        return RegistroComidaResponse.fromEntity(registro);
    }

    // ============================================================
    // Progreso Acumulado del Plan
    // ============================================================

    /**
     * Obtiene el progreso acumulado del plan nutricional activo.
     * Incluye estadísticas completas desde el inicio del plan.
     */
    @Transactional(readOnly = true)
    public ProgresoPlanResponse obtenerProgresoPlan(Long perfilUsuarioId) {
        log.info("Obteniendo progreso del plan para usuario {}", perfilUsuarioId);

        LocalDate hoy = LocalDate.now();

        // Obtener plan activo
        var optionalPlan = usuarioPlanRepository.findPlanActivoActual(perfilUsuarioId);
        
        if (optionalPlan.isEmpty()) {
            return ProgresoPlanResponse.builder()
                    .usuarioPlanId(null)
                    .planId(null)
                    .nombrePlan(null)
                    .fechaInicio(null)
                    .fechaActual(hoy)
                    .diaActual(0)
                    .diaPlanCiclico(0)
                    .duracionDias(0)
                    .cicloActual(0)
                    .diasCompletados(0)
                    .diasParciales(0)
                    .diasSinRegistro(0)
                    .porcentajeDiasCompletados(BigDecimal.ZERO)
                    .totalComidasProgramadas(0)
                    .totalComidasRegistradas(0)
                    .porcentajeComidasRegistradas(BigDecimal.ZERO)
                    .comidasHoyProgramadas(0)
                    .comidasHoyCompletadas(0)
                    .diaActualCompleto(false)
                    .rachaActual(0)
                    .rachaMejor(0)
                    .historialDias(Collections.emptyList())
                    .build();
        }

        UsuarioPlan planActivo = optionalPlan.get();
        Plan plan = planActivo.getPlan();
        LocalDate fechaInicio = planActivo.getFechaInicio();
        int duracionDias = plan.getDuracionDias() != null ? plan.getDuracionDias() : 1;

        // Calcular día actual y ciclo
        long diasDesdeInicio = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, hoy);
        int diaActual = (int) diasDesdeInicio + 1;
        int diaPlanCiclico = diaActual <= duracionDias ? diaActual : ((diaActual - 1) % duracionDias) + 1;
        int cicloActual = diaActual <= duracionDias ? 1 : ((diaActual - 1) / duracionDias) + 1;

        // Obtener todos los registros desde el inicio del plan
        List<RegistroComida> todosLosRegistros = registroComidaRepository
                .findByPerfilUsuarioIdAndFechaBetween(perfilUsuarioId, fechaInicio, hoy);

        // Agrupar registros por fecha
        java.util.Map<LocalDate, List<RegistroComida>> registrosPorFecha = todosLosRegistros.stream()
                .collect(Collectors.groupingBy(RegistroComida::getFecha));

        // Calcular estadísticas día por día
        int diasCompletados = 0;
        int diasParciales = 0;
        int diasSinRegistro = 0;
        int totalComidasProgramadas = 0;
        int totalComidasRegistradas = 0;
        int rachaActual = 0;
        int rachaMejor = 0;
        int rachaTemp = 0;

        List<ProgresoPlanResponse.DiaPlanInfo> historialDias = new java.util.ArrayList<>();

        // Iterar desde fecha de inicio hasta hoy
        LocalDate fecha = fechaInicio;
        while (!fecha.isAfter(hoy)) {
            // Calcular día del plan para esta fecha
            long diasDesdeFecha = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fecha);
            int diaDelPlan = (int) diasDesdeFecha + 1;
            int diaCiclico = diaDelPlan <= duracionDias ? diaDelPlan : ((diaDelPlan - 1) % duracionDias) + 1;

            // Obtener comidas programadas para ese día del plan
            List<PlanDia> comidasProgramadas = planDiaRepository.findByPlanIdAndNumeroDia(plan.getId(), diaCiclico);
            int numProgramadas = comidasProgramadas.size();

            // Obtener registros de ese día
            List<RegistroComida> registrosDia = registrosPorFecha.getOrDefault(fecha, Collections.emptyList());
            
            // Contar cuántas comidas del plan fueron registradas
            int numRegistradas = 0;
            for (PlanDia planDia : comidasProgramadas) {
                boolean registrada = registrosDia.stream()
                        .anyMatch(r -> r.getComida().getId().equals(planDia.getComida().getId()));
                if (registrada) numRegistradas++;
            }

            totalComidasProgramadas += numProgramadas;
            totalComidasRegistradas += numRegistradas;

            // Determinar estado del día
            String estado;
            boolean completo;
            if (numProgramadas == 0) {
                estado = "SIN_COMIDAS";
                completo = true; // Si no hay comidas programadas, se considera completo
                rachaTemp++;
            } else if (numRegistradas == numProgramadas) {
                estado = "COMPLETO";
                completo = true;
                diasCompletados++;
                rachaTemp++;
            } else if (numRegistradas > 0) {
                estado = "PARCIAL";
                completo = false;
                diasParciales++;
                rachaMejor = Math.max(rachaMejor, rachaTemp);
                rachaTemp = 0;
            } else {
                estado = "SIN_REGISTRO";
                completo = false;
                diasSinRegistro++;
                rachaMejor = Math.max(rachaMejor, rachaTemp);
                rachaTemp = 0;
            }

            // Agregar al historial (últimos 7 días)
            if (java.time.temporal.ChronoUnit.DAYS.between(fecha, hoy) < 7) {
                historialDias.add(ProgresoPlanResponse.DiaPlanInfo.builder()
                        .fecha(fecha)
                        .diaPlan(diaCiclico)
                        .comidasProgramadas(numProgramadas)
                        .comidasCompletadas(numRegistradas)
                        .completo(completo)
                        .estado(estado)
                        .build());
            }

            fecha = fecha.plusDays(1);
        }

        // Actualizar racha actual
        rachaMejor = Math.max(rachaMejor, rachaTemp);
        rachaActual = rachaTemp;

        // Calcular comidas de hoy
        List<PlanDia> comidasHoy = planDiaRepository.findByPlanIdAndNumeroDia(plan.getId(), diaPlanCiclico);
        List<RegistroComida> registrosHoy = registrosPorFecha.getOrDefault(hoy, Collections.emptyList());
        int comidasHoyProgramadas = comidasHoy.size();
        int comidasHoyCompletadas = 0;
        for (PlanDia planDia : comidasHoy) {
            boolean registrada = registrosHoy.stream()
                    .anyMatch(r -> r.getComida().getId().equals(planDia.getComida().getId()));
            if (registrada) comidasHoyCompletadas++;
        }
        boolean diaActualCompleto = comidasHoyProgramadas > 0 && comidasHoyCompletadas == comidasHoyProgramadas;

        // Calcular porcentajes
        int diasTranscurridos = diasCompletados + diasParciales + diasSinRegistro;
        BigDecimal porcentajeDias = diasTranscurridos > 0
                ? BigDecimal.valueOf((double) diasCompletados / diasTranscurridos * 100)
                        .setScale(1, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal porcentajeComidas = totalComidasProgramadas > 0
                ? BigDecimal.valueOf((double) totalComidasRegistradas / totalComidasProgramadas * 100)
                        .setScale(1, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ProgresoPlanResponse.builder()
                .usuarioPlanId(planActivo.getId())
                .planId(plan.getId())
                .nombrePlan(plan.getNombre())
                .fechaInicio(fechaInicio)
                .fechaActual(hoy)
                .diaActual(diaActual)
                .diaPlanCiclico(diaPlanCiclico)
                .duracionDias(duracionDias)
                .cicloActual(cicloActual)
                .diasCompletados(diasCompletados)
                .diasParciales(diasParciales)
                .diasSinRegistro(diasSinRegistro)
                .porcentajeDiasCompletados(porcentajeDias)
                .totalComidasProgramadas(totalComidasProgramadas)
                .totalComidasRegistradas(totalComidasRegistradas)
                .porcentajeComidasRegistradas(porcentajeComidas)
                .comidasHoyProgramadas(comidasHoyProgramadas)
                .comidasHoyCompletadas(comidasHoyCompletadas)
                .diaActualCompleto(diaActualCompleto)
                .rachaActual(rachaActual)
                .rachaMejor(rachaMejor)
                .historialDias(historialDias)
                .build();
    }
}
