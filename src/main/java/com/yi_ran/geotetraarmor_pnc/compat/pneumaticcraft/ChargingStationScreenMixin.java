package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.client.gui.ChargingStationScreen;
import me.desht.pneumaticcraft.client.gui.widget.WidgetButtonExtended;
import me.desht.pneumaticcraft.common.block.entity.ChargingStationBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forces the Charging Station upgrade button to be visible for GeoTetraArmor
 * armor even if the item is not recognised as IChargeableContainerProvider.
 */
@Mixin(ChargingStationScreen.class)
public abstract class ChargingStationScreenMixin {
    @Shadow(remap = false)
    private WidgetButtonExtended guiSelectButton;

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void pnc$showUpgradeButtonForGTA(CallbackInfo ci) {
        ChargingStationScreen self = (ChargingStationScreen) (Object) this;
        ItemStack stack = self.te.getPrimaryInventory().getStackInSlot(ChargingStationBlockEntity.CHARGE_INVENTORY_INDEX);
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            guiSelectButton.visible = true;
            guiSelectButton.setTooltipText(Component.translatable(
                    "pneumaticcraft.gui.tooltip.charging_station.manageUpgrades", stack.getHoverName()));
        }
    }
}
