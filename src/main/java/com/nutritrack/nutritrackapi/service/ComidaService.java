package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.ComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.ComidaResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Comida;
import com.nutritrack.nutritrackapi.repository.ComidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComidaService {

    private final ComidaRepository comidaRepository;

    @Transactional
    public ComidaResponse crearComida(ComidaRequest request) {
        // Verificar nombre duplicado (similar a ingredientes/ejercicios)
        if (comidaRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe una comida con el nombre: " + request.nombre()
            );
        }

        Comida comida = Comida.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .tipoComida(request.tipoComida())
                .tiempoPreparacion(request.tiempoPreparacion())
                .instruccionesPreparacion(request.instruccionesPreparacion())
                .porciones(request.porciones())
                .imagenUrl(request.imagenUrl())
                .build();

        Comida saved = comidaRepository.save(comida);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ComidaResponse> obtenerTodasComidas() {
        return comidaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComidaResponse obtenerComidaPorId(Long id) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comida no encontrada con ID: " + id
                ));
        return toResponse(comida);
    }

    @Transactional
    public ComidaResponse actualizarComida(Long id, ComidaRequest request) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comida no encontrada con ID: " + id
                ));

        // Verificar que el nuevo nombre no exista (si cambió)
        if (!comida.getNombre().equals(request.nombre()) &&
                comidaRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe una comida con el nombre: " + request.nombre()
            );
        }

        comida.setNombre(request.nombre());
        comida.setDescripcion(request.descripcion());
        comida.setTipoComida(request.tipoComida());
        comida.setTiempoPreparacion(request.tiempoPreparacion());
        comida.setInstruccionesPreparacion(request.instruccionesPreparacion());
        comida.setPorciones(request.porciones());
        comida.setImagenUrl(request.imagenUrl());

        Comida updated = comidaRepository.save(comida);
        return toResponse(updated);
    }

    @Transactional
    public void eliminarComida(Long id) {
        if (!comidaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + id);
        }

        // Las recetas se eliminan en cascada gracias a orphanRemoval = true
        comidaRepository.deleteById(id);
    }

    private ComidaResponse toResponse(Comida comida) {
        return new ComidaResponse(
                comida.getId(),
                comida.getNombre(),
                comida.getDescripcion(),
                comida.getTipoComida(),
                comida.getTiempoPreparacion(),
                comida.getInstruccionesPreparacion(),
                comida.getPorciones(),
                comida.getImagenUrl(),
                comida.getCreatedAt(),
                comida.getUpdatedAt()
        );
    }
}