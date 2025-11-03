package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.AsignarRutinaRequest;
import com.nutritrack.nutritrackapi.dto.response.UsuarioRutinaResponse;
import com.nutritrack.nutritrackapi.exception.BusinessRuleException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.PerfilUsuario;
import com.nutritrack.nutritrackapi.model.Rutina;
import com.nutritrack.nutritrackapi.model.UsuarioRutina;
import com.nutritrack.nutritrackapi.model.enums.EstadoAsignacion;
import com.nutritrack.nutritrackapi.repository.PerfilUsuarioRepository;
import com.nutritrack.nutritrackapi.repository.RutinaRepository;
import com.nutritrack.nutritrackapi.repository.UsuarioRutinaRepository;
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
@DisplayName("Tests del servicio UsuarioRutinaService")
class UsuarioRutinaServiceTest {

    @Mock
    private UsuarioRutinaRepository usuarioRutinaRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private RutinaRepository rutinaRepository;

    @InjectMocks
    private UsuarioRutinaService usuarioRutinaService;

    private PerfilUsuario perfilUsuarioMock;
    private Rutina rutinaMock;
    private UsuarioRutina usuarioRutinaMock;
    private AsignarRutinaRequest asignarRutinaRequest;

    @BeforeEach
    void setUp() {
        perfilUsuarioMock = PerfilUsuario.builder()
                .id(1L)
                .nombre("María")
                .apellido("García")
                .build();

        rutinaMock = Rutina.builder()
                .id(1L)
                .nombre("Rutina de Hipertrofia")
                .duracionSemanas(12)
                .activo(true)
                .build();

        usuarioRutinaMock = UsuarioRutina.builder()
                .id(1L)
                .perfilUsuario(perfilUsuarioMock)
                .rutina(rutinaMock)
                .fechaInicio(LocalDate.of(2025, 11, 1))
                .fechaFin(LocalDate.of(2026, 1, 23))
                .estado(EstadoAsignacion.ACTIVO)
                .semanaActual(1)
                .notas("Primera rutina de hipertrofia")
                .build();

        asignarRutinaRequest = AsignarRutinaRequest.builder()
                .rutinaId(1L)
                .fechaInicio(LocalDate.of(2025, 11, 1))
                .notas("Primera rutina de hipertrofia")
                .build();
    }

