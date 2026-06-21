---
name: minecraft-mod-dev
description: General conventions and patterns for Minecraft mod and plugin development within the InputEngine project.
trigger: When working on any Java file inside client-fabric/, client-neoforge/, plugin-spigot/, common/, or example-plugin/.
---

# Minecraft Mod Development — InputEngine

## Project Context

InputEngine is a cross-platform bridge that captures Minecraft client keystrokes and transmits them to the server. The project consists of:

- `common/` — Shared constants and types between client and server
- `client-fabric/` — Client-side mod for Fabric (Yarn mappings)
- `client-neoforge/` — Client-side mod for NeoForge (Mojang mappings)
- `plugin-spigot/` — Server-side plugin for Spigot/Paper
- `example-plugin/` — Example plugin demonstrating API usage

## Code Conventions

### Java
- **Version**: Java 21 (source and target). Use modern Java features like records, pattern matching, and sealed classes where applicable.
- **Base Package**: `dev.darkblade.mod.input_engine`
  - `.common` — Shared code
  - `.client` — Client-side code (Fabric and NeoForge)
  - `.client.network` — Client network payloads
  - `.server` — Server-side code (Spigot)
  - `.server.api` — Public API for plugin developers
  - `.mixin` — Mixin classes
  - `.example` — Example plugin
- **Records**: Use `record` for DTOs and immutable data carriers (e.g., `KeybindData`, `KeystrokePayload`).
- **Immutability**: Prefer `final` fields and immutable collections whenever possible.

### Naming
- Classes: Descriptive `PascalCase` (e.g., `InputEngineClient`, `PlayerKeyPressEvent`)
- Methods: `camelCase` with verbs (e.g., `registerExpectedKey`, `onPlayerJoin`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `CHANNEL_NAMESPACE`, `FULL_CHANNEL`)
- Packages: All lowercase with underscores to separate mod ID words

### Module Structure
Each module follows this structure:
```
module/
├── build.gradle.kts           # Module build config
└── src/
    └── main/
        ├── java/dev/darkblade/mod/input_engine/...
        └── resources/          # Metadata, lang files, JSON mixins
```

## Development Rules

1. **Shared code goes in `common/`**: If a constant, enum, or data type is used in both client and server, it must reside in the `common/` module.

2. **Each loader has its implementation**: Fabric and NeoForge use different APIs. Never import classes from one loader into the other. Shared code is included via `from(project(":common").sourceSets.main.get().output)` in the JAR.

3. **Network payloads**: Implement manual codecs with `PacketCodec` (Fabric) or `StreamCodec` (NeoForge). Use `StandardCharsets.UTF_8` for strings. Always write the string length before the content.

4. **Vanilla compatibility**: Network channels must be optional. The mod should not crash if the server does not have the plugin installed.

5. **Testing**: The project uses MC-Runtime-Test for headless load tests in CI. Ensure any changes pass the GitHub Actions build.

6. **Logging**:
   - Fabric: Use `LoggerFactory.getLogger()` or the mod's logger
   - NeoForge: Use `LoggerFactory.getLogger(MOD_ID)`
   - Spigot: Use `getLogger()` inherited from `JavaPlugin`

7. **Do not hardcode the mod ID**: Use `NetworkConstants.CHANNEL_NAMESPACE` and constants from the `common/` module.

## Checklist When Creating/Modifying Code

- [ ] Does the code compile with Java 21?
- [ ] Are shared constants in `common/`?
- [ ] Is vanilla compatibility maintained (optional channels)?
- [ ] Were changes in one loader reflected in the other if applicable?
- [ ] Were resources (fabric.mod.json, neoforge.mods.toml, plugin.yml) updated if necessary?
- [ ] Were translations added/updated in the lang files?
