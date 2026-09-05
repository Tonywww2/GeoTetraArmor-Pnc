package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.client.pneumatic_armor.upgrade_handler.CoreComponentsClientHandler;
import me.desht.pneumaticcraft.common.core.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The core-components HUD icon normally only shows PneumaticArmorItem stacks;
 * show GeoTetraArmor stacks in the pressure HUD too.
 */
@Mixin(CoreComponentsClientHandler.class)
public abstract class CoreComponentsClientHandlerMixin {

    @Redirect(method = "tickClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$redirectGetItem(ItemStack stack) {
        Item item = stack.getItem();
        return GeoTetraArmorItems.isPneumaticArmor(stack) ? GeoTetraArmorItems.pneumaticArmorItem(GeoTetraArmorItems.getEquipmentSlot(stack)) : item;
    }
}
