package com.nutritrack.nutritrackapi.model.enums;

public enum UnidadesMedida {
    KG("Kilogramos"),
    LBS("Libras");

    private final String descripcion;

    UnidadesMedida(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
