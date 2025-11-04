package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.EjercicioRequest;
import com.example.nutritrackapi.dto.EjercicioResponse;
import com.example.nutritrackapi.model.Ejercicio;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.repository.EjercicioRepository;
import com.example.nutritrackapi.repository.EtiquetaRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EjercicioService - Tests unitarios")
class EjercicioServiceTest {

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private EjercicioService ejercicioService;

    private Ejercicio ejercicio;
    private EjercicioRequest ejercicioRequest;
    private Etiqueta etiqueta;

    @BeforeEach
    void setUp() {
        etiqueta = new Etiqueta();
        etiqueta.setId(1L);
        etiqueta.setNombre("Cardio");

        ejercicio = new Ejercicio();
        ejercicio.setId(1L);
        ejercicio.setNombre("Correr");
        ejercicio.setDescripcion("Carrera continua a ritmo moderado");
        ejercicio.setTipoEjercicio(Ejercicio.TipoEjercicio.CARDIO);
        ejercicio.setGrupoMuscular(Ejercicio.GrupoMuscular.PIERNAS);
        ejercicio.setNivelDificultad(Ejercicio.NivelDificultad.INTERMEDIO);
        ejercicio.setCaloriasQuemadasPorMinuto(new BigDecimal("10.0"));
        ejercicio.setDuracionEstimadaMinutos(30);
        ejercicio.setEquipoNecesario("Zapatillas deportivas");
        ejercicio.setEtiquetas(new HashSet<>(Set.of(etiqueta)));

        ejercicioRequest = new EjercicioRequest();
        ejercicioRequest.setNombre("Correr");
        ejercicioRequest.setDescripcion("Carrera continua a ritmo moderado");
        ejercicioRequest.setTipoEjercicio(Ejercicio.TipoEjercicio.CARDIO);
        ejercicioRequest.setGrupoMuscular(Ejercicio.GrupoMuscular.PIERNAS);
        ejercicioRequest.setNivelDificultad(Ejercicio.NivelDificultad.INTERMEDIO);
        ejercicioRequest.setCaloriasQuemadasPorMinuto(new BigDecimal("10.0"));
        ejercicioRequest.setDuracionEstimadaMinutos(30);
        ejercicioRequest.setEquipoNecesario("Zapatillas deportivas");
        ejercicioRequest.setEtiquetaIds(Set.of(1L));
    }

