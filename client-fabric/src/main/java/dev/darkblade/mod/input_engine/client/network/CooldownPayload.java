package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public record CooldownPayload(String actionId, int durationMs) implements CustomPayload {
    public static final CustomPayload.Id<CooldownPayload> ID = new CustomPayload.Id<>(
            Identifier.of(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.COOLDOWN_PATH));

    public static final PacketCodec<PacketByteBuf, CooldownPayload> CODEC = new PacketCodec<PacketByteBuf, CooldownPayload>() {
        @Override
        public CooldownPayload decode(PacketByteBuf buf) {
            int len = buf.readInt();
            String actionId = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
            return new CooldownPayload(actionId, buf.readInt());
        }

        @Override
        public void encode(PacketByteBuf buf, CooldownPayload payload) {
            byte[] idBytes = payload.actionId().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(idBytes.length);
            buf.writeBytes(idBytes);
            buf.writeInt(payload.durationMs());
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
