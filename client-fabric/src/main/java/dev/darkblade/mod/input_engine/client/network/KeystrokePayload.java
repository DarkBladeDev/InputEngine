package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.minecraft.network.PacketByteBuf;
import java.nio.charset.StandardCharsets;

public record KeystrokePayload(String actionId, boolean isPressed, long holdDurationMs, boolean isDoubleTap) implements CustomPayload {
    public KeystrokePayload(String actionId, boolean isPressed) {
        this(actionId, isPressed, 0, false);
    }
    public static final CustomPayload.Id<KeystrokePayload> ID = new CustomPayload.Id<>(Identifier.of(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.CHANNEL_PATH));

    public static final PacketCodec<PacketByteBuf, KeystrokePayload> CODEC = new PacketCodec<PacketByteBuf, KeystrokePayload>() {
        @Override
        public KeystrokePayload decode(PacketByteBuf buf) {
            int len = buf.readInt();
            String actionId = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
            return new KeystrokePayload(actionId, buf.readBoolean(), buf.readLong(), buf.readBoolean());
        }

        @Override
        public void encode(PacketByteBuf buf, KeystrokePayload payload) {
            byte[] idBytes = payload.actionId().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(idBytes.length);
            buf.writeBytes(idBytes);
            buf.writeBoolean(payload.isPressed());
            buf.writeLong(payload.holdDurationMs());
            buf.writeBoolean(payload.isDoubleTap());
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
