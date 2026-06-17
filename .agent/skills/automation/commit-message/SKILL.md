---
name: commit-message
description: Estandariza el formato de mensajes de commit siguiendo Conventional Commits adaptado al proyecto InputEngine.
trigger: Cuando el usuario solicita crear un commit, hacer staging de cambios, o pide ayuda con un mensaje de commit.
---

# Commit Messages

## Formato

Seguir **Conventional Commits** con scope obligatorio que refleje el módulo afectado:

```
<tipo>(<scope>): <descripción concisa>

[cuerpo opcional]

[footer opcional]
```

## Tipos

| Tipo | Cuándo usarlo | Ejemplo |
|---|---|---|
| `feat` | Nueva funcionalidad | `feat(fabric): add dynamic keybind registration` |
| `fix` | Corrección de bug | `fix(spigot): prevent NPE on player disconnect` |
| `refactor` | Reestructuración sin cambio funcional | `refactor(common): extract constants to NetworkConstants` |
| `docs` | Solo documentación | `docs: update API usage example in README` |
| `build` | Cambios en build/CI | `build(gradle): add git branch to artifact name` |
| `style` | Formato, whitespace, imports | `style(neoforge): organize imports` |
| `test` | Agregar o modificar tests | `test(ci): add headless load test for NeoForge` |
| `chore` | Tareas de mantenimiento | `chore: update .gitignore` |

## Scopes

El scope debe reflejar el módulo o área afectada:

| Scope | Módulo |
|---|---|
| `common` | `common/` |
| `fabric` | `client-fabric/` |
| `neoforge` | `client-neoforge/` |
| `spigot` | `plugin-spigot/` |
| `example` | `example-plugin/` |
| `gradle` | Archivos de build (build.gradle.kts, gradle.properties) |
| `ci` | GitHub Actions workflows |
| `docs` | Documentación (docs/, README.md) |

### Múltiples Scopes

Si el cambio afecta múltiples módulos:
- Si afecta Fabric + NeoForge por sincronización: `feat(client): ...` (usar "client" como scope genérico)
- Si afecta todo el proyecto: omitir scope o usar root: `build: ...`
- Nunca listar múltiples scopes separados por coma

## Descripción

- **Minúscula** al inicio (sin capitalizar)
- **Sin punto** al final
- **Imperativo**: "add", "fix", "remove", "update" (no "added", "fixes", "removing")
- **Conciso**: máximo ~72 caracteres en la primera línea
- **En inglés**: Los commits siempre en inglés

## Cuerpo (Opcional)

Usar cuando el "por qué" no es obvio desde la descripción:

```
feat(neoforge): add dynamic translation support via mixin

The NeoForge translation system uses Language.getInstance() instead of
TranslationStorage, requiring a different mixin target. This mixin
intercepts getOrDefault() to inject server-sent translations.
```

## Footer (Opcional)

Para breaking changes o referencias a issues:

```
feat(spigot): redesign PlayerKeyPressEvent API

BREAKING CHANGE: getAction() renamed to getActionId() for clarity.
The return type changed from KeyAction enum to String.

Closes #42
```

## Ejemplos Reales para InputEngine

```bash
# Feature nueva en un loader
feat(fabric): add vanilla server compatibility check

# Fix que afecta al server
fix(spigot): handle malformed UTF-8 in keystroke payload

# Cambio en common que afecta a todos
feat(common): add CONFIG_PATH constant for keybind config channel

# Sincronización entre loaders
feat(client): implement dynamic keybind categories via reflection

# Build changes
build(gradle): centralize version properties in gradle.properties

# Documentation
docs: add developer API usage section to README

# CI
build(ci): add MC-Runtime-Test for headless load testing
```

## Anti-Patrones

❌ `Update files` — Demasiado vago
❌ `fix bug` — ¿Qué bug?
❌ `WIP` — No commitear trabajo en progreso al branch principal
❌ `feat: Added new feature for fabric and neoforge` — Scope faltante, pasado, demasiado genérico
❌ `FEAT(FABRIC): ADD KEYBINDS.` — No usar mayúsculas ni punto final

## Reglas

1. **Siempre incluir tipo y scope** (excepto para `docs`, `chore` donde el scope es opcional)
2. **Un commit = un cambio lógico**: No mezclar fix + feat en un mismo commit
3. **Idioma: inglés** para los mensajes de commit
4. **Si hay breaking change**: Agregar `BREAKING CHANGE:` en el footer
