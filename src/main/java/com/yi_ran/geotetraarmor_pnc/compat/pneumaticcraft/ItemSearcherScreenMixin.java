package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.client.gui.ItemSearcherScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The item searcher (target picker of the helmet's Search upgrade) is opened purely
 * client-side from the armor GUI and closes itself every tick unless
 * {@code helmet.getItem() == PNEUMATIC_HELMET}, so the search target could not be chosen
 * with an attachment helmet. The server side of the choice is handled by
 * {@link PacketUpdateSearchItemMixin}.
 */
@Mixin(ItemSearcherScreen.class)
public abstract class ItemSearcherScreenMixin {

    @Redirect(method = "containerTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$acceptAttachmentHelmet(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}
