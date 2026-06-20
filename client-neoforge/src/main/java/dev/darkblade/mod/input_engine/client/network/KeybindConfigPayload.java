package dev.darkblade.mod.input_engine.client.network;

import dev.darkblade.mod.input_engine.common.NetworkConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record KeybindConfigPayload(List<KeybindData> keys) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<KeybindConfigPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NetworkConstants.CHANNEL_NAMESPACE, NetworkConstants.CONFIG_PATH));

    public static final StreamCodec<FriendlyByteBuf, KeybindConfigPayload> CODEC = new StreamCodec<FriendlyByteBuf, KeybindConfigPayload>() {
        @Override
        public KeybindConfigPayload decode(FriendlyByteBuf buf) {
            int size = buf.readInt();
            List<KeybindData> keys = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                int idLen = buf.readInt();
                String actionId = buf.readCharSequence(idLen, StandardCharsets.UTF_8).toString();
                int defaultKey = buf.readInt();
                int transLen = buf.readInt();
                String transKey = buf.readCharSequence(transLen, StandardCharsets.UTF_8).toString();
                
                int mapSize = buf.readInt();
                java.util.Map<String, String> map = new java.util.HashMap<>();
                for (int j = 0; j < mapSize; j++) {
                    int kLen = buf.readInt();
                    String k = buf.readCharSequence(kLen, StandardCharsets.UTF_8).toString();
                    int vLen = buf.readInt();
                    String v = buf.readCharSequence(vLen, StandardCharsets.UTF_8).toString();
                    map.put(k, v);
                }
                
                boolean hasShift = buf.readBoolean();
                boolean hasCtrl = buf.readBoolean();
                boolean hasAlt = buf.readBoolean();
                boolean requiresDoubleTap = buf.readBoolean();
                boolean trackHoldDuration = buf.readBoolean();
                boolean isPartOfCombo = buf.readBoolean();
                
                keys.add(new KeybindData(actionId, defaultKey, transKey, map, hasShift, hasCtrl, hasAlt, requiresDoubleTap, trackHoldDuration, isPartOfCombo));
            }
            return new KeybindConfigPayload(keys);
        }

        @Override
        public void encode(FriendlyByteBuf buf, KeybindConfigPayload payload) {
            buf.writeInt(payload.keys().size());
            for (KeybindData key : payload.keys()) {
                byte[] idBytes = key.actionId().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(idBytes.length);
                buf.writeBytes(idBytes);
                
                buf.writeInt(key.defaultKey());
                
                byte[] transBytes = key.translationKey().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(transBytes.length);
                buf.writeBytes(transBytes);
                
                buf.writeInt(key.translations().size());
                for (java.util.Map.Entry<String, String> entry : key.translations().entrySet()) {
                    byte[] kBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                    buf.writeInt(kBytes.length);
                    buf.writeBytes(kBytes);
                    
                    byte[] vBytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                    buf.writeInt(vBytes.length);
                    buf.writeBytes(vBytes);
                }
                
                buf.writeBoolean(key.hasShift());
                buf.writeBoolean(key.hasCtrl());
                buf.writeBoolean(key.hasAlt());
                buf.writeBoolean(key.requiresDoubleTap());
                buf.writeBoolean(key.trackHoldDuration());
                buf.writeBoolean(key.isPartOfCombo());
            }
        }
    };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record KeybindData(String actionId, int defaultKey, String translationKey, java.util.Map<String, String> translations, boolean hasShift, boolean hasCtrl, boolean hasAlt, boolean requiresDoubleTap, boolean trackHoldDuration, boolean isPartOfCombo) {}
}
