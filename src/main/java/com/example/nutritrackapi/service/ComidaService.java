package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.AgregarIngredienteRequest;
import com.example.nutritrackapi.dto.ComidaRequest;
import com.example.nutritrackapi.dto.ComidaResponse;
import com.example.nutritrackapi.dto.EtiquetaResponse;
import com.example.nutritrackapi.dto.RecetaIngredienteResponse;
import com.example.nutritrackapi.model.Comida;
import com.example.nutritrackapi.model.ComidaIngrediente;
import com.example.nutritrackapi.model.ComidaIngrediente.ComidaIngredienteId;
import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.model.Ingrediente;
import com.example.nutritrackapi.repository.ComidaIngredienteRepository;
import com.example.nutritrackapi.repository.ComidaRepository;
import com.example.nutritrackapi.repository.EtiquetaRepository;
import com.example.nutritrackapi.repository.IngredienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de comidas y recetas.
 * Implementa US-09: Gestionar Comidas y US-10: Gestionar Recetas
 * RN07: Nombre único de comida
 * RN10: Cantidad positiva de ingredientes
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComidaService {

    private final ComidaRepository comidaRepository;
    private final ComidaIngredienteRepository comidaIngredienteRepository;
    private final IngredienteRepository ingredienteRepository;
    private final EtiquetaRepository etiquetaRepository;

    /**
     * Crea una nueva comida.
     * RN07: Valida que el nombre sea único.
     */
    @Transactional
    public ComidaResponse crearComida(ComidaRequest request) {
        // RN07: Validar nombre único
        if (comidaRepository.existsByNombre(request.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe una comida con el nombre: " + request.getNombre()
            );
        }

        Comida comida = new Comida();
        comida.setNombre(request.getNombre());
        comida.setTipoComida(request.getTipoComida());
        comida.setDescripcion(request.getDescripcion());
        comida.setTiempoPreparacionMinutos(request.getTiempoPreparacionMinutos());
        comida.setPorciones(request.getPorciones());
        comida.setInstrucciones(request.getInstrucciones());

        // Asociar etiquetas
        if (request.getEtiquetaIds() != null && !request.getEtiquetaIds().isEmpty()) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            comida.setEtiquetas(etiquetas);
        }

        Comida guardada = comidaRepository.save(comida);
        return construirComidaResponse(guardada);
    }

    /**
     * Obtiene una comida por su ID con información nutricional calculada.
     */
    public ComidaResponse obtenerComidaPorId(Long id) {
        Comida comida = comidaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Comida no encontrada con ID: " + id
            ));
        return construirComidaResponse(comida);
    }

    /**
     * Lista todas las comidas con paginación.
     */
    public Page<ComidaResponse> listarComidas(Pageable pageable) {
        return comidaRepository.findAll(pageable)
            .map(this::construirComidaResponse);
    }

    /**
     * Busca comidas por nombre (parcial, case-insensitive).
     */
    public Page<ComidaResponse> buscarPorNombre(String nombre, Pageable pageable) {
        return comidaRepository.findByNombreContainingIgnoreCase(nombre, pageable)
            .map(this::construirComidaResponse);
    }

    /**
     * Filtra comidas por tipo.
     */
    public Page<ComidaResponse> filtrarPorTipo(Comida.TipoComida tipo, Pageable pageable) {
        return comidaRepository.findByTipoComida(tipo, pageable)
            .map(this::construirComidaResponse);
    }

    /**
     * Actualiza una comida existente.
     * RN07: Valida que el nuevo nombre sea único (excluyendo la comida actual).
     */
    @Transactional
    public ComidaResponse actualizarComida(Long id, ComidaRequest request) {
        Comida comida = comidaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Comida no encontrada con ID: " + id
            ));

        // RN07: Validar nombre único (excluyendo la comida actual)
        if (comidaRepository.existsByNombreAndIdNot(request.getNombre(), id)) {
            throw new IllegalArgumentException(
                "Ya existe otra comida con el nombre: " + request.getNombre()
            );
        }

        comida.setNombre(request.getNombre());
        comida.setTipoComida(request.getTipoComida());
        comida.setDescripcion(request.getDescripcion());
        comida.setTiempoPreparacionMinutos(request.getTiempoPreparacionMinutos());
        comida.setPorciones(request.getPorciones());
        comida.setInstrucciones(request.getInstrucciones());

        // Actualizar etiquetas
        if (request.getEtiquetaIds() != null) {
            Set<Etiqueta> etiquetas = new HashSet<>(
                etiquetaRepository.findAllById(request.getEtiquetaIds())
            );
            if (etiquetas.size() != request.getEtiquetaIds().size()) {
                throw new EntityNotFoundException("Una o más etiquetas no fueron encontradas");
            }
            comida.setEtiquetas(etiquetas);
        } else {
            comida.getEtiquetas().clear();
        }

        Comida actualizada = comidaRepository.save(comida);
        return construirComidaResponse(actualizada);
    }

    /**
     * Elimina una comida.
     */
    @Transactional
    public void eliminarComida(Long id) {
        if (!comidaRepository.existsById(id)) {
            throw new EntityNotFoundException("Comida no encontrada con ID: " + id);
        }
        comidaRepository.deleteById(id);
    }

    /**
     * Agrega un ingrediente a una comida (receta).
     * RN10: Valida que la cantidad sea positiva.
     */
    @Transactional
    public ComidaResponse agregarIngrediente(Long comidaId, AgregarIngredienteRequest request) {
        Comida comida = comidaRepository.findById(comidaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Comida no encontrada con ID: " + comidaId
            ));

        Ingrediente ingrediente = ingredienteRepository.findById(request.getIngredienteId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Ingrediente no encontrado con ID: " + request.getIngredienteId()
            ));

        // Verificar si ya existe la relación
        ComidaIngredienteId id = new ComidaIngredienteId(comidaId, request.getIngredienteId());
        if (comidaIngredienteRepository.existsById(id)) {
            throw new IllegalArgumentException(
                "El ingrediente ya está agregado a esta comida. Use actualizar para modificar la cantidad."
            );
        }

        // RN10: La validación @Positive ya se aplica en el DTO, pero validamos por seguridad
        if (request.getCantidadGramos().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        ComidaIngrediente comidaIngrediente = new ComidaIngrediente();
        comidaIngrediente.setId(id);
        comidaIngrediente.setComida(comida);
        comidaIngrediente.setIngrediente(ingrediente);
        comidaIngrediente.setCantidadGramos(request.getCantidadGramos());
        comidaIngrediente.setNotas(request.getNotas());

        comidaIngredienteRepository.save(comidaIngrediente);
        
        // Recargar la comida con los ingredientes actualizados
        Comida actualizada = comidaRepository.findById(comidaId).orElseThrow();
        return construirComidaResponse(actualizada);
    }

    /**
     * Actualiza la cantidad de un ingrediente en una comida.
     * RN10: Valida que la nueva cantidad sea positiva.
     */
    @Transactional
    public ComidaResponse actualizarIngrediente(
        Long comidaId, 
        Long ingredienteId, 
        AgregarIngredienteRequest request
    ) {
        ComidaIngredienteId id = new ComidaIngredienteId(comidaId, ingredienteId);
        ComidaIngrediente comidaIngrediente = comidaIngredienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "No se encontró el ingrediente en esta comida"
            ));

        // RN10: Validar cantidad positiva
        if (request.getCantidadGramos().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }

        comidaIngrediente.setCantidadGramos(request.getCantidadGramos());
        comidaIngrediente.setNotas(request.getNotas());

        comidaIngredienteRepository.save(comidaIngrediente);
        
        // Recargar la comida
        Comida comida = comidaRepository.findById(comidaId).orElseThrow();
        return construirComidaResponse(comida);
    }

    /**
     * Elimina un ingrediente de una comida.
     */
    @Transactional
    public ComidaResponse eliminarIngrediente(Long comidaId, Long ingredienteId) {
        ComidaIngredienteId id = new ComidaIngredienteId(comidaId, ingredienteId);
        if (!comidaIngredienteRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el ingrediente en esta comida");
        }

        comidaIngredienteRepository.deleteById(id);
        
        // Recargar la comida
        Comida comida = comidaRepository.findById(comidaId).orElseThrow();
        return construirComidaResponse(comida);
    }

    /**
     * Construye el ComidaResponse con información nutricional calculada.
     */
    private ComidaResponse construirComidaResponse(Comida comida) {
        List<ComidaIngrediente> ingredientes = 
            comidaIngredienteRepository.findByComidaId(comida.getId());

        List<RecetaIngredienteResponse> ingredientesResponse = ingredientes.stream()
            .map(this::construirRecetaIngredienteResponse)
            .collect(Collectors.toList());

        ComidaResponse.InformacionNutricionalResponse nutricionTotal = 
            calcularNutricionTotal(ingredientes);

        return ComidaResponse.builder()
            .id(comida.getId())
            .nombre(comida.getNombre())
            .tipoComida(comida.getTipoComida())
            .descripcion(comida.getDescripcion())
            .tiempoPreparacionMinutos(comida.getTiempoPreparacionMinutos())
            .porciones(comida.getPorciones())
            .instrucciones(comida.getInstrucciones())
            .ingredientes(ingredientesResponse)
            .nutricionTotal(nutricionTotal)
            .etiquetas(comida.getEtiquetas().stream()
                .map(etiqueta -> EtiquetaResponse.fromEntity(etiqueta))
                .collect(Collectors.toSet()))
            .createdAt(comida.getCreatedAt())
            .updatedAt(comida.getUpdatedAt())
            .build();
    }

    /**
     * Construye la respuesta para un ingrediente de la receta con valores nutricionales calculados.
     */
    private RecetaIngredienteResponse construirRecetaIngredienteResponse(ComidaIngrediente ci) {
        Ingrediente ing = ci.getIngrediente();
        BigDecimal factor = ci.getCantidadGramos().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        return RecetaIngredienteResponse.builder()
            .ingredienteId(ing.getId())
            .ingredienteNombre(ing.getNombre())
            .cantidadGramos(ci.getCantidadGramos())
            .proteinasAportadas(ing.getProteinas().multiply(factor).setScale(2, RoundingMode.HALF_UP))
            .carbohidratosAportados(ing.getCarbohidratos().multiply(factor).setScale(2, RoundingMode.HALF_UP))
            .grasasAportadas(ing.getGrasas().multiply(factor).setScale(2, RoundingMode.HALF_UP))
            .energiaAportada(ing.getEnergia().multiply(factor).setScale(2, RoundingMode.HALF_UP))
            .fibraAportada(ing.getFibra().multiply(factor).setScale(2, RoundingMode.HALF_UP))
            .notas(ci.getNotas())
            .build();
    }

    /**
     * Calcula la información nutricional total de la comida.
     */
    private ComidaResponse.InformacionNutricionalResponse calcularNutricionTotal(
        List<ComidaIngrediente> ingredientes
    ) {
        BigDecimal proteinasTotal = BigDecimal.ZERO;
        BigDecimal carbohidratosTotal = BigDecimal.ZERO;
        BigDecimal grasasTotal = BigDecimal.ZERO;
        BigDecimal energiaTotal = BigDecimal.ZERO;
        BigDecimal fibraTotal = BigDecimal.ZERO;

        for (ComidaIngrediente ci : ingredientes) {
            Ingrediente ing = ci.getIngrediente();
            BigDecimal factor = ci.getCantidadGramos().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

            proteinasTotal = proteinasTotal.add(ing.getProteinas().multiply(factor));
            carbohidratosTotal = carbohidratosTotal.add(ing.getCarbohidratos().multiply(factor));
            grasasTotal = grasasTotal.add(ing.getGrasas().multiply(factor));
            energiaTotal = energiaTotal.add(ing.getEnergia().multiply(factor));
            fibraTotal = fibraTotal.add(ing.getFibra().multiply(factor));
        }

        return ComidaResponse.InformacionNutricionalResponse.builder()
            .proteinasTotales(proteinasTotal.setScale(2, RoundingMode.HALF_UP))
            .carbohidratosTotales(carbohidratosTotal.setScale(2, RoundingMode.HALF_UP))
            .grasasTotales(grasasTotal.setScale(2, RoundingMode.HALF_UP))
            .energiaTotal(energiaTotal.setScale(2, RoundingMode.HALF_UP))
            .fibraTotal(fibraTotal.setScale(2, RoundingMode.HALF_UP))
            .pesoTotal(ingredientes.stream()
                .map(ComidaIngrediente::getCantidadGramos)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP))
            .build();
    }
}
