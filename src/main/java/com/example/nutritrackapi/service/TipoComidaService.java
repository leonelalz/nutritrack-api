package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.TipoComidaRequest;
import com.example.nutritrackapi.dto.TipoComidaResponse;
import com.example.nutritrackapi.model.TipoComidaEntity;
import com.example.nutritrackapi.repository.TipoComidaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de tipos de comida.
 * Permite agregar, modificar y eliminar tipos de comida dinámicamente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoComidaService {

    private final TipoComidaRepository tipoComidaRepository;

    /**
     * Crea un nuevo tipo de comida.
     * Valida que el nombre sea único.
     */
    @Transactional
    public TipoComidaResponse crearTipoComida(TipoComidaRequest request) {
        // Validar nombre único
        if (tipoComidaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un tipo de comida con el nombre: " + request.getNombre()
            );
        }

        TipoComidaEntity entity = TipoComidaEntity.builder()
                .nombre(request.getNombre().toUpperCase().trim())
                .descripcion(request.getDescripcion())
                .ordenVisualizacion(request.getOrdenVisualizacion() != null ? request.getOrdenVisualizacion() : 0)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        TipoComidaEntity guardado = tipoComidaRepository.save(entity);
        log.info("Tipo de comida creado: {}", guardado.getNombre());
        return TipoComidaResponse.fromEntity(guardado);
    }

    /**
     * Obtiene un tipo de comida por su ID.
     */
    public TipoComidaResponse obtenerTipoComidaPorId(Long id) {
        TipoComidaEntity entity = tipoComidaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de comida no encontrado con ID: " + id
            ));
        return TipoComidaResponse.fromEntity(entity);
    }

    /**
     * Obtiene un tipo de comida por su nombre.
     */
    public TipoComidaResponse obtenerTipoComidaPorNombre(String nombre) {
        TipoComidaEntity entity = tipoComidaRepository.findByNombreIgnoreCase(nombre)
            .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de comida no encontrado con nombre: " + nombre
            ));
        return TipoComidaResponse.fromEntity(entity);
    }

    /**
     * Lista todos los tipos de comida activos ordenados.
     */
    public List<TipoComidaResponse> listarTiposComidaActivos() {
        return tipoComidaRepository.findAllActiveOrdered()
            .stream()
            .map(TipoComidaResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Lista todos los tipos de comida con paginación.
     */
    public Page<TipoComidaResponse> listarTiposComida(Pageable pageable) {
        return tipoComidaRepository.findAll(pageable)
            .map(TipoComidaResponse::fromEntity);
    }

    /**
     * Lista todos los tipos de comida ordenados (incluye inactivos).
     */
    public List<TipoComidaResponse> listarTodosTiposComida() {
        return tipoComidaRepository.findAllByOrderByOrdenVisualizacionAscNombreAsc()
            .stream()
            .map(TipoComidaResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Actualiza un tipo de comida existente.
     * Valida que el nuevo nombre sea único (excluyendo el actual).
     */
    @Transactional
    public TipoComidaResponse actualizarTipoComida(Long id, TipoComidaRequest request) {
        TipoComidaEntity entity = tipoComidaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de comida no encontrado con ID: " + id
            ));

        // Validar nombre único (excluyendo el actual)
        tipoComidaRepository.findByNombreIgnoreCase(request.getNombre())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException(
                        "Ya existe otro tipo de comida con el nombre: " + request.getNombre()
                    );
                }
            });

        entity.setNombre(request.getNombre().toUpperCase().trim());
        entity.setDescripcion(request.getDescripcion());
        
        if (request.getOrdenVisualizacion() != null) {
            entity.setOrdenVisualizacion(request.getOrdenVisualizacion());
        }
        
        if (request.getActivo() != null) {
            entity.setActivo(request.getActivo());
        }

        TipoComidaEntity actualizado = tipoComidaRepository.save(entity);
        log.info("Tipo de comida actualizado: {}", actualizado.getNombre());
        return TipoComidaResponse.fromEntity(actualizado);
    }

    /**
     * Activa o desactiva un tipo de comida (soft delete).
     */
    @Transactional
    public TipoComidaResponse cambiarEstado(Long id, boolean activo) {
        TipoComidaEntity entity = tipoComidaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de comida no encontrado con ID: " + id
            ));

        entity.setActivo(activo);
        TipoComidaEntity actualizado = tipoComidaRepository.save(entity);
        log.info("Tipo de comida {} {}", actualizado.getNombre(), activo ? "activado" : "desactivado");
        return TipoComidaResponse.fromEntity(actualizado);
    }

    /**
     * Elimina permanentemente un tipo de comida.
     * ADVERTENCIA: Solo debe usarse si no hay comidas/planes asociados.
     */
    @Transactional
    public void eliminarTipoComida(Long id) {
        if (!tipoComidaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tipo de comida no encontrado con ID: " + id);
        }

        // TODO: Agregar validación para verificar si hay comidas/planes usando este tipo
        // Por ahora, soft delete es preferible
        
        tipoComidaRepository.deleteById(id);
        log.info("Tipo de comida eliminado con ID: {}", id);
    }

    /**
     * Busca tipos de comida por nombre.
     */
    public List<TipoComidaResponse> buscarPorNombre(String nombre) {
        return tipoComidaRepository.findByNombreContainingIgnoreCase(nombre)
            .stream()
            .map(TipoComidaResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene la entidad por ID (para uso interno).
     */
    public TipoComidaEntity obtenerEntidadPorId(Long id) {
        return tipoComidaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de comida no encontrado con ID: " + id
            ));
    }

    /**
     * Obtiene la entidad por nombre (para uso interno).
     */
    public TipoComidaEntity obtenerEntidadPorNombre(String nombre) {
        return tipoComidaRepository.findByNombreIgnoreCase(nombre)
            .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de comida no encontrado con nombre: " + nombre
            ));
    }
}
