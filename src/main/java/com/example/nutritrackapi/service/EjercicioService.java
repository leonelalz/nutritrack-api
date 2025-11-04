package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.EjercicioRequest;
import com.example.nutritrackapi.dto.EjercicioResponse;
import com.example.nutritrackapi.model.Ejercicio;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.repository.EjercicioRepository;
import com.example.nutritrackapi.repository.EtiquetaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio para gestión de ejercicios.
 * Implementa US-08: Gestionar Ejercicios
 * RN07: Nombre único de ejercicio
 * RN09: No eliminar ejercicios en uso en rutinas (pendiente)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final EtiquetaRepository etiquetaRepository;

    /**
     * Crea un nuevo ejercicio.
     * RN07: Valida que el nombre sea único.
     */
    @Transactional
    public EjercicioResponse crearEjercicio(EjercicioRequest request) {
        // RN07: Validar nombre único
        if (ejercicioRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un ejercicio con el nombre: " + request.getNombre()
            );
        }

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setNombre(request.getNombre());
        ejercicio.setDescripcion(request.getDescripcion());
        ejercicio.setTipoEjercicio(request.getTipoEjercicio());
        ejercicio.setGrupoMuscular(request.getGrupoMuscular());
        ejercicio.setNivelDificultad(request.getNivelDificultad());
        ejercicio.setCaloriasQuemadasPorMinuto(request.getCaloriasQuemadasPorMinuto());
        ejercicio.setDuracionEstimadaMinutos(request.getDuracionEstimadaMinutos());
        ejercicio.setEquipoNecesario(request.getEquipoNecesario());

        // Asociar etiquetas
        if (request.getEtiquetaIds() != null && !request.getEtiquetaIds().isEmpty()) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            ejercicio.setEtiquetas(etiquetas);
        }

        Ejercicio guardado = ejercicioRepository.save(ejercicio);
        return EjercicioResponse.fromEntity(guardado);
    }

    /**
     * Obtiene un ejercicio por su ID.
     */
    public EjercicioResponse obtenerEjercicioPorId(Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ejercicio no encontrado con ID: " + id
            ));
        return EjercicioResponse.fromEntity(ejercicio);
    }

    /**
     * Lista todos los ejercicios con paginación.
     */
    public Page<EjercicioResponse> listarEjercicios(Pageable pageable) {
        return ejercicioRepository.findAll(pageable)
            .map(EjercicioResponse::fromEntity);
    }

    /**
     * Busca ejercicios por nombre (parcial, case-insensitive).
     */
    public Page<EjercicioResponse> buscarPorNombre(String nombre, Pageable pageable) {
        return ejercicioRepository.findByNombreContainingIgnoreCase(nombre, pageable)
            .map(EjercicioResponse::fromEntity);
    }

    /**
     * Filtra ejercicios por tipo.
     */
    public Page<EjercicioResponse> filtrarPorTipo(
        Ejercicio.TipoEjercicio tipo, 
        Pageable pageable
    ) {
        return ejercicioRepository.findByTipoEjercicio(tipo, pageable)
            .map(EjercicioResponse::fromEntity);
    }

    /**
     * Filtra ejercicios por grupo muscular.
     */
    public Page<EjercicioResponse> filtrarPorGrupoMuscular(
        Ejercicio.GrupoMuscular grupo, 
        Pageable pageable
    ) {
        return ejercicioRepository.findByGrupoMuscular(grupo, pageable)
            .map(EjercicioResponse::fromEntity);
    }

    /**
     * Filtra ejercicios por nivel de dificultad.
     */
    public Page<EjercicioResponse> filtrarPorNivel(
        Ejercicio.NivelDificultad nivel, 
        Pageable pageable
    ) {
        return ejercicioRepository.findByNivelDificultad(nivel, pageable)
            .map(EjercicioResponse::fromEntity);
    }

    /**
     * Filtra ejercicios por múltiples criterios.
     */
    public Page<EjercicioResponse> filtrarEjercicios(
        Ejercicio.TipoEjercicio tipo,
        Ejercicio.GrupoMuscular grupo,
        Ejercicio.NivelDificultad nivel,
        Pageable pageable
    ) {
        return ejercicioRepository.findByFiltros(tipo, grupo, nivel, pageable)
            .map(EjercicioResponse::fromEntity);
    }

    /**
     * Actualiza un ejercicio existente.
     * RN07: Valida que el nuevo nombre sea único (excluyendo el ejercicio actual).
     */
    @Transactional
    public EjercicioResponse actualizarEjercicio(Long id, EjercicioRequest request) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ejercicio no encontrado con ID: " + id
            ));

        // RN07: Validar nombre único (excluyendo el ejercicio actual)
        if (ejercicioRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new IllegalArgumentException(
                "Ya existe otro ejercicio con el nombre: " + request.getNombre()
            );
        }

        ejercicio.setNombre(request.getNombre());
        ejercicio.setDescripcion(request.getDescripcion());
        ejercicio.setTipoEjercicio(request.getTipoEjercicio());
        ejercicio.setGrupoMuscular(request.getGrupoMuscular());
        ejercicio.setNivelDificultad(request.getNivelDificultad());
        ejercicio.setCaloriasQuemadasPorMinuto(request.getCaloriasQuemadasPorMinuto());
        ejercicio.setDuracionEstimadaMinutos(request.getDuracionEstimadaMinutos());
        ejercicio.setEquipoNecesario(request.getEquipoNecesario());

        // Actualizar etiquetas
        if (request.getEtiquetaIds() != null) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            ejercicio.setEtiquetas(etiquetas);
        } else {
            ejercicio.getEtiquetas().clear();
        }

        Ejercicio actualizado = ejercicioRepository.save(ejercicio);
        return EjercicioResponse.fromEntity(actualizado);
    }

    /**
     * Elimina un ejercicio.
     * RN09: No permite eliminar ejercicios que estén en uso en rutinas.
     * Nota: Validación pendiente hasta implementar entidad RutinaEjercicio.
     */
    @Transactional
    public void eliminarEjercicio(Long id) {
        if (!ejercicioRepository.existsById(id)) {
            throw new EntityNotFoundException("Ejercicio no encontrado con ID: " + id);
        }

        // RN09: Validar que el ejercicio no esté en uso (pendiente)
        // TODO: Implementar cuando se cree la entidad RutinaEjercicio
        // if (ejercicioRepository.estaEnUsoEnRutinas(id)) {
        //     throw new IllegalStateException(
        //         "No se puede eliminar el ejercicio porque está en uso en una o más rutinas"
        //     );
        // }

        ejercicioRepository.deleteById(id);
    }
}