    @Test
    @DisplayName("Crear ejercicio - Caso exitoso")
    void crearEjercicio_DebeCrearExitosamente() {
        // Given
        when(ejercicioRepository.existsByNombre(ejercicioRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(ejercicioRequest.getEtiquetaIds())).thenReturn(List.of(etiqueta));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicio);

        // When
        EjercicioResponse response = ejercicioService.crearEjercicio(ejercicioRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Correr");
        assertThat(response.getTipoEjercicio()).isEqualTo(Ejercicio.TipoEjercicio.CARDIO);
        assertThat(response.getGrupoMuscular()).isEqualTo(Ejercicio.GrupoMuscular.PIERNAS);
        verify(ejercicioRepository).existsByNombre("Correr");
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Crear ejercicio - RN07: Nombre duplicado debe lanzar excepción")
    void crearEjercicio_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Given
        when(ejercicioRepository.existsByNombre(ejercicioRequest.getNombre())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> ejercicioService.crearEjercicio(ejercicioRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un ejercicio con el nombre");
        verify(ejercicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener ejercicio por ID - Caso exitoso")
    void obtenerEjercicioPorId_DebeRetornarEjercicio() {
        // Given
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        // When
        EjercicioResponse response = ejercicioService.obtenerEjercicioPorId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Correr");
        verify(ejercicioRepository).findById(1L);
    }

    @Test
    @DisplayName("Filtrar por tipo - Debe retornar resultados")
    void filtrarPorTipo_DebeRetornarResultados() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Ejercicio> ejercicios = List.of(ejercicio);
        Page<Ejercicio> pageEjercicios = new PageImpl<>(ejercicios, pageable, 1);
        when(ejercicioRepository.findByTipoEjercicio(Ejercicio.TipoEjercicio.CARDIO, pageable))
                .thenReturn(pageEjercicios);

        // When
        Page<EjercicioResponse> response = ejercicioService
                .filtrarPorTipo(Ejercicio.TipoEjercicio.CARDIO, pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getTipoEjercicio())
                .isEqualTo(Ejercicio.TipoEjercicio.CARDIO);
        verify(ejercicioRepository).findByTipoEjercicio(Ejercicio.TipoEjercicio.CARDIO, pageable);
    }

    @Test
    @DisplayName("Filtrar por grupo muscular - Debe retornar resultados")
    void filtrarPorGrupoMuscular_DebeRetornarResultados() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Ejercicio> ejercicios = List.of(ejercicio);
        Page<Ejercicio> pageEjercicios = new PageImpl<>(ejercicios, pageable, 1);
        when(ejercicioRepository.findByGrupoMuscular(Ejercicio.GrupoMuscular.PIERNAS, pageable))
                .thenReturn(pageEjercicios);

        // When
        Page<EjercicioResponse> response = ejercicioService
                .filtrarPorGrupoMuscular(Ejercicio.GrupoMuscular.PIERNAS, pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getGrupoMuscular())
                .isEqualTo(Ejercicio.GrupoMuscular.PIERNAS);
        verify(ejercicioRepository).findByGrupoMuscular(Ejercicio.GrupoMuscular.PIERNAS, pageable);
    }

    @Test
    @DisplayName("Filtrar por múltiples criterios - Debe retornar resultados")
    void filtrarEjercicios_ConMultiplesCriterios_DebeRetornarResultados() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Ejercicio> ejercicios = List.of(ejercicio);
        Page<Ejercicio> pageEjercicios = new PageImpl<>(ejercicios, pageable, 1);
        when(ejercicioRepository.findByFiltros(
                Ejercicio.TipoEjercicio.CARDIO,
                Ejercicio.GrupoMuscular.PIERNAS,
                Ejercicio.NivelDificultad.INTERMEDIO,
                pageable
        )).thenReturn(pageEjercicios);

        // When
        Page<EjercicioResponse> response = ejercicioService.filtrarEjercicios(
                Ejercicio.TipoEjercicio.CARDIO,
                Ejercicio.GrupoMuscular.PIERNAS,
                Ejercicio.NivelDificultad.INTERMEDIO,
                pageable
        );

        // Then
        assertThat(response).isNotEmpty();
        verify(ejercicioRepository).findByFiltros(
                Ejercicio.TipoEjercicio.CARDIO,
                Ejercicio.GrupoMuscular.PIERNAS,
                Ejercicio.NivelDificultad.INTERMEDIO,
                pageable
        );
    }

    @Test
    @DisplayName("Actualizar ejercicio - Caso exitoso")
    void actualizarEjercicio_DebeActualizarExitosamente() {
        // Given
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(ejercicioRepository.existsByNombreAndIdNot("Correr Modificado", 1L)).thenReturn(false);
        when(etiquetaRepository.findAllById(ejercicioRequest.getEtiquetaIds())).thenReturn(List.of(etiqueta));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicio);

        ejercicioRequest.setNombre("Correr Modificado");

        // When
        EjercicioResponse response = ejercicioService.actualizarEjercicio(1L, ejercicioRequest);

        // Then
        assertThat(response).isNotNull();
        verify(ejercicioRepository).findById(1L);
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    @DisplayName("Eliminar ejercicio - Caso exitoso")
    void eliminarEjercicio_DebeEliminarExitosamente() {
        // Given
        when(ejercicioRepository.existsById(1L)).thenReturn(true);

        // When
        ejercicioService.eliminarEjercicio(1L);

        // Then
        verify(ejercicioRepository).existsById(1L);
        verify(ejercicioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar ejercicio - No encontrado debe lanzar excepción")
    void eliminarEjercicio_NoEncontrado_DebeLanzarExcepcion() {
        // Given
        when(ejercicioRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> ejercicioService.eliminarEjercicio(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ejercicio no encontrado con ID: 999");
        verify(ejercicioRepository, never()).deleteById(any());
    }
}
