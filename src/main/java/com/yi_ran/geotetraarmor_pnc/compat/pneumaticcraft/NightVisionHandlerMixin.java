package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.api.pneumatic_armor.ICommonArmorHandler;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.pneumatic_armor.handlers.NightVisionHandler;
import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;
import me.desht.pneumaticcraft.common.util.UpgradableItemUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Whenever a helmet finishes (re)initialising, the client sends the on/off state of
 * <em>every</em> head upgrade to the server, installed or not, and
 * {@code CommonArmorHandler#setUpgradeEnabled} forwards each one to the handler's
 * {@code onToggle}. With the night vision checkbox off, {@code NightVisionHandler#onToggle}
 * then removes the player's Night Vision effect on both sides even though no night vision
 * upgrade is installed, wiping Night Vision from potions or other mods.
 * <p>
 * Only let the toggle remove the effect when the worn helmet actually carries the upgrade.
 * Removal when the helmet is taken off still goes through {@code onShutdown}, which PNC
 * already limits to installed upgrades.
 */
@Mixin(NightVisionHandler.class)
public abstract class NightVisionHandlerMixin {

    @Inject(method = "onToggle", remap = false, at = @At("HEAD"), cancellable = true)
    private void pnc$keepForeignNightVision(ICommonArmorHandler armorHandler, boolean newState, CallbackInfo ci) {
        if (newState) return;
        ItemStack helmet = armorHandler.getPlayer().getItemBySlot(EquipmentSlot.HEAD);
        boolean pneumaticHelmet = helmet.getItem() instanceof PneumaticArmorItem
                || GeoTetraArmorItems.isPneumaticArmor(helmet);
        if (!pneumaticHelmet
                || UpgradableItemUtils.getUpgradeCount(helmet, ModUpgrades.NIGHT_VISION.get()) <= 0) {
            ci.cancel();
        }
    }
}
