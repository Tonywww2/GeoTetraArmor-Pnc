package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;
import me.desht.pneumaticcraft.common.util.UpgradableItemUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.ItemModularHandheld;

/**
 * GeoTetraArmor inherits {@code isDamageable(ItemStack)} from Tetra, which answers
 * {@code getMaxDamage(stack) > 0}. Tetra items never set the vanilla max-damage field,
 * so this must not be replaced by a method on ModularArmorItem (an earlier version did,
 * falling back to {@code canBeDepleted()} and making every non-pneumatic piece
 * indestructible). Instead, only the PNC creative upgrade is layered on top, matching
 * {@code PneumaticArmorItem#isDamageable}; everything else keeps Tetra's logic, so
 * Tetra's damage clamp (armor breaks into the "broken" state instead of vanishing),
 * Unbreaking and Tetra durability effects keep working.
 */
@Mixin(ItemModularHandheld.class)
public abstract class ItemModularHandheldMixin {

    @Inject(method = "isDamageable", remap = false, at = @At("HEAD"), cancellable = true)
    private void pnc$creativeUpgradePreventsDamage(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (GeoTetraArmorItems.isPneumaticArmor(stack)
                && UpgradableItemUtils.getUpgradeCount(stack, ModUpgrades.CREATIVE.get()) > 0) {
            cir.setReturnValue(false);
        }
    }
}