    @Test
    @DisplayName("Debe asignar una rutina correctamente")
    void debeAsignarRutinaCorrectamente() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(usuarioRutinaRepository.existsOverlappingActiveRutina(any(), any(), any())).thenReturn(false);
        when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutinaMock);

        UsuarioRutinaResponse response = usuarioRutinaService.asignarRutina(1L, asignarRutinaRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Rutina de Hipertrofia", response.getNombreRutina());
        assertEquals(EstadoAsignacion.ACTIVO, response.getEstado());
        verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> usuarioRutinaService.asignarRutina(1L, asignarRutinaRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la rutina no existe")
    void debeLanzarExcepcionCuandoRutinaNoExiste() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(rutinaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> usuarioRutinaService.asignarRutina(1L, asignarRutinaRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la rutina está inactiva")
    void debeLanzarExcepcionCuandoRutinaEstaInactiva() {
        rutinaMock.setActivo(false);
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));

        assertThrows(BusinessRuleException.class, 
            () -> usuarioRutinaService.asignarRutina(1L, asignarRutinaRequest));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando hay solapamiento de fechas")
    void debeLanzarExcepcionCuandoHaySolapamiento() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(usuarioRutinaRepository.existsOverlappingActiveRutina(any(), any(), any())).thenReturn(true);

        assertThrows(BusinessRuleException.class, 
            () -> usuarioRutinaService.asignarRutina(1L, asignarRutinaRequest));
    }

    @Test
    @DisplayName("Debe listar rutinas activas correctamente")
    void debeListarRutinasActivas() {
        when(usuarioRutinaRepository.findByPerfilUsuarioIdAndEstadoWithRutina(1L, EstadoAsignacion.ACTIVO))
                .thenReturn(Arrays.asList(usuarioRutinaMock));

        List<UsuarioRutinaResponse> rutinas = usuarioRutinaService.listarRutinasActivas(1L);

        assertNotNull(rutinas);
        assertEquals(1, rutinas.size());
        assertEquals("Rutina de Hipertrofia", rutinas.get(0).getNombreRutina());
    }

    @Test
    @DisplayName("Debe buscar rutina por ID correctamente")
    void debeBuscarRutinaPorId() {
        when(usuarioRutinaRepository.findByIdWithRutina(1L)).thenReturn(Optional.of(usuarioRutinaMock));

        UsuarioRutinaResponse response = usuarioRutinaService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Rutina de Hipertrofia", response.getNombreRutina());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la rutina no pertenece al usuario")
    void debeLanzarExcepcionCuandoRutinaNoPertenece() {
        when(usuarioRutinaRepository.findByIdWithRutina(1L)).thenReturn(Optional.of(usuarioRutinaMock));

        assertThrows(BusinessRuleException.class, 
            () -> usuarioRutinaService.buscarPorId(2L, 1L));
    }

    @Test
    @DisplayName("Debe completar rutina correctamente")
    void debeCompletarRutina() {
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutinaMock);

        UsuarioRutinaResponse response = usuarioRutinaService.completarRutina(1L, 1L);

        assertNotNull(response);
        verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
    }

    @Test
    @DisplayName("Debe cancelar rutina correctamente")
    void debeCancelarRutina() {
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutinaMock);

        UsuarioRutinaResponse response = usuarioRutinaService.cancelarRutina(1L, 1L);

        assertNotNull(response);
        verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
    }

    @Test
    @DisplayName("Debe avanzar semana correctamente")
    void debeAvanzarSemana() {
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutinaMock);

        UsuarioRutinaResponse response = usuarioRutinaService.avanzarSemana(1L, 1L);

        assertNotNull(response);
        verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
    }

    @Test
    @DisplayName("Debe completar automáticamente al alcanzar la última semana")
    void debeCompletarAutomaticamenteAlAlcanzarUltimaSemana() {
        usuarioRutinaMock.setSemanaActual(12);
        when(usuarioRutinaRepository.findById(1L)).thenReturn(Optional.of(usuarioRutinaMock));
        when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenReturn(usuarioRutinaMock);

        UsuarioRutinaResponse response = usuarioRutinaService.avanzarSemana(1L, 1L);

        assertNotNull(response);
        verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
    }

    @Test
    @DisplayName("Debe calcular progreso correctamente")
    void debeCalcularProgresoCorrectamente() {
        usuarioRutinaMock.setSemanaActual(6);
        when(usuarioRutinaRepository.findByIdWithRutina(1L)).thenReturn(Optional.of(usuarioRutinaMock));

        UsuarioRutinaResponse response = usuarioRutinaService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("50.00"), response.getProgreso());
        assertEquals(5, response.getSemanasCompletadas());
        assertEquals(7, response.getSemanasRestantes());
    }

    @Test
    @DisplayName("Debe mostrar progreso 100% cuando la rutina está completada")
    void debeMostrarProgreso100CuandoCompletada() {
        usuarioRutinaMock.setEstado(EstadoAsignacion.COMPLETADO);
        when(usuarioRutinaRepository.findByIdWithRutina(1L)).thenReturn(Optional.of(usuarioRutinaMock));

        UsuarioRutinaResponse response = usuarioRutinaService.buscarPorId(1L, 1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getProgreso());
    }

    @Test
    @DisplayName("Debe calcular correctamente la fecha fin (12 semanas)")
    void debeCalcularCorrectamenteFechaFin() {
        when(perfilUsuarioRepository.findById(1L)).thenReturn(Optional.of(perfilUsuarioMock));
        when(rutinaRepository.findById(1L)).thenReturn(Optional.of(rutinaMock));
        when(usuarioRutinaRepository.existsOverlappingActiveRutina(any(), any(), any())).thenReturn(false);
        when(usuarioRutinaRepository.save(any(UsuarioRutina.class))).thenAnswer(invocation -> {
            UsuarioRutina savedRutina = invocation.getArgument(0);
            // 12 semanas = 84 días - 1 = LocalDate.of(2025, 11, 1).plusWeeks(12).minusDays(1)
            assertEquals(LocalDate.of(2026, 1, 23), savedRutina.getFechaFin());
            return savedRutina;
        });

        usuarioRutinaService.asignarRutina(1L, asignarRutinaRequest);

        verify(usuarioRutinaRepository).save(any(UsuarioRutina.class));
    }
}
