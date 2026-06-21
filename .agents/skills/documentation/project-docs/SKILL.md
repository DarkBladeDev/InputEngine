---
name: project-docs
description: Defines the structure, format, and style of the general documentation for the InputEngine project.
trigger: When a .md file is created or modified inside docs/.
---

# Project Documentation

## Documentation Structure

```
docs/
├── README.md              # Main documentation index
├── client/
│   └── README.md          # Client-side mods documentation
└── server/
    └── README.md          # Server-side plugin documentation
```

Additionally:
- `README.md` (root) — General project description, installation, and API example
- `Changelogs/` — Changelogs by loader (see `changelog-entry` skill)

## Writing Style

### Audience
The documentation is aimed at **two audiences**:
1. **End users**: Players who install the mod (installation sections)
2. **Developers**: Plugin programmers who use the API (API sections, code examples)

### Tone
- **Concise and direct**: Get straight to the point. Do not over-explain basic Minecraft modding concepts.
- **Action-oriented**: Use imperative verbs ("Download", "Add", "Listen").
- **Technically accurate**: Name classes, methods, and packages correctly using `code` formatting.

### Language
- Public documentation (README.md, docs/): **English**
- Internal comments and agent skills: **English** (used to be Spanish, updated per translation request)

## Markdown Format

### Headers
```markdown
# Main Title (one per file)
## Section
### Subsection
```

### Code
- Inline: `` `ClassName` ``, `` `methodName()` ``
- Blocks: Always specify the language

````markdown
```java
// Java code example
```
````

### Lists
- Use `*` for unordered lists
- Use `1.` for sequential steps
- **Bold** for the key concept of each item, followed by a description

### Links
- Internal: `[link text](relative/path.md)`
- External: Full URL
- Module references: **bold** with backticks (e.g., **`client-fabric`**)

## Documentation Rules

1. **Do not duplicate information**: If something is documented in the root README, do not repeat it in `docs/`. Reference it with a link.

2. **Functional examples**: Every code example must be **compilable and functional**. Do not use pseudocode in public documentation.

3. **Keep synchronized**: If the API changes, update the documentation in the same commit/PR.

4. **Consistent structure**: Each module README must have:
   - Descriptive title
   - Brief description of purpose
   - Relevant sections (functionality, modules, requirements)

5. **Badges**: Only in the root README. Currently: CodeFactor.

## Module Documentation Template

```markdown
# [Module Name] Documentation

Brief description of what this module does and its role in the project.

## Overview

High-level explanation of functionality.

## Setup / Installation

Steps specific to this module.

## Usage

### [Feature 1]
Description + code example.

### [Feature 2]
Description + code example.

## Configuration

Any configuration options available.

## Requirements

- Required dependencies
- Compatible versions
```

## Checklist When Creating/Modifying Docs

- [ ] Does the document have a single `#` (h1) as the title?
- [ ] Are code examples functional and compilable?
- [ ] Is correct markdown formatting used (lists, code, bold)?
- [ ] Are correct internal links maintained?
- [ ] Is the language consistent (English for public docs)?
