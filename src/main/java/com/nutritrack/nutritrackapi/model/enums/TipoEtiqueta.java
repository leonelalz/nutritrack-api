package com.nutritrack.nutritrackapi.model.enums;

public enum TipoEtiqueta {
    ALERGIA("Alergia Alimentaria"),
    CONDICION_MEDICA("Condición Médica"),
    OBJETIVO("Objetivo de Fitness"),
    DIETA("Tipo de Dieta"),
    DIFICULTAD("Nivel de Dificultad"),
    GRUPO_MUSCULAR("Grupo Muscular"),
    TIPO_EJERCICIO("Tipo de Ejercicio");

    private final String descripcion;

    TipoEtiqueta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
