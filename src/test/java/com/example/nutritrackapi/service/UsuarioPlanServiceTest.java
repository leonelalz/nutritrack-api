package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.ActivarPlanRequest;
import com.example.nutritrackapi.dto.UsuarioPlanResponse;
import com.example.nutritrackapi.exception.BusinessException;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.PerfilUsuarioRepository;
import com.example.nutritrackapi.repository.PlanRepository;
import com.example.nutritrackapi.repository.UsuarioPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios completos para UsuarioPlanService
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * Cubre:
 * - US-18: Activar una Meta
 * - US-19: Pausar/Reanudar Meta
 * - US-20: Completar/Cancelar Meta
 * - RN17: No duplicar el mismo plan/rutina activo
 * - RN18: Proponer reemplazo al activar nuevo plan
 * - RN19: No pausar/reanudar meta completada/cancelada
 * - RN26: Estado de Asignaciones (Transiciones válidas)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioPlanService - Tests unitarios Módulo 4")
class UsuarioPlanServiceTest {

    @Mock
    private UsuarioPlanRepository usuarioPlanRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private com.example.nutritrackapi.repository.UsuarioEtiquetasSaludRepository etiquetasSaludRepository;

    @Mock
    private com.example.nutritrackapi.repository.EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private UsuarioPlanService usuarioPlanService;

    private PerfilUsuario perfilUsuario;
    private Plan plan;
    private Plan planDiferente;
    private UsuarioPlan usuarioPlan;

    @BeforeEach
    void setUp() {
        // Setup perfil usuario
        perfilUsuario = PerfilUsuario.builder()
                .id(1L)
                .nombre("Test")
                .apellido("Usuario")
                .build();

        // Setup plan principal
        plan = Plan.builder()
                .id(1L)
                .nombre("Plan de Pérdida de Peso")
                .descripcion("Plan para perder peso")
                .duracionDias(30)
                .activo(true)
                .build();

        // Setup plan diferente para probar múltiples planes simultáneos
        planDiferente = Plan.builder()
                .id(2L)
                .nombre("Plan de Ganancia Muscular")
                .descripcion("Plan para ganar músculo")
                .duracionDias(45)
                .activo(true)
                .build();

        // Setup usuario plan
        usuarioPlan = UsuarioPlan.builder()
                .id(1L)
                .perfilUsuario(perfilUsuario)
                .plan(plan)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(29))
                .diaActual(1)
                .estado(UsuarioPlan.EstadoAsignacion.ACTIVO)
                .build();

