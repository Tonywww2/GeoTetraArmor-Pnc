package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityArmorMixin {

    @Inject(method = "onEquipItem", at = @At("TAIL"))
    private void pnc$refreshArmorHandler(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack,
                                         CallbackInfo ci) {
        if (!((Object) this instanceof Player player)) return;

        boolean oldSupported = oldStack.getItem() instanceof PneumaticArmorItem
                || GeoTetraArmorItems.isPneumaticArmor(oldStack);
        boolean newSupported = newStack.getItem() instanceof PneumaticArmorItem
                || GeoTetraArmorItems.isPneumaticArmor(newStack);
        boolean involvesGTA = GeoTetraArmorItems.isPneumaticArmor(oldStack)
                || GeoTetraArmorItems.isPneumaticArmor(newStack);
        if (oldSupported && newSupported && involvesGTA) {
            CommonArmorHandler.getHandlerForPlayer(player).armorSwitched(slot);
        }
    }
}