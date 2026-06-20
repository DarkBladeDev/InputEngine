package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;

public record CooldownPayload(String actionId, int durationMs) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CooldownPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.COOLDOWN_PATH));

    public static final StreamCodec<FriendlyByteBuf, CooldownPayload> CODEC = new StreamCodec<FriendlyByteBuf, CooldownPayload>() {
        @Override
        public CooldownPayload decode(FriendlyByteBuf buf) {
            int len = buf.readInt();
            String actionId = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
            return new CooldownPayload(actionId, buf.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, CooldownPayload payload) {
            byte[] idBytes = payload.actionId().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(idBytes.length);
            buf.writeBytes(idBytes);
            buf.writeInt(payload.durationMs());
        }
    };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
