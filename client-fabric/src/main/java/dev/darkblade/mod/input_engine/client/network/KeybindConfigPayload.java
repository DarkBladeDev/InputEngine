package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record KeybindConfigPayload(List<KeybindData> keys) implements CustomPayload {
    public static final CustomPayload.Id<KeybindConfigPayload> ID = new CustomPayload.Id<>(Identifier.of(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.CONFIG_PATH));

    public static final PacketCodec<PacketByteBuf, KeybindConfigPayload> CODEC = new PacketCodec<PacketByteBuf, KeybindConfigPayload>() {
        @Override
        public KeybindConfigPayload decode(PacketByteBuf buf) {
            int size = buf.readInt();
            List<KeybindData> keys = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                int idLen = buf.readInt();
                String actionId = buf.readCharSequence(idLen, StandardCharsets.UTF_8).toString();
                int defaultKey = buf.readInt();
                int transLen = buf.readInt();
                String transKey = buf.readCharSequence(transLen, StandardCharsets.UTF_8).toString();
                keys.add(new KeybindData(actionId, defaultKey, transKey));
            }
            return new KeybindConfigPayload(keys);
        }

        @Override
        public void encode(PacketByteBuf buf, KeybindConfigPayload payload) {
            buf.writeInt(payload.keys().size());
            for (KeybindData key : payload.keys()) {
                byte[] idBytes = key.actionId().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(idBytes.length);
                buf.writeBytes(idBytes);
                
                buf.writeInt(key.defaultKey());
                
                byte[] transBytes = key.translationKey().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(transBytes.length);
                buf.writeBytes(transBytes);
            }
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public record KeybindData(String actionId, int defaultKey, String translationKey) {}
}
