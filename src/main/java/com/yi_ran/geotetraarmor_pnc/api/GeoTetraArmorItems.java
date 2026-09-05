package com.yi_ran.geotetraarmor_pnc.api;

import me.desht.pneumaticcraft.common.core.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModule;

import java.util.Set;

/**
 * Runtime detection for GeoTetraArmor armor items.
 *
 * This deliberately does not depend on the mixin-added marker interface, so the
 * Charging Station UI/tooltip/armor detection still works even if the interface
 * mixin is not applied for some reason.
 */
public final class GeoTetraArmorItems {
    private static final Set<String> ARMOR_PATHS = Set.of("head", "chest", "legs", "feet");

    private GeoTetraArmorItems() {
    }

    public static boolean isGeoTetraArmor(ItemStack stack) {
        return !stack.isEmpty() && isGeoTetraArmor(stack.getItem());
    }

    public static boolean isGeoTetraArmor(Item item) {
        if (item == null) return false;
        if (item instanceof ModularArmorItem) return true;
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        return key != null
                && key.getNamespace().equals("geotetraarmor")
                && ARMOR_PATHS.contains(key.getPath());
    }

    public static boolean isPneumaticArmor(ItemStack stack) {
        if (!isGeoTetraArmor(stack)) return false;
        EquipmentSlot slot = getEquipmentSlot(stack);
        if (slot == null || !(stack.getItem() instanceof IModularItem modularItem)) return false;
        ItemModule module = modularItem.getModuleFromSlot(stack, extraSlot(slot));
        return module != null && pneumaticModule(slot).equals(module.getKey());
    }

    public static EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.getItem() instanceof ModularArmorItem armor) {
            return armor.getEquipmentSlot();
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null || !key.getNamespace().equals("geotetraarmor")) return null;
        return switch (key.getPath()) {
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    public static Item pneumaticArmorItem(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ModItems.PNEUMATIC_HELMET.get();
            case CHEST -> ModItems.PNEUMATIC_CHESTPLATE.get();
            case LEGS -> ModItems.PNEUMATIC_LEGGINGS.get();
            case FEET -> ModItems.PNEUMATIC_BOOTS.get();
            default -> throw new IllegalArgumentException("Not an armor slot: " + slot);
        };
    }

    public static String extraSlot(EquipmentSlot slot) {
        return slot.getName() + "/extra";
    }

    public static String pneumaticModule(EquipmentSlot slot) {
        return "armor/" + extraSlot(slot) + "/pneumatic";
    }
}
