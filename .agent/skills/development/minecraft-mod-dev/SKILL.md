---
name: minecraft-mod-dev
description: Convenciones y patrones generales para el desarrollo de mods y plugins de Minecraft dentro del proyecto InputEngine.
trigger: Cuando se trabaja en cualquier archivo Java dentro de client-fabric/, client-neoforge/, plugin-spigot/, common/, o example-plugin/.
---

# Desarrollo de Mods Minecraft — InputEngine

## Contexto del Proyecto

InputEngine es un bridge multi-plataforma que captura keystrokes del cliente Minecraft y los transmite al servidor. El proyecto se compone de:

- `common/` — Constantes y tipos compartidos entre client y server
- `client-fabric/` — Mod client-side para Fabric (Yarn mappings)
- `client-neoforge/` — Mod client-side para NeoForge (Mojang mappings)
- `plugin-spigot/` — Plugin server-side para Spigot/Paper
- `example-plugin/` — Plugin de ejemplo que demuestra el uso de la API

## Convenciones de Código

### Java
- **Versión**: Java 21 (source y target). Usar features modernas de Java como records, pattern matching, y sealed classes cuando aplique.
- **Package base**: `dev.darkblade.mod.input_engine`
  - `.common` — Código compartido
  - `.client` — Código client-side (Fabric y NeoForge)
  - `.client.network` — Payloads de red del cliente
  - `.server` — Código server-side (Spigot)
  - `.server.api` — API pública para desarrolladores de plugins
  - `.mixin` — Clases Mixin
  - `.example` — Plugin de ejemplo
- **Records**: Usar `record` para DTOs y data carriers inmutables (ej: `KeybindData`, `KeystrokePayload`).
- **Inmutabilidad**: Preferir campos `final` y colecciones inmutables siempre que sea posible.

### Naming
- Clases: `PascalCase` descriptivo (ej: `InputEngineClient`, `PlayerKeyPressEvent`)
- Métodos: `camelCase` con verbos (ej: `registerExpectedKey`, `onPlayerJoin`)
- Constantes: `UPPER_SNAKE_CASE` (ej: `CHANNEL_NAMESPACE`, `FULL_CHANNEL`)
- Packages: todo en minúsculas con underscores para separar palabras del mod ID

### Estructura de Módulo
Cada módulo sigue esta estructura:
```
modulo/
├── build.gradle.kts           # Build config del módulo
└── src/
    └── main/
        ├── java/dev/darkblade/mod/input_engine/...
        └── resources/          # Metadata, lang files, mixins JSON
```

## Reglas de Desarrollo

1. **Código compartido va en `common/`**: Si una constante, enum, o tipo de dato se usa tanto en client como en server, debe estar en el módulo `common/`.

2. **Cada loader tiene su implementación**: Fabric y NeoForge usan APIs diferentes. Nunca importar clases de un loader en el otro. El código compartido se incluye via `from(project(":common").sourceSets.main.get().output)` en el JAR.

3. **Payloads de red**: Implementar codecs manuales con `PacketCodec` (Fabric) o `StreamCodec` (NeoForge). Usar `StandardCharsets.UTF_8` para strings. Siempre escribir la longitud del string antes del contenido.

4. **Compatibilidad vanilla**: Los canales de red deben ser opcionales. El mod no debe crashear si el servidor no tiene el plugin instalado.

5. **Testing**: El proyecto usa MC-Runtime-Test para load tests headless en CI. Verificar que cualquier cambio pase el build de GitHub Actions.

6. **Logging**:
   - Fabric: Usar `LoggerFactory.getLogger()` o el logger del mod
   - NeoForge: Usar `LoggerFactory.getLogger(MOD_ID)`
   - Spigot: Usar `getLogger()` heredado de `JavaPlugin`

7. **No hardcodear el mod ID**: Usar `NetworkConstants.CHANNEL_NAMESPACE` y constantes del módulo `common/`.

## Checklist al Crear/Modificar Código

- [ ] ¿El código compila con Java 21?
- [ ] ¿Las constantes compartidas están en `common/`?
- [ ] ¿Se mantiene la compatibilidad vanilla (canales opcionales)?
- [ ] ¿Los cambios en un loader se reflejaron en el otro si aplica?
- [ ] ¿Se actualizaron los resources (fabric.mod.json, neoforge.mods.toml, plugin.yml) si es necesario?
- [ ] ¿Se añadieron/actualizaron las traducciones en los lang files?
