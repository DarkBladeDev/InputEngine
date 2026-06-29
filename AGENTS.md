# Input Engine Agent Guidelines

> [!IMPORTANT]
The InputEngine project consists of multiple modules targeting different Minecraft mod loaders:
> - **client-fabric**: Fabric mod
> - **client-neoforge**: NeoForge mod
> - **plugin-spigot**: Spigot plugin

When making changes that affect all loaders, you MUST update ALL relevant modules. The project maintains functional parity between Fabric and NeoForge, with Spigot providing a separate server-side API.

Always consult the [`multi-loader-sync`](.agents/skills/development/multi-loader-sync/SKILL.md) skill for specific API mapping information.

## Available Skills

The following skills are available under `.agents/skills/` and should be consulted when their trigger conditions are met.

### 🛠️ Development

| Skill | Description | Example Use Case |
|---|---|---|
| `development/minecraft-mod-dev` | Core conventions for Minecraft mod/plugin development: Java 21, package structure, naming, records, and compatibility rules. | Creating a new Java class in any module — ensures correct package, naming, and Java version compliance. |
| `development/multi-loader-sync` | Ensures functional parity between Fabric and NeoForge implementations using a detailed API equivalence map. | After adding a new keybind detection feature in `client-fabric/`, replicate it to `client-neoforge/` using the correct NeoForge API equivalents (`KeyBinding` → `KeyMapping`, etc.). |
| `development/network-payloads` | Guide for creating and modifying custom network payloads with manual codecs, including the exact binary protocol format. | Creating a new server→client payload to send configuration data — defines channel registration, codec implementation, and byte-level format for Fabric, NeoForge, and Spigot. |
| `development/bukkit-event-api` | Patterns for creating proper Bukkit events as part of InputEngine's public API, including `HandlerList`, immutability, and Javadoc. | Adding a new `PlayerKeyReleaseEvent` to the Spigot API — ensures correct `HandlerList`, `final` fields, Javadoc with usage examples, and retrocompatibility. |
| `development/mixin-development` | Best practices for Mixin development: `@Inject`, `@Accessor`, JSON config, and mapping differences between loaders. | Creating a new Mixin to intercept Minecraft's key options screen — guides target selection, handler naming, and registration in `*.mixins.json`. |
| `development/gradle-build-config` | Conventions for the Gradle Kotlin DSL multi-project build: version properties, `gitBranch`, `archivesName`, and `processResources`. | Adding a new dependency to `plugin-spigot` — ensures the version goes in `gradle.properties` and the dependency declaration uses `providers.gradleProperty()`. |

### 📖 Documentation

| Skill | Description | Example Use Case |
|---|---|---|
| `documentation/project-docs` | Structure, format, and writing style for project documentation under `docs/`. | Creating a new documentation page for a feature — provides the markdown template, heading hierarchy, and linking conventions. |
| `documentation/api-reference` | Guide for documenting public API classes with Javadoc, code examples, and parameter descriptions. | Documenting a new public method `registerExpectedKey()` — ensures Javadoc with `@param`, a compilable example in the README, and an updated `example-plugin`. |
| `documentation/changelog-entry` | Exact format for changelog entries per loader: version headers, emoji sections (✨🐛♻️🗑️🔧), and bullet point style. | Writing a changelog entry for a NeoForge bug fix — uses the correct `CHANGELOG_NEOFORGE.md` file, `🐛 Bug Fixes` section, and bold item names. |

### ⚡ Automation

| Skill | Description | Example Use Case |
|---|---|---|
| `automation/changelog-generator` | Automates changelog generation from git commits, classifying them by module and change type. | Running `/changelog` after finishing a development sprint — analyzes commits since the last tag, groups by loader, and generates draft entries for each `CHANGELOG_*.md`. |
| `automation/commit-message` | Standardized Conventional Commits format with project-specific scopes (`fabric`, `neoforge`, `spigot`, `common`, etc.). | Committing a keybind fix in Fabric — generates `fix(fabric): reset keybind state on player disconnect` instead of a vague message. |
| `automation/version-bump` | Guide for updating versions in `gradle.properties` following SemVer, with support for independent per-loader versioning. | Preparing a release after adding a new feature — determines which module versions to bump (PATCH vs MINOR) and updates `gradle.properties` accordingly. |
| `automation/release-checklist` | Complete pre-release checklist: build verification, version checks, changelog updates, artifact validation, and Modrinth publication steps. | Publishing a new version to Modrinth — walks through every step from `./gradlew clean build` to uploading JARs with correct metadata and changelogs. |

## Post-Task Workflow

To ensure quality, proper versioning, and documentation, ALWAYS follow this workflow upon completing a development task (whether it is a complex feature or a simple fix):

1. **User Validation**: Stop and ask the user to verify if the result meets their requirements and the project's quality standards. Do not proceed to commit until the user explicitly confirms.
2. **Version Bump**: Remember to check and increment the version string of the modified modules in `gradle.properties`. Consult the `automation/version-bump` skill if needed.
3. **Commit & Document**: Once the user confirms the implementation is correct:
   - Generate a standardized commit message utilizing the `automation/commit-message` skill and execute the commit process.
   - Document the changes in their respective changelog utilizing the `automation/changelog-generator` skill.