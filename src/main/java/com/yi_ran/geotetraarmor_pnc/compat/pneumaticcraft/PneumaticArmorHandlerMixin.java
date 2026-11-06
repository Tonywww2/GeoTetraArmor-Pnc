package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.event.PneumaticArmorHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * PNC's armor event handler type-checks the worn item directly in two places
 * (one {@code getItem()} call each):
 * - {@code onPlayerJump}: jump boost returns early unless the leggings are a PneumaticArmorItem;
 * - {@code onPlayerTrack}: the jet boots state is only sent to a player who starts tracking
 *   you when your boots are exactly PNC's pneumatic boots, so other players saw attachment
 *   jet boots in the wrong pose until the state next changed.
 */
@Mixin(PneumaticArmorHandler.class)
public abstract class PneumaticArmorHandlerMixin {

    @Redirect(method = "onPlayerJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$redirectGetItem(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }

    @Redirect(method = "onPlayerTrack", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$syncAttachmentJetBoots(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}
