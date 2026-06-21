---
name: changelog-entry
description: Defines the exact format and conventions for creating changelog entries per loader in the InputEngine project.
trigger: When asked to create or update a changelog, or when closing a development milestone.
---

# Changelog Entries

## Changelogs Structure

InputEngine maintains **one changelog per loader**:

```
Changelogs/
├── CHANGELOG_FABRIC.md       # Fabric mod changes
├── CHANGELOG_NEOFORGE.md     # NeoForge mod changes
└── CHANGELOG_SPIGOT.md       # Spigot plugin changes
```

## Changelog Format

Each file follows this format:

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

## Conventions

### Loader Names
| Loader | Name in Changelog |
|---|---|
| `client-fabric` | `Fabric Client` |
| `client-neoforge` | `NeoForge Client` |
| `plugin-spigot` | `Spigot Plugin` |

### Versioning
- Format: `[major.minor.patch]`
- Versions may differ between loaders (e.g., NeoForge can be on 1.0.5 while Fabric is on 1.0.4)
- Always include a brief description after the version

### Emoji Sections
Always use these sections in this order (omit those that do not apply):

| Section | Emoji | When to use it |
|---|---|---|
| Features | ✨ | New functionality |
| Bug Fixes | 🐛 | Bug corrections |
| Changes | ♻️ | Changes to existing functionality |
| Removed | 🗑️ | Removed functionality |
| Requirements | 🔧 | Required dependencies |
| Developer Notes | 🛠️ | Notes for plugin developers |

### Item Format
- Each item starts with `*` (bullet point)
- **Bold** for the name/concept of the change
- Followed by `:` and a clear description
- One line per item

### Chronological Order
- The most recent entries go **at the top** (after the `# Changelog - ...` title)
- Each new version is inserted before the previous ones

## Real Project Example

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

## Rules

1. **One changelog per loader**: Do not mix Fabric changes with NeoForge. If a change affects both, create an entry in each changelog.
2. **Changes in `common/`**: Document in all changelogs affected by the change.
3. **Be specific**: "Fixed key detection" is bad. "Fixed keybind state not resetting when player disconnects" is good.
4. **User perspective**: Write from what the user experiences, not from internal technical details.

## Checklist When Creating Changelog Entry

- [ ] Is the entry in the correct changelog according to the affected loader?
- [ ] Is the version format correct `[x.y.z]`?
- [ ] Were the correct emojis used for each section?
- [ ] Does each item have a bold name + description?
- [ ] Was the entry inserted in chronological order (most recent at the top)?
- [ ] Were separate entries created for each affected loader?
