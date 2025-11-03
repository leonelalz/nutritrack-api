package com.nutritrack.nutritrackapi.model.enums;

public enum EstadoAsignacion {
    ACTIVO,      // Plan/Rutina actualmente en progreso
    COMPLETADO,  // Plan/Rutina finalizado exitosamente
    CANCELADO,   // Plan/Rutina cancelado antes de completarse
    PAUSADO      // Plan/Rutina pausado temporalmente
}
