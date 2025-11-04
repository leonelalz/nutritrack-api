package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.EtiquetaRequest;
import com.example.nutritrackapi.dto.EtiquetaResponse;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.repository.EtiquetaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de etiquetas del sistema.
 * Implementa US-06: Gestionar Etiquetas
 * RN06: Nombre único de etiqueta
 * RN08: No eliminar etiquetas en uso
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;

    /**
     * Crea una nueva etiqueta.
     * RN06: Valida que el nombre sea único.
     */
    @Transactional
    public EtiquetaResponse crearEtiqueta(EtiquetaRequest request) {
        // RN06: Validar nombre único
        if (etiquetaRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe una etiqueta con el nombre: " + request.getNombre()
            );
        }

        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre(request.getNombre());
        etiqueta.setTipoEtiqueta(request.getTipoEtiqueta());
        etiqueta.setDescripcion(request.getDescripcion());

        Etiqueta guardada = etiquetaRepository.save(etiqueta);
        return EtiquetaResponse.fromEntity(guardada);
    }

    /**
     * Obtiene una etiqueta por su ID.
     */
    public EtiquetaResponse obtenerEtiquetaPorId(Long id) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Etiqueta no encontrada con ID: " + id
            ));
        return EtiquetaResponse.fromEntity(etiqueta);
    }

    /**
     * Lista todas las etiquetas con paginación.
     */
    public Page<EtiquetaResponse> listarEtiquetas(Pageable pageable) {
        return etiquetaRepository.findAll(pageable)
            .map(EtiquetaResponse::fromEntity);
    }

    /**
     * Busca etiquetas por nombre (parcial, case-insensitive).
     */
    public Page<EtiquetaResponse> buscarPorNombre(String nombre, Pageable pageable) {
        return etiquetaRepository.findByNombreContainingIgnoreCase(nombre, pageable)
            .map(EtiquetaResponse::fromEntity);
    }

    /**
     * Actualiza una etiqueta existente.
     * RN06: Valida que el nuevo nombre sea único (excluyendo la etiqueta actual).
     */
    @Transactional
    public EtiquetaResponse actualizarEtiqueta(Long id, EtiquetaRequest request) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Etiqueta no encontrada con ID: " + id
            ));

        // RN06: Validar nombre único (excluyendo la etiqueta actual)
        if (etiquetaRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new IllegalArgumentException(
                "Ya existe otra etiqueta con el nombre: " + request.getNombre()
            );
        }

        etiqueta.setNombre(request.getNombre());
        etiqueta.setTipoEtiqueta(request.getTipoEtiqueta());
        etiqueta.setDescripcion(request.getDescripcion());

        Etiqueta actualizada = etiquetaRepository.save(etiqueta);
        return EtiquetaResponse.fromEntity(actualizada);
    }

    /**
     * Elimina una etiqueta.
     * RN08: No permite eliminar etiquetas que estén en uso.
     */
    @Transactional
    public void eliminarEtiqueta(Long id) {
        if (!etiquetaRepository.existsById(id)) {
            throw new EntityNotFoundException("Etiqueta no encontrada con ID: " + id);
        }

        // RN08: Validar que la etiqueta no esté en uso
        if (etiquetaRepository.estaEnUsoEnIngredientes(id)) {
            throw new IllegalStateException(
                "No se puede eliminar la etiqueta porque está en uso en ingredientes"
            );
        }
        if (etiquetaRepository.estaEnUsoEnEjercicios(id)) {
            throw new IllegalStateException(
                "No se puede eliminar la etiqueta porque está en uso en ejercicios"
            );
        }
        if (etiquetaRepository.estaEnUsoEnComidas(id)) {
            throw new IllegalStateException(
                "No se puede eliminar la etiqueta porque está en uso en comidas"
            );
        }
        if (etiquetaRepository.isEtiquetaEnUso(id)) {
            throw new IllegalStateException(
                "No se puede eliminar la etiqueta porque está en uso en perfiles de usuario"
            );
        }

        etiquetaRepository.deleteById(id);
    }
}
