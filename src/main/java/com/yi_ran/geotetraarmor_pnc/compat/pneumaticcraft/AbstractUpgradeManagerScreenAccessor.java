package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import me.desht.pneumaticcraft.client.gui.upgrademanager.AbstractUpgradeManagerScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractUpgradeManagerScreen.class)
public interface AbstractUpgradeManagerScreenAccessor {
    @Accessor(remap = false)
    ItemStack getItemStack();
}
