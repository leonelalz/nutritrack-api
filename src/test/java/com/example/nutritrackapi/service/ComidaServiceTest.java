package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.AgregarIngredienteRequest;
import com.example.nutritrackapi.dto.ComidaRequest;
import com.example.nutritrackapi.dto.ComidaResponse;
import com.example.nutritrackapi.model.Comida;
import com.example.nutritrackapi.model.ComidaIngrediente;
import com.example.nutritrackapi.model.ComidaIngrediente.ComidaIngredienteId;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.model.Ingrediente;
import com.example.nutritrackapi.repository.ComidaIngredienteRepository;
import com.example.nutritrackapi.repository.ComidaRepository;
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComidaService - Tests unitarios")
class ComidaServiceTest {

    @Mock
    private ComidaRepository comidaRepository;

    @Mock
    private ComidaIngredienteRepository comidaIngredienteRepository;

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @InjectMocks
    private ComidaService comidaService;

    private Comida comida;
    private ComidaRequest comidaRequest;
    private Ingrediente ingrediente;
    private ComidaIngrediente comidaIngrediente;
    private Etiqueta etiqueta;

    @BeforeEach
    void setUp() {
        etiqueta = new Etiqueta();
        etiqueta.setId(1L);
        etiqueta.setNombre("Saludable");

        comida = new Comida();
        comida.setId(1L);
        comida.setNombre("Ensalada César");
        comida.setTipoComida(Comida.TipoComida.ALMUERZO);
        comida.setDescripcion("Ensalada con pollo");
        comida.setTiempoPreparacionMinutos(20);
        comida.setPorciones(2);
        comida.setInstrucciones("Mezclar todos los ingredientes");
        comida.setEtiquetas(new HashSet<>(Set.of(etiqueta)));

        comidaRequest = new ComidaRequest();
        comidaRequest.setNombre("Ensalada César");
        comidaRequest.setTipoComida(Comida.TipoComida.ALMUERZO);
        comidaRequest.setDescripcion("Ensalada con pollo");
        comidaRequest.setTiempoPreparacionMinutos(20);
        comidaRequest.setPorciones(2);
        comidaRequest.setInstrucciones("Mezclar todos los ingredientes");
        comidaRequest.setEtiquetaIds(Set.of(1L));

        ingrediente = new Ingrediente();
        ingrediente.setId(1L);
        ingrediente.setNombre("Pollo");
        ingrediente.setProteinas(new BigDecimal("31.0"));
        ingrediente.setCarbohidratos(new BigDecimal("0.0"));
        ingrediente.setGrasas(new BigDecimal("3.6"));
        ingrediente.setEnergia(new BigDecimal("165.0"));
        ingrediente.setFibra(new BigDecimal("0.0"));

        ComidaIngredienteId id = new ComidaIngredienteId(1L, 1L);
        comidaIngrediente = new ComidaIngrediente();
        comidaIngrediente.setId(id);
        comidaIngrediente.setComida(comida);
        comidaIngrediente.setIngrediente(ingrediente);
        comidaIngrediente.setCantidadGramos(new BigDecimal("150.0"));
        comidaIngrediente.setNotas("Pechuga sin piel");
    }

