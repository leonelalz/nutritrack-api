package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CrearEjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EjercicioResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Ejercicio;
import com.nutritrack.nutritrackapi.model.Etiqueta;
import com.nutritrack.nutritrackapi.model.enums.Dificultad;
import com.nutritrack.nutritrackapi.model.enums.MusculoPrincipal;
import com.nutritrack.nutritrackapi.model.enums.TipoEjercicio;
import com.nutritrack.nutritrackapi.repository.EjercicioRepository;
import com.nutritrack.nutritrackapi.repository.EtiquetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EjercicioService {
    
    private final EjercicioRepository ejercicioRepository;
    private final EtiquetaRepository etiquetaRepository;
    
    public EjercicioResponse crear(CrearEjercicioRequest request) {
        if (ejercicioRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe un ejercicio con el nombre: " + request.nombre());
        }
        
        Ejercicio ejercicio = Ejercicio.builder()
                .nombre(request.nombre())
                .tipoEjercicio(request.tipoEjercicio())
                .musculoPrincipal(request.musculoPrincipal())
                .duracion(request.duracion())
                .dificultad(request.dificultad())
                .caloriasEstimadas(request.caloriasEstimadas())
                .build();
        
        if (request.etiquetasIds() != null && !request.etiquetasIds().isEmpty()) {
            Set<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.etiquetasIds())
                    .stream()
                    .collect(Collectors.toSet());
            ejercicio.setEtiquetas(etiquetas);
        }
        
        Ejercicio guardado = ejercicioRepository.save(ejercicio);
        return convertirAResponse(guardado);
    }
    
    public EjercicioResponse actualizar(Long id, CrearEjercicioRequest request) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + id));
        
        if (!ejercicio.getNombre().equalsIgnoreCase(request.nombre()) &&
            ejercicioRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe un ejercicio con el nombre: " + request.nombre());
        }
        
        ejercicio.setNombre(request.nombre());
        ejercicio.setTipoEjercicio(request.tipoEjercicio());
        ejercicio.setMusculoPrincipal(request.musculoPrincipal());
        ejercicio.setDuracion(request.duracion());
        ejercicio.setDificultad(request.dificultad());
        ejercicio.setCaloriasEstimadas(request.caloriasEstimadas());
        
        if (request.etiquetasIds() != null) {
            Set<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.etiquetasIds())
                    .stream()
                    .collect(Collectors.toSet());
            ejercicio.setEtiquetas(etiquetas);
        }
        
        Ejercicio actualizado = ejercicioRepository.save(ejercicio);
        return convertirAResponse(actualizado);
    }
    
    public void eliminar(Long id) {
        if (!ejercicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ejercicio no encontrado con ID: " + id);
        }
        ejercicioRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public EjercicioResponse buscarPorId(Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + id));
        return convertirAResponse(ejercicio);
    }
    
    @Transactional(readOnly = true)
    public List<EjercicioResponse> buscarPorNombre(String nombre) {
        return ejercicioRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EjercicioResponse> buscarPorTipo(TipoEjercicio tipo) {
        return ejercicioRepository.findByTipoEjercicio(tipo)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EjercicioResponse> buscarPorMusculo(MusculoPrincipal musculo) {
        return ejercicioRepository.findByMusculoPrincipal(musculo)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EjercicioResponse> buscarPorDificultad(Dificultad dificultad) {
        return ejercicioRepository.findByDificultad(dificultad)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarTodos() {
        return ejercicioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    public EjercicioResponse agregarEtiqueta(Long ejercicioId, Long etiquetaId) {
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + ejercicioId));
        
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));
        
        ejercicio.getEtiquetas().add(etiqueta);
        Ejercicio actualizado = ejercicioRepository.save(ejercicio);
        return convertirAResponse(actualizado);
    }
    
    public EjercicioResponse removerEtiqueta(Long ejercicioId, Long etiquetaId) {
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado con ID: " + ejercicioId));
        
        ejercicio.getEtiquetas().removeIf(e -> e.getId().equals(etiquetaId));
        Ejercicio actualizado = ejercicioRepository.save(ejercicio);
        return convertirAResponse(actualizado);
    }
    
    private EjercicioResponse convertirAResponse(Ejercicio ejercicio) {
        Set<EjercicioResponse.EtiquetaSimpleResponse> etiquetas = ejercicio.getEtiquetas()
                .stream()
                .map(e -> EjercicioResponse.EtiquetaSimpleResponse.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .build())
                .collect(Collectors.toSet());
        
        return EjercicioResponse.builder()
                .id(ejercicio.getId())
                .nombre(ejercicio.getNombre())
                .tipoEjercicio(ejercicio.getTipoEjercicio())
                .musculoPrincipal(ejercicio.getMusculoPrincipal())
                .duracion(ejercicio.getDuracion())
                .dificultad(ejercicio.getDificultad())
                .caloriasEstimadas(ejercicio.getCaloriasEstimadas())
                .etiquetas(etiquetas)
                .createdAt(ejercicio.getCreatedAt())
                .updatedAt(ejercicio.getUpdatedAt())
                .build();
    }
}
