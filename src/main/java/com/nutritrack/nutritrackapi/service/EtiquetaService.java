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


    private final EjercicioRepository ejercicioRepository;
    private final IngredienteRepository ingredienteRepository;
    //private final CatalogoPlanRepository catalogoPlanRepository; // Cuando Víctor lo tenga funcionara tengo fe
    //private final CatalogoMetaRepository catalogoMetaRepository; // Cuando Jhamil lo tenga funcionara tengo fe

    //CRUD de Etiquetas

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

        // Verificar que el nuevo nombre no exista (si cambió)
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

    //Asignación a Ejercicios

    @Transactional
    public void asignarEtiquetasAEjercicio(Long idEjercicio, List<Long> etiquetasIds) {
        // Buscar el ejercicio completo
        Ejercicio ejercicio = ejercicioRepository.findById(idEjercicio)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ejercicio no encontrado con ID: " + idEjercicio
                ));

        for (Long idEtiqueta : etiquetasIds) {
            // Buscar la etiqueta completa
            Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Etiqueta no encontrada con ID: " + idEtiqueta
                    ));

            // Evitar duplicados
            if (etiquetaEjercicioRepository.findByIdEjercicioAndIdEtiqueta(idEjercicio, idEtiqueta).isEmpty()) {
                EtiquetaEjercicio etiquetaEjercicio = EtiquetaEjercicio.builder()
                        .ejercicio(ejercicio)
                        .etiqueta(etiqueta)
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
        //Buscar el ingrediente completo
        Ingrediente ingrediente = ingredienteRepository.findById(idIngrediente)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingrediente no encontrado con ID: " + idIngrediente
                ));

        for (Long idEtiqueta : etiquetasIds) {
            //Buscar la etiqueta completa
            Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Etiqueta no encontrada con ID: " + idEtiqueta
                    ));

            //Evitar duplicados
            if (etiquetaIngredienteRepository.findByIdIngredienteAndIdEtiqueta(idIngrediente, idEtiqueta).isEmpty()) {
                EtiquetaIngrediente etiquetaIngrediente = EtiquetaIngrediente.builder()
                        .ingrediente(ingrediente)
                        .etiqueta(etiqueta)
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

    //Asignación a Planes (COMENTADO hasta que Víctor(ELGOAT) tenga el repo)

    /*
    @Transactional
    public void asignarEtiquetasAPlan(Long idPlan, List<Long> etiquetasIds) {
        // Buscar el plan
        CatalogoPlan plan = catalogoPlanRepository.findById(idPlan)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + idPlan));

        for (Long idEtiqueta : etiquetasIds) {
            Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta)
                    .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + idEtiqueta));

            // Verificar si ya existe la relación
            if (etiquetaPlanRepository.findByIdCatalogoPlanAndIdEtiqueta(idPlan, idEtiqueta).isEmpty()) {
                EtiquetaPlan etiquetaPlan = EtiquetaPlan.builder()
                        .plan(plan)
                        .etiqueta(etiqueta)
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
    */

    //Asignación a Metas (COMENTADO hasta que Lu tenga el repo)

    /*
    @Transactional
    public void asignarEtiquetasAMeta(Long idMeta, List<Long> etiquetasIds) {
        // Buscar la meta
        CatalogoMeta meta = catalogoMetaRepository.findById(idMeta)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con ID: " + idMeta));

        for (Long idEtiqueta : etiquetasIds) {
            Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta)
                    .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada con ID: " + idEtiqueta));

            // Evitar duplicados
            if (etiquetaMetaRepository.findByIdCatalogoMetaAndIdEtiqueta(idMeta, idEtiqueta).isEmpty()) {
                EtiquetaMeta etiquetaMeta = EtiquetaMeta.builder()
                        .meta(meta)
                        .etiqueta(etiqueta)
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
    */

    //Metodo Helper

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