---
name: multi-loader-sync
description: Ensures functional synchronization between the Fabric and NeoForge implementations when modifying code in either loader.
trigger: When a Java file is modified in client-fabric/ or client-neoforge/ that has a functional equivalent in the other loader.
---

# Multi-Loader Synchronization

## Principle

Fabric and NeoForge are two implementations of the **same behavior** using different APIs. Any functional change in one loader must be replicated in the other, adapting the APIs but keeping the logic identical.

## Equivalence Map

| Concept | Fabric | NeoForge |
|---|---|---|
| Entry point | `ClientModInitializer.onInitializeClient()` | `@Mod` constructor + `IEventBus` |
| KeyBinding | `net.minecraft.client.option.KeyBinding` | `net.minecraft.client.KeyMapping` |
| Input type | `InputUtil.Type.KEYSYM` | `InputConstants.Type.KEYSYM` |
| Client tick | `ClientTickEvents.END_CLIENT_TICK` | `ClientTickEvent.Post` |
| Networking send | `ClientPlayNetworking.send(payload)` | `connection.send(payload)` |
| Networking receive | `ClientPlayNetworking.registerGlobalReceiver()` | `PayloadRegistrar.playToClient()` |
| Payload type | `CustomPayload` with `CustomPayload.Id` | `CustomPacketPayload` with `CustomPacketPayload.Type` |
| Codec | `PacketCodec<PacketByteBuf, T>` | `StreamCodec<RegistryFriendlyByteBuf, T>` |
| Mixin accessor | `GameOptionsAccessor` (field `allKeys`) | `OptionsAccessor` (field `keyMappings`) |
| Options reset | `KeyBinding.updateKeysByCode()` | `KeyMapping.resetMapping()` |
| Client instance | `MinecraftClient.getInstance()` | `Minecraft.getInstance()` |
| Network check | `client.getNetworkHandler()` | `client.getConnection()` |
| Is pressed | `keyBinding.isPressed()` | `keyBinding.isDown()` |

## Mirror Files

These files have direct equivalences between loaders:

| Fabric | NeoForge |
|---|---|
| `client/InputEngineClient.java` | `client/InputEngineNeoForge.java` |
| `client/network/KeystrokePayload.java` | `client/network/KeystrokePayload.java` |
| `client/network/KeybindConfigPayload.java` | `client/network/KeybindConfigPayload.java` |
| `client/CategoryFixer.java` | `client/CategoryFixer.java` |
| `mixin/TranslationStorageMixin.java` | `mixin/TranslationStorageMixin.java` |
| `mixin/GameOptionsAccessor.java` | `mixin/OptionsAccessor.java` |

## Workflow When Modifying a Loader

1. **Make the change** in the loader you are working on.
2. **Identify if the change is functional** (new feature, logic fix) or cosmetic (internal refactor, renaming).
3. **If functional**, locate the mirror file in the other loader.
4. **Adapt the change** to the other loader using the equivalence table.
5. **Verify** that both modules compile: `./gradlew :client-fabric:build :client-neoforge:build`.

## Acceptable Differences

These differences are **expected** and do not require synchronization:

- Payload registration structure (Fabric uses `registerGlobalReceiver`, NeoForge uses `PayloadRegistrar`)
- Event system (Fabric uses functional callbacks, NeoForge uses `@SubscribeEvent` or `addListener`)
- How packets are sent to the server
- Metadata files (`fabric.mod.json` vs `neoforge.mods.toml`)
- Mixin JSON configuration (package names might differ slightly)

## What MUST be identical

- **Business logic**: which keys are registered, how press/release are detected, what data is sent
- **Payload format**: the binary protocol must be byte-by-byte compatible with the Spigot server
- **Dynamic translations**: the `DYNAMIC_TRANSLATIONS` system must work exactly the same
- **CategoryFixer**: the reflection logic to inject the keybind category

## Synchronization Checklist

- [ ] Was the functional change replicated in the other loader?
- [ ] Is the payload format compatible with the server?
- [ ] Do both loaders compile without errors?
- [ ] Are translations handled the same way?
- [ ] Was the mixins.json file updated in both loaders if a new mixin was added?
