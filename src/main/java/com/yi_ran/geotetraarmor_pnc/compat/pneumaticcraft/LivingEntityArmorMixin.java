package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import com.yi_ran.geotetraarmor_pnc.pneumatic.PneumaticArmorState;
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

/**
 * Mirrors PNC's own {@code LivingEntityMixin}, which resets the armor handler when one
 * pneumatic piece is swapped directly for another, for swaps involving attachment armor.
 * <p>
 * PNC only reaches its reset inside vanilla's "the stack really changed" branch. This
 * hook runs at TAIL, so it must filter on its own: Curios' armor slots (and machine menus
 * copying that pattern) call {@code onEquipItem} on every {@code Slot#set}, including the
 * client-side re-sync of the piece already being worn whenever such a screen opens or the
 * stored air changes. Treating those as swaps re-initialised the armor every time.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityArmorMixin {

    @Inject(method = "onEquipItem", at = @At("TAIL"))
    private void pnc$refreshArmorHandler(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack,
                                         CallbackInfo ci) {
        if (slot.getType() != EquipmentSlot.Type.ARMOR || oldStack == newStack
                || !((Object) this instanceof Player player)) return;

        boolean oldAttachment = GeoTetraArmorItems.isPneumaticArmor(oldStack);
        boolean newAttachment = GeoTetraArmorItems.isPneumaticArmor(newStack);
        // Pure PNC-to-PNC swaps are handled by PNC itself.
        if (!oldAttachment && !newAttachment) return;

        // Putting a piece on or taking it off is detected by CommonArmorHandler#tickArmorPiece;
        // only a direct pneumatic-to-pneumatic swap needs an explicit reset.
        boolean oldSupported = oldAttachment || oldStack.getItem() instanceof PneumaticArmorItem;
        boolean newSupported = newAttachment || newStack.getItem() instanceof PneumaticArmorItem;
        if (!oldSupported || !newSupported) return;

        // Same piece re-synced (air/durability changes only): not a swap.
        if (PneumaticArmorState.hasSameConfiguration(oldStack, newStack)) return;

        CommonArmorHandler.getHandlerForPlayer(player).armorSwitched(slot);
    }
}
