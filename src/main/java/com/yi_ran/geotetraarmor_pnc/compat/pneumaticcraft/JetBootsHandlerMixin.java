package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.api.pneumatic_armor.ICommonArmorHandler;
import me.desht.pneumaticcraft.common.pneumatic_armor.handlers.JetBootsHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(JetBootsHandler.class)
public abstract class JetBootsHandlerMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"))
    private void pnc$coolDownEquippedBoots(ItemCooldowns cooldowns, Item originalItem, int ticks,
                                           ICommonArmorHandler armorHandler, boolean enabled) {
        ItemStack boots = armorHandler.getPlayer().getItemBySlot(EquipmentSlot.FEET);
        cooldowns.addCooldown(GeoTetraArmorItems.isPneumaticArmor(boots) ? boots.getItem() : originalItem, ticks);
    }
}