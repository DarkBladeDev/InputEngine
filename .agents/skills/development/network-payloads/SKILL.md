---
name: network-payloads
description: Guide for creating and modifying custom network payloads that communicate between the Minecraft client and the Spigot server.
trigger: When a file is created or modified in any network/ package, or when working with NetworkConstants.java.
---

# Network Payloads

## Network Architecture

InputEngine uses a custom payload system for client → server and server → client communication:

```
Client (Fabric/NeoForge)  ──KeystrokePayload──►  Server (Spigot)
                           ◄──KeybindConfigPayload──
```

- **KeystrokePayload**: Client → Server. Sends `actionId` (String) + `isPressed` (boolean).
- **KeybindConfigPayload**: Server → Client. Sends the list of registered keybinds with their data.

## Network Channels

Defined in `common/NetworkConstants.java`:

```java
CHANNEL_NAMESPACE = "inputengine"
CHANNEL_PATH      = "keystrokes"       // → inputengine:keystrokes
CONFIG_PATH       = "config"           // → inputengine:config
```

**Rule**: Every new channel must be added as a constant in `NetworkConstants.java`. Never hardcode channel strings.

## Binary Protocol

The protocol is **manual** (no Mojang serializers). Format for strings:

```
[int: byte length] [bytes: UTF-8 content]
```

### KeystrokePayload Format
```
[int: actionId.length] [bytes: actionId] [boolean: isPressed]
```

### KeybindConfigPayload Format
```
[int: key count]
  For each key:
    [int: actionId.length] [bytes: actionId]
    [int: defaultKey (keycode)]
    [int: translationKey.length] [bytes: translationKey]
    [int: translations map size]
      For each translation:
        [int: langCode.length] [bytes: langCode]
        [int: translatedText.length] [bytes: translatedText]
```

## How to Create a New Payload

### 1. Define the channel in `common/NetworkConstants.java`

```java
public static final String NEW_PATH = "new_channel";
public static final String FULL_NEW_CHANNEL = CHANNEL_NAMESPACE + ":" + NEW_PATH;
```

### 2. Create the Payload record in Fabric (`client-fabric/.../network/`)

```java
public record MyPayload(String data, int value) implements CustomPayload {
    public static final CustomPayload.Id<MyPayload> ID = 
        new CustomPayload.Id<>(Identifier.of(
            NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.NEW_PATH));

    public static final PacketCodec<PacketByteBuf, MyPayload> CODEC = 
        new PacketCodec<>() {
            @Override
            public MyPayload decode(PacketByteBuf buf) {
                int len = buf.readInt();
                String data = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
                int value = buf.readInt();
                return new MyPayload(data, value);
            }

            @Override
            public void encode(PacketByteBuf buf, MyPayload payload) {
                byte[] bytes = payload.data().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
                buf.writeInt(payload.value());
            }
        };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
}
```

### 3. Create the equivalent in NeoForge (`client-neoforge/.../network/`)

Adapt using `StreamCodec`, `CustomPacketPayload.Type`, and `RegistryFriendlyByteBuf`.

### 4. Register on the Spigot server

In `InputEnginePlugin.onEnable()`:
```java
getServer().getMessenger().registerIncomingPluginChannel(this, NetworkConstants.FULL_NEW_CHANNEL, this);
// or registerOutgoingPluginChannel if it is server → client
```

### 5. Implement decode/encode in Spigot

Use `ByteStreams.newDataInput()` / `ByteStreams.newDataOutput()` from Guava with the **same binary format** as the client codecs.

## Critical Rules

1. **The binary format must be identical** across Fabric, NeoForge, and Spigot. A single byte difference breaks communication.
2. **Always use `StandardCharsets.UTF_8`** to encode strings.
3. **Channels must be optional** on the client to avoid breaking connections to vanilla servers.
4. **Write the length before the content** for all variable-length fields.
5. **Test serialization** by verifying that encode → decode produces the same original object.

## Checklist When Modifying Payloads

- [ ] Is the binary format identical across all 3 modules (Fabric, NeoForge, Spigot)?
- [ ] Was `NetworkConstants.java` updated if a new channel was added?
- [ ] Is the channel registered as incoming/outgoing in Spigot?
- [ ] Is the payload optional on the client (doesn't break vanilla)?
- [ ] Was the encode/decode roundtrip tested?
