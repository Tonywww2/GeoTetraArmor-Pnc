package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.client.render.overlays.PneumaticArmorHUDOverlay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Let the PneumaticCraft armor HUD treat GeoTetraArmor pieces as pneumatic
 * armor so pressure bars/init progress render for them.
 */
@Mixin(PneumaticArmorHUDOverlay.class)
public abstract class PneumaticArmorHUDOverlayMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$redirectGetItem(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}
