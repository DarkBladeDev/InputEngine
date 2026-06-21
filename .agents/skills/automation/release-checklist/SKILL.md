---
name: release-checklist
description: Automated checklist to prepare an InputEngine release, verifying that all steps are complete before publishing.
trigger: When the user indicates they want to make a release, publish on Modrinth, or prepare a new version for distribution.
---

# Release Checklist

## Pre-Release Checklist

Execute in order. Do not publish until all items are ✅.

### 1. Code

- [ ] **All changes merged**: No pending PRs or unmerged feature branches.
- [ ] **Successful build**: `./gradlew clean build` passes without errors.
- [ ] **Green CI**: GitHub Actions build and load tests pass on the current branch.
- [ ] **No critical warnings**: Check build output for deprecation warnings or ignored errors.

### 2. Versions

- [ ] **Versions bumped**: `gradle.properties` has the correct versions for this release.
  ```properties
  common_version=X.Y.Z
  fabric_version=X.Y.Z
  neoforge_version=X.Y.Z
  spigot_version=X.Y.Z
  ```
- [ ] **Versions in metadata**: Metadata files reflect the correct version:
  - `fabric.mod.json` — uses `${version}` (replaced at build time)
  - `neoforge.mods.toml` — uses `${version}` (replaced at build time)
  - `plugin.yml` — uses `${version}` (replaced at build time)
- [ ] **MC version compatibility**: `minecraft_version` in `gradle.properties` corresponds to the target version.

### 3. Changelogs

- [ ] **Changelogs updated**: Each affected loader has its entry in `Changelogs/`:
  - `CHANGELOG_FABRIC.md` (if Fabric was modified)
  - `CHANGELOG_NEOFORGE.md` (if NeoForge was modified)
  - `CHANGELOG_SPIGOT.md` (if Spigot was modified)
- [ ] **Correct format**: Entries follow the format defined in the `changelog-entry` skill.
- [ ] **Correct version**: The version in the changelog matches `gradle.properties`.

### 4. Documentation

- [ ] **Updated README**: If the API changed, the root README has updated examples.
- [ ] **Updated docs**: Files in `docs/` reflect the changes.
- [ ] **Updated example-plugin**: If the API changed, the example plugin is up to date.

### 5. Artifacts

- [ ] **JARs generated correctly**: Verify they exist in the build directories:
  ```
  client-fabric/build/libs/input-engine-fabric-{version}-{branch}.jar
  client-neoforge/build/libs/input-engine-neoforge-{version}-{branch}.jar
  plugin-spigot/build/libs/input-engine-spigot-{version}-{branch}.jar
  ```
- [ ] **Correct name**: Artifacts follow the `input-engine-{loader}-{version}-{branch}.jar` convention.
- [ ] **Reasonable size**: JARs do not have an unusually large or small size.

### 6. Git

- [ ] **Release commit**: Create a clean commit with version and changelog changes.
  ```bash
  git add gradle.properties Changelogs/
  git commit -m "build: release vX.Y.Z"
  ```
- [ ] **Tag created**: Create a version tag.
  ```bash
  git tag vX.Y.Z
  git push origin vX.Y.Z
  ```
- [ ] **Push**: All commits and tags pushed to the remote.

## Publishing on Modrinth

### For each loader:

#### Fabric Client
- [ ] Upload `input-engine-fabric-{version}-{branch}.jar`
- [ ] Select compatible Minecraft versions (currently 1.21.x)
- [ ] Mark as "Release" (not beta or alpha)
- [ ] Copy the content of `CHANGELOG_FABRIC.md` as the release description
- [ ] Dependencies: Fabric API (required), Fabric Loader (required)

#### NeoForge Client
- [ ] Upload `input-engine-neoforge-{version}-{branch}.jar`
- [ ] Select compatible Minecraft versions
- [ ] Mark as "Release"
- [ ] Copy the content of `CHANGELOG_NEOFORGE.md`
- [ ] Dependencies: NeoForge (required)

#### Spigot Plugin
- [ ] Upload `input-engine-spigot-{version}-{branch}.jar`
- [ ] Select compatible Minecraft versions
- [ ] Mark as "Release"
- [ ] Copy the content of `CHANGELOG_SPIGOT.md`
- [ ] No external dependencies

## Post-Release

- [ ] **Verify download**: Download each artifact from Modrinth and confirm it works.
- [ ] **Announce**: If there are communication channels (Discord, etc.), announce the release.
- [ ] **Prepare next cycle**: Evaluate if there are pending features for the next release.

## Quick Verification Command

To quickly verify that everything is in order before release:

```bash
# Full clean build
./gradlew clean build

# Verify generated artifacts
ls client-fabric/build/libs/
ls client-neoforge/build/libs/
ls plugin-spigot/build/libs/

# Verify versions
grep "_version=" gradle.properties

# Verify git status
git status
git log --oneline -5
```
