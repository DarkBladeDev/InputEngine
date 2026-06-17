---
name: version-bump
description: Guía para actualizar las versiones del proyecto en gradle.properties de forma correcta y sincronizada.
trigger: Cuando el usuario solicita subir la versión, cuando se prepara un release, o cuando se ha completado un conjunto de cambios que justifica una nueva versión.
---

# Version Bump

## Ubicación de Versiones

Todas las versiones se definen en `gradle.properties`:

```properties
common_version=1.0.4
fabric_version=1.0.4
neoforge_version=1.0.5
spigot_version=1.0.4
```

Cada módulo consume su versión desde aquí:
```kotlin
// En build.gradle.kts de cada módulo
version = "${providers.gradleProperty("fabric_version").get()}-$gitBranch"
```

## Semántica de Versiones (SemVer)

```
MAJOR.MINOR.PATCH
  │     │     └── Bug fixes, cambios menores que no afectan la API
  │     └──────── Nuevas features retrocompatibles
  └────────────── Breaking changes en la API pública
```

### Cuándo hacer cada tipo de bump

| Tipo | Cuándo | Ejemplo |
|---|---|---|
| **PATCH** | Fix de bugs, mejoras internas, refactors | 1.0.4 → 1.0.5 |
| **MINOR** | Nueva feature retrocompatible (nuevo evento, nuevo método API) | 1.0.4 → 1.1.0 |
| **MAJOR** | Breaking change en API (renombrar evento, cambiar firma de método) | 1.0.4 → 2.0.0 |

## Versiones Independientes por Loader

Las versiones de cada loader **pueden diferir**. Esto es intencional:

- Si un fix solo afecta a NeoForge, solo se bumpa `neoforge_version`
- Si una feature afecta a Fabric y NeoForge, se bumpan ambos
- Si un cambio en `common/` afecta a todos, se bumpan todos

**Regla**: `common_version` se bumpa cuando cambia algo en `common/` que requiere que los consumidores se actualicen.

## Flujo de Trabajo para Version Bump

### 1. Identificar qué módulos cambiaron

Revisar los commits desde la última versión:
```bash
git log --oneline --name-only $(git describe --tags --abbrev=0)..HEAD
```

Clasificar los archivos modificados por módulo.

### 2. Determinar el tipo de bump

Para cada módulo afectado, evaluar:
- ¿Hay breaking changes en la API? → MAJOR
- ¿Hay nuevas features? → MINOR
- ¿Solo fixes/refactors? → PATCH

### 3. Actualizar `gradle.properties`

Editar solo las properties de los módulos afectados:

```diff
-common_version=1.0.4
+common_version=1.0.5
-fabric_version=1.0.4
+fabric_version=1.0.5
 neoforge_version=1.0.5
-spigot_version=1.0.4
+spigot_version=1.0.5
```

### 4. Verificar el build

```bash
./gradlew clean build
```

Confirmar que los artefactos generados tienen la versión correcta en su nombre.

### 5. Actualizar Changelogs

Crear entradas en los changelogs de los loaders afectados (ver skill `changelog-entry`).

### 6. Commit y Tag

```bash
git add gradle.properties Changelogs/
git commit -m "build: bump version to X.Y.Z"
git tag vX.Y.Z
```

## Dependencias de Versión

Al hacer bump, también verificar si es necesario actualizar:

| Property | Qué es | Cuándo actualizar |
|---|---|---|
| `minecraft_version` | Versión target de Minecraft | Al portar a nueva versión de MC |
| `yarn_mappings` | Mappings de Fabric | Cuando cambia `minecraft_version` |
| `loader_version` | Fabric Loader | Cuando hay nueva versión disponible |
| `fabric_api_version` | Fabric API | Cuando cambia `minecraft_version` |
| `neoforge_loader_version` | NeoForge | Cuando cambia `minecraft_version` |

## Reglas

1. **No saltear versiones**: 1.0.4 → 1.0.5, no 1.0.4 → 1.0.7
2. **MINOR bump resetea PATCH**: 1.0.4 → 1.1.0 (no 1.1.4)
3. **MAJOR bump resetea MINOR y PATCH**: 1.0.4 → 2.0.0
4. **Siempre verificar build** después del bump
5. **Commit de bump separado**: No mezclar cambios de código con el commit de version bump

## Checklist de Version Bump

- [ ] ¿Se identificaron correctamente los módulos afectados?
- [ ] ¿El tipo de bump (MAJOR/MINOR/PATCH) es correcto?
- [ ] ¿Se actualizaron las properties correctas en `gradle.properties`?
- [ ] ¿El build compila sin errores?
- [ ] ¿Los artefactos generados tienen la versión correcta?
- [ ] ¿Se crearon/actualizaron los changelogs?
- [ ] ¿Se creó un commit separado para el bump?
