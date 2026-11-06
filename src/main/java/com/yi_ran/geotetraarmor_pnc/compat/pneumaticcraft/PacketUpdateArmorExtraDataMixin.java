package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.network.PacketUpdateArmorExtraData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketUpdateArmorExtraData.class)
public abstract class PacketUpdateArmorExtraDataMixin {

    @Redirect(method = "lambda$handle$1", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$acceptAttachmentArmor(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }
}