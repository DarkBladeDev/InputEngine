package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record KeystrokePayload(int actionId, boolean isPressed) implements CustomPayload {
    public static final CustomPayload.Id<KeystrokePayload> ID = new CustomPayload.Id<>(Identifier.of(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.CHANNEL_PATH));

    public static final PacketCodec<ByteBuf, KeystrokePayload> CODEC = new PacketCodec<ByteBuf, KeystrokePayload>() {
        @Override
        public KeystrokePayload decode(ByteBuf buf) {
            return new KeystrokePayload(buf.readInt(), buf.readBoolean());
        }

        @Override
        public void encode(ByteBuf buf, KeystrokePayload payload) {
            buf.writeInt(payload.actionId());
            buf.writeBoolean(payload.isPressed());
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
