---
name: commit-message
description: Standardizes the commit message format following Conventional Commits adapted to the InputEngine project.
trigger: When the user requests to create a commit, stage changes, or asks for help with a commit message.
---

# Commit Messages

## Format

Follow **Conventional Commits** with a mandatory scope that reflects the affected module:

```
<type>(<scope>): <concise description>

[optional body]

[optional footer]
```

## Types

| Type | When to use it | Example |
|---|---|---|
| `feat` | New functionality | `feat(fabric): add dynamic keybind registration` |
| `fix` | Bug correction | `fix(spigot): prevent NPE on player disconnect` |
| `refactor` | Restructuring without functional change | `refactor(common): extract constants to NetworkConstants` |
| `docs` | Documentation only | `docs: update API usage example in README` |
| `build` | Build/CI changes | `build(gradle): add git branch to artifact name` |
| `style` | Formatting, whitespace, imports | `style(neoforge): organize imports` |
| `test` | Add or modify tests | `test(ci): add headless load test for NeoForge` |
| `chore` | Maintenance tasks | `chore: update .gitignore` |

## Scopes

The scope must reflect the affected module or area:

| Scope | Module |
|---|---|
| `common` | `common/` |
| `fabric` | `client-fabric/` |
| `neoforge` | `client-neoforge/` |
| `spigot` | `plugin-spigot/` |
| `example` | `example-plugin/` |
| `gradle` | Build files (build.gradle.kts, gradle.properties) |
| `ci` | GitHub Actions workflows |
| `docs` | Documentation (docs/, README.md) |

### Multiple Scopes

If the change affects multiple modules:
- If it affects Fabric + NeoForge due to synchronization: `feat(client): ...` (use "client" as a generic scope)
- If it affects the whole project: omit scope or use root: `build: ...`
- Never list multiple scopes separated by commas

## Description

- **Lowercase** at the beginning (not capitalized)
- **No period** at the end
- **Imperative**: "add", "fix", "remove", "update" (not "added", "fixes", "removing")
- **Concise**: maximum ~72 characters on the first line
- **In English**: Commits are always in English

## Body (Optional)

Use when the "why" is not obvious from the description:

```
feat(neoforge): add dynamic translation support via mixin

The NeoForge translation system uses Language.getInstance() instead of
TranslationStorage, requiring a different mixin target. This mixin
intercepts getOrDefault() to inject server-sent translations.
```

## Footer (Optional)

For breaking changes or issue references:

```
feat(spigot): redesign PlayerKeyPressEvent API

BREAKING CHANGE: getAction() renamed to getActionId() for clarity.
The return type changed from KeyAction enum to String.

Closes #42
```

## Real Examples for InputEngine

```bash
# New feature in a loader
feat(fabric): add vanilla server compatibility check

# Fix affecting the server
fix(spigot): handle malformed UTF-8 in keystroke payload

# Change in common affecting all
feat(common): add CONFIG_PATH constant for keybind config channel

# Synchronization between loaders
feat(client): implement dynamic keybind categories via reflection

# Build changes
build(gradle): centralize version properties in gradle.properties

# Documentation
docs: add developer API usage section to README

# CI
build(ci): add MC-Runtime-Test for headless load testing
```

## Anti-Patterns

❌ `Update files` — Too vague
❌ `fix bug` — Which bug?
❌ `WIP` — Do not commit work in progress to the main branch
❌ `feat: Added new feature for fabric and neoforge` — Missing scope, past tense, too generic
❌ `FEAT(FABRIC): ADD KEYBINDS.` — Do not use uppercase or trailing period

## Rules

1. **Always include type and scope** (except for `docs`, `chore` where scope is optional)
2. **One commit = one logical change**: Do not mix fix + feat in the same commit
3. **Language: English** for commit messages
4. **If there is a breaking change**: Add `BREAKING CHANGE:` in the footer
