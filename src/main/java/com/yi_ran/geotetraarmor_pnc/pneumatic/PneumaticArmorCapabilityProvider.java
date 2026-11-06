package com.yi_ran.geotetraarmor_pnc.pneumatic;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.common.capabilities.AirHandlerItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

/**
 * Air capability for GeoTetraArmor stacks. It is attached to every GTA armor stack
 * (Forge only offers per-item capability init), but the air handler is only exposed
 * while the stack carries the pneumatic attachment, and it is created lazily so the
 * frequent copies of ordinary Tetra armor stacks do not allocate one.
 */
public final class PneumaticArmorCapabilityProvider implements ICapabilityProvider {
    private final ItemStack stack;
    @Nullable
    private AirHandlerItemStack airHandler;

    public PneumaticArmorCapabilityProvider(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap != PNCCapabilities.AIR_HANDLER_ITEM_CAPABILITY || !GeoTetraArmorItems.isPneumaticArmor(stack)) {
            return LazyOptional.empty();
        }
        if (airHandler == null) {
            airHandler = new AirHandlerItemStack(stack);
        }
        return airHandler.getCapability(cap, side);
    }
}
