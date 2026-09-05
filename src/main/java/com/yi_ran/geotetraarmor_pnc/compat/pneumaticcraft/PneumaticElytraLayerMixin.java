package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.client.render.pneumatic_armor.PneumaticElytraLayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PneumaticElytraLayer.class)
public abstract class PneumaticElytraLayerMixin {

    @Redirect(method = "shouldRender", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item pnc$acceptAttachmentChestplate(ItemStack stack) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                ? ModItems.PNEUMATIC_CHESTPLATE.get()
                : stack.getItem();
    }
}