# 🚀 Guía: Trabajar con Fork y Render

## 1. Crear Fork (Ya lo estás haciendo)
✅ Clic en "Create fork" en GitHub

Resultado: `Jaed69/nutritrack-api` (tu fork personal)

## 2. Clonar tu fork localmente
```powershell
cd C:\Users\twofi\Documents\Intell
git clone https://github.com/Jaed69/nutritrack-api.git nutritrack-API-fork
cd nutritrack-API-fork
```

## 3. Configurar upstream (repo original)
```powershell
git remote add upstream https://github.com/leonelalz/nutritrack-api.git
git remote -v
```

Verás:
- `origin` → Tu fork (Jaed69)
- `upstream` → Repo original (leonelalz)

## 4. Conectar tu fork a Render
1. Dashboard Render → New → Blueprint
2. Connect repository: `Jaed69/nutritrack-api`
3. Render desplegará automáticamente

## 5. Sincronizar con el repo grupal (cuando haya cambios)
```powershell
# Traer cambios del repo original
git fetch upstream
git checkout main
git merge upstream/main

# Subir a tu fork
git push origin main
```

Render detectará el push y re-desplegará automáticamente.

## 6. Contribuir al repo grupal
Cuando tengas cambios listos:
1. Push a tu fork: `git push origin main`
2. GitHub → Pull Request → De tu fork al repo original
3. El equipo revisa y acepta el PR

## Ventajas de esta estrategia:
✅ No afectas el trabajo de otros
✅ Puedes experimentar libremente en tu fork
✅ Contribuyes al repo grupal vía Pull Requests
✅ Render despliega automáticamente desde tu fork
✅ Mantienes sincronizado con el equipo

## ⚠️ Alternativa: Sobrescribir master del repo principal

**SOLO si tienes permisos y el equipo está de acuerdo:**

```powershell
cd C:\Users\twofi\Documents\Intell\nutritrack-API

# Renombrar rama main a master
git branch -M main master

# Forzar push (¡DESTRUYE el master actual!)
git push origin master --force

# Actualizar rama por defecto en GitHub:
# Settings → Branches → Default branch → master
```

## 🎯 Decisión: ¿Qué hacer?

**Fork (Recomendado):**
- Completa el fork en GitHub
- Clona tu fork
- Conecta a Render

**Sobrescribir master:**
- Solo si tienes admin access
- Solo si el equipo lo aprueba
- Ejecuta los comandos de arriba

