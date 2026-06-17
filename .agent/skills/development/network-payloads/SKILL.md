---
name: network-payloads
description: Guía para crear y modificar custom payloads de red que comunican el cliente Minecraft con el servidor Spigot.
trigger: Cuando se crea o modifica un archivo en algún paquete network/, o cuando se trabaja con NetworkConstants.java.
---

# Network Payloads

## Arquitectura de Red

InputEngine usa un sistema de custom payloads para comunicación client → server y server → client:

```
Client (Fabric/NeoForge)  ──KeystrokePayload──►  Server (Spigot)
                           ◄──KeybindConfigPayload──
```

- **KeystrokePayload**: Client → Server. Envía `actionId` (String) + `isPressed` (boolean).
- **KeybindConfigPayload**: Server → Client. Envía la lista de keybinds registrados con sus datos.

## Canales de Red

Definidos en `common/NetworkConstants.java`:

```java
CHANNEL_NAMESPACE = "inputengine"
CHANNEL_PATH      = "keystrokes"       // → inputengine:keystrokes
CONFIG_PATH       = "config"           // → inputengine:config
```

**Regla**: Todo nuevo canal debe agregarse como constante en `NetworkConstants.java`. Nunca hardcodear strings de canales.

## Protocolo Binario

El protocolo es **manual** (sin Mojang serializers). Formato para strings:

```
[int: longitud en bytes] [bytes: contenido UTF-8]
```

### Formato KeystrokePayload
```
[int: actionId.length] [bytes: actionId] [boolean: isPressed]
```

### Formato KeybindConfigPayload
```
[int: cantidad de keys]
  Para cada key:
    [int: actionId.length] [bytes: actionId]
    [int: defaultKey (keycode)]
    [int: translationKey.length] [bytes: translationKey]
    [int: translations map size]
      Para cada translation:
        [int: langCode.length] [bytes: langCode]
        [int: translatedText.length] [bytes: translatedText]
```

## Cómo Crear un Nuevo Payload

### 1. Definir el canal en `common/NetworkConstants.java`

```java
public static final String NEW_PATH = "new_channel";
public static final String FULL_NEW_CHANNEL = CHANNEL_NAMESPACE + ":" + NEW_PATH;
```

### 2. Crear el Payload record en Fabric (`client-fabric/.../network/`)

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

### 3. Crear el equivalente en NeoForge (`client-neoforge/.../network/`)

Adaptar usando `StreamCodec`, `CustomPacketPayload.Type`, y `RegistryFriendlyByteBuf`.

### 4. Registrar en el servidor Spigot

En `InputEnginePlugin.onEnable()`:
```java
getServer().getMessenger().registerIncomingPluginChannel(this, NetworkConstants.FULL_NEW_CHANNEL, this);
// o registerOutgoingPluginChannel si es server → client
```

### 5. Implementar decode/encode en Spigot

Usar `ByteStreams.newDataInput()` / `ByteStreams.newDataOutput()` de Guava con el **mismo formato binario** que los codecs del client.

## Reglas Críticas

1. **El formato binario debe ser idéntico** entre Fabric, NeoForge, y Spigot. Un byte de diferencia rompe la comunicación.
2. **Usar siempre `StandardCharsets.UTF_8`** para codificar strings.
3. **Los canales deben ser opcionales** en el cliente para no romper conexiones a servidores vanilla.
4. **Escribir longitud antes del contenido** para todos los campos de longitud variable.
5. **Probar la serialización** verificando que encode → decode produce el mismo objeto original.

## Checklist al Modificar Payloads

- [ ] ¿El formato binario es idéntico en los 3 módulos (Fabric, NeoForge, Spigot)?
- [ ] ¿Se actualizó `NetworkConstants.java` si se agregó un nuevo canal?
- [ ] ¿El canal está registrado como incoming/outgoing en Spigot?
- [ ] ¿El payload es opcional en el cliente (no rompe vanilla)?
- [ ] ¿Se probó encode/decode roundtrip?
