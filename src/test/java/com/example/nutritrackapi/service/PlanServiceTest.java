package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlanService - Tests unitarios Módulo 3")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanObjetivoRepository planObjetivoRepository;

    @Mock
    private PlanDiaRepository planDiaRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private CuentaAuthRepository cuentaAuthRepository;

    @Mock
    private TipoComidaRepository tipoComidaRepository;

    @InjectMocks
    private PlanService planService;

    private Plan plan;
    private PlanRequest planRequest;
    private PlanObjetivo planObjetivo;
    private Etiqueta etiqueta;
    private Comida comida;
    private PlanDia planDia;
    private TipoComidaEntity tipoComidaDesayuno;

    @BeforeEach
    void setUp() {
        tipoComidaDesayuno = TipoComidaEntity.builder()
                .id(1L)
                .nombre("DESAYUNO")
                .descripcion("Primera comida del día")
                .ordenVisualizacion(1)
                .activo(true)
                .build();

        etiqueta = new Etiqueta();
        etiqueta.setId(1L);
        etiqueta.setNombre("Pérdida de peso");
        etiqueta.setTipoEtiqueta(Etiqueta.TipoEtiqueta.OBJETIVO);

        plan = new Plan();
        plan.setId(1L);
        plan.setNombre("Plan Keto 30 días");
        plan.setDescripcion("Plan cetogénico para 30 días");
        plan.setDuracionDias(30);
        plan.setActivo(true);
        plan.setEtiquetas(new HashSet<>(Set.of(etiqueta)));

        planObjetivo = new PlanObjetivo();
        planObjetivo.setId(1L);
        planObjetivo.setPlan(plan);
        planObjetivo.setCaloriasObjetivo(new BigDecimal("1800.00"));
        planObjetivo.setProteinasObjetivo(new BigDecimal("135.00"));
        planObjetivo.setCarbohidratosObjetivo(new BigDecimal("20.00"));
        planObjetivo.setGrasasObjetivo(new BigDecimal("140.00"));

        plan.setObjetivo(planObjetivo);

        PlanRequest.PlanObjetivoRequest objetivoRequest = new PlanRequest.PlanObjetivoRequest();
        objetivoRequest.setCaloriasObjetivo(new BigDecimal("1800.00"));
        objetivoRequest.setProteinasObjetivo(new BigDecimal("135.00"));
        objetivoRequest.setCarbohidratosObjetivo(new BigDecimal("20.00"));
        objetivoRequest.setGrasasObjetivo(new BigDecimal("140.00"));

        planRequest = new PlanRequest();
        planRequest.setNombre("Plan Keto 30 días");
        planRequest.setDescripcion("Plan cetogénico para 30 días");
        planRequest.setDuracionDias(30);
        planRequest.setObjetivo(objetivoRequest);
        planRequest.setEtiquetaIds(Set.of(1L));

        comida = new Comida();
        comida.setId(1L);
        comida.setNombre("Huevos con aguacate");
        comida.setTipoComida(tipoComidaDesayuno);

        planDia = new PlanDia();
        planDia.setId(1L);
        planDia.setPlan(plan);
        planDia.setNumeroDia(1);
        planDia.setTipoComida(tipoComidaDesayuno);
        planDia.setComida(comida);
        planDia.setNotas("Primera comida del día");
    }

    @Test
    @DisplayName("US-11: Debe crear plan con objetivos exitosamente")
    void debeCrearPlanConExito() {
        // Given
        when(planRepository.existsByNombre(planRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(planRequest.getEtiquetaIds()))
            .thenReturn(List.of(etiqueta));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        // When
        PlanResponse response = planService.crearPlan(planRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Plan Keto 30 días");
        assertThat(response.getDuracionDias()).isEqualTo(30);
        assertThat(response.getActivo()).isTrue();
        assertThat(response.getObjetivo()).isNotNull();
        assertThat(response.getObjetivo().getCaloriasObjetivo())
            .isEqualByComparingTo(new BigDecimal("1800.00"));
        
        verify(planRepository).existsByNombre(planRequest.getNombre());
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("RN11: No debe crear plan con nombre duplicado")
    void noDebeCrearPlanConNombreDuplicado() {
        // Given
        when(planRepository.existsByNombre(planRequest.getNombre())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> planService.crearPlan(planRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ya existe un plan con el nombre");
        
        verify(planRepository).existsByNombre(planRequest.getNombre());
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    @DisplayName("RN12: No debe crear plan con etiquetas inexistentes")
    void noDebeCrearPlanConEtiquetasInexistentes() {
        // Given
        when(planRepository.existsByNombre(planRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(planRequest.getEtiquetaIds()))
            .thenReturn(List.of()); // No encuentra etiquetas

        // When & Then
        assertThatThrownBy(() -> planService.crearPlan(planRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Una o más etiquetas no fueron encontradas");
    }

    @Test
    @DisplayName("US-12: Debe actualizar plan exitosamente")
    void debeActualizarPlanConExito() {
        // Given
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.existsByNombreAndIdNot(planRequest.getNombre(), 1L))
            .thenReturn(false);
        when(etiquetaRepository.findAllById(planRequest.getEtiquetaIds()))
            .thenReturn(List.of(etiqueta));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        // When
        PlanResponse response = planService.actualizarPlan(1L, planRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo(planRequest.getNombre());
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("US-17: Debe obtener plan por ID")
    void debeObtenerPlanPorId() {
        // Given
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // When
        PlanResponse response = planService.obtenerPlanPorId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Plan Keto 30 días");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando plan no existe")
    void debeLanzarExcepcionCuandoPlanNoExiste() {
        // Given
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> planService.obtenerPlanPorId(999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Plan no encontrado");
    }

    @Test
    @DisplayName("US-13: Debe listar todos los planes (admin)")
    void debeListarTodosPlanesAdmin() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plan> page = new PageImpl<>(List.of(plan));
        when(planRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<PlanResponse> response = planService.listarPlanesAdmin(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getNombre()).isEqualTo("Plan Keto 30 días");
    }

    @Test
    @DisplayName("RN28: Debe listar solo planes activos")
    void debeListarSoloPlanesActivos() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plan> page = new PageImpl<>(List.of(plan));
        when(planRepository.findByActivoTrue(pageable)).thenReturn(page);

        // When
        Page<PlanResponse> response = planService.listarPlanesActivos(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getActivo()).isTrue();
        verify(planRepository).findByActivoTrue(pageable);
    }

    @Test
    @DisplayName("US-14, RN28: Debe eliminar plan (soft delete)")
    void debeEliminarPlanSoftDelete() {
        // Given
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.tieneUsuariosActivos(1L)).thenReturn(false);
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        // When
        planService.eliminarPlan(1L);

        // Then
        verify(planRepository).findById(1L);
        verify(planRepository).tieneUsuariosActivos(1L);
        verify(planRepository).save(argThat(p -> !p.getActivo()));
    }

    @Test
    @DisplayName("RN14: No debe eliminar plan con usuarios activos")
    void noDebeEliminarPlanConUsuariosActivos() {
        // Given
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.tieneUsuariosActivos(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> planService.eliminarPlan(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("tiene usuarios activos");
        
        verify(planRepository).findById(1L);
        verify(planRepository).tieneUsuariosActivos(1L);
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-12: Debe agregar día al plan")
    void debeAgregarDiaAlPlan() {
        // Given
        PlanDiaRequest diaRequest = new PlanDiaRequest();
        diaRequest.setNumeroDia(1);
        diaRequest.setTipoComidaId(1L);
        diaRequest.setComidaId(1L);
        diaRequest.setNotas("Primera comida del día");

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(tipoComidaRepository.findById(1L)).thenReturn(Optional.of(tipoComidaDesayuno));
        when(planDiaRepository.existsByPlanIdAndNumeroDiaAndTipoComidaId(1L, 1, 1L))
            .thenReturn(false);
        when(planDiaRepository.save(any(PlanDia.class))).thenReturn(planDia);

        // When
        PlanDiaResponse response = planService.agregarDiaAPlan(1L, diaRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNumeroDia()).isEqualTo(1);
        assertThat(response.getTipoComida()).isEqualTo("DESAYUNO");
        verify(planDiaRepository).save(any(PlanDia.class));
    }

    @Test
    @DisplayName("Debe validar que número de día no exceda duración")
    void debeValidarNumeroDiaNoExcedaDuracion() {
        // Given
        PlanDiaRequest diaRequest = new PlanDiaRequest();
        diaRequest.setNumeroDia(31); // Plan tiene 30 días
        diaRequest.setTipoComidaId(1L);
        diaRequest.setComidaId(1L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // When & Then
        assertThatThrownBy(() -> planService.agregarDiaAPlan(1L, diaRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no puede exceder la duración del plan");
    }

    @Test
    @DisplayName("No debe permitir duplicar día y tipo de comida")
    void noDebePermitirDuplicarDiaYTipo() {
        // Given
        PlanDiaRequest diaRequest = new PlanDiaRequest();
        diaRequest.setNumeroDia(1);
        diaRequest.setTipoComidaId(1L);
        diaRequest.setComidaId(1L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(tipoComidaRepository.findById(1L)).thenReturn(Optional.of(tipoComidaDesayuno));
        when(planDiaRepository.existsByPlanIdAndNumeroDiaAndTipoComidaId(1L, 1, 1L))
            .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> planService.agregarDiaAPlan(1L, diaRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Ya existe una comida");
    }

    @Test
    @DisplayName("US-17: Debe obtener días del plan ordenados")
    void debeObtenerDiasDePlanOrdenados() {
        // Given
        when(planRepository.existsById(1L)).thenReturn(true);
        when(planDiaRepository.findByPlanIdOrderByNumeroDiaAscTipoComidaAsc(1L))
            .thenReturn(List.of(planDia));

        // When
        List<PlanDiaResponse> response = planService.obtenerDiasDePlan(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getNumeroDia()).isEqualTo(1);
    }

    @Test
    @DisplayName("US-21: Debe obtener actividades de un día específico")
    void debeObtenerActividadesDeDia() {
        // Given
        when(planRepository.existsById(1L)).thenReturn(true);
        when(planDiaRepository.findByPlanIdAndNumeroDia(1L, 1))
            .thenReturn(List.of(planDia));

        // When
        List<PlanDiaResponse> response = planService.obtenerActividadesDia(1L, 1);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTipoComida()).isEqualTo("DESAYUNO");
    }

    @Test
    @DisplayName("Debe eliminar día del plan")
    void debeEliminarDiaDePlan() {
        // Given
        when(planDiaRepository.findById(1L)).thenReturn(Optional.of(planDia));

        // When
        planService.eliminarDiaDePlan(1L, 1L);

        // Then
        verify(planDiaRepository).findById(1L);
        verify(planDiaRepository).delete(planDia);
    }

    @Test
    @DisplayName("Debe buscar planes por nombre")
    void debeBuscarPlanesPorNombre() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plan> page = new PageImpl<>(List.of(plan));
        when(planRepository.findByNombreContainingIgnoreCaseAndActivoTrue("keto", pageable))
            .thenReturn(page);

        // When
        Page<PlanResponse> response = planService.buscarPorNombre("keto", pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        verify(planRepository).findByNombreContainingIgnoreCaseAndActivoTrue("keto", pageable);
    }

    // ============================================================================
    // RN16: Filtrado de Alérgenos (CRÍTICO - SEGURIDAD DE SALUD)
    // ============================================================================

    @Test
    @DisplayName("RN16 🚨: Usuario alérgico a nueces NO ve planes con nueces")
    void verCatalogo_RN16_FiltrarAlergenosNueces() {
        // Given
        Long perfilUsuarioId = 1L;
        
        // Crear etiqueta de alergia
        Etiqueta alergiaNueces = new Etiqueta();
        alergiaNueces.setId(10L);
        alergiaNueces.setNombre("Nueces");
        alergiaNueces.setTipoEtiqueta(Etiqueta.TipoEtiqueta.ALERGIA);
        
        // Crear perfil con alergia a nueces
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(perfilUsuarioId);
        perfil.setNombre("Usuario");
        
        // Crear UsuarioEtiquetasSalud para la relación
        UsuarioEtiquetasSalud usuarioEtiqueta = UsuarioEtiquetasSalud.builder()
                .perfilUsuario(perfil)
                .etiqueta(alergiaNueces)
                .build();
        perfil.setEtiquetasSalud(Set.of(usuarioEtiqueta));
        
        // Crear plan con nueces (debe ser filtrado)
        Plan planConNueces = Plan.builder()
                .id(1L)
                .nombre("Plan con Nueces")
                .activo(true)
                .etiquetas(Set.of(alergiaNueces))
                .build();
        
        // Crear plan sin nueces (debe aparecer)
        Plan planSinNueces = Plan.builder()
                .id(2L)
                .nombre("Plan sin Nueces")
                .activo(true)
                .etiquetas(new HashSet<>())
                .build();
        
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plan> page = new PageImpl<>(List.of(planConNueces, planSinNueces));
        
        when(perfilUsuarioRepository.findById(perfilUsuarioId)).thenReturn(Optional.of(perfil));
        when(planRepository.findByActivoTrue(pageable)).thenReturn(page);

        // When
        Page<PlanResponse> response = planService.verCatalogo(perfilUsuarioId, false, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getNombre()).isEqualTo("Plan sin Nueces");
        assertThat(response.getContent()).noneMatch(p -> p.getNombre().equals("Plan con Nueces"));
        
        verify(perfilUsuarioRepository).findById(perfilUsuarioId);
        verify(planRepository).findByActivoTrue(pageable);
    }

    @Test
    @DisplayName("RN16 🚨: Usuario sin alergias ve todos los planes")
    void verCatalogo_RN16_SinAlergias() {
        // Given
        Long perfilUsuarioId = 1L;
        
        // Crear perfil sin alergias
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(perfilUsuarioId);
        perfil.setNombre("Usuario");
        perfil.setEtiquetasSalud(new HashSet<>());
        
        Plan plan1 = Plan.builder().id(1L).nombre("Plan 1").activo(true).build();
        Plan plan2 = Plan.builder().id(2L).nombre("Plan 2").activo(true).build();
        
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plan> page = new PageImpl<>(List.of(plan1, plan2));
        
        when(perfilUsuarioRepository.findById(perfilUsuarioId)).thenReturn(Optional.of(perfil));
        when(planRepository.findByActivoTrue(pageable)).thenReturn(page);

        // When
        Page<PlanResponse> response = planService.verCatalogo(perfilUsuarioId, false, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent()).extracting("nombre")
                .containsExactlyInAnyOrder("Plan 1", "Plan 2");
    }

    @Test
    @DisplayName("RN16 🚨: Detalle de plan con alérgenos lanza excepción")
    void verDetallePlan_RN16_ConAlergenos() {
        // Given
        Long planId = 1L;
        Long perfilUsuarioId = 1L;
        
        Etiqueta alergiaGluten = new Etiqueta();
        alergiaGluten.setId(11L);
        alergiaGluten.setNombre("Gluten");
        alergiaGluten.setTipoEtiqueta(Etiqueta.TipoEtiqueta.ALERGIA);
        
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(perfilUsuarioId);
        
        // Crear UsuarioEtiquetasSalud para la relación
        UsuarioEtiquetasSalud usuarioEtiqueta = UsuarioEtiquetasSalud.builder()
                .perfilUsuario(perfil)
                .etiqueta(alergiaGluten)
                .build();
        perfil.setEtiquetasSalud(Set.of(usuarioEtiqueta));
        
        Plan planConGluten = Plan.builder()
                .id(planId)
                .nombre("Plan con Gluten")
                .activo(true)
                .etiquetas(Set.of(alergiaGluten))
                .build();
        
        when(planRepository.findById(planId)).thenReturn(Optional.of(planConGluten));
        when(perfilUsuarioRepository.findById(perfilUsuarioId)).thenReturn(Optional.of(perfil));

        // When & Then
        assertThatThrownBy(() -> planService.verDetallePlan(planId, perfilUsuarioId))
                .isInstanceOf(com.example.nutritrackapi.exception.BusinessException.class)
                .hasMessageContaining("ADVERTENCIA")
                .hasMessageContaining("alergias");
        
        verify(planRepository).findById(planId);
        verify(perfilUsuarioRepository).findById(perfilUsuarioId);
    }

    @Test
    @DisplayName("RN16: Detalle de plan sin alérgenos permite visualización")
    void verDetallePlan_RN16_SinAlergenos() {
        // Given
        Long planId = 1L;
        Long perfilUsuarioId = 1L;
        
        Etiqueta alergiaLactosa = new Etiqueta();
        alergiaLactosa.setId(12L);
        alergiaLactosa.setNombre("Lactosa");
        alergiaLactosa.setTipoEtiqueta(Etiqueta.TipoEtiqueta.ALERGIA);
        
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(perfilUsuarioId);
        
        // Crear UsuarioEtiquetasSalud para la relación
        UsuarioEtiquetasSalud usuarioEtiqueta = UsuarioEtiquetasSalud.builder()
                .perfilUsuario(perfil)
                .etiqueta(alergiaLactosa)
                .build();
        perfil.setEtiquetasSalud(Set.of(usuarioEtiqueta));
        
        // Plan sin lactosa
        Plan planSeguro = Plan.builder()
                .id(planId)
                .nombre("Plan Sin Lactosa")
                .activo(true)
                .etiquetas(new HashSet<>())
                .build();
        
        when(planRepository.findById(planId)).thenReturn(Optional.of(planSeguro));
        when(perfilUsuarioRepository.findById(perfilUsuarioId)).thenReturn(Optional.of(perfil));

        // When
        PlanResponse response = planService.verDetallePlan(planId, perfilUsuarioId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Plan Sin Lactosa");
    }

    @Test
    @DisplayName("RN15: Catálogo con filtro de sugeridos según objetivo")
    void verCatalogo_RN15_Sugeridos() {
        // Given
        Long perfilUsuarioId = 1L;
        
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setId(perfilUsuarioId);
        
        UsuarioPerfilSalud perfilSalud = new UsuarioPerfilSalud();
        perfilSalud.setObjetivoActual(UsuarioPerfilSalud.ObjetivoSalud.PERDER_PESO);
        perfil.setPerfilSalud(perfilSalud);
        
        Plan planPerderPeso = Plan.builder()
                .id(1L)
                .nombre("Plan Pérdida de Peso")
                .activo(true)
                .build();
        
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plan> page = new PageImpl<>(List.of(planPerderPeso));
        
        when(perfilUsuarioRepository.findById(perfilUsuarioId)).thenReturn(Optional.of(perfil));
        when(planRepository.findByActivoTrueAndEtiquetasNombre("PERDER_PESO", pageable))
                .thenReturn(page);

        // When
        Page<PlanResponse> response = planService.verCatalogo(perfilUsuarioId, true, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        verify(planRepository).findByActivoTrueAndEtiquetasNombre("PERDER_PESO", pageable);
    }
}
