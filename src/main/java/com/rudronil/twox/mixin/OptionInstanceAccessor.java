package com.rudronil.twox.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets Fullbright write a raw gamma value, bypassing the vanilla 0..1 clamp. */
@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor<T> {
    @Mutable
    @Accessor("value")
    void twox$setValue(T value);
}
