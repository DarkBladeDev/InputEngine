---
name: release-checklist
description: Checklist automatizado para preparar un release de InputEngine, verificando que todos los pasos estén completos antes de publicar.
trigger: Cuando el usuario indica que quiere hacer un release, publicar en Modrinth, o preparar una nueva versión para distribución.
---

# Release Checklist

## Pre-Release Checklist

Ejecutar en orden. No publicar hasta que todos los items estén ✅.

### 1. Código

- [ ] **Todos los cambios mergeados**: No hay PRs pendientes ni branches de feature sin mergear.
- [ ] **Build exitoso**: `./gradlew clean build` pasa sin errores.
- [ ] **CI verde**: GitHub Actions build y load tests pasan en la branch actual.
- [ ] **Sin warnings críticos**: Revisar output del build por deprecation warnings o errores ignorados.

### 2. Versiones

- [ ] **Versiones bumpeadas**: `gradle.properties` tiene las versiones correctas para este release.
  ```properties
  common_version=X.Y.Z
  fabric_version=X.Y.Z
  neoforge_version=X.Y.Z
  spigot_version=X.Y.Z
  ```
- [ ] **Versiones en metadata**: Los archivos de metadata reflejan la versión correcta:
  - `fabric.mod.json` — usa `${version}` (se reemplaza en build)
  - `neoforge.mods.toml` — usa `${version}` (se reemplaza en build)
  - `plugin.yml` — usa `${version}` (se reemplaza en build)
- [ ] **Compatibilidad de MC version**: `minecraft_version` en `gradle.properties` corresponde a la versión target.

### 3. Changelogs

- [ ] **Changelogs actualizados**: Cada loader afectado tiene su entrada en `Changelogs/`:
  - `CHANGELOG_FABRIC.md` (si Fabric fue modificado)
  - `CHANGELOG_NEOFORGE.md` (si NeoForge fue modificado)
  - `CHANGELOG_SPIGOT.md` (si Spigot fue modificado)
- [ ] **Formato correcto**: Las entradas siguen el formato definido en el skill `changelog-entry`.
- [ ] **Versión correcta**: La versión en el changelog coincide con `gradle.properties`.

### 4. Documentación

- [ ] **README actualizado**: Si la API cambió, el README root tiene ejemplos actualizados.
- [ ] **Docs actualizados**: Los archivos en `docs/` reflejan los cambios.
- [ ] **example-plugin actualizado**: Si la API cambió, el plugin de ejemplo está al día.

### 5. Artefactos

- [ ] **JARs generados correctamente**: Verificar que existan en los directorios de build:
  ```
  client-fabric/build/libs/input-engine-fabric-{version}-{branch}.jar
  client-neoforge/build/libs/input-engine-neoforge-{version}-{branch}.jar
  plugin-spigot/build/libs/input-engine-spigot-{version}-{branch}.jar
  ```
- [ ] **Nombre correcto**: Los artefactos siguen la convención `input-engine-{loader}-{version}-{branch}.jar`.
- [ ] **Tamaño razonable**: Los JARs no tienen tamaño inusualmente grande o pequeño.

### 6. Git

- [ ] **Commit de release**: Crear un commit limpio con los cambios de versión y changelogs.
  ```bash
  git add gradle.properties Changelogs/
  git commit -m "build: release vX.Y.Z"
  ```
- [ ] **Tag creado**: Crear un tag de versión.
  ```bash
  git tag vX.Y.Z
  git push origin vX.Y.Z
  ```
- [ ] **Push**: Todos los commits y tags pusheados al remote.

## Publicación en Modrinth

### Para cada loader:

#### Fabric Client
- [ ] Subir `input-engine-fabric-{version}-{branch}.jar`
- [ ] Seleccionar versiones de Minecraft compatibles (actualmente 1.21.x)
- [ ] Marcar como "Release" (no beta ni alpha)
- [ ] Copiar el contenido de `CHANGELOG_FABRIC.md` como descripción del release
- [ ] Dependencias: Fabric API (required), Fabric Loader (required)

#### NeoForge Client
- [ ] Subir `input-engine-neoforge-{version}-{branch}.jar`
- [ ] Seleccionar versiones de Minecraft compatibles
- [ ] Marcar como "Release"
- [ ] Copiar el contenido de `CHANGELOG_NEOFORGE.md`
- [ ] Dependencias: NeoForge (required)

#### Spigot Plugin
- [ ] Subir `input-engine-spigot-{version}-{branch}.jar`
- [ ] Seleccionar versiones de Minecraft compatibles
- [ ] Marcar como "Release"
- [ ] Copiar el contenido de `CHANGELOG_SPIGOT.md`
- [ ] Sin dependencias externas

## Post-Release

- [ ] **Verificar descarga**: Descargar cada artefacto de Modrinth y confirmar que funciona.
- [ ] **Anunciar**: Si hay canales de comunicación (Discord, etc.), anunciar el release.
- [ ] **Preparar próximo ciclo**: Evaluar si hay features pendientes para el próximo release.

## Comando Rápido de Verificación

Para verificar rápidamente que todo está en orden antes del release:

```bash
# Clean build completo
./gradlew clean build

# Verificar artefactos generados
ls client-fabric/build/libs/
ls client-neoforge/build/libs/
ls plugin-spigot/build/libs/

# Verificar versiones
grep "_version=" gradle.properties

# Verificar estado de git
git status
git log --oneline -5
```
