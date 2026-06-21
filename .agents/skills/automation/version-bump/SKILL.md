---
name: version-bump
description: Guide to updating project versions in gradle.properties correctly and synchronously.
trigger: When the user requests to bump the version, when preparing a release, or when a set of changes justifying a new version has been completed.
---

# Version Bump

## Version Location

All versions are defined in `gradle.properties`:

```properties
common_version=1.0.4
fabric_version=1.0.4
neoforge_version=1.0.5
spigot_version=1.0.4
```

Each module consumes its version from here:
```kotlin
// In each module's build.gradle.kts
version = "${providers.gradleProperty("fabric_version").get()}-$gitBranch"
```

## Versioning Semantics (SemVer)

```
MAJOR.MINOR.PATCH
  │     │     └── Bug fixes, minor changes that don't affect the API
  │     └──────── New backward-compatible features
  └────────────── Breaking changes in the public API
```

### When to do each type of bump

| Type | When | Example |
|---|---|---|
| **PATCH** | Bug fixes, internal improvements, refactors | 1.0.4 → 1.0.5 |
| **MINOR** | New backward-compatible feature (new event, new API method) | 1.0.4 → 1.1.0 |
| **MAJOR** | Breaking change in API (rename event, change method signature) | 1.0.4 → 2.0.0 |

## Independent Versions per Loader

The versions of each loader **can differ**. This is intentional:

- If a fix only affects NeoForge, only `neoforge_version` is bumped
- If a feature affects Fabric and NeoForge, both are bumped
- If a change in `common/` affects everyone, all are bumped

**Rule**: `common_version` is bumped when something changes in `common/` that requires consumers to update.

## Version Bump Workflow

### 1. Identify which modules changed

Review the commits since the last version:
```bash
git log --oneline --name-only $(git describe --tags --abbrev=0)..HEAD
```

Classify the modified files by module.

### 2. Determine the bump type

For each affected module, evaluate:
- Are there breaking changes in the API? → MAJOR
- Are there new features? → MINOR
- Only fixes/refactors? → PATCH

### 3. Update `gradle.properties`

Edit only the properties of the affected modules:

```diff
-common_version=1.0.4
+common_version=1.0.5
-fabric_version=1.0.4
+fabric_version=1.0.5
 neoforge_version=1.0.5
-spigot_version=1.0.4
+spigot_version=1.0.5
```

### 4. Verify the build

```bash
./gradlew clean build
```

Confirm that the generated artifacts have the correct version in their name.

### 5. Update Changelogs

Create entries in the changelogs of the affected loaders (see `changelog-entry` skill).

### 6. Commit and Tag

```bash
git add gradle.properties Changelogs/
git commit -m "build: bump version to X.Y.Z"
git tag vX.Y.Z
```

## Version Dependencies

When bumping, also verify if it's necessary to update:

| Property | What it is | When to update |
|---|---|---|
| `minecraft_version` | Target Minecraft version | When porting to a new MC version |
| `yarn_mappings` | Fabric mappings | When `minecraft_version` changes |
| `loader_version` | Fabric Loader | When a new version is available |
| `fabric_api_version` | Fabric API | When `minecraft_version` changes |
| `neoforge_loader_version` | NeoForge | When `minecraft_version` changes |

## Rules

1. **Do not skip versions**: 1.0.4 → 1.0.5, not 1.0.4 → 1.0.7
2. **MINOR bump resets PATCH**: 1.0.4 → 1.1.0 (not 1.1.4)
3. **MAJOR bump resets MINOR and PATCH**: 1.0.4 → 2.0.0
4. **Always verify build** after the bump
5. **Separate bump commit**: Do not mix code changes with the version bump commit

## Version Bump Checklist

- [ ] Were the affected modules correctly identified?
- [ ] Is the bump type (MAJOR/MINOR/PATCH) correct?
- [ ] Were the correct properties updated in `gradle.properties`?
- [ ] Does the build compile without errors?
- [ ] Do the generated artifacts have the correct version?
- [ ] Were the changelogs created/updated?
- [ ] Was a separate commit created for the bump?
