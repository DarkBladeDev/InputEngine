package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;

public record KeystrokePayload(String actionId, boolean isPressed, long holdDurationMs, boolean isDoubleTap)
        implements CustomPacketPayload {
    public KeystrokePayload(String actionId, boolean isPressed) {
        this(actionId, isPressed, 0, false);
    }

    public static final CustomPacketPayload.Type<KeystrokePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.CHANNEL_PATH));

    public static final StreamCodec<FriendlyByteBuf, KeystrokePayload> CODEC = new StreamCodec<FriendlyByteBuf, KeystrokePayload>() {
        @Override
        public KeystrokePayload decode(FriendlyByteBuf buf) {
            int len = buf.readInt();
            String actionId = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
            return new KeystrokePayload(actionId, buf.readBoolean(), buf.readLong(), buf.readBoolean());
        }

        @Override
        public void encode(FriendlyByteBuf buf, KeystrokePayload payload) {
            byte[] idBytes = payload.actionId().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(idBytes.length);
            buf.writeBytes(idBytes);
            buf.writeBoolean(payload.isPressed());
            buf.writeLong(payload.holdDurationMs());
            buf.writeBoolean(payload.isDoubleTap());
        }
    };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
