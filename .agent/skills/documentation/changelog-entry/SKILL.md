---
name: changelog-entry
description: Define el formato exacto y las convenciones para crear entradas de changelog por loader en el proyecto InputEngine.
trigger: Cuando se pide crear o actualizar un changelog, o al cerrar un milestone de desarrollo.
---

# Changelog Entries

## Estructura de Changelogs

InputEngine mantiene **un changelog por loader**:

```
Changelogs/
├── CHANGELOG_FABRIC.md       # Cambios del mod Fabric
├── CHANGELOG_NEOFORGE.md     # Cambios del mod NeoForge
└── CHANGELOG_SPIGOT.md       # Cambios del plugin Spigot
```

## Formato de un Changelog

Cada archivo sigue este formato:

```markdown
# Changelog - [Loader Name]

## [version] - Description
Brief summary of what this release is about.

### ✨ Features
* **Feature Name**: Description of the feature.

### 🐛 Bug Fixes
* **Fix Name**: Description of what was fixed.

### ♻️ Changes
* **Change Name**: Description of what changed.

### 🗑️ Removed
* **Removed Feature**: Description of what was removed and why.

### 🔧 Requirements
* Requires **Dependency Name**.
```

## Convenciones

### Nombres de Loader
| Loader | Nombre en Changelog |
|---|---|
| `client-fabric` | `Fabric Client` |
| `client-neoforge` | `NeoForge Client` |
| `plugin-spigot` | `Spigot Plugin` |

### Versionado
- Formato: `[major.minor.patch]`
- Las versiones pueden diferir entre loaders (ej: NeoForge puede estar en 1.0.5 mientras Fabric está en 1.0.4)
- Siempre incluir descripción breve después de la versión

### Secciones con Emojis
Usar siempre estas secciones en este orden (omitir las que no apliquen):

| Sección | Emoji | Cuándo usarla |
|---|---|---|
| Features | ✨ | Nueva funcionalidad |
| Bug Fixes | 🐛 | Corrección de errores |
| Changes | ♻️ | Cambios en funcionalidad existente |
| Removed | 🗑️ | Funcionalidad eliminada |
| Requirements | 🔧 | Dependencias necesarias |
| Developer Notes | 🛠️ | Notas para desarrolladores de plugins |

### Formato de Items
- Cada item empieza con `*` (bullet point)
- **Bold** para el nombre/concepto del cambio
- Seguido de `:` y descripción clara
- Una línea por item

### Orden Cronológico
- Las entradas más recientes van **arriba** (después del título `# Changelog - ...`)
- Cada nueva versión se inserta antes de las anteriores

## Ejemplo Real del Proyecto

```markdown
# Changelog - Fabric Client

## [1.0.4] - Initial Modrinth Release
This is the first official release of the InputEngine Fabric client mod on Modrinth!

### ✨ Features
* **Keybind Registration**: Added custom keybinds for various in-game actions that can be configured in the Minecraft controls menu.
* **Server Communication**: Added a lightweight network payload system to transmit key press and release states to the server in real-time.
* **Vanilla Friendly**: Safely connects to vanilla servers without the server-side plugin installed. The mod disables networking if the server doesn't support it.
* **Version Support**: Compiled and tested for Minecraft 1.21.x.

### 🔧 Requirements
* Requires **Fabric Loader**.
* Requires **Fabric API**.
```

## Reglas

1. **Un changelog por loader**: No mezclar cambios de Fabric con NeoForge. Si un cambio afecta a ambos, crear una entrada en cada changelog.
2. **Cambios en `common/`**: Documentar en todos los changelogs que se vean afectados por el cambio.
3. **Ser específico**: "Fixed key detection" es malo. "Fixed keybind state not resetting when player disconnects" es bueno.
4. **Perspectiva del usuario**: Escribir desde lo que el usuario experimenta, no desde los detalles técnicos internos.

## Checklist al Crear Changelog Entry

- [ ] ¿La entrada está en el changelog correcto según el loader afectado?
- [ ] ¿El formato de versión es correcto `[x.y.z]`?
- [ ] ¿Se usaron los emojis correctos para cada sección?
- [ ] ¿Cada item tiene nombre en bold + descripción?
- [ ] ¿La entrada se insertó en orden cronológico (más reciente arriba)?
- [ ] ¿Se crearon entradas separadas para cada loader afectado?
