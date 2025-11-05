#!/bin/bash
# Script de inicio para Render

echo "🚀 Iniciando NutriTrack API..."

# Ejecutar la aplicación con el perfil de producción
java -Dspring.profiles.active=production \
     -Xmx512m \
     -jar target/nutritrack-API-0.0.1-SNAPSHOT.jar
