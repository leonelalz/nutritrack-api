package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.EjercicioRequest;
import com.nutritrack.nutritrackapi.dto.response.EjercicioResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Ejercicio;
import com.nutritrack.nutritrackapi.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    @Transactional
    public EjercicioResponse crearEjercicio(EjercicioRequest request) {
        // RN07: No pueden existir dos ejercicios con el mismo nombre
        if (ejercicioRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe un ejercicio con el nombre: " + request.nombre()
            );
        }

        Ejercicio ejercicio = Ejercicio.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .instrucciones(request.instrucciones())
                .duracionMinutos(request.duracionMinutos())
                .caloriasQuemadas(request.caloriasQuemadas())
                .videoUrl(request.videoUrl())
                .build();

        Ejercicio saved = ejercicioRepository.save(ejercicio);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EjercicioResponse> obtenerTodosEjercicios() {
        return ejercicioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EjercicioResponse obtenerEjercicioPorId(Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ejercicio no encontrado con ID: " + id
                ));
        return toResponse(ejercicio);
    }

    @Transactional
    public EjercicioResponse actualizarEjercicio(Long id, EjercicioRequest request) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ejercicio no encontrado con ID: " + id
                ));

        // RN07: Verificar que el nuevo nombre no exista (si cambió)
        if (!ejercicio.getNombre().equals(request.nombre()) &&
                ejercicioRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe un ejercicio con el nombre: " + request.nombre()
            );
        }

        ejercicio.setNombre(request.nombre());
        ejercicio.setDescripcion(request.descripcion());
        ejercicio.setInstrucciones(request.instrucciones());
        ejercicio.setDuracionMinutos(request.duracionMinutos());
        ejercicio.setCaloriasQuemadas(request.caloriasQuemadas());
        ejercicio.setVideoUrl(request.videoUrl());

        Ejercicio updated = ejercicioRepository.save(ejercicio);
        return toResponse(updated);
    }

    @Transactional
    public void eliminarEjercicio(Long id) {
        if (!ejercicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ejercicio no encontrado con ID: " + id);
        }
        // cuando alguien implemente CatalogoRutinas, agregar verificación JEJE
        //Descomentar cuando Lu tenga CatalogoRutinas
        /*
        if (catalogoRutinaRepository.existsByEjercicioId(id)) {
            throw new ResourceInUseException(
                    "No se puede eliminar el ejercicio porque está en uso en rutinas"
            );
        }
        */
        ejercicioRepository.deleteById(id);
    }

    private EjercicioResponse toResponse(Ejercicio ejercicio) {
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getDescripcion(),
                ejercicio.getInstrucciones(),
                ejercicio.getDuracionMinutos(),
                ejercicio.getCaloriasQuemadas(),
                ejercicio.getVideoUrl(),
                ejercicio.getCreatedAt(),
                ejercicio.getUpdatedAt()
        );
    }
}