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
import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanDiaRepository planDiaRepository;

    @Mock
    private PlanObjetivoRepository planObjetivoRepository;

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private PlanService planService;

    private Plan planMock;
    private PlanObjetivo objetivoMock;
    private Comida comidaMock;
    private Etiqueta etiquetaMock;
    private Ingrediente ingredienteMock;
    private Receta recetaMock;

    @BeforeEach
    void setUp() {
        // Plan mock
        planMock = Plan.builder()
                .id(1L)
                .nombre("Plan Fitness 30 días")
                .descripcion("Plan nutricional para pérdida de peso")
                .duracionDias(30)
                .activo(true)
                .etiquetas(new ArrayList<>())
                .planDias(new ArrayList<>())
                .build();

        // Objetivo mock
        objetivoMock = PlanObjetivo.builder()
                .id(1L)
                .plan(planMock)
                .caloriasObjetivo(new BigDecimal("2000.00"))
                .proteinasObjetivo(new BigDecimal("150.00"))
                .grasasObjetivo(new BigDecimal("60.00"))
                .carbohidratosObjetivo(new BigDecimal("200.00"))
                .descripcion("Objetivo de pérdida de peso")
                .build();

        planMock.setObjetivo(objetivoMock);

        // Comida mock
        comidaMock = Comida.builder()
                .id(1L)
                .nombre("Pollo con Arroz")
                .tipoComida(TipoComida.ALMUERZO)
                .tiempoElaboracion(30)
                .build();

        // Ingrediente mock
        ingredienteMock = Ingrediente.builder()
                .id(1L)
                .nombre("Pechuga de Pollo")
                .grupoAlimenticio(GrupoAlimenticio.PROTEINAS_ANIMALES)
                .energia(new BigDecimal("165.00"))
                .proteinas(new BigDecimal("31.00"))
                .grasas(new BigDecimal("3.60"))
                .carbohidratos(new BigDecimal("0.00"))
                .build();

        // Receta mock
        recetaMock = Receta.builder()
                .idComida(1L)
                .idIngrediente(1L)
                .cantidadIngrediente(new BigDecimal("150.00"))
                .comida(comidaMock)
                .ingrediente(ingredienteMock)
                .build();

        // Etiqueta mock
        etiquetaMock = Etiqueta.builder()
                .id(1L)
                .nombre("Fitness")
                .build();
    }

    @Test
    @DisplayName("Crear plan válido con objetivo debe guardar correctamente")
    void crearPlan_ConObjetivo_DebeGuardarCorrectamente() {
        // Arrange
        CrearPlanRequest request = CrearPlanRequest.builder()
                .nombre("Plan Fitness 30 días")
                .descripcion("Plan nutricional para pérdida de peso")
                .duracionDias(30)
                .objetivo(CrearPlanRequest.ObjetivoNutricional.builder()
                        .caloriasObjetivo(new BigDecimal("2000.00"))
                        .proteinasObjetivo(new BigDecimal("150.00"))
                        .grasasObjetivo(new BigDecimal("60.00"))
                        .carbohidratosObjetivo(new BigDecimal("200.00"))
                        .descripcion("Objetivo de pérdida de peso")
                        .build())
                .build();

        when(planRepository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(planRepository.save(any(Plan.class))).thenReturn(planMock);
        when(planObjetivoRepository.save(any(PlanObjetivo.class))).thenReturn(objetivoMock);
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanResponse response = planService.crear(request);

        // Assert
        assertNotNull(response);
        assertEquals("Plan Fitness 30 días", response.getNombre());
        assertEquals(30, response.getDuracionDias());
        assertNotNull(response.getObjetivo());
        assertEquals(new BigDecimal("2000.00"), response.getObjetivo().getCaloriasObjetivo());
        verify(planRepository).save(any(Plan.class));
        verify(planObjetivoRepository).save(any(PlanObjetivo.class));
    }

    @Test
    @DisplayName("Crear plan con nombre duplicado debe lanzar excepción")
    void crearPlan_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Arrange
        CrearPlanRequest request = CrearPlanRequest.builder()
                .nombre("Plan Fitness 30 días")
                .duracionDias(30)
                .build();

        when(planRepository.existsByNombreIgnoreCase(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> planService.crear(request));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    @DisplayName("Crear plan con etiquetas debe asociarlas correctamente")
    void crearPlan_ConEtiquetas_DebeAsociarlas() {
        // Arrange
        CrearPlanRequest request = CrearPlanRequest.builder()
                .nombre("Plan Fitness 30 días")
                .duracionDias(30)
                .etiquetasIds(Arrays.asList(1L))
                .build();

        when(planRepository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(etiquetaRepository.findAllById(anyList())).thenReturn(Arrays.asList(etiquetaMock));
        when(planRepository.save(any(Plan.class))).thenReturn(planMock);
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanResponse response = planService.crear(request);

        // Assert
        assertNotNull(response);
        verify(etiquetaRepository).findAllById(anyList());
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("Actualizar plan con nombre duplicado debe lanzar excepción")
    void actualizarPlan_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Arrange
        ActualizarPlanRequest request = ActualizarPlanRequest.builder()
                .nombre("Plan Duplicado")
                .build();

        when(planRepository.findById(anyLong())).thenReturn(Optional.of(planMock));
        when(planRepository.existsByNombreIgnoreCaseAndIdNot(anyString(), anyLong())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> planService.actualizar(1L, request));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    @DisplayName("Actualizar plan inexistente debe lanzar excepción")
    void actualizarPlan_Inexistente_DebeLanzarExcepcion() {
        // Arrange
        ActualizarPlanRequest request = ActualizarPlanRequest.builder()
                .nombre("Plan Actualizado")
                .build();

        when(planRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> planService.actualizar(1L, request));
    }

    @Test
    @DisplayName("Actualizar plan con nuevo objetivo debe actualizarlo")
    void actualizarPlan_ConNuevoObjetivo_DebeActualizarlo() {
        // Arrange
        ActualizarPlanRequest request = ActualizarPlanRequest.builder()
                .objetivo(ActualizarPlanRequest.ObjetivoNutricional.builder()
                        .caloriasObjetivo(new BigDecimal("2500.00"))
                        .proteinasObjetivo(new BigDecimal("180.00"))
                        .build())
                .build();

        when(planRepository.findById(anyLong())).thenReturn(Optional.of(planMock));
        when(planObjetivoRepository.findByPlanId(anyLong())).thenReturn(Optional.of(objetivoMock));
        when(planObjetivoRepository.save(any(PlanObjetivo.class))).thenReturn(objetivoMock);
        when(planRepository.save(any(Plan.class))).thenReturn(planMock);
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanResponse response = planService.actualizar(1L, request);

        // Assert
        assertNotNull(response);
        verify(planObjetivoRepository).save(any(PlanObjetivo.class));
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("Eliminar plan existente debe eliminarlo")
    void eliminarPlan_Existente_DebeEliminarlo() {
        // Arrange
        when(planRepository.existsById(anyLong())).thenReturn(true);

        // Act
        planService.eliminar(1L);

        // Assert
        verify(planRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar plan inexistente debe lanzar excepción")
    void eliminarPlan_Inexistente_DebeLanzarExcepcion() {
        // Arrange
        when(planRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> planService.eliminar(1L));
        verify(planRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Buscar plan por ID debe retornarlo")
    void buscarPlanPorId_Existente_DebeRetornarlo() {
        // Arrange
        when(planRepository.findById(anyLong())).thenReturn(Optional.of(planMock));
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanResponse response = planService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Plan Fitness 30 días", response.getNombre());
        verify(planRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar plan por ID inexistente debe lanzar excepción")
    void buscarPlanPorId_Inexistente_DebeLanzarExcepcion() {
        // Arrange
        when(planRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> planService.buscarPorId(1L));
    }

    @Test
    @DisplayName("Listar todos los planes debe retornar lista")
    void listarTodosLosPlanes_DebeRetornarLista() {
        // Arrange
        when(planRepository.findAll()).thenReturn(Arrays.asList(planMock));
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        List<PlanResponse> response = planService.listarTodos();

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        verify(planRepository).findAll();
    }

    @Test
    @DisplayName("Listar planes activos debe retornar solo activos")
    void listarPlanesActivos_DebeRetornarSoloActivos() {
        // Arrange
        when(planRepository.findByActivoTrue()).thenReturn(Arrays.asList(planMock));
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        List<PlanResponse> response = planService.listarActivos();

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        verify(planRepository).findByActivoTrue();
    }

    @Test
    @DisplayName("Buscar planes por nombre debe retornar coincidencias")
    void buscarPlanesPorNombre_DebeRetornarCoincidencias() {
        // Arrange
        when(planRepository.findByNombreContainingIgnoreCase(anyString())).thenReturn(Arrays.asList(planMock));
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        List<PlanResponse> response = planService.buscarPorNombre("Fitness");

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        verify(planRepository).findByNombreContainingIgnoreCase("Fitness");
    }

    @Test
    @DisplayName("Buscar planes por etiqueta debe retornarlos")
    void buscarPlanesPorEtiqueta_DebeRetornarlos() {
        // Arrange
        when(etiquetaRepository.existsById(anyLong())).thenReturn(true);
        when(planRepository.findByEtiquetaId(anyLong())).thenReturn(Arrays.asList(planMock));
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        List<PlanResponse> response = planService.buscarPorEtiqueta(1L);

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        verify(planRepository).findByEtiquetaId(1L);
    }

    @Test
    @DisplayName("Buscar planes por etiqueta inexistente debe lanzar excepción")
    void buscarPlanesPorEtiqueta_Inexistente_DebeLanzarExcepcion() {
        // Arrange
        when(etiquetaRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> planService.buscarPorEtiqueta(1L));
    }

    @Test
    @DisplayName("Agregar comida al plan debe crearla correctamente")
    void agregarComidaAlPlan_DebeCrearla() {
        // Arrange
        AgregarComidaPlanRequest request = AgregarComidaPlanRequest.builder()
                .numeroDia(1)
                .tipoComida(TipoComida.DESAYUNO)
                .comidaId(1L)
                .notas("Primera comida del día")
                .build();

        when(planRepository.findById(anyLong())).thenReturn(Optional.of(planMock));
        when(comidaRepository.findById(anyLong())).thenReturn(Optional.of(comidaMock));
        when(planDiaRepository.existsByPlanIdAndNumeroDiaAndTipoComida(anyLong(), anyInt(), any())).thenReturn(false);
        when(planDiaRepository.save(any(PlanDia.class))).thenReturn(new PlanDia());
        when(planRepository.findByIdWithDias(anyLong())).thenReturn(Optional.of(planMock));
        when(planObjetivoRepository.findByPlanId(anyLong())).thenReturn(Optional.of(objetivoMock));
        when(planDiaRepository.findByPlanIdWithComida(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanDetalleResponse response = planService.agregarComida(1L, request);

        // Assert
        assertNotNull(response);
        verify(planDiaRepository).save(any(PlanDia.class));
    }

    @Test
    @DisplayName("Agregar comida con día que excede duración debe lanzar excepción")
    void agregarComida_ConDiaExcedeDuracion_DebeLanzarExcepcion() {
        // Arrange
        AgregarComidaPlanRequest request = AgregarComidaPlanRequest.builder()
                .numeroDia(50)
                .tipoComida(TipoComida.DESAYUNO)
                .comidaId(1L)
                .build();

        when(planRepository.findById(anyLong())).thenReturn(Optional.of(planMock));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> planService.agregarComida(1L, request));
    }

    @Test
    @DisplayName("Agregar comida duplicada (mismo día y tipo) debe lanzar excepción")
    void agregarComida_Duplicada_DebeLanzarExcepcion() {
        // Arrange
        AgregarComidaPlanRequest request = AgregarComidaPlanRequest.builder()
                .numeroDia(1)
                .tipoComida(TipoComida.DESAYUNO)
                .comidaId(1L)
                .build();

        when(planRepository.findById(anyLong())).thenReturn(Optional.of(planMock));
        when(comidaRepository.findById(anyLong())).thenReturn(Optional.of(comidaMock));
        when(planDiaRepository.existsByPlanIdAndNumeroDiaAndTipoComida(anyLong(), anyInt(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> planService.agregarComida(1L, request));
    }

    @Test
    @DisplayName("Remover comida del plan debe eliminarla")
    void removerComidaDelPlan_DebeEliminarla() {
        // Arrange
        PlanDia planDia = PlanDia.builder()
                .id(1L)
                .plan(planMock)
                .numeroDia(1)
                .tipoComida(TipoComida.DESAYUNO)
                .comida(comidaMock)
                .build();

        when(planRepository.existsById(anyLong())).thenReturn(true);
        when(planDiaRepository.findByPlanIdAndNumeroDiaAndTipoComida(anyLong(), anyInt(), any()))
                .thenReturn(Optional.of(planDia));
        when(planRepository.findByIdWithDias(anyLong())).thenReturn(Optional.of(planMock));
        when(planObjetivoRepository.findByPlanId(anyLong())).thenReturn(Optional.of(objetivoMock));
        when(planDiaRepository.findByPlanIdWithComida(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanDetalleResponse response = planService.removerComida(1L, 1, TipoComida.DESAYUNO);

        // Assert
        assertNotNull(response);
        verify(planDiaRepository).delete(planDia);
    }

    @Test
    @DisplayName("Remover comida inexistente debe lanzar excepción")
    void removerComida_Inexistente_DebeLanzarExcepcion() {
        // Arrange
        when(planRepository.existsById(anyLong())).thenReturn(true);
        when(planDiaRepository.findByPlanIdAndNumeroDiaAndTipoComida(anyLong(), anyInt(), any()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
                () -> planService.removerComida(1L, 1, TipoComida.DESAYUNO));
    }

    @Test
    @DisplayName("Agregar etiqueta al plan debe asociarla")
    void agregarEtiquetaAlPlan_DebeAsociarla() {
        // Arrange
        when(planRepository.findByIdWithEtiquetas(anyLong())).thenReturn(Optional.of(planMock));
        when(etiquetaRepository.findById(anyLong())).thenReturn(Optional.of(etiquetaMock));
        when(planRepository.save(any(Plan.class))).thenReturn(planMock);
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanResponse response = planService.agregarEtiqueta(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("Agregar etiqueta duplicada debe lanzar excepción")
    void agregarEtiqueta_Duplicada_DebeLanzarExcepcion() {
        // Arrange
        planMock.getEtiquetas().add(etiquetaMock);

        when(planRepository.findByIdWithEtiquetas(anyLong())).thenReturn(Optional.of(planMock));
        when(etiquetaRepository.findById(anyLong())).thenReturn(Optional.of(etiquetaMock));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> planService.agregarEtiqueta(1L, 1L));
    }

    @Test
    @DisplayName("Remover etiqueta del plan debe desasociarla")
    void removerEtiquetaDelPlan_DebeDesasociarla() {
        // Arrange
        planMock.getEtiquetas().add(etiquetaMock);

        when(planRepository.findByIdWithEtiquetas(anyLong())).thenReturn(Optional.of(planMock));
        when(etiquetaRepository.findById(anyLong())).thenReturn(Optional.of(etiquetaMock));
        when(planRepository.save(any(Plan.class))).thenReturn(planMock);
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(anyLong())).thenReturn(new ArrayList<>());

        // Act
        PlanResponse response = planService.removerEtiqueta(1L, 1L);

        // Assert
        assertNotNull(response);
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("Buscar detalle del plan con comidas debe calcular nutrición")
    void buscarDetallePlan_ConComidas_DebeCalcularNutricion() {
        // Arrange
        PlanDia planDia = PlanDia.builder()
                .id(1L)
                .plan(planMock)
                .numeroDia(1)
                .tipoComida(TipoComida.DESAYUNO)
                .comida(comidaMock)
                .build();

        when(planRepository.findByIdWithDias(anyLong())).thenReturn(Optional.of(planMock));
        when(planObjetivoRepository.findByPlanId(anyLong())).thenReturn(Optional.of(objetivoMock));
        when(planDiaRepository.findByPlanIdWithComida(anyLong())).thenReturn(Arrays.asList(planDia));
        when(recetaRepository.findByIdComida(anyLong())).thenReturn(Arrays.asList(recetaMock));

        // Act
        PlanDetalleResponse response = planService.buscarDetallePorId(1L);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getDiasPorNumero());
        assertNotNull(response.getNutricionPorDia());
        verify(recetaRepository, atLeastOnce()).findByIdComida(anyLong());
    }
}