        // Mock RN32: Por defecto, usuario sin alergias (lenient para tests que no activan planes)
        lenient().when(etiquetasSaludRepository.findEtiquetasAlergenosByPerfilUsuarioId(anyLong()))
                .thenReturn(new ArrayList<>());
    }

    // ============================================================
    // US-18: Activar una Meta
    // ============================================================

    @Nested
    @DisplayName("US-18: Activar Plan")
    class ActivarPlanTests {

        @Test
        @DisplayName("Debe activar plan exitosamente cuando no hay duplicados")
        void activarPlan_Success() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);
            request.setFechaInicio(LocalDate.now());
            request.setNotas("Plan de prueba");

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(false);
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlan);

            // When
            UsuarioPlanResponse response = usuarioPlanService.activarPlan(1L, request);

            // Then
            assertNotNull(response, "La respuesta no debe ser nula");
            assertEquals("Plan de Pérdida de Peso", response.getPlanNombre());
            verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
            verify(usuarioPlanRepository).existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO);
        }

        @Test
        @DisplayName("Debe calcular fecha fin correctamente según duración del plan")
        void activarPlan_CalculaFechaFin() {
            // Given
            LocalDate fechaInicio = LocalDate.of(2025, 11, 1);
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);
            request.setFechaInicio(fechaInicio);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(false);
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.activarPlan(1L, request);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getFechaInicio().equals(fechaInicio) &&
                up.getFechaFin().equals(fechaInicio.plusDays(29)) && // 30 días = inicio + 29
                up.getDiaActual() == 1 &&
                up.getEstado() == UsuarioPlan.EstadoAsignacion.ACTIVO
            ));
        }

        @Test
        @DisplayName("Debe usar fecha actual si no se proporciona fecha inicio")
        void activarPlan_UsaFechaActualPorDefecto() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);
            // No se establece fechaInicio

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    anyLong(), anyLong(), any())).thenReturn(false);
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.activarPlan(1L, request);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getFechaInicio().equals(LocalDate.now())
            ));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el perfil no existe")
        void activarPlan_PerfilNoEncontrado() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);

            when(perfilUsuarioRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                usuarioPlanService.activarPlan(999L, request);
            });
            
            assertTrue(exception.getMessage().contains("Perfil de usuario no encontrado"));
            verify(usuarioPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el plan no existe")
        void activarPlan_PlanNoEncontrado() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(999L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                usuarioPlanService.activarPlan(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("Plan no encontrado"));
            verify(usuarioPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el plan está inactivo")
        void activarPlan_PlanInactivo() {
            // Given
            plan.setActivo(false);
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.activarPlan(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("no está disponible"));
            verify(usuarioPlanRepository, never()).save(any());
        }
    }

    // ============================================================
    // RN17: No duplicar el mismo plan/rutina activo
    // ============================================================

    @Nested
    @DisplayName("RN17: No duplicar mismo plan activo")
    class RN17_NoDuplicarPlanActivoTests {

        @Test
        @DisplayName("Debe lanzar excepción si ya existe el MISMO plan ACTIVO")
        void activarPlan_RN17_MismoPlanActivoLanzaExcepcion() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.activarPlan(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("Ya tienes este plan activo"));
            verify(usuarioPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe permitir activar DIFERENTE plan aunque ya tenga otro activo")
        void activarPlan_RN17_PermiteDiferentePlanActivo() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(2L); // Plan DIFERENTE

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(2L)).thenReturn(Optional.of(planDiferente));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 2L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(false);
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlan);

            // When
            UsuarioPlanResponse response = usuarioPlanService.activarPlan(1L, request);

            // Then
            assertNotNull(response, "Debe permitir activar plan diferente");
            verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
        }

        @Test
        @DisplayName("Debe permitir activar MISMO plan si está PAUSADO (no activo)")
        void activarPlan_RN17_PermiteMismoPlanSiEstaPausado() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            // El plan existe pero está PAUSADO (no ACTIVO)
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(false);
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlan);

            // When
            UsuarioPlanResponse response = usuarioPlanService.activarPlan(1L, request);

            // Then
            assertNotNull(response, "Debe permitir activar mismo plan si está pausado");
            verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
        }

        @Test
        @DisplayName("Debe permitir activar MISMO plan si está COMPLETADO")
        void activarPlan_RN17_PermiteMismoPlanSiEstaCompletado() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(false);
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenReturn(usuarioPlan);

            // When
            UsuarioPlanResponse response = usuarioPlanService.activarPlan(1L, request);

            // Then
            assertNotNull(response, "Debe permitir reactivar plan completado");
            verify(usuarioPlanRepository).save(any(UsuarioPlan.class));
        }
    }

    // ============================================================
    // RN18: Proponer reemplazo al activar nueva meta
    // ============================================================

    @Nested
    @DisplayName("RN18: Proponer reemplazo")
    class RN18_ProponerReemplazoTests {

        @Test
        @DisplayName("Mensaje de error debe indicar necesidad de pausar/cancelar plan actual")
        void activarPlan_RN18_MensajeProponePausarOCancelar() {
            // Given
            ActivarPlanRequest request = new ActivarPlanRequest();
            request.setPlanId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
            when(usuarioPlanRepository.existsByPerfilUsuarioIdAndPlanIdAndEstado(
                    1L, 1L, UsuarioPlan.EstadoAsignacion.ACTIVO)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.activarPlan(1L, request);
            });
            
            // RN18: Verificar que el mensaje propone pausar o cancelar
            String mensaje = exception.getMessage().toLowerCase();
            assertTrue(mensaje.contains("pausarlo") || mensaje.contains("cancelarlo"),
                    "El mensaje debe proponer pausar o cancelar el plan actual");
        }
    }

    // ============================================================
    // US-19: Pausar/Reanudar Meta
    // ============================================================

    @Nested
    @DisplayName("US-19: Pausar Plan")
    class PausarPlanTests {

        @Test
        @DisplayName("Debe pausar plan ACTIVO exitosamente")
        void pausarPlan_Success() {
            // Given
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioPlanResponse response = usuarioPlanService.pausarPlan(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.PAUSADO
            ));
        }

        @Test
        @DisplayName("Debe validar que el plan pertenece al usuario")
        void pausarPlan_ValidaPropietario() {
            // Given
            usuarioPlan.getPerfilUsuario().setId(99L); // Diferente usuario
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.pausarPlan(1L, 1L);
            });
            
            assertTrue(exception.getMessage().contains("no pertenece al usuario"));
        }
    }

    @Nested
    @DisplayName("US-19: Reanudar Plan")
    class ReanudarPlanTests {

        @Test
        @DisplayName("Debe reanudar plan PAUSADO exitosamente")
        void reanudarPlan_Success() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            usuarioPlan.setFechaInicio(LocalDate.now().minusDays(5));
            usuarioPlan.setDiaActual(3);
            
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioPlanResponse response = usuarioPlanService.reanudarPlan(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.ACTIVO
            ));
        }

        @Test
        @DisplayName("Debe ajustar fecha fin al reanudar plan pausado")
        void reanudarPlan_AjustaFechaFin() {
            // Given
            LocalDate fechaInicio = LocalDate.now().minusDays(10);
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            usuarioPlan.setFechaInicio(fechaInicio);
            usuarioPlan.setFechaFin(fechaInicio.plusDays(29));
            usuarioPlan.setDiaActual(5); // Se pausó en el día 5
            
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.reanudarPlan(1L, 1L);

            // Then - Debe extender la fecha fin por los días pausados
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getFechaFin().isAfter(fechaInicio.plusDays(29))
            ));
        }
    }

    // ============================================================
    // RN19: No pausar/reanudar meta completada/cancelada
    // ============================================================

    @Nested
    @DisplayName("RN19: No pausar/reanudar en estado final")
    class RN19_NoOperarEstadoFinalTests {

        @Test
        @DisplayName("No debe permitir pausar plan COMPLETADO")
        void pausarPlan_RN19_CompletadoLanzaExcepcion() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.pausarPlan(1L, 1L);
            });
            
            assertTrue(exception.getMessage().contains("completado") || 
                      exception.getMessage().contains("cancelado"));
            verify(usuarioPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("No debe permitir pausar plan CANCELADO")
        void pausarPlan_RN19_CanceladoLanzaExcepcion() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.pausarPlan(1L, 1L);
            });
            
            assertTrue(exception.getMessage().toLowerCase().contains("completado") || 
                      exception.getMessage().toLowerCase().contains("cancelado"));
        }

        @Test
        @DisplayName("No debe permitir reanudar plan COMPLETADO")
        void reanudarPlan_RN19_CompletadoLanzaExcepcion() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.reanudarPlan(1L, 1L);
            });
            
            assertTrue(exception.getMessage().toLowerCase().contains("completado") || 
                      exception.getMessage().toLowerCase().contains("cancelado"));
        }

        @Test
        @DisplayName("No debe permitir reanudar plan CANCELADO")
        void reanudarPlan_RN19_CanceladoLanzaExcepcion() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioPlanService.reanudarPlan(1L, 1L);
            });
        }

        @Test
        @DisplayName("No debe permitir reanudar plan que ya está ACTIVO")
        void reanudarPlan_RN19_ActivoLanzaExcepcion() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioPlanService.reanudarPlan(1L, 1L);
            });
            
            assertTrue(exception.getMessage().toLowerCase().contains("pausado"));
        }
    }

    // ============================================================
    // US-20: Completar/Cancelar Meta
    // ============================================================

    @Nested
    @DisplayName("US-20: Completar Plan")
    class CompletarPlanTests {

        @Test
        @DisplayName("Debe completar plan ACTIVO exitosamente")
        void completarPlan_Success() {
            // Given
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioPlanResponse response = usuarioPlanService.completarPlan(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO &&
                up.getFechaFin().equals(LocalDate.now())
            ));
        }

        @Test
        @DisplayName("Debe completar plan PAUSADO exitosamente")
        void completarPlan_DesdePausado() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioPlanResponse response = usuarioPlanService.completarPlan(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO
            ));
        }

        @Test
        @DisplayName("Debe actualizar fecha fin al día actual al completar")
        void completarPlan_ActualizaFechaFin() {
            // Given
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.completarPlan(1L, 1L);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getFechaFin().equals(LocalDate.now())
            ));
        }
    }

    @Nested
    @DisplayName("US-20: Cancelar Plan")
    class CancelarPlanTests {

        @Test
        @DisplayName("Debe cancelar plan ACTIVO exitosamente")
        void cancelarPlan_Success() {
            // Given
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioPlanResponse response = usuarioPlanService.cancelarPlan(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.CANCELADO &&
                up.getFechaFin().equals(LocalDate.now())
            ));
        }

        @Test
        @DisplayName("Debe cancelar plan PAUSADO exitosamente")
        void cancelarPlan_DesdePausado() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioPlanResponse response = usuarioPlanService.cancelarPlan(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.CANCELADO
            ));
        }
    }

    // ============================================================
    // RN26: Estado de Asignaciones (Transiciones válidas)
    // ============================================================

    @Nested
    @DisplayName("RN26: Transiciones de estado válidas")
    class RN26_TransicionesEstadoTests {

        @Test
        @DisplayName("ACTIVO → COMPLETADO es transición válida")
        void completarPlan_RN26_ActivoACompletado() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.completarPlan(1L, 1L);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO
            ));
        }

        @Test
        @DisplayName("ACTIVO → CANCELADO es transición válida")
        void cancelarPlan_RN26_ActivoACancelado() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.cancelarPlan(1L, 1L);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.CANCELADO
            ));
        }

        @Test
        @DisplayName("ACTIVO → PAUSADO es transición válida")
        void pausarPlan_RN26_ActivoAPausado() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.pausarPlan(1L, 1L);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.PAUSADO
            ));
        }

        @Test
        @DisplayName("PAUSADO → ACTIVO es transición válida")
        void reanudarPlan_RN26_PausadoAActivo() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));
            when(usuarioPlanRepository.save(any(UsuarioPlan.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.reanudarPlan(1L, 1L);

            // Then
            verify(usuarioPlanRepository).save(argThat(up -> 
                up.getEstado() == UsuarioPlan.EstadoAsignacion.ACTIVO
            ));
        }

        @Test
        @DisplayName("COMPLETADO → ACTIVO NO es transición válida")
        void completarPlan_RN26_CompletadoAActivoNoValido() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioPlanService.completarPlan(1L, 1L);
            });
        }

        @Test
        @DisplayName("CANCELADO → ACTIVO NO es transición válida")
        void reanudarPlan_RN26_CanceladoAActivoNoValido() {
            // Given
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
            when(usuarioPlanRepository.findById(1L)).thenReturn(Optional.of(usuarioPlan));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioPlanService.reanudarPlan(1L, 1L);
            });
        }
    }

    // ============================================================
    // Tests de Consulta
    // ============================================================

    @Nested
    @DisplayName("Consultas de planes")
    class ConsultasTests {

        @Test
        @DisplayName("Debe obtener plan activo actual del usuario")
        void obtenerPlanActivo_Success() {
            // Given
            when(usuarioPlanRepository.findPlanActivoActual(1L)).thenReturn(Optional.of(usuarioPlan));

            // When
            UsuarioPlanResponse response = usuarioPlanService.obtenerPlanActivo(1L);

            // Then
            assertNotNull(response);
            assertEquals("Plan de Pérdida de Peso", response.getPlanNombre());
            assertEquals("ACTIVO", response.getEstado());
        }

        @Test
        @DisplayName("Debe retornar null si no hay plan activo")
        void obtenerPlanActivo_NoExiste() {
            // Given
            when(usuarioPlanRepository.findPlanActivoActual(1L)).thenReturn(Optional.empty());

            // When
            UsuarioPlanResponse response = usuarioPlanService.obtenerPlanActivo(1L);

            // Then
            assertNull(response, "Debe retornar null si no hay plan activo");
        }

        @Test
        @DisplayName("Debe obtener todos los planes del usuario")
        void obtenerPlanes_Success() {
            // Given
            UsuarioPlan planPausado = UsuarioPlan.builder()
                    .id(2L)
                    .perfilUsuario(perfilUsuario)
                    .plan(planDiferente)
                    .estado(UsuarioPlan.EstadoAsignacion.PAUSADO)
                    .build();
            
            when(usuarioPlanRepository.findByPerfilUsuarioId(1L))
                    .thenReturn(List.of(usuarioPlan, planPausado));

            // When
            List<UsuarioPlanResponse> response = usuarioPlanService.obtenerPlanes(1L);

            // Then
            assertNotNull(response);
            assertEquals(2, response.size());
        }

        @Test
        @DisplayName("Debe obtener solo planes activos del usuario")
        void obtenerPlanesActivos_Success() {
            // Given
            when(usuarioPlanRepository.findAllPlanesActivos(1L))
                    .thenReturn(List.of(usuarioPlan));

            // When
            List<UsuarioPlanResponse> response = usuarioPlanService.obtenerPlanesActivos(1L);

            // Then
            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals("ACTIVO", response.get(0).getEstado());
        }

        @Test
        @DisplayName("Debe retornar lista vacía si usuario no tiene planes")
        void obtenerPlanes_ListaVacia() {
            // Given
            when(usuarioPlanRepository.findByPerfilUsuarioId(1L))
                    .thenReturn(new ArrayList<>());

            // When
            List<UsuarioPlanResponse> response = usuarioPlanService.obtenerPlanes(1L);

            // Then
            assertNotNull(response);
            assertTrue(response.isEmpty());
        }
    }

    // ============================================================
    // Tests de Actualización Automática
    // ============================================================

    @Nested
    @DisplayName("Actualización automática de días")
    class ActualizacionAutomaticaTests {

        @Test
        @DisplayName("Debe auto-completar plan al llegar al último día")
        void actualizarDiasActuales_AutoCompletarPlan() {
            // Given
            usuarioPlan.setFechaInicio(LocalDate.now().minusDays(30));
            usuarioPlan.setDiaActual(30);
            usuarioPlan.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            
            when(usuarioPlanRepository.findByPerfilUsuarioIdAndEstado(
                    null, UsuarioPlan.EstadoAsignacion.ACTIVO))
                    .thenReturn(List.of(usuarioPlan));
            when(usuarioPlanRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioPlanService.actualizarDiasActuales();

            // Then
            verify(usuarioPlanRepository).saveAll(argThat(list -> {
                List<UsuarioPlan> planes = (List<UsuarioPlan>) list;
                return planes.get(0).getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO;
            }));
        }
    }
}
