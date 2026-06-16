package dev.darkblade.mod.input_engine.mixin;

import net.minecraft.client.Options;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Options.class)
public interface OptionsAccessor {
    @Accessor("keyMappings")
    KeyMapping[] getAllKeys();

    @Accessor("keyMappings")
    @Mutable
    void setAllKeys(KeyMapping[] keys);
}
