package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.IngredienteRequest;
import com.example.nutritrackapi.dto.IngredienteResponse;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.model.Ingrediente;
import com.example.nutritrackapi.repository.EtiquetaRepository;
import com.example.nutritrackapi.repository.IngredienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio para gestión de ingredientes.
 * Implementa US-07: Gestionar Ingredientes
 * RN07: Nombre único de ingrediente
 * RN09: No eliminar ingredientes en uso en recetas
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final EtiquetaRepository etiquetaRepository;

    /**
     * Crea un nuevo ingrediente.
     * RN07: Valida que el nombre sea único.
     */
    @Transactional
    public IngredienteResponse crearIngrediente(IngredienteRequest request) {
        // RN07: Validar nombre único
        if (ingredienteRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un ingrediente con el nombre: " + request.getNombre()
            );
        }

        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(request.getNombre());
        ingrediente.setProteinas(request.getProteinas());
        ingrediente.setCarbohidratos(request.getCarbohidratos());
        ingrediente.setGrasas(request.getGrasas());
        ingrediente.setEnergia(request.getEnergia());
        ingrediente.setFibra(request.getFibra());
        ingrediente.setCategoriaAlimento(request.getCategoriaAlimento());
        ingrediente.setDescripcion(request.getDescripcion());

        // Asociar etiquetas
        if (request.getEtiquetaIds() != null && !request.getEtiquetaIds().isEmpty()) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            ingrediente.setEtiquetas(etiquetas);
        }

        Ingrediente guardado = ingredienteRepository.save(ingrediente);
        return IngredienteResponse.fromEntity(guardado);
    }

    /**
     * Obtiene un ingrediente por su ID.
     */
    public IngredienteResponse obtenerIngredientePorId(Long id) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ingrediente no encontrado con ID: " + id
            ));
        return IngredienteResponse.fromEntity(ingrediente);
    }

    /**
     * Lista todos los ingredientes con paginación.
     */
    public Page<IngredienteResponse> listarIngredientes(Pageable pageable) {
        return ingredienteRepository.findAll(pageable)
            .map(IngredienteResponse::fromEntity);
    }

    /**
     * Busca ingredientes por nombre (parcial, case-insensitive).
     */
    public Page<IngredienteResponse> buscarPorNombre(String nombre, Pageable pageable) {
        return ingredienteRepository.findByNombreContainingIgnoreCase(nombre, pageable)
            .map(IngredienteResponse::fromEntity);
    }

    /**
     * Filtra ingredientes por categoría.
     */
    public Page<IngredienteResponse> filtrarPorCategoria(
        Ingrediente.CategoriaAlimento categoria, 
        Pageable pageable
    ) {
        return ingredienteRepository.findByCategoriaAlimento(categoria, pageable)
            .map(IngredienteResponse::fromEntity);
    }

    /**
     * Actualiza un ingrediente existente.
     * RN07: Valida que el nuevo nombre sea único (excluyendo el ingrediente actual).
     */
    @Transactional
    public IngredienteResponse actualizarIngrediente(Long id, IngredienteRequest request) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ingrediente no encontrado con ID: " + id
            ));

        // RN07: Validar nombre único (excluyendo el ingrediente actual)
        if (ingredienteRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new IllegalArgumentException(
                "Ya existe otro ingrediente con el nombre: " + request.getNombre()
            );
        }

        ingrediente.setNombre(request.getNombre());
        ingrediente.setProteinas(request.getProteinas());
        ingrediente.setCarbohidratos(request.getCarbohidratos());
        ingrediente.setGrasas(request.getGrasas());
        ingrediente.setEnergia(request.getEnergia());
        ingrediente.setFibra(request.getFibra());
        ingrediente.setCategoriaAlimento(request.getCategoriaAlimento());
        ingrediente.setDescripcion(request.getDescripcion());

        // Actualizar etiquetas
        if (request.getEtiquetaIds() != null) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            ingrediente.setEtiquetas(etiquetas);
        } else {
            ingrediente.getEtiquetas().clear();
        }

        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        return IngredienteResponse.fromEntity(actualizado);
    }

    /**
     * Elimina un ingrediente.
     * RN09: No permite eliminar ingredientes que estén en uso en recetas.
     */
    @Transactional
    public void eliminarIngrediente(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Ingrediente no encontrado con ID: " + id);
        }

        // RN09: Validar que el ingrediente no esté en uso
        if (ingredienteRepository.estaEnUsoEnRecetas(id)) {
            throw new IllegalStateException(
                "No se puede eliminar el ingrediente porque está en uso en una o más recetas"
            );
        }

        ingredienteRepository.deleteById(id);
    }
}
