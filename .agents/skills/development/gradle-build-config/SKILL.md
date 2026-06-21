---
name: gradle-build-config
description: Conventions and patterns for configuring the Gradle multi-project build system for InputEngine.
trigger: When modifying build.gradle.kts, settings.gradle.kts, or gradle.properties at any project level.
---

# Gradle Build Configuration

## Build Structure

InputEngine uses **Gradle Kotlin DSL** with a multi-project setup:

```
settings.gradle.kts          # Defines modules and plugin repositories
build.gradle.kts              # Root: global configuration (allprojects)
├── common/build.gradle.kts   # Pure Java module
├── client-fabric/build.gradle.kts   # Fabric Loom
├── client-neoforge/build.gradle.kts # NeoForge ModDev
├── plugin-spigot/build.gradle.kts   # Pure Java + Spigot API
└── example-plugin/build.gradle.kts  # Pure Java + Spigot API + InputEngine
```

## gradle.properties — Source of Truth

All versions are centralized in `gradle.properties`:

```properties
# Minecraft / Loader versions
minecraft_version=1.21.8
yarn_mappings=1.21.8+build.1
loader_version=0.19.3
loom_version=1.17-SNAPSHOT

# Mod versions (can be independent of each other)
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

**Rule**: Never hardcode versions in `build.gradle.kts`. Always use `providers.gradleProperty("key")`.

## Build Patterns

### Git Branch in Artifact Name

The root `build.gradle.kts` defines a `gitBranch` extra property:

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

Each subproject consumes it:
```kotlin
val gitBranch: String by rootProject.extra
version = "${providers.gradleProperty("fabric_version").get()}-$gitBranch"
```

**Result**: `input-engine-fabric-1.0.4-1.21.8.jar`

### Archives Name

Each module defines its `archivesName`:
```kotlin
base {
    archivesName.set("input-engine-fabric")  // or neoforge, spigot
}
```

### Including Common in JARs

All modules include the output of `common/` in their JAR:
```kotlin
tasks.withType<Jar> {
    from(project(":common").sourceSets.main.get().output)
}
```

### Process Resources with Expand

Metadata files use `${version}` which is replaced at build time:
```kotlin
tasks.processResources {
    val version = project.version.toString()
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {  // or plugin.yml, neoforge.mods.toml
        expand("version" to version)
    }
}
```

## Global Configuration (allprojects)

The root `build.gradle.kts` applies to all subprojects:
- `java` plugin
- Java 21 (sourceCompatibility, targetCompatibility, options.release)
- Repositories: Maven Central, SpigotMC Nexus, Sonatype

## Plugins by Module

| Module | Plugin |
|---|---|
| `common` | `java` |
| `client-fabric` | `net.fabricmc.fabric-loom-remap` |
| `client-neoforge` | `java` + `net.neoforged.moddev` |
| `plugin-spigot` | `java` |
| `example-plugin` | `java` |

## Rules When Modifying the Build

1. **Versions in `gradle.properties`**: Any new dependency or version goes as a property.
2. **Do not break `gitBranch` resolution**: The extra property is defined in root and consumed via `by rootProject.extra`.
3. **Maintain `processResources`**: If a new metadata file is added, add the corresponding `filesMatching`.
4. **Java 21**: Do not change the compilation target without updating all modules.
5. **Repositories**: Add new repos in `allprojects` of the root, not in individual subprojects (except for loader-specific repos).

## Useful Commands

```bash
# Full build
./gradlew build

# Build a specific module
./gradlew :client-fabric:build
./gradlew :client-neoforge:build
./gradlew :plugin-spigot:build

# Clean
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies build
```

## Checklist When Modifying Build

- [ ] Are versions in `gradle.properties` (not hardcoded)?
- [ ] Does the `archivesName` follow the `input-engine-{loader}` convention?
- [ ] Was `common` included in the JAR if the module needs it?
- [ ] Does `processResources` expand `${version}` in metadata files?
- [ ] Does the project compile with `./gradlew build`?
