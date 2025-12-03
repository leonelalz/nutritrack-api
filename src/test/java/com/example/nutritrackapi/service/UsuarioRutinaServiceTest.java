package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.ActivarRutinaRequest;
import com.example.nutritrackapi.dto.UsuarioRutinaResponse;
import com.example.nutritrackapi.exception.BusinessException;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.PerfilUsuarioRepository;
import com.example.nutritrackapi.repository.RutinaRepository;
import com.example.nutritrackapi.repository.UsuarioRutinaRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios completos para UsuarioRutinaService
 * Módulo 4: Exploración y Activación (Cliente)
 * 
 * Cubre:
 * - US-18: Activar una Meta (Rutina)
 * - US-19: Pausar/Reanudar Meta (Rutina)
 * - US-20: Completar/Cancelar Meta (Rutina)
 * - RN17: No duplicar el mismo plan/rutina activo
 * - RN18: Proponer reemplazo al activar nueva rutina
 * - RN19: No pausar/reanudar meta completada/cancelada
 * - RN26: Estado de Asignaciones (Transiciones válidas)
 * - RN33: Validación de contraindicaciones médicas en rutinas
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioRutinaService - Tests unitarios Módulo 4")
class UsuarioRutinaServiceTest {

    @Mock
    private UsuarioRutinaRepository usuarioRutinaRepository;

    @Mock
    private RutinaRepository rutinaRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @InjectMocks
    private UsuarioRutinaService usuarioRutinaService;

    private PerfilUsuario perfilUsuario;
    private Rutina rutina;
    private Rutina rutinaDiferente;
    private UsuarioRutina usuarioRutina;

