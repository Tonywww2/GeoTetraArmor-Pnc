package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.api.pressure.IPressurizableItem;
import me.desht.pneumaticcraft.client.gui.upgrademanager.PneumaticArmorUpgradeScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * PNC's PneumaticArmorUpgradeScreen assumes the charged item is a PneumaticArmorItem.
 * Its constructor calls {@code itemStack.getItem()} twice: once for the registry name
 * used as the info tab's translation key, once cast to ArmorItem for the slot.
 * For attachment armor both now see the matching PNC armor piece, so the screen shows
 * PNC's own description and slot-specific upgrade tabs instead of a raw "head" key.
 * <p>
 * (An earlier version returned static leather ArmorItem instances created in this
 * mixin; creating Item instances outside registration leaves unregistered intrusive
 * registry holders behind, so the real PNC items are used instead.)
 */
@Mixin(PneumaticArmorUpgradeScreen.class)
public abstract class PneumaticArmorUpgradeScreenMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$useMatchingPneumaticArmor(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }

    @Inject(method = "getDefaultVolume", remap = false, at = @At("HEAD"), cancellable = true)
    private void pnc$gtaDefaultVolume(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ((AbstractUpgradeManagerScreenAccessor) this).getItemStack();
        if (GeoTetraArmorItems.isPneumaticArmor(stack) && stack.getItem() instanceof IPressurizableItem pressurizable) {
            cir.setReturnValue(pressurizable.getBaseVolume());
        }
    }
}
