package com.example.nutritrackapi.service;

import com.example.nutritrackapi.dto.*;
import com.example.nutritrackapi.model.*;
import com.example.nutritrackapi.repository.*;
import com.example.nutritrackapi.util.UnidadesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final UsuarioPerfilSaludRepository perfilSaludRepository;
    private final UsuarioHistorialMedidasRepository historialMedidasRepository;
    private final UsuarioEtiquetasSaludRepository etiquetasSaludRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final CuentaAuthRepository cuentaAuthRepository;

    /**
     * US-03: Actualizar unidades de medida
     * RN03: La unidad aplica a todas las vistas
     */
    @Transactional
    public void actualizarUnidadesMedida(String email, UpdateUnidadesMedidaRequest request) {
        PerfilUsuario perfil = obtenerPerfilPorEmail(email);
        perfil.setUnidadesMedida(request.getUnidadesMedida());
        perfilUsuarioRepository.save(perfil);
    }

    /**
     * US-04: Actualizar perfil de salud
     * RN04: Usar etiquetas maestras
     */
    @Transactional
    public PerfilSaludResponse actualizarPerfilSalud(String email, PerfilSaludRequest request) {
        PerfilUsuario perfil = obtenerPerfilPorEmail(email);
        
        // Crear o actualizar perfil de salud
        UsuarioPerfilSalud perfilSalud = perfilSaludRepository.findByPerfilUsuarioId(perfil.getId())
            .orElse(UsuarioPerfilSalud.builder()
                .perfilUsuario(perfil)
                .build());
        
        perfilSalud.setObjetivoActual(request.getObjetivoActual());
        perfilSalud.setNivelActividadActual(request.getNivelActividadActual());
        perfilSalud.setFechaActualizacion(java.time.LocalDate.now());
        perfilSaludRepository.save(perfilSalud);
        
        // Actualizar etiquetas (alergias, condiciones médicas)
        if (request.getEtiquetasId() != null) {
            // Eliminar etiquetas existentes
            etiquetasSaludRepository.deleteByPerfilUsuarioId(perfil.getId());
            
            // Agregar nuevas etiquetas
            for (Long etiquetaId : request.getEtiquetasId()) {
                Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                    .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada: " + etiquetaId));
                
                UsuarioEtiquetasSalud usuarioEtiqueta = UsuarioEtiquetasSalud.builder()
                    .perfilUsuario(perfil)
                    .etiqueta(etiqueta)
                    .build();
                etiquetasSaludRepository.save(usuarioEtiqueta);
            }
        }
        
        return obtenerPerfilSalud(email);
    }

    /**
     * Obtener perfil de salud del usuario
     */
    @Transactional(readOnly = true)
    public PerfilSaludResponse obtenerPerfilSalud(String email) {
        PerfilUsuario perfil = obtenerPerfilPorEmail(email);
        
        UsuarioPerfilSalud perfilSalud = perfilSaludRepository.findByPerfilUsuarioId(perfil.getId())
            .orElse(null);
        
        if (perfilSalud == null) {
            return null;
        }
        
        List<Etiqueta> etiquetas = etiquetasSaludRepository.findEtiquetasByPerfilId(perfil.getId());
        
        return PerfilSaludResponse.builder()
            .id(perfilSalud.getId())
            .objetivoActual(perfilSalud.getObjetivoActual())
            .nivelActividadActual(perfilSalud.getNivelActividadActual())
            .etiquetas(etiquetas.stream()
                .map(e -> PerfilSaludResponse.EtiquetaDTO.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .tipoEtiqueta(e.getTipoEtiqueta())
                    .descripcion(e.getDescripcion())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    /**
     * US-24: Registrar medición corporal
     * RN22: Validar mediciones en rango
     * RN27: Convertir a KG antes de guardar
     */
    @Transactional
    public HistorialMedidasResponse registrarMedicion(String email, HistorialMedidasRequest request) {
        PerfilUsuario perfil = obtenerPerfilPorEmail(email);
        
        // Validar que no existe medición para la misma fecha
        if (historialMedidasRepository.existsByPerfilUsuarioIdAndFechaMedicion(
                perfil.getId(), request.getFechaMedicion())) {
            throw new RuntimeException("Ya existe una medición para la fecha " + request.getFechaMedicion());
        }
        
        // Convertir peso a KG si viene en LBS (RN27)
        var unidadEntrada = request.getUnidadPeso() != null ? 
            request.getUnidadPeso() : PerfilUsuario.UnidadesMedida.KG;
        var pesoKg = UnidadesUtil.convertirAKg(request.getPeso(), unidadEntrada);
        
        UsuarioHistorialMedidas medicion = UsuarioHistorialMedidas.builder()
            .perfilUsuario(perfil)
            .peso(pesoKg)
            .altura(request.getAltura())
            .fechaMedicion(request.getFechaMedicion())
            .build();
        
        medicion = historialMedidasRepository.save(medicion);
        
        // Convertir a unidad preferida del usuario para la respuesta
        return convertirAResponse(medicion, perfil.getUnidadesMedida());
    }

    /**
     * US-24: Obtener historial de mediciones
     */
    @Transactional(readOnly = true)
    public List<HistorialMedidasResponse> obtenerHistorialMediciones(String email) {
        PerfilUsuario perfil = obtenerPerfilPorEmail(email);
        List<UsuarioHistorialMedidas> historial = historialMedidasRepository
            .findByPerfilUsuarioIdOrderByFechaMedicionDesc(perfil.getId());
        
        return historial.stream()
            .map(m -> convertirAResponse(m, perfil.getUnidadesMedida()))
            .collect(Collectors.toList());
    }

    /**
     * Obtener perfil de usuario por email
     */
    private PerfilUsuario obtenerPerfilPorEmail(String email) {
        CuentaAuth cuenta = cuentaAuthRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return cuenta.getPerfilUsuario();
    }

    /**
     * Convertir medición a DTO de respuesta con unidad preferida del usuario
     */
    private HistorialMedidasResponse convertirAResponse(UsuarioHistorialMedidas medicion, 
                                                        PerfilUsuario.UnidadesMedida unidadPreferida) {
        var pesoConvertido = UnidadesUtil.convertirDesdeKg(medicion.getPeso(), unidadPreferida);
        
        return HistorialMedidasResponse.builder()
            .id(medicion.getId())
            .peso(pesoConvertido)
            .altura(medicion.getAltura())
            .imc(medicion.getImc())
            .fechaMedicion(medicion.getFechaMedicion())
            .unidadPeso(unidadPreferida)
            .build();
    }
}
