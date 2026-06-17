---
name: changelog-generator
description: Automatiza la generación de changelogs a partir de commits de git, agrupándolos por tipo y loader.
trigger: Cuando el usuario pide generar un changelog, usa el comando /changelog, o indica que quiere crear notas de release.
---

# Changelog Generator

## Propósito

Generar automáticamente entradas de changelog para cada loader (Fabric, NeoForge, Spigot) basándose en los commits de git desde el último tag o release.

## Flujo de Trabajo

### 1. Determinar el Rango de Commits

Obtener commits desde el último tag:
```bash
git log --oneline $(git describe --tags --abbrev=0)..HEAD
```

Si no hay tags previos:
```bash
git log --oneline
```

### 2. Clasificar Commits por Módulo

Analizar los archivos modificados en cada commit para determinar qué loaders se ven afectados:

| Archivos modificados | Loader(s) afectado(s) |
|---|---|
| `common/...` | Todos (Fabric, NeoForge, Spigot) |
| `client-fabric/...` | Fabric |
| `client-neoforge/...` | NeoForge |
| `plugin-spigot/...` | Spigot |
| `example-plugin/...` | Spigot (documentación) |
| `build.gradle.kts` (root) | Todos |
| `gradle.properties` | Depende de qué properties cambiaron |
| `docs/...` | Ninguno (no va en changelog) |

### 3. Clasificar por Tipo de Cambio

Mapear el prefijo del commit al tipo de sección del changelog:

| Prefijo del Commit | Sección del Changelog | Emoji |
|---|---|---|
| `feat:` / `feat(scope):` | Features | ✨ |
| `fix:` / `fix(scope):` | Bug Fixes | 🐛 |
| `refactor:` / `change:` | Changes | ♻️ |
| `remove:` / `deprecate:` | Removed | 🗑️ |
| `docs:` | — (no incluir en changelog) | — |
| `build:` / `ci:` | — (no incluir en changelog) | — |
| `chore:` | — (no incluir en changelog) | — |

### 4. Generar la Entrada

Para cada loader afectado, generar la entrada siguiendo el formato definido en el skill `changelog-entry`:

```markdown
## [nueva_version] - Título descriptivo del release
Breve resumen de los cambios más importantes.

### ✨ Features
* **Nombre del Feature**: Descripción user-facing del cambio.

### 🐛 Bug Fixes
* **Nombre del Fix**: Descripción de lo que se corrigió.
```

### 5. Determinar la Nueva Versión

Consultar `gradle.properties` para la versión actual de cada loader y sugerir el bump:
- **Patch** (1.0.4 → 1.0.5): Bug fixes, cambios menores
- **Minor** (1.0.4 → 1.1.0): Nuevas features retrocompatibles
- **Major** (1.0.4 → 2.0.0): Breaking changes en la API

## Reglas de Generación

1. **Lenguaje humano**: Los commits son técnicos. El changelog debe ser **user-facing**. Transformar "fix null check in onPluginMessage" → "Fixed crash when receiving malformed key packets".

2. **Agrupar commits relacionados**: Si varios commits son parte de una misma feature, fusionarlos en un solo bullet point del changelog.

3. **No incluir cambios internos**: Refactors, CI changes, y doc updates no van en el changelog público a menos que afecten al usuario.

4. **Incluir Requirements** solo si las dependencias cambiaron.

5. **Cada loader tiene su changelog**: No generar un changelog unificado. Separar por `CHANGELOG_FABRIC.md`, `CHANGELOG_NEOFORGE.md`, `CHANGELOG_SPIGOT.md`.

## Output Esperado

Al ejecutar la generación, producir:
1. Las entradas de changelog propuestas para cada loader afectado
2. Sugerencia de versión nueva para cada loader
3. Preguntar al usuario para confirmar antes de escribir los archivos

## Ubicación de los Archivos

Escribir en:
- `Changelogs/CHANGELOG_FABRIC.md`
- `Changelogs/CHANGELOG_NEOFORGE.md`
- `Changelogs/CHANGELOG_SPIGOT.md`

Insertar la nueva entrada **después de la línea del título** (`# Changelog - ...`) y **antes de la primera entrada existente**.
