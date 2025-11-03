package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.RecetaRequest;
import com.nutritrack.nutritrackapi.dto.response.RecetaResponse;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Comida;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.model.Receta;
import com.nutritrack.nutritrackapi.repository.ComidaRepository;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import com.nutritrack.nutritrackapi.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final ComidaRepository comidaRepository;
    private final IngredienteRepository ingredienteRepository;

    //CRUD de Recetas

    @Transactional
    public RecetaResponse agregarIngredienteAComida(Long idComida, RecetaRequest request) {
        // CA 1 (CREATE): Verificar que la comida existe
        Comida comida = comidaRepository.findById(idComida)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comida no encontrada con ID: " + idComida
                ));

        // Verificar que el ingrediente existe
        Ingrediente ingrediente = ingredienteRepository.findById(request.idIngrediente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingrediente no encontrado con ID: " + request.idIngrediente()
                ));

        // RN10: La validación numérica ya está en RecetaRequest con @DecimalMin

        // Crear la receta
        Receta receta = Receta.builder()
                .comida(comida)
                .ingrediente(ingrediente)
                .cantidadIngrediente(request.cantidadIngrediente())
                .unidadMedida(request.unidadMedida())
                .build();

        Receta saved = recetaRepository.save(receta);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RecetaResponse> obtenerRecetasPorComida(Long idComida) {
        // Verificar que la comida existe
        if (!comidaRepository.existsById(idComida)) {
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + idComida);
        }

        return recetaRepository.findByIdComida(idComida).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecetaResponse actualizarReceta(Long idReceta, RecetaRequest request) {
        // CA 2 (UPDATE): Buscar la receta existente
        Receta receta = recetaRepository.findById(idReceta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receta no encontrada con ID: " + idReceta
                ));

        // Verificar que el nuevo ingrediente existe (si cambió)
        if (!receta.getIngrediente().getId().equals(request.idIngrediente())) {
            Ingrediente nuevoIngrediente = ingredienteRepository.findById(request.idIngrediente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ingrediente no encontrado con ID: " + request.idIngrediente()
                    ));
            receta.setIngrediente(nuevoIngrediente);
        }

        // Actualizar cantidad y unidad
        receta.setCantidadIngrediente(request.cantidadIngrediente());
        receta.setUnidadMedida(request.unidadMedida());

        Receta updated = recetaRepository.save(receta);
        return toResponse(updated);
    }

    @Transactional
    public void eliminarReceta(Long idReceta) {
        // CA 3 (DELETE): Verificar que existe
        if (!recetaRepository.existsById(idReceta)) {
            throw new ResourceNotFoundException("Receta no encontrada con ID: " + idReceta);
        }

        recetaRepository.deleteById(idReceta);
    }

    @Transactional
    public void eliminarIngredienteDeComida(Long idComida, Long idIngrediente) {
        // Buscar la receta específica
        List<Receta> recetas = recetaRepository.findByIdComida(idComida);

        Receta receta = recetas.stream()
                .filter(r -> r.getIngrediente().getId().equals(idIngrediente))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe relación entre la comida " + idComida +
                                " y el ingrediente " + idIngrediente
                ));

        recetaRepository.delete(receta);
    }

    //Metodo Helper

    private RecetaResponse toResponse(Receta receta) {
        return new RecetaResponse(
                receta.getId(),
                receta.getComida().getId(),
                receta.getIngrediente().getId(),
                receta.getIngrediente().getNombre(),
                receta.getCantidadIngrediente(),
                receta.getUnidadMedida()
        );
    }
}