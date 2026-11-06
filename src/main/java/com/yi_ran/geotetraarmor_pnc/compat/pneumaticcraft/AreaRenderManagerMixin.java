package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.client.render.area.AreaRenderManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Drone debugger area rendering is gated on {@code helmet.getItem() == PNEUMATIC_HELMET};
 * see also the drone debugger redirect in {@link PneumaticArmorItemMixin}.
 */
@Mixin(AreaRenderManager.class)
public abstract class AreaRenderManagerMixin {

    @Redirect(method = "maybeRenderDroneDebug", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$acceptAttachmentHelmet(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}
