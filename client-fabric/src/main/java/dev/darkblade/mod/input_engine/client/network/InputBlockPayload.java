package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public record InputBlockPayload(String actionId, boolean isBlocked) implements CustomPayload {
    public static final CustomPayload.Id<InputBlockPayload> ID = new CustomPayload.Id<>(
            Identifier.of(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.BLOCK_INPUT_PATH));

    public static final PacketCodec<PacketByteBuf, InputBlockPayload> CODEC = new PacketCodec<PacketByteBuf, InputBlockPayload>() {
        @Override
        public InputBlockPayload decode(PacketByteBuf buf) {
            int len = buf.readInt();
            String actionId = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
            return new InputBlockPayload(actionId, buf.readBoolean());
        }

        @Override
        public void encode(PacketByteBuf buf, InputBlockPayload payload) {
            byte[] idBytes = payload.actionId().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(idBytes.length);
            buf.writeBytes(idBytes);
            buf.writeBoolean(payload.isBlocked());
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
