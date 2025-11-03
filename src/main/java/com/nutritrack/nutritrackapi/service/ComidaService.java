package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CrearComidaRequest;
import com.nutritrack.nutritrackapi.dto.response.ComidaResponse;
import com.nutritrack.nutritrackapi.exception.DuplicateResourceException;
import com.nutritrack.nutritrackapi.exception.ResourceNotFoundException;
import com.nutritrack.nutritrackapi.model.Comida;
import com.nutritrack.nutritrackapi.model.Ingrediente;
import com.nutritrack.nutritrackapi.model.Receta;
import com.nutritrack.nutritrackapi.model.enums.TipoComida;
import com.nutritrack.nutritrackapi.repository.ComidaRepository;
import com.nutritrack.nutritrackapi.repository.IngredienteRepository;
import com.nutritrack.nutritrackapi.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ComidaService {
    
    private final ComidaRepository comidaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final RecetaRepository recetaRepository;
    
    public ComidaResponse crear(CrearComidaRequest request) {
        if (comidaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe una comida con el nombre: " + request.nombre());
        }
        
        Comida comida = Comida.builder()
                .nombre(request.nombre())
                .tipoComida(request.tipoComida())
                .tiempoElaboracion(request.tiempoElaboracion())
                .build();
        
        Comida guardada = comidaRepository.save(comida);
        
        if (request.ingredientes() != null && !request.ingredientes().isEmpty()) {
            for (CrearComidaRequest.IngredienteReceta ir : request.ingredientes()) {
                Ingrediente ingrediente = ingredienteRepository.findById(ir.idIngrediente())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + ir.idIngrediente()));
                
                Receta receta = Receta.builder()
                        .idComida(guardada.getId())
                        .idIngrediente(ingrediente.getId())
                        .cantidadIngrediente(ir.cantidad())
                        .comida(guardada)
                        .ingrediente(ingrediente)
                        .build();
                
                recetaRepository.save(receta);
            }
        }
        
        return buscarPorId(guardada.getId());
    }
    
    public ComidaResponse actualizar(Long id, CrearComidaRequest request) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con ID: " + id));
        
        if (!comida.getNombre().equalsIgnoreCase(request.nombre()) &&
            comidaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe una comida con el nombre: " + request.nombre());
        }
        
        comida.setNombre(request.nombre());
        comida.setTipoComida(request.tipoComida());
        comida.setTiempoElaboracion(request.tiempoElaboracion());
        
        comidaRepository.save(comida);
        
        // Actualizar recetas
        recetaRepository.deleteByIdComida(id);
        
        if (request.ingredientes() != null && !request.ingredientes().isEmpty()) {
            for (CrearComidaRequest.IngredienteReceta ir : request.ingredientes()) {
                Ingrediente ingrediente = ingredienteRepository.findById(ir.idIngrediente())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + ir.idIngrediente()));
                
                Receta receta = Receta.builder()
                        .idComida(comida.getId())
                        .idIngrediente(ingrediente.getId())
                        .cantidadIngrediente(ir.cantidad())
                        .comida(comida)
                        .ingrediente(ingrediente)
                        .build();
                
                recetaRepository.save(receta);
            }
        }
        
        return buscarPorId(id);
    }
    
    public void eliminar(Long id) {
        if (!comidaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + id);
        }
        recetaRepository.deleteByIdComida(id);
        comidaRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public ComidaResponse buscarPorId(Long id) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con ID: " + id));
        return convertirAResponse(comida);
    }
    
    @Transactional(readOnly = true)
    public List<ComidaResponse> buscarPorNombre(String nombre) {
        return comidaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ComidaResponse> buscarPorTipo(TipoComida tipo) {
        return comidaRepository.findByTipoComida(tipo)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ComidaResponse> listarTodos() {
        return comidaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    public ComidaResponse agregarIngrediente(Long comidaId, Long ingredienteId, BigDecimal cantidad) {
        Comida comida = comidaRepository.findById(comidaId)
                .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con ID: " + comidaId));
        
        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + ingredienteId));
        
        Receta receta = Receta.builder()
                .idComida(comidaId)
                .idIngrediente(ingredienteId)
                .cantidadIngrediente(cantidad)
                .comida(comida)
                .ingrediente(ingrediente)
                .build();
        
        recetaRepository.save(receta);
        return buscarPorId(comidaId);
    }
    
    public ComidaResponse removerIngrediente(Long comidaId, Long ingredienteId) {
        if (!comidaRepository.existsById(comidaId)) {
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + comidaId);
        }
        
        recetaRepository.deleteByIdComidaAndIdIngrediente(comidaId, ingredienteId);
        return buscarPorId(comidaId);
    }
    
    private ComidaResponse convertirAResponse(Comida comida) {
        List<Receta> recetas = recetaRepository.findByIdComida(comida.getId());
        
        List<ComidaResponse.IngredienteRecetaResponse> ingredientes = recetas.stream()
                .map(r -> ComidaResponse.IngredienteRecetaResponse.builder()
                        .id(r.getIngrediente().getId())
                        .nombre(r.getIngrediente().getNombre())
                        .cantidad(r.getCantidadIngrediente())
                        .build())
                .collect(Collectors.toList());
        
        // Calcular nutrición total
        BigDecimal energiaTotal = BigDecimal.ZERO;
        BigDecimal proteinasTotal = BigDecimal.ZERO;
        BigDecimal grasasTotal = BigDecimal.ZERO;
        BigDecimal carbohidratosTotal = BigDecimal.ZERO;
        
        for (Receta r : recetas) {
            BigDecimal factor = r.getCantidadIngrediente().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            energiaTotal = energiaTotal.add(r.getIngrediente().getEnergia().multiply(factor));
            proteinasTotal = proteinasTotal.add(r.getIngrediente().getProteinas().multiply(factor));
            grasasTotal = grasasTotal.add(r.getIngrediente().getGrasas().multiply(factor));
            carbohidratosTotal = carbohidratosTotal.add(r.getIngrediente().getCarbohidratos().multiply(factor));
        }
        
        ComidaResponse.NutricionTotal nutricion = ComidaResponse.NutricionTotal.builder()
                .energiaTotal(energiaTotal.setScale(2, RoundingMode.HALF_UP))
                .proteinasTotal(proteinasTotal.setScale(2, RoundingMode.HALF_UP))
                .grasasTotal(grasasTotal.setScale(2, RoundingMode.HALF_UP))
                .carbohidratosTotal(carbohidratosTotal.setScale(2, RoundingMode.HALF_UP))
                .build();
        
        return ComidaResponse.builder()
                .id(comida.getId())
                .nombre(comida.getNombre())
                .tipoComida(comida.getTipoComida())
                .tiempoElaboracion(comida.getTiempoElaboracion())
                .ingredientes(ingredientes)
                .nutricionTotal(nutricion)
                .createdAt(comida.getCreatedAt())
                .updatedAt(comida.getUpdatedAt())
                .build();
    }
}
