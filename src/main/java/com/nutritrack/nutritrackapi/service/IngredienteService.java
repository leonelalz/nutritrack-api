package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.IngredienteRequest;
import com.nutritrack.nutritrackapi.dto.response.IngredienteResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceInUseException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import com.nutritrack.nutritrackapi.repository.EtiquetaIngredienteRepository;
import com.nutritrack.nutritrackapi.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final EtiquetaIngredienteRepository etiquetaIngredienteRepository;
    private final RecetaRepository recetaRepository;

    @Transactional
    public IngredienteResponse crearIngrediente(IngredienteRequest request) {
        // RN07: No pueden existir dos ingredientes con el mismo nombre
        if (ingredienteRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe un ingrediente con el nombre: " + request.nombre()
            );
        }

        Ingrediente ingrediente = Ingrediente.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .energia(request.energia())
                .proteinas(request.proteinas())
                .grasas(request.grasas())
                .carbohidratos(request.carbohidratos())
                .grupoAlimenticio(request.grupoAlimenticio())
                .build();

        Ingrediente saved = ingredienteRepository.save(ingrediente);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IngredienteResponse> obtenerTodosIngredientes() {
        return ingredienteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IngredienteResponse obtenerIngredientePorId(Long id) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingrediente no encontrado con ID: " + id
                ));
        return toResponse(ingrediente);
    }

    @Transactional
    public IngredienteResponse actualizarIngrediente(Long id, IngredienteRequest request) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingrediente no encontrado con ID: " + id
                ));

        // RN07: Verificar que el nuevo nombre no exista (si cambió)
        if (!ingrediente.getNombre().equals(request.nombre()) &&
                ingredienteRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe un ingrediente con el nombre: " + request.nombre()
            );
        }

        ingrediente.setNombre(request.nombre());
        ingrediente.setDescripcion(request.descripcion());
        ingrediente.setEnergia(request.energia());
        ingrediente.setProteinas(request.proteinas());
        ingrediente.setGrasas(request.grasas());
        ingrediente.setCarbohidratos(request.carbohidratos());
        ingrediente.setGrupoAlimenticio(request.grupoAlimenticio());

        Ingrediente updated = ingredienteRepository.save(ingrediente);
        return toResponse(updated);
    }

    @Transactional
    public void eliminarIngrediente(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id);
        }
        if (recetaRepository.existsByIngredienteId(id)) {
            throw new ResourceInUseException("No se puede eliminar el ingrediente porque está en uso en recetas");
        }
        // }

        ingredienteRepository.deleteById(id);
    }

    private IngredienteResponse toResponse(Ingrediente ingrediente) {
        return new IngredienteResponse(
                ingrediente.getId(),
                ingrediente.getNombre(),
                ingrediente.getDescripcion(),
                ingrediente.getEnergia(),
                ingrediente.getProteinas(),
                ingrediente.getGrasas(),
                ingrediente.getCarbohidratos(),
                ingrediente.getGrupoAlimenticio(),
                ingrediente.getCreatedAt(),
                ingrediente.getUpdatedAt()
        );
    }
}