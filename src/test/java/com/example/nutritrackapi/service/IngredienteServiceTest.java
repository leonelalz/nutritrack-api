package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.IngredienteRequest;
import com.example.nutritrackapi.dto.IngredienteResponse;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.model.Ingrediente;
import com.example.nutritrackapi.repository.EtiquetaRepository;
import com.example.nutritrackapi.repository.IngredienteRepository;
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
@DisplayName("IngredienteService - Tests unitarios")
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private IngredienteService ingredienteService;

    private Ingrediente ingrediente;
    private IngredienteRequest ingredienteRequest;
    private Etiqueta etiqueta;

    @BeforeEach
    void setUp() {
        etiqueta = new Etiqueta();
        etiqueta.setId(1L);
        etiqueta.setNombre("Alto en Proteína");

        ingrediente = new Ingrediente();
        ingrediente.setId(1L);
        ingrediente.setNombre("Pechuga de Pollo");
        ingrediente.setProteinas(new BigDecimal("31.0"));
        ingrediente.setCarbohidratos(new BigDecimal("0.0"));
        ingrediente.setGrasas(new BigDecimal("3.6"));
        ingrediente.setEnergia(new BigDecimal("165.0"));
        ingrediente.setFibra(new BigDecimal("0.0"));
        ingrediente.setCategoriaAlimento(Ingrediente.CategoriaAlimento.PROTEINAS);
        ingrediente.setDescripcion("Pechuga de pollo sin piel");
        ingrediente.setEtiquetas(new HashSet<>(Set.of(etiqueta)));

        ingredienteRequest = new IngredienteRequest();
        ingredienteRequest.setNombre("Pechuga de Pollo");
        ingredienteRequest.setProteinas(new BigDecimal("31.0"));
        ingredienteRequest.setCarbohidratos(new BigDecimal("0.0"));
        ingredienteRequest.setGrasas(new BigDecimal("3.6"));
        ingredienteRequest.setEnergia(new BigDecimal("165.0"));
        ingredienteRequest.setFibra(new BigDecimal("0.0"));
        ingredienteRequest.setCategoriaAlimento(Ingrediente.CategoriaAlimento.PROTEINAS);
        ingredienteRequest.setDescripcion("Pechuga de pollo sin piel");
        ingredienteRequest.setEtiquetaIds(Set.of(1L));
    }

    @Test
    @DisplayName("Crear ingrediente - Caso exitoso")
    void crearIngrediente_DebeCrearExitosamente() {
        // Given
        when(ingredienteRepository.existsByNombre(ingredienteRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(ingredienteRequest.getEtiquetaIds())).thenReturn(List.of(etiqueta));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        // When
        IngredienteResponse response = ingredienteService.crearIngrediente(ingredienteRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Pechuga de Pollo");
        assertThat(response.getProteinas()).isEqualByComparingTo(new BigDecimal("31.0"));
        assertThat(response.getCategoriaAlimento()).isEqualTo(Ingrediente.CategoriaAlimento.PROTEINAS);
        verify(ingredienteRepository).existsByNombre("Pechuga de Pollo");
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Crear ingrediente - RN07: Nombre duplicado debe lanzar excepción")
    void crearIngrediente_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Given
        when(ingredienteRepository.existsByNombre(ingredienteRequest.getNombre())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> ingredienteService.crearIngrediente(ingredienteRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un ingrediente con el nombre");
        verify(ingredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear ingrediente - Etiqueta no encontrada debe lanzar excepción")
    void crearIngrediente_ConEtiquetaInvalida_DebeLanzarExcepcion() {
        // Given
        when(ingredienteRepository.existsByNombre(ingredienteRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(ingredienteRequest.getEtiquetaIds())).thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> ingredienteService.crearIngrediente(ingredienteRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Una o más etiquetas no fueron encontradas");
        verify(ingredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener ingrediente por ID - Caso exitoso")
    void obtenerIngredientePorId_DebeRetornarIngrediente() {
        // Given
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));

        // When
        IngredienteResponse response = ingredienteService.obtenerIngredientePorId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Pechuga de Pollo");
        verify(ingredienteRepository).findById(1L);
    }

    @Test
    @DisplayName("Filtrar por categoría - Debe retornar resultados")
    void filtrarPorCategoria_DebeRetornarResultados() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Ingrediente> ingredientes = List.of(ingrediente);
        Page<Ingrediente> pageIngredientes = new PageImpl<>(ingredientes, pageable, 1);
        when(ingredienteRepository.findByCategoriaAlimento(Ingrediente.CategoriaAlimento.PROTEINAS, pageable))
                .thenReturn(pageIngredientes);

        // When
        Page<IngredienteResponse> response = ingredienteService
                .filtrarPorCategoria(Ingrediente.CategoriaAlimento.PROTEINAS, pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getCategoriaAlimento())
                .isEqualTo(Ingrediente.CategoriaAlimento.PROTEINAS);
        verify(ingredienteRepository).findByCategoriaAlimento(Ingrediente.CategoriaAlimento.PROTEINAS, pageable);
    }

    @Test
    @DisplayName("Actualizar ingrediente - Caso exitoso")
    void actualizarIngrediente_DebeActualizarExitosamente() {
        // Given
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.existsByNombreAndIdNot("Pechuga Modificada", 1L)).thenReturn(false);
        when(etiquetaRepository.findAllById(ingredienteRequest.getEtiquetaIds())).thenReturn(List.of(etiqueta));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        ingredienteRequest.setNombre("Pechuga Modificada");

        // When
        IngredienteResponse response = ingredienteService.actualizarIngrediente(1L, ingredienteRequest);

        // Then
        assertThat(response).isNotNull();
        verify(ingredienteRepository).findById(1L);
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Eliminar ingrediente - Caso exitoso")
    void eliminarIngrediente_DebeEliminarExitosamente() {
        // Given
        when(ingredienteRepository.existsById(1L)).thenReturn(true);
        when(ingredienteRepository.estaEnUsoEnRecetas(1L)).thenReturn(false);

        // When
        ingredienteService.eliminarIngrediente(1L);

        // Then
        verify(ingredienteRepository).existsById(1L);
        verify(ingredienteRepository).estaEnUsoEnRecetas(1L);
        verify(ingredienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar ingrediente - RN09: En uso debe lanzar excepción")
    void eliminarIngrediente_EnUsoEnRecetas_DebeLanzarExcepcion() {
        // Given
        when(ingredienteRepository.existsById(1L)).thenReturn(true);
        when(ingredienteRepository.estaEnUsoEnRecetas(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> ingredienteService.eliminarIngrediente(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("está en uso en una o más recetas");
        verify(ingredienteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar ingrediente - No encontrado debe lanzar excepción")
    void eliminarIngrediente_NoEncontrado_DebeLanzarExcepcion() {
        // Given
        when(ingredienteRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> ingredienteService.eliminarIngrediente(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ingrediente no encontrado con ID: 999");
        verify(ingredienteRepository, never()).deleteById(any());
    }
}
