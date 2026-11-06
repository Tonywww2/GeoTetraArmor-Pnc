package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.block.entity.ChargingStationBlockEntity;
import me.desht.pneumaticcraft.common.core.ModMenuTypes;
import me.desht.pneumaticcraft.common.inventory.handler.ChargeableItemHandler;
import me.desht.pneumaticcraft.common.item.IChargeableContainerProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes the Charging Station open the upgrade manager for GeoTetraArmor armor
 * even if the item class itself is not recognised as IChargeableContainerProvider.
 * <p>
 * Uses the same menu type as PNC's own armor ({@code CHARGING_ARMOR}), whose screen
 * ({@code PneumaticArmorUpgradeScreen}) is made attachment-aware by
 * {@link PneumaticArmorUpgradeScreenMixin}. The Amadron tablet menu used previously
 * showed the tablet's description and a 3000 mL base volume for every armor piece.
 */
@Mixin(ChargingStationBlockEntity.class)
public abstract class ChargingStationBlockEntityMixin {
    @Shadow(remap = false)
    private ChargeableItemHandler chargeableInventory;

    @Inject(method = "handleGUIButtonPress", remap = false, at = @At("HEAD"), cancellable = true)
    private void pnc$openUpgradesForGTA(String tag, boolean shiftHeld, ServerPlayer player, CallbackInfo ci) {
        ChargingStationBlockEntity self = (ChargingStationBlockEntity) (Object) this;
        if ("open_upgrades".equals(tag) && GeoTetraArmorItems.isPneumaticArmor(self.getChargingStack())) {
            MenuProvider provider = new IChargeableContainerProvider.Provider(self, ModMenuTypes.CHARGING_ARMOR.get());
            NetworkHooks.openScreen(player, provider, self.getBlockPos());
            ci.cancel();
        }
    }

    @Inject(method = "getChargeableInventory", remap = false, at = @At("HEAD"))
    private void pnc$ensureChargeableInventory(CallbackInfoReturnable<ChargeableItemHandler> cir) {
        if (chargeableInventory != null) return;
        ChargingStationBlockEntity self = (ChargingStationBlockEntity) (Object) this;
        Level level = self.getLevel();
        // The client always gets a fresh handler from PNC; only the server keeps this field.
        if (level != null && !level.isClientSide && GeoTetraArmorItems.isPneumaticArmor(self.getChargingStack())) {
            chargeableInventory = new ChargeableItemHandler(self);
        }
    }
}
