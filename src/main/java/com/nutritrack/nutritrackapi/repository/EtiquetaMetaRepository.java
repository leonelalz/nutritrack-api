package com.nutritrack.nutritrackapi.repository;

import com.nutritrack.nutritrackapi.model.EtiquetaMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaMetaRepository extends JpaRepository<EtiquetaMeta, Long> {

    List<EtiquetaMeta> findByIdCatalogoMeta(Long idCatalogoMeta);

    boolean existsByIdEtiqueta(Long idEtiqueta);

    Optional<EtiquetaMeta> findByIdCatalogoMetaAndIdEtiqueta(Long idCatalogoMeta, Long idEtiqueta);

    void deleteByIdCatalogoMetaAndIdEtiqueta(Long idCatalogoMeta, Long idEtiqueta);
}