package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CrearIngredienteRequest;
import com.nutritrack.nutritrackapi.dto.response.IngredienteResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Etiqueta;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.model.enums.GrupoAlimenticio;
import com.nutritrack.nutritrackapi.repository.EtiquetaRepository;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredienteService {
    
    private final IngredienteRepository ingredienteRepository;
    private final EtiquetaRepository etiquetaRepository;
    
    public IngredienteResponse crear(CrearIngredienteRequest request) {
        if (ingredienteRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe un ingrediente con el nombre: " + request.nombre());
        }
        
        Ingrediente ingrediente = Ingrediente.builder()
                .nombre(request.nombre())
                .grupoAlimenticio(request.grupoAlimenticio())
                .energia(request.energia())
                .proteinas(request.proteinas())
                .grasas(request.grasas())
                .carbohidratos(request.carbohidratos())
                .build();
        
        if (request.etiquetasIds() != null && !request.etiquetasIds().isEmpty()) {
            Set<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.etiquetasIds())
                    .stream()
                    .collect(Collectors.toSet());
            ingrediente.setEtiquetas(etiquetas);
        }
        
        Ingrediente guardado = ingredienteRepository.save(ingrediente);
        return convertirAResponse(guardado);
    }
    
    public IngredienteResponse actualizar(Long id, CrearIngredienteRequest request) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id));
        
        if (!ingrediente.getNombre().equalsIgnoreCase(request.nombre()) &&
            ingredienteRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe un ingrediente con el nombre: " + request.nombre());
        }
        
        ingrediente.setNombre(request.nombre());
        ingrediente.setGrupoAlimenticio(request.grupoAlimenticio());
        ingrediente.setEnergia(request.energia());
        ingrediente.setProteinas(request.proteinas());
        ingrediente.setGrasas(request.grasas());
        ingrediente.setCarbohidratos(request.carbohidratos());
        
        if (request.etiquetasIds() != null) {
            Set<Etiqueta> etiquetas = etiquetaRepository.findAllById(request.etiquetasIds())
                    .stream()
                    .collect(Collectors.toSet());
            ingrediente.setEtiquetas(etiquetas);
        }
        
        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        return convertirAResponse(actualizado);
    }
    
    public void eliminar(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id);
        }
        ingredienteRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public IngredienteResponse buscarPorId(Long id) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id));
        return convertirAResponse(ingrediente);
    }
    
    @Transactional(readOnly = true)
    public List<IngredienteResponse> buscarPorNombre(String nombre) {
        return ingredienteRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<IngredienteResponse> buscarPorGrupo(GrupoAlimenticio grupo) {
        return ingredienteRepository.findByGrupoAlimenticio(grupo)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<IngredienteResponse> listarTodos() {
        return ingredienteRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    public IngredienteResponse agregarEtiqueta(Long ingredienteId, Long etiquetaId) {
        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + ingredienteId));
        
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + etiquetaId));
        
        ingrediente.getEtiquetas().add(etiqueta);
        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        return convertirAResponse(actualizado);
    }
    
    public IngredienteResponse removerEtiqueta(Long ingredienteId, Long etiquetaId) {
        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + ingredienteId));
        
        ingrediente.getEtiquetas().removeIf(e -> e.getId().equals(etiquetaId));
        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        return convertirAResponse(actualizado);
    }
    
    private IngredienteResponse convertirAResponse(Ingrediente ingrediente) {
        Set<IngredienteResponse.EtiquetaSimpleResponse> etiquetas = ingrediente.getEtiquetas()
                .stream()
                .map(e -> IngredienteResponse.EtiquetaSimpleResponse.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .build())
                .collect(Collectors.toSet());
        
        return IngredienteResponse.builder()
                .id(ingrediente.getId())
                .nombre(ingrediente.getNombre())
                .grupoAlimenticio(ingrediente.getGrupoAlimenticio())
                .energia(ingrediente.getEnergia())
                .proteinas(ingrediente.getProteinas())
                .grasas(ingrediente.getGrasas())
                .carbohidratos(ingrediente.getCarbohidratos())
                .etiquetas(etiquetas)
                .createdAt(ingrediente.getCreatedAt())
                .updatedAt(ingrediente.getUpdatedAt())
                .build();
    }
}
