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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio UsuarioPlanService")
class UsuarioPlanServiceTest {

    @Mock
    private UsuarioPlanRepository usuarioPlanRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private UsuarioPlanService usuarioPlanService;

    private PerfilUsuario perfilUsuarioMock;
    private Plan planMock;
    private UsuarioPlan usuarioPlanMock;
    private AsignarPlanRequest asignarPlanRequest;

    @BeforeEach
    void setUp() {
        perfilUsuarioMock = PerfilUsuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .build();

        planMock = Plan.builder()
                .id(1L)
                .nombre("Plan de Pérdida de Peso")
                .duracionDias(30)
                .activo(true)
                .build();

        usuarioPlanMock = UsuarioPlan.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .plan(planMock)
                .fechaInicio(LocalDate.of(2025, 11, 1))
                .fechaFin(LocalDate.of(2025, 11, 30))
                .estado(EstadoAsignacion.ACTIVO)
                .diaActual(1)
                .notas("Plan inicial")
                .build();

        asignarPlanRequest = AsignarPlanRequest.builder()
                .planId(1L)
                .fechaInicio(LocalDate.of(2025, 11, 1))
                .notas("Plan inicial")
                .build();
    }

    @Test
    @DisplayName("Debe asignar un plan correctamente")
    void debeAsignarPlanCorrectamente() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(planRepository.findById(1L)).thenReturn(Optional.of(planMock));
        when(usuarioPlanRepository.existsOverlappingActivePlan(any(), any(), any())).thenReturn(false);
        when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlanMock);

        UsuarioPlanResponse response = usuarioPlanService.asignarPlan(1L, asignarPlanRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Plan de Pérdida de Peso", response.getNombrePlan());
        assertEquals(EstadoAsignacion.ACTIVO, response.getEstado());
        verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> usuarioPlanService.asignarPlan(1L, asignarPlanRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el plan no existe")
    void debeLanzarExcepcionCuandoPlanNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> usuarioPlanService.asignarPlan(1L, asignarPlanRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el plan está inactivo")
    void debeLanzarExcepcionCuandoPlanEstaInactivo() {
        planMock.setActivo(false);
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(planRepository.findById(1L)).thenReturn(Optional.of(planMock));

        assertThrows(BusinessRuleException.class, 
            () -> usuarioPlanService.asignarPlan(1L, asignarPlanRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando hay solapamiento de fechas")
    void debeLanzarExcepcionCuandoHaySolapamiento() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(planRepository.findById(1L)).thenReturn(Optional.of(planMock));
        when(usuarioPlanRepository.existsOverlappingActivePlan(any(), any(), any())).thenReturn(true);

        assertThrows(BusinessRuleException.class, 
            () -> usuarioPlanService.asignarPlan(1L, asignarPlanRequest));
    }

    @Test
    @DisplayName("Debe listar planes activos correctamente")
    void debeListarPlanesActivos() {
        when(usuarioPlanRepository.findByPerfilUsuarioIdAndEstadoWithPlan(1L, EstadoAsignacion.ACTIVO))
                .thenReturn(Arrays.asList(usuarioPlanMock));

        List<UsuarioPlanResponse> planes = usuarioPlanService.listarPlanesActivos(1L);

        assertNotNull(planes);
        assertEquals(1, planes.size());
        assertEquals("Plan de Pérdida de Peso", planes.get(0).getNombrePlan());
    }

    @Test
    @DisplayName("Debe buscar plan por ID correctamente")
    void debeBuscarPlanPorId() {
        when(usuarioPlanRepository.findByIdWithPlan(1L)).thenReturn(Optional.of(usuarioPlanMock));

        UsuarioPlanResponse response = usuarioPlanService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Plan de Pérdida de Peso", response.getNombrePlan());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el plan no pertenece al usuario")
    void debeLanzarExcepcionCuandoPlanNoPertenece() {
        when(usuarioPlanRepository.findByIdWithPlan(1L)).thenReturn(Optional.of(usuarioPlanMock));

        assertThrows(BusinessRuleException.class, 
            () -> usuarioPlanService.buscarPorId(2L, 1L));
    }

    @Test
    @DisplayName("Debe completar plan correctamente")
    void debeCompletarPlan() {
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlanMock);

        UsuarioPlanResponse response = usuarioPlanService.completarPlan(1L, 1L);

        assertNotNull(response);
        verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al completar plan que no está activo")
    void debeLanzarExcepcionAlCompletarPlanNoActivo() {
        usuarioPlanMock.setEstado(EstadoAsignacion.COMPLETADO);
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));

        assertThrows(BusinessRuleException.class, 
            () -> usuarioPlanService.completarPlan(1L, 1L));
    }

    @Test
    @DisplayName("Debe cancelar plan correctamente")
    void debeCancelarPlan() {
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlanMock);

        UsuarioPlanResponse response = usuarioPlanService.cancelarPlan(1L, 1L);

        assertNotNull(response);
        verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
    }

    @Test
    @DisplayName("Debe avanzar día correctamente")
    void debeAvanzarDia() {
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlanMock);

        UsuarioPlanResponse response = usuarioPlanService.avanzarDia(1L, 1L);

        assertNotNull(response);
        verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
    }

    @Test
    @DisplayName("Debe completar automáticamente al alcanzar el último día")
    void debeCompletarAutomaticamenteAlAlcanzarUltimoDia() {
        usuarioPlanMock.setDiaActual(30);
        when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlanMock);

        UsuarioPlanResponse response = usuarioPlanService.avanzarDia(1L, 1L);

        assertNotNull(response);
        verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
    }

    @Test
    @DisplayName("Debe calcular progreso correctamente")
    void debeCalcularProgresoCorrectamente() {
        usuarioPlanMock.setDiaActual(15);
        when(usuarioPlanRepository.findByIdWithPlan(1L)).thenReturn(Optional.of(usuarioPlanMock));

        UsuarioPlanResponse response = usuarioPlanService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("50.00"), response.getProgreso());
        assertEquals(14, response.getDiasCompletados());
        assertEquals(16, response.getDiasRestantes());
    }

    @Test
    @DisplayName("Debe mostrar progreso 100% cuando el plan está completado")
    void debeMostrarProgreso100CuandoCompletado() {
        usuarioPlanMock.setEstado(EstadoAsignacion.COMPLETADO);
        when(usuarioPlanRepository.findByIdWithPlan(1L)).thenReturn(Optional.of(usuarioPlanMock));

        UsuarioPlanResponse response = usuarioPlanService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getProgreso());
    }

    @Test
    @DisplayName("Debe listar todos los planes del usuario")
    void debeListarTodosLosPlanes() {
        UsuarioPlan planCompletado = UsuarioPlan.builder()
                .id(2L)
                .perfilUsuario(perfilUsuarioMock)
                .plan(planMock)
                .estado(EstadoAsignacion.COMPLETADO)
                .diaActual(30)
                .build();

        when(usuarioPlanRepository.findByPerfilUsuarioId(1L))
                .thenReturn(Arrays.asList(usuarioPlanMock, planCompletado));
        when(usuarioPlanRepository.findByIdWithPlan(1L)).thenReturn(Optional.of(usuarioPlanMock));
        when(usuarioPlanRepository.findByIdWithPlan(2L)).thenReturn(Optional.of(planCompletado));

        List<UsuarioPlanResponse> planes = usuarioPlanService.listarTodosLosPlanes(1L);

        assertNotNull(planes);
        assertEquals(2, planes.size());
    }
}
