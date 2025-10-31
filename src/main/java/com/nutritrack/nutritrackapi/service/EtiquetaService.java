package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.EtiquetaRequest;
import com.nutritrack.nutritrackapi.dto.response.EtiquetaResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceInUseException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.*;
import com.nutritrack.nutritrackapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final EtiquetaPlanRepository etiquetaPlanRepository;
    private final EtiquetaEjercicioRepository etiquetaEjercicioRepository;
    private final EtiquetaIngredienteRepository etiquetaIngredienteRepository;
    private final EtiquetaMetaRepository etiquetaMetaRepository;

    // ==================== CRUD de Etiquetas ====================

    @Transactional
    public EtiquetaResponse crearEtiqueta(EtiquetaRequest request) {
        // RN06: No pueden existir dos etiquetas con el mismo nombre
        if (etiquetaRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe una etiqueta con el nombre: " + request.nombre()
            );
        }

        Etiqueta etiqueta = Etiqueta.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .tipoEtiqueta(request.tipoEtiqueta())
                .build();

        Etiqueta saved = etiquetaRepository.save(etiqueta);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EtiquetaResponse> obtenerTodasEtiquetas() {
        return etiquetaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EtiquetaResponse obtenerEtiquetaPorId(Long id) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Etiqueta no encontrada con ID: " + id
                ));
        return toResponse(etiqueta);
    }

    @Transactional
    public EtiquetaResponse actualizarEtiqueta(Long id, EtiquetaRequest request) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Etiqueta no encontrada con ID: " + id
                ));

        // RN06: Verificar que el nuevo nombre no exista (si cambió)
        if (!etiqueta.getNombre().equals(request.nombre()) &&
                etiquetaRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException(
                    "Ya existe una etiqueta con el nombre: " + request.nombre()
            );
        }

        etiqueta.setNombre(request.nombre());
        etiqueta.setDescripcion(request.descripcion());
        etiqueta.setTipoEtiqueta(request.tipoEtiqueta());

        Etiqueta updated = etiquetaRepository.save(etiqueta);
        return toResponse(updated);
    }

    @Transactional
    public void eliminarEtiqueta(Long id) {
        if (!etiquetaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etiqueta no encontrada con ID: " + id);
        }

        // RN08: Verificar que no esté en uso
        if (etiquetaPlanRepository.existsByIdEtiqueta(id) ||
                etiquetaEjercicioRepository.existsByIdEtiqueta(id) ||
                etiquetaIngredienteRepository.existsByIdEtiqueta(id) ||
                etiquetaMetaRepository.existsByIdEtiqueta(id)) {
            throw new ResourceInUseException(
                    "No se puede eliminar la etiqueta porque está en uso"
            );
        }

        etiquetaRepository.deleteById(id);
    }

    //Asignación a Planes

    @Transactional
    public void asignarEtiquetasAPlan(Long idPlan, List<Long> etiquetasIds) {
        for (Long idEtiqueta : etiquetasIds) {
            // RN12: Validar que la etiqueta exista
            if (!etiquetaRepository.existsById(idEtiqueta)) {
                throw new ResourceNotFoundException(
                        "Etiqueta no encontrada con ID: " + idEtiqueta
                );
            }

            // Evitar duplicados
            if (etiquetaPlanRepository.findByIdCatalogoPlanAndIdEtiqueta(idPlan, idEtiqueta).isEmpty()) {
                EtiquetaPlan etiquetaPlan = EtiquetaPlan.builder()
                        .idCatalogoPlan(idPlan)
                        .idEtiqueta(idEtiqueta)
                        .build();
                etiquetaPlanRepository.save(etiquetaPlan);
            }
        }
    }

    @Transactional
    public void quitarEtiquetaDePlan(Long idPlan, Long idEtiqueta) {
        EtiquetaPlan etiquetaPlan = etiquetaPlanRepository
                .findByIdCatalogoPlanAndIdEtiqueta(idPlan, idEtiqueta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la relación entre el plan " + idPlan +
                                " y la etiqueta " + idEtiqueta
                ));

        etiquetaPlanRepository.delete(etiquetaPlan);
    }

    //Asignación a Ejercicios

    @Transactional
    public void asignarEtiquetasAEjercicio(Long idEjercicio, List<Long> etiquetasIds) {
        for (Long idEtiqueta : etiquetasIds) {
            // RN12: Validar que la etiqueta exista
            if (!etiquetaRepository.existsById(idEtiqueta)) {
                throw new ResourceNotFoundException(
                        "Etiqueta no encontrada con ID: " + idEtiqueta
                );
            }

            // Evitar duplicados
            if (etiquetaEjercicioRepository.findByIdEjercicioAndIdEtiqueta(idEjercicio, idEtiqueta).isEmpty()) {
                EtiquetaEjercicio etiquetaEjercicio = EtiquetaEjercicio.builder()
                        .idEjercicio(idEjercicio)
                        .idEtiqueta(idEtiqueta)
                        .build();
                etiquetaEjercicioRepository.save(etiquetaEjercicio);
            }
        }
    }

    @Transactional
    public void quitarEtiquetaDeEjercicio(Long idEjercicio, Long idEtiqueta) {
        EtiquetaEjercicio etiquetaEjercicio = etiquetaEjercicioRepository
                .findByIdEjercicioAndIdEtiqueta(idEjercicio, idEtiqueta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la relación entre el ejercicio " + idEjercicio +
                                " y la etiqueta " + idEtiqueta
                ));

        etiquetaEjercicioRepository.delete(etiquetaEjercicio);
    }

    //Asignación a Ingredientes

    @Transactional
    public void asignarEtiquetasAIngrediente(Long idIngrediente, List<Long> etiquetasIds) {
        for (Long idEtiqueta : etiquetasIds) {
            // RN12: Validar que la etiqueta exista
            if (!etiquetaRepository.existsById(idEtiqueta)) {
                throw new ResourceNotFoundException(
                        "Etiqueta no encontrada con ID: " + idEtiqueta
                );
            }

            // Evitar duplicados
            if (etiquetaIngredienteRepository.findByIdIngredienteAndIdEtiqueta(idIngrediente, idEtiqueta).isEmpty()) {
                EtiquetaIngrediente etiquetaIngrediente = EtiquetaIngrediente.builder()
                        .idIngrediente(idIngrediente)
                        .idEtiqueta(idEtiqueta)
                        .build();
                etiquetaIngredienteRepository.save(etiquetaIngrediente);
            }
        }
    }

    @Transactional
    public void quitarEtiquetaDeIngrediente(Long idIngrediente, Long idEtiqueta) {
        EtiquetaIngrediente etiquetaIngrediente = etiquetaIngredienteRepository
                .findByIdIngredienteAndIdEtiqueta(idIngrediente, idEtiqueta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la relación entre el ingrediente " + idIngrediente +
                                " y la etiqueta " + idEtiqueta
                ));

        etiquetaIngredienteRepository.delete(etiquetaIngrediente);
    }

    //Asignación a Metas

    @Transactional
    public void asignarEtiquetasAMeta(Long idMeta, List<Long> etiquetasIds) {
        for (Long idEtiqueta : etiquetasIds) {
            // RN12: Validar que la etiqueta exista
            if (!etiquetaRepository.existsById(idEtiqueta)) {
                throw new ResourceNotFoundException(
                        "Etiqueta no encontrada con ID: " + idEtiqueta
                );
            }

            // Evitar duplicados
            if (etiquetaMetaRepository.findByIdCatalogoMetaAndIdEtiqueta(idMeta, idEtiqueta).isEmpty()) {
                EtiquetaMeta etiquetaMeta = EtiquetaMeta.builder()
                        .idCatalogoMeta(idMeta)
                        .idEtiqueta(idEtiqueta)
                        .build();
                etiquetaMetaRepository.save(etiquetaMeta);
            }
        }
    }

    @Transactional
    public void quitarEtiquetaDeMeta(Long idMeta, Long idEtiqueta) {
        EtiquetaMeta etiquetaMeta = etiquetaMetaRepository
                .findByIdCatalogoMetaAndIdEtiqueta(idMeta, idEtiqueta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la relación entre la meta " + idMeta +
                                " y la etiqueta " + idEtiqueta
                ));

        etiquetaMetaRepository.delete(etiquetaMeta);
    }

    //Método helper (conversión)

    private EtiquetaResponse toResponse(Etiqueta etiqueta) {
        return new EtiquetaResponse(
                etiqueta.getId(),
                etiqueta.getNombre(),
                etiqueta.getDescripcion(),
                etiqueta.getTipoEtiqueta(),
                etiqueta.getCreatedAt(),
                etiqueta.getUpdatedAt()
        );
    }
}