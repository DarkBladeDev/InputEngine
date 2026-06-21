---
name: changelog-generator
description: Automates the generation of changelogs from git commits, grouping them by type and loader.
trigger: When the user asks to generate a changelog, uses the /changelog command, or indicates they want to create release notes.
---

# Changelog Generator

## Purpose

Automatically generate changelog entries for each loader (Fabric, NeoForge, Spigot) based on git commits since the last tag or release.

## Workflow

### 1. Determine the Commit Range

Get commits since the last tag:
```bash
git log --oneline $(git describe --tags --abbrev=0)..HEAD
```

If there are no previous tags:
```bash
git log --oneline
```

### 2. Classify Commits by Module

Analyze the modified files in each commit to determine which loaders are affected:

| Modified Files | Affected Loader(s) |
|---|---|
| `common/...` | All (Fabric, NeoForge, Spigot) |
| `client-fabric/...` | Fabric |
| `client-neoforge/...` | NeoForge |
| `plugin-spigot/...` | Spigot |
| `example-plugin/...` | Spigot (documentation) |
| `build.gradle.kts` (root) | All |
| `gradle.properties` | Depends on which properties changed |
| `docs/...` | None (does not go in changelog) |

### 3. Classify by Change Type

Map the commit prefix to the changelog section type:

| Commit Prefix | Changelog Section | Emoji |
|---|---|---|
| `feat:` / `feat(scope):` | Features | ✨ |
| `fix:` / `fix(scope):` | Bug Fixes | 🐛 |
| `refactor:` / `change:` | Changes | ♻️ |
| `remove:` / `deprecate:` | Removed | 🗑️ |
| `docs:` | — (do not include in changelog) | — |
| `build:` / `ci:` | — (do not include in changelog) | — |
| `chore:` | — (do not include in changelog) | — |

### 4. Generate the Entry

For each affected loader, generate the entry following the format defined in the `changelog-entry` skill:

```markdown
## [new_version] - Descriptive release title
Brief summary of the most important changes.

### ✨ Features
* **Feature Name**: User-facing description of the change.

### 🐛 Bug Fixes
* **Fix Name**: Description of what was fixed.
```

### 5. Determine the New Version

Consult `gradle.properties` for the current version of each loader and suggest the bump:
- **Patch** (1.0.4 → 1.0.5): Bug fixes, minor changes
- **Minor** (1.0.4 → 1.1.0): New backward-compatible features
- **Major** (1.0.4 → 2.0.0): Breaking changes in the API

## Generation Rules

1. **Human language**: Commits are technical. The changelog must be **user-facing**. Transform "fix null check in onPluginMessage" → "Fixed crash when receiving malformed key packets".

2. **Group related commits**: If several commits are part of the same feature, merge them into a single changelog bullet point.

3. **Do not include internal changes**: Refactors, CI changes, and doc updates do not go in the public changelog unless they affect the user.

4. **Include Requirements** only if dependencies changed.

5. **Each loader has its changelog**: Do not generate a unified changelog. Separate by `CHANGELOG_FABRIC.md`, `CHANGELOG_NEOFORGE.md`, `CHANGELOG_SPIGOT.md`.

## Expected Output

When running generation, produce:
1. Proposed changelog entries for each affected loader
2. New version suggestion for each loader
3. Ask the user for confirmation before writing the files

## File Locations

Write to:
- `Changelogs/CHANGELOG_FABRIC.md`
- `Changelogs/CHANGELOG_NEOFORGE.md`
- `Changelogs/CHANGELOG_SPIGOT.md`

Insert the new entry **after the title line** (`# Changelog - ...`) and **before the first existing entry**.
