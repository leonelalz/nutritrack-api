# Colecciones de Postman

Esta carpeta contiene las colecciones de Postman para cada módulo del proyecto.

## 📦 Colecciones Disponibles

1. **Module_1_Cuentas_Preferencias.postman_collection.json**
   - US-01 a US-05
   - 5 endpoints
   - Responsable: Leonel Alzamora

2. **Module_2_Biblioteca_Contenido.postman_collection.json**
   - US-06 a US-10
   - 11 endpoints
   - Responsables: Fabian Rojas, Gonzalo Huaranga, Victor Carranza

3. **Module_3_Gestor_Catalogo.postman_collection.json**
   - US-11 a US-15
   - 6 endpoints
   - Responsables: Gonzalo Huaranga, Victor Carranza

4. **Module_4_Exploracion_Activacion.postman_collection.json**
   - US-16 a US-20
   - 4 endpoints
   - Responsables: Gonzalo Huaranga, Victor Carranza

5. **Module_5_Seguimiento_Progreso.postman_collection.json**
   - US-21 a US-25
   - 7 endpoints
   - Responsables: Gonzalo Huaranga, Jhamil Peña, Victor Carranza

## 🔄 Cómo Usar

### Importar Colecciones

1. Abrir Postman
2. Click en "Import"
3. Seleccionar los archivos .json de esta carpeta
4. Las colecciones aparecerán en el panel izquierdo

### Exportar Colecciones Actualizadas

1. Click derecho en la colección
2. "Export"
3. Seleccionar "Collection v2.1"
4. Guardar en esta carpeta (sobrescribir archivo existente)
5. Commitear los cambios

## ⚠️ Importante

- **NO editar** manualmente los archivos JSON
- **SIEMPRE** exportar desde Postman después de hacer cambios
- Incluir estos archivos en los commits cuando se actualicen endpoints
- Mantener sincronizado con el código del API

## 📝 Convenciones

- Nombres de requests: `[MÉTODO] [Nombre Descriptivo] (US-XX)`
- Ejemplo: `POST Create Tag (US-06)`
- Usar carpetas para organizar por tipo de recurso
- Incluir tests de validación en cada request
- Documentar casos de error

---

**Para crear las colecciones base, seguir:** [POSTMAN_GUIDE.md](../POSTMAN_GUIDE.md)
