package com.yi_ran.geotetraarmor_pnc.pneumatic;

import me.desht.pneumaticcraft.common.capabilities.AirHandlerItemStack;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.util.UpgradableItemUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PneumaticArmorState {
    private static final String DISPLAY_KEY = "display";
    private static final List<String> COLOR_KEYS = List.of("color", "color2", "color_eye");
    private static final String UPGRADE_CACHE_KEY = "UpgradeCache";
    private static final List<String> KEYS = List.of(
            AirHandlerItemStack.AIR_NBT_KEY,
            UpgradableItemUtils.NBT_UPGRADE_TAG,
            UpgradableItemUtils.NBT_CREATIVE,
            PneumaticArmorItem.NBT_SEARCH_ITEM,
            PneumaticArmorItem.NBT_COORD_TRACKER,
            PneumaticArmorItem.NBT_ENTITY_FILTER,
            PneumaticArmorItem.NBT_JUMP_BOOST,
            PneumaticArmorItem.NBT_SPEED_BOOST,
            PneumaticArmorItem.NBT_BUILDER_MODE,
            PneumaticArmorItem.NBT_JET_BOOTS_POWER,
            PneumaticArmorItem.NBT_FLIGHT_STABILIZERS,
            PneumaticArmorItem.NBT_HOVER,
            PneumaticArmorItem.NBT_SMART_HOVER
    );

    private PneumaticArmorState() {
    }

    public static void copy(ItemStack source, ItemStack target) {
        target.removeTagKey(UPGRADE_CACHE_KEY);
        CompoundTag sourceTag = source.getTag();
        for (String key : KEYS) {
            Tag value = sourceTag == null ? null : sourceTag.get(key);
            if (value == null) {
                target.removeTagKey(key);
            } else {
                target.getOrCreateTag().put(key, value.copy());
            }
        }
        copyColors(sourceTag, target);
    }

    public static void move(ItemStack source, ItemStack target) {
        copy(source, target);
        source.removeTagKey(UPGRADE_CACHE_KEY);
        for (String key : KEYS) {
            source.removeTagKey(key);
        }
        removeColors(source);
    }

    private static void copyColors(CompoundTag sourceTag, ItemStack target) {
        CompoundTag sourceDisplay = sourceTag == null ? null : sourceTag.getCompound(DISPLAY_KEY);
        CompoundTag targetDisplay = target.getOrCreateTagElement(DISPLAY_KEY);
        for (String key : COLOR_KEYS) {
            Tag value = sourceDisplay == null ? null : sourceDisplay.get(key);
            if (value == null) {
                targetDisplay.remove(key);
            } else {
                targetDisplay.put(key, value.copy());
            }
        }
        removeEmptyDisplay(target);
    }

    private static void removeColors(ItemStack stack) {
        CompoundTag display = stack.getTagElement(DISPLAY_KEY);
        if (display == null) return;
        for (String key : COLOR_KEYS) {
            display.remove(key);
        }
        removeEmptyDisplay(stack);
    }

    private static void removeEmptyDisplay(ItemStack stack) {
        CompoundTag display = stack.getTagElement(DISPLAY_KEY);
        if (display != null && display.isEmpty()) {
            stack.removeTagKey(DISPLAY_KEY);
        }
    }
}