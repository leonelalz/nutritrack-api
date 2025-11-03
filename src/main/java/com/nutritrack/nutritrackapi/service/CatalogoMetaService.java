package com.nutritrack.nutritrackapi.service;

import com.nutritrack.nutritrackapi.dto.request.CatalogoMetaRequestDTO;
import com.nutritrack.nutritrackapi.dto.response.CatalogoMetaResponseDTO;
import com.nutritrack.nutritrackapi.dto.response.MessageResponseDTO;
import com.nutritrack.nutritrackapi.exception.MetaConUsuariosActivosException;
import com.nutritrack.nutritrackapi.exception.MetaDuplicadaException;
import com.nutritrack.nutritrackapi.exception.MetaNoEncontradaException;
import com.nutritrack.nutritrackapi.model.CatalogoMeta;
import com.nutritrack.nutritrackapi.repository.CatalogoMetaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoMetaService {
    private final CatalogoMetaRepository catalogoMetaRepository;

    // Crear meta catálogo (US-11 / RN11)
    @Transactional
    public CatalogoMetaResponseDTO crearMetaCatalogo(CatalogoMetaRequestDTO request) {
        catalogoMetaRepository.findByNombreIgnoreCase(request.nombre())
                .ifPresent(meta -> { throw new MetaDuplicadaException(request.nombre()); });

        CatalogoMeta nuevaMeta = new CatalogoMeta();
        nuevaMeta.setNombre(request.nombre());
        nuevaMeta.setDescripcion(request.descripcion());
        nuevaMeta = catalogoMetaRepository.save(nuevaMeta);

        return new CatalogoMetaResponseDTO(
                nuevaMeta.getId(),
                nuevaMeta.getNombre(),
                nuevaMeta.getDescripcion()
        );
    }

    // Eliminar meta catálogo (US-15 / RN14)
    @Transactional
    public MessageResponseDTO eliminarMeta(Long idMeta) {
        CatalogoMeta meta = catalogoMetaRepository.findById(idMeta)
                .orElseThrow(() -> new MetaNoEncontradaException(idMeta));

        boolean tieneUsuariosActivos = catalogoMetaRepository.existsMetaConUsuariosActivos(idMeta);
        if (tieneUsuariosActivos) {
            throw new MetaConUsuariosActivosException(idMeta);
        }

        catalogoMetaRepository.delete(meta);
        return new MessageResponseDTO("Meta '" + meta.getNombre() + "' eliminada correctamente.");
    }

    // Ver catálogo de metas (US-16 / RN15–RN16)
    @Transactional
    public List<CatalogoMetaResponseDTO> listarMetas() {
        return catalogoMetaRepository.findAll().stream()
                .map(meta -> new CatalogoMetaResponseDTO(
                        meta.getId(),
                        meta.getNombre(),
                        meta.getDescripcion()
                )).toList();
    }
}
