---
name: gradle-build-config
description: Convenciones y patrones para la configuración del sistema de build Gradle multi-proyecto de InputEngine.
trigger: Cuando se modifica build.gradle.kts, settings.gradle.kts, o gradle.properties en cualquier nivel del proyecto.
---

# Gradle Build Configuration

## Estructura del Build

InputEngine usa **Gradle Kotlin DSL** con un setup multi-proyecto:

```
settings.gradle.kts          # Define módulos y plugin repositories
build.gradle.kts              # Root: configuración global (allprojects)
├── common/build.gradle.kts   # Módulo Java puro
├── client-fabric/build.gradle.kts   # Fabric Loom
├── client-neoforge/build.gradle.kts # NeoForge ModDev
├── plugin-spigot/build.gradle.kts   # Java puro + Spigot API
└── example-plugin/build.gradle.kts  # Java puro + Spigot API + InputEngine
```

## gradle.properties — Fuente de Verdad

Todas las versiones se centralizan en `gradle.properties`:

```properties
# Minecraft / Loader versions
minecraft_version=1.21.8
yarn_mappings=1.21.8+build.1
loader_version=0.19.3
loom_version=1.17-SNAPSHOT

# Mod versions (pueden ser independientes entre sí)
common_version=1.0.4
fabric_version=1.0.4
neoforge_version=1.0.5
spigot_version=1.0.4

# Group
maven_group=dev.darkblade.mod.input_engine

# Dependencies
fabric_api_version=0.136.1+1.21.8
neoforge_loader_version=21.8.46
```

**Regla**: Nunca hardcodear versiones en `build.gradle.kts`. Siempre usar `providers.gradleProperty("key")`.

## Patrones del Build

### Git Branch en el Nombre del Artefacto

El root `build.gradle.kts` define un `gitBranch` extra property:

```kotlin
val gitBranch: String by extra {
    try {
        val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD").start()
        process.inputStream.bufferedReader().readText().trim().let {
            if (it == "HEAD" || it.isEmpty()) "unknown" else it
        }
    } catch (e: Exception) { "unknown" }
}
```

Cada subproyecto lo consume:
```kotlin
val gitBranch: String by rootProject.extra
version = "${providers.gradleProperty("fabric_version").get()}-$gitBranch"
```

**Resultado**: `input-engine-fabric-1.0.4-1.21.8.jar`

### Archives Name

Cada módulo define su `archivesName`:
```kotlin
base {
    archivesName.set("input-engine-fabric")  // o neoforge, spigot
}
```

### Inclusión de Common en JARs

Todos los módulos incluyen el output de `common/` en su JAR:
```kotlin
tasks.withType<Jar> {
    from(project(":common").sourceSets.main.get().output)
}
```

### Process Resources con Expand

Los metadata files usan `${version}` que se reemplaza en build time:
```kotlin
tasks.processResources {
    val version = project.version.toString()
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {  // o plugin.yml, neoforge.mods.toml
        expand("version" to version)
    }
}
```

## Configuración Global (allprojects)

El root `build.gradle.kts` aplica a todos los subproyectos:
- Plugin `java`
- Java 21 (sourceCompatibility, targetCompatibility, options.release)
- Repositories: Maven Central, SpigotMC Nexus, Sonatype

## Plugins por Módulo

| Módulo | Plugin |
|---|---|
| `common` | `java` |
| `client-fabric` | `net.fabricmc.fabric-loom-remap` |
| `client-neoforge` | `java` + `net.neoforged.moddev` |
| `plugin-spigot` | `java` |
| `example-plugin` | `java` |

## Reglas al Modificar el Build

1. **Versiones en `gradle.properties`**: Toda nueva dependencia o versión va como property.
2. **No romper la resolución de `gitBranch`**: El extra property se define en root y se consume via `by rootProject.extra`.
3. **Mantener `processResources`**: Si se agrega un nuevo metadata file, agregar el `filesMatching` correspondiente.
4. **Java 21**: No cambiar el target de compilación sin actualizar todos los módulos.
5. **Repositories**: Agregar nuevos repos en `allprojects` del root, no en subproyectos individuales (excepto repos específicos del loader).

## Comandos Útiles

```bash
# Build completo
./gradlew build

# Build de un módulo específico
./gradlew :client-fabric:build
./gradlew :client-neoforge:build
./gradlew :plugin-spigot:build

# Clean
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies build
```

## Checklist al Modificar Build

- [ ] ¿Las versiones están en `gradle.properties` (no hardcodeadas)?
- [ ] ¿El `archivesName` sigue la convención `input-engine-{loader}`?
- [ ] ¿Se incluyó `common` en el JAR si el módulo lo necesita?
- [ ] ¿El `processResources` expande `${version}` en los metadata files?
- [ ] ¿El proyecto compila con `./gradlew build`?
