package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.module.schematic.BaseSchematic;

import java.util.Map;

@Mixin(BaseSchematic.class)
public abstract class BaseSchematicMixin {

    @Inject(method = "canApplyUpgrade", remap = false, at = @At("HEAD"), cancellable = true)
    private void pnc$rejectDuplicateAttachment(Player player, ItemStack target, ItemStack[] materials,
                                               String slot, Map<ToolAction, Integer> tools,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!GeoTetraArmorItems.isPneumaticArmor(target)) return;

        EquipmentSlot equipmentSlot = GeoTetraArmorItems.getEquipmentSlot(target);
        if (equipmentSlot == null) return;

        for (ItemStack material : materials) {
            if (material.is(GeoTetraArmorItems.pneumaticArmorItem(equipmentSlot))) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}