    @Test
    @DisplayName("Crear comida - Caso exitoso")
    void crearComida_DebeCrearExitosamente() {
        // Given
        when(comidaRepository.existsByNombre(comidaRequest.getNombre())).thenReturn(false);
        when(etiquetaRepository.findAllById(comidaRequest.getEtiquetaIds())).thenReturn(List.of(etiqueta));
        when(comidaRepository.save(any(Comida.class))).thenReturn(comida);
        when(comidaIngredienteRepository.findByComidaId(1L)).thenReturn(List.of());

        // When
        ComidaResponse response = comidaService.crearComida(comidaRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Ensalada César");
        assertThat(response.getTipoComida()).isEqualTo(Comida.TipoComida.ALMUERZO);
        verify(comidaRepository).existsByNombre("Ensalada César");
        verify(comidaRepository).save(any(Comida.class));
    }

    @Test
    @DisplayName("Crear comida - RN07: Nombre duplicado debe lanzar excepción")
    void crearComida_ConNombreDuplicado_DebeLanzarExcepcion() {
        // Given
        when(comidaRepository.existsByNombre(comidaRequest.getNombre())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> comidaService.crearComida(comidaRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe una comida con el nombre");
        verify(comidaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Agregar ingrediente - Caso exitoso")
    void agregarIngrediente_DebeAgregarExitosamente() {
        // Given
        AgregarIngredienteRequest request = new AgregarIngredienteRequest();
        request.setIngredienteId(1L);
        request.setCantidadGramos(new BigDecimal("150.0"));
        request.setNotas("Pechuga sin piel");

        ComidaIngredienteId id = new ComidaIngredienteId(1L, 1L);

        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(comidaIngredienteRepository.existsById(id)).thenReturn(false);
        when(comidaIngredienteRepository.save(any(ComidaIngrediente.class))).thenReturn(comidaIngrediente);
        when(comidaIngredienteRepository.findByComidaId(1L)).thenReturn(List.of(comidaIngrediente));

        // When
        ComidaResponse response = comidaService.agregarIngrediente(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getIngredientes()).hasSize(1);
        verify(comidaIngredienteRepository).save(any(ComidaIngrediente.class));
    }

    @Test
    @DisplayName("Agregar ingrediente - RN10: Cantidad cero debe lanzar excepción")
    void agregarIngrediente_ConCantidadCero_DebeLanzarExcepcion() {
        // Given
        AgregarIngredienteRequest request = new AgregarIngredienteRequest();
        request.setIngredienteId(1L);
        request.setCantidadGramos(BigDecimal.ZERO);

        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(comidaIngredienteRepository.existsById(any())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> comidaService.agregarIngrediente(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La cantidad debe ser positiva");
        verify(comidaIngredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Agregar ingrediente - Ingrediente duplicado debe lanzar excepción")
    void agregarIngrediente_Duplicado_DebeLanzarExcepcion() {
        // Given
        AgregarIngredienteRequest request = new AgregarIngredienteRequest();
        request.setIngredienteId(1L);
        request.setCantidadGramos(new BigDecimal("150.0"));

        ComidaIngredienteId id = new ComidaIngredienteId(1L, 1L);

        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(comidaIngredienteRepository.existsById(id)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> comidaService.agregarIngrediente(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El ingrediente ya está agregado a esta comida");
        verify(comidaIngredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar ingrediente - Caso exitoso")
    void actualizarIngrediente_DebeActualizarExitosamente() {
        // Given
        AgregarIngredienteRequest request = new AgregarIngredienteRequest();
        request.setCantidadGramos(new BigDecimal("200.0"));
        request.setNotas("Pechuga actualizada");

        ComidaIngredienteId id = new ComidaIngredienteId(1L, 1L);

        when(comidaIngredienteRepository.findById(id)).thenReturn(Optional.of(comidaIngrediente));
        when(comidaIngredienteRepository.save(any(ComidaIngrediente.class))).thenReturn(comidaIngrediente);
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(comidaIngredienteRepository.findByComidaId(1L)).thenReturn(List.of(comidaIngrediente));

        // When
        ComidaResponse response = comidaService.actualizarIngrediente(1L, 1L, request);

        // Then
        assertThat(response).isNotNull();
        verify(comidaIngredienteRepository).save(any(ComidaIngrediente.class));
    }

    @Test
    @DisplayName("Eliminar ingrediente - Caso exitoso")
    void eliminarIngrediente_DebeEliminarExitosamente() {
        // Given
        ComidaIngredienteId id = new ComidaIngredienteId(1L, 1L);

        when(comidaIngredienteRepository.existsById(id)).thenReturn(true);
        when(comidaRepository.findById(1L)).thenReturn(Optional.of(comida));
        when(comidaIngredienteRepository.findByComidaId(1L)).thenReturn(List.of());

        // When
        ComidaResponse response = comidaService.eliminarIngrediente(1L, 1L);

        // Then
        assertThat(response).isNotNull();
        verify(comidaIngredienteRepository).deleteById(id);
    }

    @Test
    @DisplayName("Eliminar ingrediente - No encontrado debe lanzar excepción")
    void eliminarIngrediente_NoEncontrado_DebeLanzarExcepcion() {
        // Given
        ComidaIngredienteId id = new ComidaIngredienteId(1L, 999L);
        when(comidaIngredienteRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> comidaService.eliminarIngrediente(1L, 999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró el ingrediente en esta comida");
        verify(comidaIngredienteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar comida - Caso exitoso")
    void eliminarComida_DebeEliminarExitosamente() {
        // Given
        when(comidaRepository.existsById(1L)).thenReturn(true);

        // When
        comidaService.eliminarComida(1L);

        // Then
        verify(comidaRepository).existsById(1L);
        verify(comidaRepository).deleteById(1L);
    }
}
