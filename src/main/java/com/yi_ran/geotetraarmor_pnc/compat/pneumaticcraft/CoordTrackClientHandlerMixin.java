package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.client.pneumatic_armor.upgrade_handler.CoordTrackClientHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Picking a coordinate tracker target (right-clicking a block after choosing "select
 * target") is ignored unless {@code helmet.getItem() == PNEUMATIC_HELMET}. The server side
 * of the resulting packet is already handled by {@link PacketUpdateArmorExtraDataMixin}.
 */
@Mixin(CoordTrackClientHandler.Listener.class)
public abstract class CoordTrackClientHandlerMixin {

    @Redirect(method = "onPlayerInteract", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private static Item pnc$acceptAttachmentHelmet(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}
