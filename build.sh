#!/bin/bash
# Script de construcción para Render

echo "🔧 Iniciando build de NutriTrack API..."

# Limpiar y compilar (sin tests para ser más rápido)
./mvnw clean package -DskipTests

echo "✅ Build completado"
