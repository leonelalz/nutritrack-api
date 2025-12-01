package com.example.nutritrackapi.repository;

import com.example.nutritrackapi.model.Etiqueta;
import com.example.nutritrackapi.model.UsuarioEtiquetasSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioEtiquetasSaludRepository extends JpaRepository<UsuarioEtiquetasSalud, UsuarioEtiquetasSalud.UsuarioEtiquetaId> {

    /**
     * Buscar todas las etiquetas de un usuario
     */
    List<UsuarioEtiquetasSalud> findByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Buscar etiquetas de un usuario por tipo específico
     */
    @Query("""
        SELECT ues.etiqueta
        FROM UsuarioEtiquetasSalud ues
        WHERE ues.perfilUsuario.id = :perfilUsuarioId
        AND ues.etiqueta.tipoEtiqueta = :tipoEtiqueta
        """)
    List<Etiqueta> findEtiquetasByPerfilIdAndTipo(Long perfilUsuarioId, Etiqueta.TipoEtiqueta tipoEtiqueta);

    /**
     * Obtener todas las etiquetas de un usuario (solo las etiquetas)
     */
    @Query("""
        SELECT ues.etiqueta
        FROM UsuarioEtiquetasSalud ues
        WHERE ues.perfilUsuario.id = :perfilUsuarioId
        """)
    List<Etiqueta> findEtiquetasByPerfilId(Long perfilUsuarioId);

    /**
     * RN32: Obtener IDs de etiquetas de alergias del usuario
     */
    @Query("""
        SELECT e.id FROM Etiqueta e
        INNER JOIN UsuarioEtiquetasSalud ues ON e.id = ues.etiqueta.id
        WHERE ues.perfilUsuario.id = :perfilUsuarioId
        AND e.tipoEtiqueta = 'ALERGIA'
        """)
    List<Long> findEtiquetasAlergenosByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * RN33: Obtener IDs de etiquetas de condiciones médicas (lesiones) del usuario
     * Usado para validar que las rutinas no contengan ejercicios contraindicados
     */
    @Query("""
        SELECT e.id FROM Etiqueta e
        INNER JOIN UsuarioEtiquetasSalud ues ON e.id = ues.etiqueta.id
        WHERE ues.perfilUsuario.id = :perfilUsuarioId
        AND e.tipoEtiqueta = 'CONDICION_MEDICA'
        """)
    List<Long> findCondicionesMedicasByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Verificar si usuario tiene una etiqueta específica
     */
    boolean existsByPerfilUsuarioIdAndEtiquetaId(Long perfilUsuarioId, Long etiquetaId);

    /**
     * Eliminar todas las etiquetas de un usuario
     */
    void deleteByPerfilUsuarioId(Long perfilUsuarioId);

    /**
     * Eliminar una etiqueta específica de un usuario
     */
    void deleteByPerfilUsuarioIdAndEtiquetaId(Long perfilUsuarioId, Long etiquetaId);
}
