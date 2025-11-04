package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.EtiquetaRequest;
import com.example.nutritrackapi.dto.EtiquetaResponse;
import com.example.nutritrackapi.model.Etiqueta;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EtiquetaService - Tests unitarios")
class EtiquetaServiceTest {

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private EtiquetaService etiquetaService;

    private Etiqueta etiqueta;
    private EtiquetaRequest etiquetaRequest;

    @BeforeEach
    void setUp() {
        etiqueta = new Etiqueta();
        etiqueta.setId(1L);
        etiqueta.setNombre("Sin Gluten");
        etiqueta.setTipoEtiqueta(Etiqueta.TipoEtiqueta.ALERGIA);
        etiqueta.setDescripcion("Productos sin gluten");

        etiquetaRequest = new EtiquetaRequest();
        etiquetaRequest.setNombre("Sin Gluten");
        etiquetaRequest.setTipoEtiqueta(Etiqueta.TipoEtiqueta.ALERGIA);
        etiquetaRequest.setDescripcion("Productos sin gluten");
    }

    @Test
    @DisplayName("Crear etiqueta - Caso exitoso")
    void crearEtiqueta_DebeCrearExitosamente() {
        // Given
        when(etiquetaRepository.existsByNombre(etiquetaRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.save(any(Etiqueta.class))).thenReturn(etiqueta);

        // When
        EtiquetaResponse response = etiquetaService.crearEtiqueta(etiquetaRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Sin Gluten");
        assertThat(response.getTipoEtiqueta()).isEqualTo(Etiqueta.TipoEtiqueta.ALERGIA);
        verify(etiquetaRepository).existsByNombre("Sin Gluten");
        verify(etiquetaRepository).save(any(Etiqueta.class));
    }

    @Test
    @DisplayName("Crear etiqueta - RN06: Nombre duplicado debe lanzar excepción")
    void crearEtiqueta_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Given
        when(etiquetaRepository.existsByNombre(etiquetaRequest.getNombre())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> etiquetaService.crearEtiqueta(etiquetaRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe una etiqueta con el nombre");
        verify(etiquetaRepository).existsByNombre("Sin Gluten");
        verify(etiquetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener etiqueta por ID - Caso exitoso")
    void obtenerEtiquetaPorId_DebeRetornarEtiqueta() {
        // Given
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiqueta));

        // When
        EtiquetaResponse response = etiquetaService.obtenerEtiquetaPorId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Sin Gluten");
        verify(etiquetaRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener etiqueta por ID - Etiqueta no encontrada")
    void obtenerEtiquetaPorId_NoEncontrada_DebeLanzarExcepcion() {
        // Given
        when(etiquetaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> etiquetaService.obtenerEtiquetaPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Etiqueta no encontrada con ID: 999");
        verify(etiquetaRepository).findById(999L);
    }

    @Test
    @DisplayName("Listar etiquetas - Debe retornar página de etiquetas")
    void listarEtiquetas_DebeRetornarPagina() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Etiqueta> etiquetas = List.of(etiqueta);
        Page<Etiqueta> pageEtiquetas = new PageImpl<>(etiquetas, pageable, 1);
        when(etiquetaRepository.findAll(pageable)).thenReturn(pageEtiquetas);

        // When
        Page<EtiquetaResponse> response = etiquetaService.listarEtiquetas(pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getNombre()).isEqualTo("Sin Gluten");
        verify(etiquetaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Buscar por nombre - Debe retornar resultados")
    void buscarPorNombre_DebeRetornarResultados() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Etiqueta> etiquetas = List.of(etiqueta);
        Page<Etiqueta> pageEtiquetas = new PageImpl<>(etiquetas, pageable, 1);
        when(etiquetaRepository.findByNombreContainingIgnoreCase("gluten", pageable))
                .thenReturn(pageEtiquetas);

        // When
        Page<EtiquetaResponse> response = etiquetaService.buscarPorNombre("gluten", pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getContent()).hasSize(1);
        verify(etiquetaRepository).findByNombreContainingIgnoreCase("gluten", pageable);
    }

    @Test
    @DisplayName("Actualizar etiqueta - Caso exitoso")
    void actualizarEtiqueta_DebeActualizarExitosamente() {
        // Given
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiqueta));
        when(etiquetaRepository.existsByNombreAndIdNot("Sin Gluten Modificado", 1L)).thenReturn(false);
        when(etiquetaRepository.save(any(Etiqueta.class))).thenReturn(etiqueta);

        etiquetaRequest.setNombre("Sin Gluten Modificado");

        // When
        EtiquetaResponse response = etiquetaService.actualizarEtiqueta(1L, etiquetaRequest);

        // Then
        assertThat(response).isNotNull();
        verify(etiquetaRepository).findById(1L);
        verify(etiquetaRepository).existsByNombreAndIdNot("Sin Gluten Modificado", 1L);
        verify(etiquetaRepository).save(any(Etiqueta.class));
    }

    @Test
    @DisplayName("Actualizar etiqueta - RN06: Nombre duplicado debe lanzar excepción")
    void actualizarEtiqueta_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Given
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiqueta));
        when(etiquetaRepository.existsByNombreAndIdNot("Vegano", 1L)).thenReturn(true);

        etiquetaRequest.setNombre("Vegano");

        // When & Then
        assertThatThrownBy(() -> etiquetaService.actualizarEtiqueta(1L, etiquetaRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe otra etiqueta con el nombre");
        verify(etiquetaRepository).findById(1L);
        verify(etiquetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar etiqueta - Caso exitoso")
    void eliminarEtiqueta_DebeEliminarExitosamente() {
        // Given
        when(etiquetaRepository.existsById(1L)).thenReturn(true);
        when(etiquetaRepository.estaEnUsoEnIngredientes(1L)).thenReturn(false);
        when(etiquetaRepository.estaEnUsoEnEjercicios(1L)).thenReturn(false);
        when(etiquetaRepository.estaEnUsoEnComidas(1L)).thenReturn(false);
        when(etiquetaRepository.isEtiquetaEnUso(1L)).thenReturn(false);

        // When
        etiquetaService.eliminarEtiqueta(1L);

        // Then
        verify(etiquetaRepository).existsById(1L);
        verify(etiquetaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar etiqueta - RN08: En uso en ingredientes debe lanzar excepción")
    void eliminarEtiqueta_EnUsoEnIngredientes_DebeLanzarExcepcion() {
        // Given
        when(etiquetaRepository.existsById(1L)).thenReturn(true);
        when(etiquetaRepository.estaEnUsoEnIngredientes(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> etiquetaService.eliminarEtiqueta(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("está en uso en ingredientes");
        verify(etiquetaRepository).existsById(1L);
        verify(etiquetaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar etiqueta - RN08: En uso en ejercicios debe lanzar excepción")
    void eliminarEtiqueta_EnUsoEnEjercicios_DebeLanzarExcepcion() {
        // Given
        when(etiquetaRepository.existsById(1L)).thenReturn(true);
        when(etiquetaRepository.estaEnUsoEnIngredientes(1L)).thenReturn(false);
        when(etiquetaRepository.estaEnUsoEnEjercicios(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> etiquetaService.eliminarEtiqueta(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("está en uso en ejercicios");
        verify(etiquetaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar etiqueta - Etiqueta no encontrada")
    void eliminarEtiqueta_NoEncontrada_DebeLanzarExcepcion() {
        // Given
        when(etiquetaRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> etiquetaService.eliminarEtiqueta(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Etiqueta no encontrada con ID: 999");
        verify(etiquetaRepository).existsById(999L);
        verify(etiquetaRepository, never()).deleteById(any());
    }
}
