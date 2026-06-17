---
name: multi-loader-sync
description: Garantiza la sincronización funcional entre las implementaciones de Fabric y NeoForge cuando se modifica código en cualquiera de los dos loaders.
trigger: Cuando se modifica un archivo Java en client-fabric/ o client-neoforge/ que tiene un equivalente funcional en el otro loader.
---

# Sincronización Multi-Loader

## Principio

Fabric y NeoForge son dos implementaciones del **mismo comportamiento** usando APIs diferentes. Todo cambio funcional en un loader debe replicarse en el otro, adaptando las APIs pero manteniendo la lógica idéntica.

## Mapa de Equivalencias

| Concepto | Fabric | NeoForge |
|---|---|---|
| Entry point | `ClientModInitializer.onInitializeClient()` | `@Mod` constructor + `IEventBus` |
| KeyBinding | `net.minecraft.client.option.KeyBinding` | `net.minecraft.client.KeyMapping` |
| Input type | `InputUtil.Type.KEYSYM` | `InputConstants.Type.KEYSYM` |
| Client tick | `ClientTickEvents.END_CLIENT_TICK` | `ClientTickEvent.Post` |
| Networking send | `ClientPlayNetworking.send(payload)` | `connection.send(payload)` |
| Networking receive | `ClientPlayNetworking.registerGlobalReceiver()` | `PayloadRegistrar.playToClient()` |
| Payload type | `CustomPayload` con `CustomPayload.Id` | `CustomPacketPayload` con `CustomPacketPayload.Type` |
| Codec | `PacketCodec<PacketByteBuf, T>` | `StreamCodec<RegistryFriendlyByteBuf, T>` |
| Mixin accessor | `GameOptionsAccessor` (campo `allKeys`) | `OptionsAccessor` (campo `keyMappings`) |
| Options reset | `KeyBinding.updateKeysByCode()` | `KeyMapping.resetMapping()` |
| Client instance | `MinecraftClient.getInstance()` | `Minecraft.getInstance()` |
| Network check | `client.getNetworkHandler()` | `client.getConnection()` |
| Is pressed | `keyBinding.isPressed()` | `keyBinding.isDown()` |

## Archivos Espejo

Estos archivos tienen equivalencias directas entre loaders:

| Fabric | NeoForge |
|---|---|
| `client/InputEngineClient.java` | `client/InputEngineNeoForge.java` |
| `client/network/KeystrokePayload.java` | `client/network/KeystrokePayload.java` |
| `client/network/KeybindConfigPayload.java` | `client/network/KeybindConfigPayload.java` |
| `client/CategoryFixer.java` | `client/CategoryFixer.java` |
| `mixin/TranslationStorageMixin.java` | `mixin/TranslationStorageMixin.java` |
| `mixin/GameOptionsAccessor.java` | `mixin/OptionsAccessor.java` |

## Flujo de Trabajo al Modificar un Loader

1. **Hacer el cambio** en el loader que se está trabajando.
2. **Identificar si el cambio es funcional** (nueva feature, fix de lógica) o cosmético (refactor interno, renaming).
3. **Si es funcional**, localizar el archivo espejo en el otro loader.
4. **Adaptar el cambio** al otro loader usando la tabla de equivalencias.
5. **Verificar** que ambos módulos compilan: `./gradlew :client-fabric:build :client-neoforge:build`.

## Diferencias Aceptables

Estas diferencias son **esperadas** y no requieren sincronización:

- Estructura de registro de payloads (Fabric usa `registerGlobalReceiver`, NeoForge usa `PayloadRegistrar`)
- Sistema de eventos (Fabric usa callbacks funcionales, NeoForge usa `@SubscribeEvent` o `addListener`)
- Forma de enviar paquetes al servidor
- Archivos de metadata (`fabric.mod.json` vs `neoforge.mods.toml`)
- Configuración de Mixin JSON (nombres de paquetes pueden diferir ligeramente)

## Lo que SÍ debe ser idéntico

- La **lógica de negocio**: qué teclas se registran, cómo se detectan press/release, qué datos se envían
- El **formato del payload**: el protocolo binario debe ser byte-por-byte compatible con el servidor Spigot
- Las **traducciones dinámicas**: el sistema de `DYNAMIC_TRANSLATIONS` debe funcionar igual
- El **CategoryFixer**: la lógica de reflection para inyectar la categoría de keybinds

## Checklist de Sincronización

- [ ] ¿El cambio funcional se replicó en el otro loader?
- [ ] ¿El formato del payload es compatible con el servidor?
- [ ] ¿Ambos loaders compilan sin errores?
- [ ] ¿Las traducciones se manejan de la misma manera?
- [ ] ¿El archivo mixins.json se actualizó en ambos loaders si se agregó un mixin nuevo?
