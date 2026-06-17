---
name: mixin-development
description: Buenas prácticas para desarrollar Mixins en Fabric y NeoForge dentro del proyecto InputEngine.
trigger: Cuando se crea o modifica un archivo dentro de cualquier paquete mixin/, o cuando se editan archivos *.mixins.json.
---

# Mixin Development

## Qué son los Mixins

Los Mixins permiten modificar clases de Minecraft en runtime sin acceso al código fuente. InputEngine los usa para:
- **Inyectar traducciones dinámicas** en el sistema de i18n de Minecraft
- **Acceder a campos privados** de las opciones del juego (keybindings array)

## Mixins Existentes

| Mixin | Loader | Target | Propósito |
|---|---|---|---|
| `TranslationStorageMixin` | Fabric + NeoForge | `TranslationStorage` | Intercepta `get()` y `hasTranslation()` para inyectar traducciones dinámicas |
| `GameOptionsAccessor` | Fabric | `GameOptions` | Accede al array `allKeys` para agregar keybinds dinámicamente |
| `OptionsAccessor` | NeoForge | `Options` | Equivalente NeoForge: accede al array `keyMappings` |

## Tipos de Mixin Usados

### @Inject
Inyecta código al inicio o final de un método existente:

```java
@Mixin(TargetClass.class)
public class MyMixin {
    @Inject(method = "targetMethod", at = @At("HEAD"), cancellable = true)
    private void onTargetMethod(ParamType param, CallbackInfoReturnable<ReturnType> cir) {
        // Código inyectado al inicio del método
        // cir.setReturnValue(value) para cancelar y retornar temprano
    }
}
```

### @Accessor
Genera getters/setters para campos privados:

```java
@Mixin(GameOptions.class)
public interface GameOptionsAccessor {
    @Accessor("allKeys")
    KeyBinding[] getAllKeys();

    @Accessor("allKeys")
    void setAllKeys(KeyBinding[] keys);
}
```

**Nota**: Los accessors deben ser **interfaces**, no clases. Se castean en runtime: `(GameOptionsAccessor) client.options`.

## Configuración de Mixins JSON

Cada loader tiene su archivo `*.mixins.json` en `src/main/resources/`:

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

## Reglas para Crear Mixins

### Naming
- Clases `@Mixin`: `TargetClassMixin` (ej: `TranslationStorageMixin`)
- Interfaces `@Accessor`: `TargetClassAccessor` (ej: `GameOptionsAccessor`)
- Package: `dev.darkblade.mod.input_engine.mixin`

### Buenas Prácticas
1. **Minimizar la superficie de inyección**: Inyectar lo menos posible. Preferir `@At("HEAD")` o `@At("RETURN")` sobre targets más específicos.
2. **Métodos privados**: Los métodos handler del mixin deben ser `private` (excepto en interfaces `@Accessor`).
3. **Nombre del handler**: Prefijo `on` + nombre del método target (ej: `onGetTranslation`).
4. **Cancelar con cuidado**: Solo usar `cancellable = true` cuando se necesita interceptar el return value.
5. **No abusar**: Si se puede lograr sin mixin (evento, API pública), preferir eso.

### Diferencias entre Loaders
- **Fabric** usa Yarn mappings: `GameOptions`, `KeyBinding`, `allKeys`
- **NeoForge** usa Mojang mappings: `Options`, `KeyMapping`, `keyMappings`
- Los targets de los mixins y los nombres de campos **difieren** entre loaders

## Cómo Agregar un Nuevo Mixin

1. Crear la clase en `src/main/java/.../mixin/`
2. Agregar el nombre de la clase al `*.mixins.json` correspondiente
3. Si es un mixin que debe existir en ambos loaders, crear la versión equivalente en el otro loader
4. Verificar con `./gradlew build` que el mixin aplica correctamente

## Checklist para Mixins

- [ ] ¿La clase está en el paquete `mixin`?
- [ ] ¿El mixin está registrado en el `*.mixins.json`?
- [ ] ¿Los métodos handler son `private`?
- [ ] ¿Se usa el nombre de método/campo correcto según el mapping del loader?
- [ ] ¿El mixin tiene equivalente en el otro loader si es necesario?
- [ ] ¿Se verificó que el mixin aplica sin errores en runtime?