    @BeforeEach
    void setUp() {
        perfilUsuario = PerfilUsuario.builder()
                .id(1L)
                .nombre("Test")
                .apellido("Usuario")
                .build();

        rutina = Rutina.builder()
                .id(1L)
                .nombre("Rutina de Fuerza")
                .descripcion("Rutina para ganar fuerza")
                .duracionSemanas(8)
                .activo(true)
                .build();

        rutinaDiferente = Rutina.builder()
                .id(2L)
                .nombre("Rutina de Cardio")
                .descripcion("Rutina cardiovascular")
                .duracionSemanas(12)
                .activo(true)
                .build();

        usuarioRutina = UsuarioRutina.builder()
                .id(1L)
                .perfilUsuario(perfilUsuario)
                .rutina(rutina)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusWeeks(8).minusDays(1))
                .semanaActual(1)
                .estado(UsuarioPlan.EstadoAsignacion.ACTIVO)
                .build();
    }

    // ============================================================
    // US-18: Activar una Meta (Rutina)
    // ============================================================

    @Nested
    @DisplayName("US-18: Activar Rutina")
    class ActivarRutinaTests {

        @Test
        @DisplayName("Debe activar rutina exitosamente cuando no hay duplicados")
        void activarRutina_Success() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);
            request.setFechaInicio(LocalDate.now());
            request.setNotas("Rutina de prueba");

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    1L, 1L)).thenReturn(Optional.empty());
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutina);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.activarRutina(1L, request);

            // Then
            assertNotNull(response, "La respuesta no debe ser nula");
            assertEquals("Rutina de Fuerza", response.getRutinaNombre());
            verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
        }

        @Test
        @DisplayName("Debe calcular fecha fin correctamente según duración en semanas")
        void activarRutina_CalculaFechaFin() {
            // Given
            LocalDate fechaInicio = LocalDate.of(2025, 11, 1);
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);
            request.setFechaInicio(fechaInicio);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    anyLong(), anyLong())).thenReturn(Optional.empty());
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.activarRutina(1L, request);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getFechaInicio().equals(fechaInicio) &&
                ur.getFechaFin().equals(fechaInicio.plusWeeks(8).minusDays(1)) &&
                ur.getSemanaActual() == 1 &&
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.ACTIVO
            ));
        }

        @Test
        @DisplayName("Debe usar fecha actual si no se proporciona fecha inicio")
        void activarRutina_UsaFechaActualPorDefecto() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    anyLong(), anyLong())).thenReturn(Optional.empty());
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.activarRutina(1L, request);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getFechaInicio().equals(LocalDate.now())
            ));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el perfil no existe")
        void activarRutina_PerfilNoEncontrado() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            when(perfilUsuarioRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                usuarioRutinaService.activarRutina(999L, request);
            });
            
            assertTrue(exception.getMessage().contains("Perfil de usuario no encontrado"));
            verify(usuarioRutinaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si la rutina no existe")
        void activarRutina_RutinaNoEncontrada() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(999L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("Rutina no encontrada"));
            verify(usuarioRutinaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si la rutina está inactiva")
        void activarRutina_RutinaInactiva() {
            // Given
            rutina.setActivo(false);
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("no está disponible"));
            verify(usuarioRutinaRepository, never()).save(any());
        }
    }

    // ============================================================
    // RN17: No duplicar la misma rutina activa
    // ============================================================

    @Nested
    @DisplayName("RN17: No duplicar misma rutina activa")
    class RN17_NoDuplicarRutinaActivaTests {

        @Test
        @DisplayName("Debe lanzar excepción si ya existe la MISMA rutina ACTIVA")
        void activarRutina_RN17_MismaRutinaActivaLanzaExcepcion() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            UsuarioRutina rutinaActiva = UsuarioRutina.builder()
                    .id(1L)
                    .perfilUsuario(perfilUsuario)
                    .rutina(rutina)
                    .estado(UsuarioPlan.EstadoAsignacion.ACTIVO)
                    .build();

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    1L, 1L)).thenReturn(Optional.of(rutinaActiva));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("Ya tienes esta rutina activa"));
            verify(usuarioRutinaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe permitir activar DIFERENTE rutina aunque ya tenga otra activa")
        void activarRutina_RN17_PermiteDiferenteRutinaActiva() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(2L); // Rutina DIFERENTE

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(2L)).thenReturn(Optional.of(rutinaDiferente));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    1L, 2L)).thenReturn(Optional.empty());
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutina);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.activarRutina(1L, request);

            // Then
            assertNotNull(response, "Debe permitir activar rutina diferente");
            verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
        }

        @Test
        @DisplayName("Debe sugerir reanudar si la rutina está PAUSADA")
        void activarRutina_RN17_PermiteMismaRutinaSiEstaPausada() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            UsuarioRutina rutinaPausada = UsuarioRutina.builder()
                    .id(1L)
                    .perfilUsuario(perfilUsuario)
                    .rutina(rutina)
                    .estado(UsuarioPlan.EstadoAsignacion.PAUSADO)
                    .build();

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    1L, 1L)).thenReturn(Optional.of(rutinaPausada));

            // When & Then - El servicio ahora sugiere usar Reanudar para rutinas pausadas
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            assertTrue(exception.getMessage().contains("pausada") || exception.getMessage().contains("Reanudar"),
                    "Debe sugerir usar reanudar para rutina pausada");
        }
    }

    // ============================================================
    // RN18: Proponer reemplazo
    // ============================================================

    @Nested
    @DisplayName("RN18: Proponer reemplazo")
    class RN18_ProponerReemplazoTests {

        @Test
        @DisplayName("Mensaje de error debe indicar necesidad de pausar/cancelar rutina actual")
        void activarRutina_RN18_MensajeProponePausarOCancelar() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            UsuarioRutina rutinaActiva = UsuarioRutina.builder()
                    .id(1L)
                    .perfilUsuario(perfilUsuario)
                    .rutina(rutina)
                    .estado(UsuarioPlan.EstadoAsignacion.ACTIVO)
                    .build();

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(usuarioRutinaRepository.findAsignacionMasReciente(
                    1L, 1L)).thenReturn(Optional.of(rutinaActiva));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            String mensaje = exception.getMessage().toLowerCase();
            assertTrue(mensaje.contains("pausarla") || mensaje.contains("cancelarla"),
                    "El mensaje debe proponer pausar o cancelar la rutina actual");
        }
    }

    // ============================================================
    // US-19: Pausar/Reanudar Meta (Rutina)
    // ============================================================

    @Nested
    @DisplayName("US-19: Pausar Rutina")
    class PausarRutinaTests {

        @Test
        @DisplayName("Debe pausar rutina ACTIVA exitosamente")
        void pausarRutina_Success() {
            // Given
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.pausarRutina(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.PAUSADO
            ));
        }

        @Test
        @DisplayName("Debe validar que la rutina pertenece al usuario")
        void pausarRutina_ValidaPropietario() {
            // Given
            usuarioRutina.getPerfilUsuario().setId(99L);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.pausarRutina(1L, 1L);
            });
            
            assertTrue(exception.getMessage().contains("no pertenece al usuario"));
        }
    }

    @Nested
    @DisplayName("US-19: Reanudar Rutina")
    class ReanudarRutinaTests {

        @Test
        @DisplayName("Debe reanudar rutina PAUSADA exitosamente")
        void reanudarRutina_Success() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            usuarioRutina.setFechaInicio(LocalDate.now().minusWeeks(2));
            usuarioRutina.setSemanaActual(2);
            
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.reanudarRutina(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.ACTIVO
            ));
        }

        @Test
        @DisplayName("Debe ajustar fecha fin al reanudar rutina pausada")
        void reanudarRutina_AjustaFechaFin() {
            // Given
            LocalDate fechaInicio = LocalDate.now().minusWeeks(5);
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            usuarioRutina.setFechaInicio(fechaInicio);
            usuarioRutina.setFechaFin(fechaInicio.plusWeeks(8).minusDays(1));
            usuarioRutina.setSemanaActual(3);
            
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.reanudarRutina(1L, 1L);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getFechaFin().isAfter(fechaInicio.plusWeeks(8).minusDays(1))
            ));
        }
    }

    // ============================================================
    // RN19: No pausar/reanudar en estado final
    // ============================================================

    @Nested
    @DisplayName("RN19: No pausar/reanudar en estado final")
    class RN19_NoOperarEstadoFinalTests {

        @Test
        @DisplayName("No debe permitir pausar rutina COMPLETADA")
        void pausarRutina_RN19_CompletadaLanzaExcepcion() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.pausarRutina(1L, 1L);
            });
            
            assertTrue(exception.getMessage().toLowerCase().contains("completada") || 
                      exception.getMessage().toLowerCase().contains("cancelada"));
        }

        @Test
        @DisplayName("No debe permitir pausar rutina CANCELADA")
        void pausarRutina_RN19_CanceladaLanzaExcepcion() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.pausarRutina(1L, 1L);
            });
        }

        @Test
        @DisplayName("No debe permitir reanudar rutina COMPLETADA")
        void reanudarRutina_RN19_CompletadaLanzaExcepcion() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.reanudarRutina(1L, 1L);
            });
        }

        @Test
        @DisplayName("No debe permitir reanudar rutina CANCELADA")
        void reanudarRutina_RN19_CanceladaLanzaExcepcion() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.reanudarRutina(1L, 1L);
            });
        }

        @Test
        @DisplayName("No debe permitir reanudar rutina que ya está ACTIVA")
        void reanudarRutina_RN19_ActivaLanzaExcepcion() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.reanudarRutina(1L, 1L);
            });
            
            assertTrue(exception.getMessage().toLowerCase().contains("pausada"));
        }
    }

    // ============================================================
    // US-20: Completar/Cancelar Meta (Rutina)
    // ============================================================

    @Nested
    @DisplayName("US-20: Completar Rutina")
    class CompletarRutinaTests {

        @Test
        @DisplayName("Debe completar rutina ACTIVA exitosamente")
        void completarRutina_Success() {
            // Given
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.completarRutina(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO &&
                ur.getFechaFin().equals(LocalDate.now())
            ));
        }

        @Test
        @DisplayName("Debe completar rutina PAUSADA exitosamente")
        void completarRutina_DesdePausada() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.completarRutina(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO
            ));
        }
    }

    @Nested
    @DisplayName("US-20: Cancelar Rutina")
    class CancelarRutinaTests {

        @Test
        @DisplayName("Debe cancelar rutina ACTIVA exitosamente")
        void cancelarRutina_Success() {
            // Given
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.cancelarRutina(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.CANCELADO &&
                ur.getFechaFin().equals(LocalDate.now())
            ));
        }

        @Test
        @DisplayName("Debe cancelar rutina PAUSADA exitosamente")
        void cancelarRutina_DesdePausada() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.cancelarRutina(1L, 1L);

            // Then
            assertNotNull(response);
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.CANCELADO
            ));
        }
    }

    // ============================================================
    // RN26: Transiciones de estado válidas
    // ============================================================

    @Nested
    @DisplayName("RN26: Transiciones de estado válidas")
    class RN26_TransicionesEstadoTests {

        @Test
        @DisplayName("ACTIVO → COMPLETADO es transición válida")
        void completarRutina_RN26_ActivoACompletado() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.completarRutina(1L, 1L);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO
            ));
        }

        @Test
        @DisplayName("ACTIVO → CANCELADO es transición válida")
        void cancelarRutina_RN26_ActivoACancelado() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.cancelarRutina(1L, 1L);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.CANCELADO
            ));
        }

        @Test
        @DisplayName("ACTIVO → PAUSADO es transición válida")
        void pausarRutina_RN26_ActivoAPausado() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.pausarRutina(1L, 1L);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.PAUSADO
            ));
        }

        @Test
        @DisplayName("PAUSADO → ACTIVO es transición válida")
        void reanudarRutina_RN26_PausadoAActivo() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.PAUSADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.reanudarRutina(1L, 1L);

            // Then
            verify(usuarioRutinaRepository).save(argThat(ur -> 
                ur.getEstado() == UsuarioPlan.EstadoAsignacion.ACTIVO
            ));
        }

        @Test
        @DisplayName("COMPLETADO → ACTIVO NO es transición válida")
        void completarRutina_RN26_CompletadoAActivoNoValido() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.COMPLETADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.completarRutina(1L, 1L);
            });
        }

        @Test
        @DisplayName("CANCELADO → ACTIVO NO es transición válida")
        void reanudarRutina_RN26_CanceladoAActivoNoValido() {
            // Given
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.CANCELADO);
            when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutina));

            // When & Then
            assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.reanudarRutina(1L, 1L);
            });
        }
    }

    // ============================================================
    // Tests de Consulta
    // ============================================================

    @Nested
    @DisplayName("Consultas de rutinas")
    class ConsultasTests {

        @Test
        @DisplayName("Debe obtener rutina activa actual del usuario")
        void obtenerRutinaActiva_Success() {
            // Given
            when(usuarioRutinaRepository.findRutinaActivaActual(1L)).thenReturn(Optional.of(usuarioRutina));

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.obtenerRutinaActiva(1L);

            // Then
            assertNotNull(response);
            assertEquals("Rutina de Fuerza", response.getRutinaNombre());
            assertEquals("ACTIVO", response.getEstado());
        }

        @Test
        @DisplayName("Debe retornar null si no hay rutina activa")
        void obtenerRutinaActiva_NoExiste() {
            // Given
            when(usuarioRutinaRepository.findRutinaActivaActual(1L)).thenReturn(Optional.empty());

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.obtenerRutinaActiva(1L);

            // Then
            assertNull(response, "Debe retornar null si no hay rutina activa");
        }

        @Test
        @DisplayName("Debe obtener todas las rutinas del usuario")
        void obtenerRutinas_Success() {
            // Given
            UsuarioRutina rutinaPausada = UsuarioRutina.builder()
                    .id(2L)
                    .perfilUsuario(perfilUsuario)
                    .rutina(rutinaDiferente)
                    .estado(UsuarioPlan.EstadoAsignacion.PAUSADO)
                    .build();
            
            when(usuarioRutinaRepository.findByPerfilUsuarioId(1L))
                    .thenReturn(List.of(usuarioRutina, rutinaPausada));

            // When
            List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinas(1L);

            // Then
            assertNotNull(response);
            assertEquals(2, response.size());
        }

        @Test
        @DisplayName("Debe obtener solo rutinas activas del usuario")
        void obtenerRutinasActivas_Success() {
            // Given
            when(usuarioRutinaRepository.findAllRutinasActivas(1L))
                    .thenReturn(List.of(usuarioRutina));

            // When
            List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinasActivas(1L);

            // Then
            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals("ACTIVO", response.get(0).getEstado());
        }

        @Test
        @DisplayName("Debe retornar lista vacía si usuario no tiene rutinas")
        void obtenerRutinas_ListaVacia() {
            // Given
            when(usuarioRutinaRepository.findByPerfilUsuarioId(1L))
                    .thenReturn(new ArrayList<>());

            // When
            List<UsuarioRutinaResponse> response = usuarioRutinaService.obtenerRutinas(1L);

            // Then
            assertNotNull(response);
            assertTrue(response.isEmpty());
        }
    }

    // ============================================================
    // Tests de Actualización Automática
    // ============================================================

    @Nested
    @DisplayName("Actualización automática de semanas")
    class ActualizacionAutomaticaTests {

        @Test
        @DisplayName("Debe auto-completar rutina al llegar a la última semana")
        void actualizarSemanasActuales_AutoCompletarRutina() {
            // Given
            usuarioRutina.setFechaInicio(LocalDate.now().minusWeeks(8));
            usuarioRutina.setSemanaActual(8);
            usuarioRutina.setEstado(UsuarioPlan.EstadoAsignacion.ACTIVO);
            
            when(usuarioRutinaRepository.findByPerfilUsuarioIdAndEstado(
                    null, UsuarioPlan.EstadoAsignacion.ACTIVO))
                    .thenReturn(List.of(usuarioRutina));
            when(usuarioRutinaRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

            // When
            usuarioRutinaService.actualizarSemanasActuales();

            // Then
            verify(usuarioRutinaRepository).saveAll(argThat(list -> {
                List<UsuarioRutina> rutinas = (List<UsuarioRutina>) list;
                return rutinas.get(0).getEstado() == UsuarioPlan.EstadoAsignacion.COMPLETADO;
            }));
        }
    }

    // ============================================================
    // RN33: Validación de contraindicaciones médicas
    // ============================================================

    @Nested
    @DisplayName("RN33: Validación de contraindicaciones médicas")
    class RN33_ContraindicacionesMedicasTests {

        @Test
        @DisplayName("Debe lanzar excepción si rutina tiene ejercicios contraindicados para el usuario")
        void activarRutina_RN33_ContraindicacionesLanzaExcepcion() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);
            request.setFechaInicio(LocalDate.now());

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(rutinaRepository.findContraindicacionesUsuarioRutina(1L, 1L))
                    .thenReturn(List.of("Lesión de rodilla", "Dolor lumbar"));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            String mensaje = exception.getMessage();
            assertTrue(mensaje.contains("condición médica") || mensaje.contains("contraindicad"),
                    "El mensaje debe indicar la contraindicación médica");
            assertTrue(mensaje.contains("Lesión de rodilla") || mensaje.contains("Dolor lumbar"),
                    "El mensaje debe incluir las condiciones específicas");
            verify(usuarioRutinaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe permitir activar rutina si no hay contraindicaciones")
        void activarRutina_RN33_SinContraindicacionesPermite() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);
            request.setFechaInicio(LocalDate.now());

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(rutinaRepository.findContraindicacionesUsuarioRutina(1L, 1L))
                    .thenReturn(List.of()); // Lista vacía = sin contraindicaciones
            when(usuarioRutinaRepository.findAsignacionMasReciente(1L, 1L))
                    .thenReturn(Optional.empty()); // No hay asignación previa
            when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutina);

            // When
            UsuarioRutinaResponse response = usuarioRutinaService.activarRutina(1L, request);

            // Then
            assertNotNull(response, "Debe permitir activar rutina sin contraindicaciones");
            verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
        }

        @Test
        @DisplayName("Debe validar contraindicaciones ANTES de verificar duplicados")
        void activarRutina_RN33_ValidaContraindicacionesPrimero() {
            // Given - Usuario con contraindicaciones para esta rutina
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(rutinaRepository.findContraindicacionesUsuarioRutina(1L, 1L))
                    .thenReturn(List.of("Hipertensión"));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            // Verificar que NO se llamó a verificar duplicados (orden correcto)
            verify(usuarioRutinaRepository, never()).findAsignacionMasReciente(
                    anyLong(), anyLong());
            assertTrue(exception.getMessage().contains("condición médica") || 
                      exception.getMessage().contains("Hipertensión"));
        }

        @Test
        @DisplayName("Mensaje de error debe recomendar consultar profesional de salud")
        void activarRutina_RN33_MensajeRecomiendaProfesional() {
            // Given
            ActivarRutinaRequest request = new ActivarRutinaRequest();
            request.setRutinaId(1L);

            when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuario));
            when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutina));
            when(rutinaRepository.findContraindicacionesUsuarioRutina(1L, 1L))
                    .thenReturn(List.of("Lesión de espalda"));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                usuarioRutinaService.activarRutina(1L, request);
            });
            
            String mensaje = exception.getMessage().toLowerCase();
            assertTrue(mensaje.contains("profesional") || mensaje.contains("salud") || 
                      mensaje.contains("otra rutina"),
                    "El mensaje debe recomendar consultar profesional o elegir otra rutina");
        }
    }
}
