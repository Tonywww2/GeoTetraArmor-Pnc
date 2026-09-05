package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.client.render.pneumatic_armor.HUDHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HUDHandler.class)
public abstract class HUDHandlerMixin {

    @Redirect(method = "getStatOverlayColor", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$usePneumaticHelmetForEyepieceColor(ItemStack stack) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                ? ModItems.PNEUMATIC_HELMET.get()
                : stack.getItem();
    }
}