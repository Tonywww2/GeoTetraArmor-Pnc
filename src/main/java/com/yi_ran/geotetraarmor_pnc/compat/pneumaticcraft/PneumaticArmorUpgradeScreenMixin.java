package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.api.pressure.IPressurizableItem;
import me.desht.pneumaticcraft.client.gui.upgrademanager.PneumaticArmorUpgradeScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;

import java.util.EnumMap;
import java.util.Map;

/**
 * PNC's PneumaticArmorUpgradeScreen assumes the charged item is a vanilla
 * ArmorItem (and specifically a PneumaticArmorItem). GeoTetraArmor's modular
 * armor is not an ArmorItem, so opening that screen throws ClassCastException.
 * <p>
 * This mixin lets the screen open for GeoTetraArmor armor:
 * - redirects the constructor's second getItem() call (the one cast to ArmorItem)
 *   to a dummy ArmorItem so the equipment slot can be read;
 * - supplies the correct base volume from IPressurizableItem instead of casting
 *   to PneumaticArmorItem.
 */
@Mixin(PneumaticArmorUpgradeScreen.class)
public abstract class PneumaticArmorUpgradeScreenMixin {
    private static final Map<EquipmentSlot, ArmorItem> DUMMY_ARMOR = new EnumMap<>(EquipmentSlot.class);

    static {
        DUMMY_ARMOR.put(EquipmentSlot.HEAD, new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()));
        DUMMY_ARMOR.put(EquipmentSlot.CHEST, new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
        DUMMY_ARMOR.put(EquipmentSlot.LEGS, new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties()));
        DUMMY_ARMOR.put(EquipmentSlot.FEET, new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties()));
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", ordinal = 1))
    private Item pnc$redirectSecondGetItem(ItemStack stack) {
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            Item item = stack.getItem();
            if (item instanceof ModularArmorItem armor) {
                return DUMMY_ARMOR.get(armor.getEquipmentSlot());
            }
        }
        return stack.getItem();
    }

    @Inject(method = "getDefaultVolume", remap = false, at = @At("HEAD"), cancellable = true)
    private void pnc$gtaDefaultVolume(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ((AbstractUpgradeManagerScreenAccessor) this).getItemStack();
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            Item item = stack.getItem();
            if (item instanceof IPressurizableItem pressurizable) {
                cir.setReturnValue(pressurizable.getBaseVolume());
            }
        }
    }
}
