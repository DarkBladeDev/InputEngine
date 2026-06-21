---
name: mixin-development
description: Best practices for developing Mixins in Fabric and NeoForge within the InputEngine project.
trigger: When a file is created or modified inside any mixin/ package, or when editing *.mixins.json files.
---

# Mixin Development

## What are Mixins

Mixins allow modifying Minecraft classes at runtime without access to the source code. InputEngine uses them to:
- **Inject dynamic translations** into the Minecraft i18n system
- **Access private fields** in the game options (keybindings array)

## Existing Mixins

| Mixin | Loader | Target | Purpose |
|---|---|---|---|
| `TranslationStorageMixin` | Fabric + NeoForge | `TranslationStorage` | Intercepts `get()` and `hasTranslation()` to inject dynamic translations |
| `GameOptionsAccessor` | Fabric | `GameOptions` | Accesses the `allKeys` array to add keybinds dynamically |
| `OptionsAccessor` | NeoForge | `Options` | NeoForge equivalent: accesses the `keyMappings` array |

## Mixin Types Used

### @Inject
Injects code at the beginning or end of an existing method:

```java
@Mixin(TargetClass.class)
public class MyMixin {
    @Inject(method = "targetMethod", at = @At("HEAD"), cancellable = true)
    private void onTargetMethod(ParamType param, CallbackInfoReturnable<ReturnType> cir) {
        // Code injected at the beginning of the method
        // cir.setReturnValue(value) to cancel and return early
    }
}
```

### @Accessor
Generates getters/setters for private fields:

```java
@Mixin(GameOptions.class)
public interface GameOptionsAccessor {
    @Accessor("allKeys")
    KeyBinding[] getAllKeys();

    @Accessor("allKeys")
    void setAllKeys(KeyBinding[] keys);
}
```

**Note**: Accessors must be **interfaces**, not classes. They are cast at runtime: `(GameOptionsAccessor) client.options`.

## Mixins JSON Configuration

Each loader has its `*.mixins.json` file in `src/main/resources/`:

### Fabric (`input-engine.mixins.json`)
```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "dev.darkblade.mod.input_engine.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "TranslationStorageMixin",
    "GameOptionsAccessor"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

### NeoForge (`input-engine.mixins.json`)
```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "dev.darkblade.mod.input_engine.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "TranslationStorageMixin",
    "OptionsAccessor"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
```

## Rules for Creating Mixins

### Naming
- `@Mixin` Classes: `TargetClassMixin` (e.g., `TranslationStorageMixin`)
- `@Accessor` Interfaces: `TargetClassAccessor` (e.g., `GameOptionsAccessor`)
- Package: `dev.darkblade.mod.input_engine.mixin`

### Best Practices
1. **Minimize the injection surface**: Inject as little as possible. Prefer `@At("HEAD")` or `@At("RETURN")` over more specific targets.
2. **Private methods**: Mixin handler methods should be `private` (except in `@Accessor` interfaces).
3. **Handler naming**: Prefix `on` + target method name (e.g., `onGetTranslation`).
4. **Cancel carefully**: Only use `cancellable = true` when you need to intercept the return value.
5. **Do not abuse**: If it can be achieved without a mixin (event, public API), prefer that.

### Differences Between Loaders
- **Fabric** uses Yarn mappings: `GameOptions`, `KeyBinding`, `allKeys`
- **NeoForge** uses Mojang mappings: `Options`, `KeyMapping`, `keyMappings`
- Mixin targets and field names **differ** between loaders

## How to Add a New Mixin

1. Create the class in `src/main/java/.../mixin/`
2. Add the class name to the corresponding `*.mixins.json`
3. If it's a mixin that must exist in both loaders, create the equivalent version in the other loader
4. Verify with `./gradlew build` that the mixin applies correctly

## Mixins Checklist

- [ ] Is the class in the `mixin` package?
- [ ] Is the mixin registered in the `*.mixins.json`?
- [ ] Are the handler methods `private`?
- [ ] Is the correct method/field name used according to the loader's mapping?
- [ ] Does the mixin have an equivalent in the other loader if necessary?
- [ ] Was it verified that the mixin applies without errors at runtime?
