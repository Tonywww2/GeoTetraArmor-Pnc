package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * PNC's CommonArmorHandler only processes armor pieces whose item is
 * {@code instanceof PneumaticArmorItem}. Redirect the single getItem() call in
 * tickArmorPiece so GeoTetraArmor items pass that check while the rest of the
 * handler continues to operate on the original ItemStack (including its air
 * handler capability and upgrade NBT).
 */
@Mixin(CommonArmorHandler.class)
public abstract class CommonArmorHandlerMixin {

    @Redirect(method = "tickArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$redirectGetItem(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}
