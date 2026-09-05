package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import com.yi_ran.geotetraarmor_pnc.pneumatic.PneumaticArmorState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;

import java.util.Arrays;

@Mixin(WorkbenchTile.class)
public abstract class WorkbenchTileMixin {
    @Unique
    private ItemStack pnc$targetBeforeCraft = ItemStack.EMPTY;
    @Unique
    private ItemStack[] pnc$materialsBeforeCraft = new ItemStack[0];

    @Inject(method = "craft", remap = false, at = @At("HEAD"))
    private void pnc$captureArmorState(Player player, CallbackInfo ci) {
        WorkbenchTile workbench = (WorkbenchTile) (Object) this;
        pnc$targetBeforeCraft = workbench.getTargetItemStack().copy();
        pnc$materialsBeforeCraft = Arrays.stream(workbench.getMaterials())
                .map(ItemStack::copy)
                .toArray(ItemStack[]::new);
    }

    @Inject(method = "craft", remap = false, at = @At("RETURN"))
    private void pnc$transferArmorState(Player player, CallbackInfo ci) {
        WorkbenchTile workbench = (WorkbenchTile) (Object) this;
        ItemStack target = workbench.getTargetItemStack();
        boolean wasPneumatic = GeoTetraArmorItems.isPneumaticArmor(pnc$targetBeforeCraft);
        boolean isPneumatic = GeoTetraArmorItems.isPneumaticArmor(target);

        if (!wasPneumatic && isPneumatic) {
            EquipmentSlot slot = GeoTetraArmorItems.getEquipmentSlot(target);
            Item sourceItem = GeoTetraArmorItems.pneumaticArmorItem(slot);
            Arrays.stream(pnc$materialsBeforeCraft)
                    .filter(material -> material.is(sourceItem))
                    .findFirst()
                    .ifPresent(material -> PneumaticArmorState.copy(material, target));
        } else if (wasPneumatic && !isPneumatic) {
            EquipmentSlot slot = GeoTetraArmorItems.getEquipmentSlot(pnc$targetBeforeCraft);
            ItemStack returnedArmor = new ItemStack(GeoTetraArmorItems.pneumaticArmorItem(slot));
            PneumaticArmorState.move(target, returnedArmor);
            if (!player.addItem(returnedArmor)) {
                player.drop(returnedArmor, false);
            }
        }

        pnc$targetBeforeCraft = ItemStack.EMPTY;
        pnc$materialsBeforeCraft = new ItemStack[0];
    }
}