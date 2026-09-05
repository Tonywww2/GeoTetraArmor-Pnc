package com.yi_ran.geotetraarmor_pnc;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import com.mojang.logging.LogUtils;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.pneumatic_armor.IArmorUpgradeHandler;
import me.desht.pneumaticcraft.api.upgrade.IUpgradeRegistry.Builder;
import me.desht.pneumaticcraft.api.upgrade.PNCUpgrade;
import me.desht.pneumaticcraft.common.pneumatic_armor.ArmorUpgradeRegistry;
import me.desht.pneumaticcraft.common.upgrades.ApplicableUpgradesDB;
import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.common.util.UpgradableItemUtils;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(GeoTetraArmorPnc.MODID)
public class GeoTetraArmorPnc {
    // The mod id is also used by the generated mixin config and mods.toml.
    public static final String MODID = "geotetraarmor_pnc";

    private static final Logger LOGGER = LogUtils.getLogger();

    public GeoTetraArmorPnc() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("GeoTetraArmor PneumaticCraft Compat loaded");
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        // PNC freezes ArmorUpgradeRegistry and initialises ApplicableUpgradesDB during
        // its common-setup deferred work, so do our additions after the load has completed.
        registerArmorUpgrades();
    }

    private void registerArmorUpgrades() {
        ApplicableUpgradesDB db = ApplicableUpgradesDB.getInstance();
        db.addApplicableUpgrades(item("head"), armorBuilder(EquipmentSlot.HEAD));
        db.addApplicableUpgrades(item("chest"), armorBuilder(EquipmentSlot.CHEST));
        db.addApplicableUpgrades(item("legs"), armorBuilder(EquipmentSlot.LEGS));
        db.addApplicableUpgrades(item("feet"), armorBuilder(EquipmentSlot.FEET));
    }

    private static Item item(String name) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation("geotetraarmor", name));
    }

    private static Builder armorBuilder(EquipmentSlot slot) {
        Builder builder = new Builder();

        // Upgrades required by PNC armor upgrade handlers for this slot.
        for (IArmorUpgradeHandler<?> handler : ArmorUpgradeRegistry.getInstance().getHandlersForSlot(slot)) {
            for (PNCUpgrade upgrade : handler.getRequiredUpgrades()) {
                builder.with(upgrade, handler.getMaxInstallableUpgrades(upgrade));
            }
        }

        // Common upgrades for every armor piece (mirrors PNC's UpgradesDBSetup).
        builder.with(ModUpgrades.SPEED.get(), 10)
                .with(ModUpgrades.VOLUME.get(), 25)
                .with(ModUpgrades.ITEM_LIFE.get(), PneumaticValues.ARMOR_REPAIR_MAX_UPGRADES)
                .with(ModUpgrades.ARMOR.get(), 4)
                .with(ModUpgrades.RADIATION_SHIELDING.get(), 1)
                .with(ModUpgrades.GILDED.get(), 1)
                .with(ModUpgrades.THAUMCRAFT.get(), 1)
                .with(ModUpgrades.CREATIVE.get(), 1);

        // Slot-specific extras which don't have a dedicated upgrade handler.
        switch (slot) {
            case HEAD -> builder.with(ModUpgrades.RANGE.get(), 5).with(ModUpgrades.SECURITY.get(), 64);
            case CHEST -> builder.with(ModUpgrades.SECURITY.get(), 1);
            case FEET -> builder.with(ModUpgrades.FLIPPERS.get(), 1);
            default -> { }
        }

        return builder;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (event.getEntity() == null || !GeoTetraArmorItems.isPneumaticArmor(stack)) return;

            // Add the standard PNC pressure line, independent of PNC's pnc_tooltip tag.
            stack.getCapability(PNCCapabilities.AIR_HANDLER_ITEM_CAPABILITY).ifPresent(h -> {
                float ratio = h.getPressure() / h.maxPressure();
                ChatFormatting color;
                if (ratio < 0.1f) {
                    color = ChatFormatting.RED;
                } else if (ratio < 0.5f) {
                    color = ChatFormatting.GOLD;
                } else {
                    color = ChatFormatting.DARK_GREEN;
                }
                event.getToolTip().add(Component.translatable(
                        "pneumaticcraft.gui.tooltip.pressure",
                        PneumaticCraftUtils.roundNumberTo(h.getPressure(), 1)
                ).withStyle(color));
            });

            // Also show installed PNC upgrades on the tooltip.
            UpgradableItemUtils.addUpgradeInformation(stack, event.getToolTip(), event.getFlags());
        }
    }
}
