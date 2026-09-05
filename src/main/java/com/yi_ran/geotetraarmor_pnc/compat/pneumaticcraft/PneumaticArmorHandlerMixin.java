package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.common.event.PneumaticArmorHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * PNC's jump-boost event returns early unless the leggings item is a
 * PneumaticArmorItem. Redirect that one instanceof check for GeoTetraArmor.
 */
@Mixin(PneumaticArmorHandler.class)
public abstract class PneumaticArmorHandlerMixin {

    @Redirect(method = "onPlayerJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$redirectGetItem(ItemStack stack) {
        Item item = stack.getItem();
        return GeoTetraArmorItems.isPneumaticArmor(stack) ? ModItems.PNEUMATIC_LEGGINGS.get() : item;
    }
}
