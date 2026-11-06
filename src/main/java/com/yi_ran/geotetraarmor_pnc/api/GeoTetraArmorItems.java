package com.yi_ran.geotetraarmor_pnc.api;

import me.desht.pneumaticcraft.common.core.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;
import se.mickelus.tetra.module.ItemModule;

import javax.annotation.Nullable;

/**
 * Runtime detection for GeoTetraArmor armor items.
 *
 * This deliberately does not depend on the mixin-added marker interface, so the
 * Charging Station UI/tooltip/armor detection still works even if the interface
 * mixin is not applied for some reason.
 *
 * These checks run on hot paths (every armor tick, HUD frame, capability query and
 * tooltip), so slot/module keys are precomputed and ordinary armor is rejected with
 * an NBT string comparison before the Tetra module registry is consulted.
 */
public final class GeoTetraArmorItems {
    /** Indexed by {@link EquipmentSlot#getIndex()} for the four armor slots. */
    private static final String[] EXTRA_SLOTS = new String[4];
    private static final String[] PNEUMATIC_MODULES = new String[4];

    static {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                String extraSlot = slot.getName() + "/extra";
                EXTRA_SLOTS[slot.getIndex()] = extraSlot;
                PNEUMATIC_MODULES[slot.getIndex()] = "armor/" + extraSlot + "/pneumatic";
            }
        }
    }

    private GeoTetraArmorItems() {
    }

    public static boolean isGeoTetraArmor(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ModularArmorItem;
    }

    public static boolean isGeoTetraArmor(Item item) {
        // All four GeoTetraArmor armor items are ModularArmorItem subclasses.
        return item instanceof ModularArmorItem;
    }

    public static boolean isPneumaticArmor(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ModularArmorItem armor)) return false;
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        EquipmentSlot slot = armor.getEquipmentSlot();
        if (slot.getType() != EquipmentSlot.Type.ARMOR) return false;

        int index = slot.getIndex();
        String moduleKey = PNEUMATIC_MODULES[index];
        // Tetra stores the module key of a slot directly under the slot name.
        if (!moduleKey.equals(tag.getString(EXTRA_SLOTS[index]))) return false;

        ItemModule module = armor.getModuleFromSlot(stack, EXTRA_SLOTS[index]);
        return module != null && moduleKey.equals(module.getKey());
    }

    @Nullable
    public static EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ModularArmorItem armor
                ? armor.getEquipmentSlot()
                : null;
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

    /**
     * The item PNC should "see" for a stack: the matching PNC armor piece for an
     * attachment-equipped GeoTetraArmor stack, the stack's real item otherwise.
     * Intended for {@code @Redirect}s of {@link ItemStack#getItem()} in PNC type checks;
     * state must still be read from and written to the original stack.
     */
    public static Item pneumaticItemView(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ModularArmorItem armor && isPneumaticArmor(stack)) {
            return pneumaticArmorItem(armor.getEquipmentSlot());
        }
        return item;
    }

    public static String extraSlot(EquipmentSlot slot) {
        return slot.getType() == EquipmentSlot.Type.ARMOR
                ? EXTRA_SLOTS[slot.getIndex()]
                : slot.getName() + "/extra";
    }

    public static String pneumaticModule(EquipmentSlot slot) {
        return slot.getType() == EquipmentSlot.Type.ARMOR
                ? PNEUMATIC_MODULES[slot.getIndex()]
                : "armor/" + extraSlot(slot) + "/pneumatic";
    }
}
