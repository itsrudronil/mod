package com.rudronil.twox.mixin;

import com.rudronil.twox.nametag.NametagIcon;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds the 2X icon in front of a player's nametag text.
 * Two hooks are used so that at least one of them catches the nametag
 * (NametagIcon.apply ignores names that already carry the icon).
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    /** Hook 1: the method that builds the nametag text. */
    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true, require = 0)
    private void twox$nametagIcon(Entity entity, CallbackInfoReturnable<Component> cir) {
        Component original = cir.getReturnValue();
        if (original == null) return;
        Component withIcon = NametagIcon.apply(entity, original);
        if (withIcon != original) cir.setReturnValue(withIcon);
    }

    /** Hook 2: the render state the nametag is actually drawn from. */
    @Inject(method = "extractRenderState", at = @At("RETURN"), require = 0)
    private void twox$nametagState(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        if (state.nameTag == null) return;
        Component withIcon = NametagIcon.apply(entity, state.nameTag);
        if (withIcon != state.nameTag) state.nameTag = withIcon;
    }
}
