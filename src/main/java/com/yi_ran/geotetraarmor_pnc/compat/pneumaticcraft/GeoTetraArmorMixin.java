package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import com.yi_ran.geotetraarmor_pnc.api.IPneumaticArmorCompat;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.client.IFOVModifierItem;
import me.desht.pneumaticcraft.api.item.ICustomDurabilityBar;
import me.desht.pneumaticcraft.api.pressure.IPressurizableItem;
import me.desht.pneumaticcraft.common.capabilities.AirHandlerItemStack;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonUpgradeHandlers;
import me.desht.pneumaticcraft.common.pneumatic_armor.handlers.ElytraHandler;
import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;
import me.desht.pneumaticcraft.common.util.UpgradableItemUtils;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ovo.yiran.geotetraarmor.items.ModularArmorItem;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Makes GeoTetraArmor's modular armor behave like PneumaticCraft pneumatic armor:
 * - implements IPneumaticArmorCompat so PNC's hard-coded checks accept it
 * - implements IPressurizableItem so AirHandlerItemStack can store compressed air
 */
@Mixin(ModularArmorItem.class)
public abstract class GeoTetraArmorMixin implements IPneumaticArmorCompat, IPressurizableItem,
    IFOVModifierItem, ICustomDurabilityBar {
    @Unique
    private static final UUID[] PNC_ARMOR_MODIFIER_IDS = {
        UUID.fromString("4a6bf01d-2e83-4b13-aaf0-a4c05958ea3c"),
        UUID.fromString("ad78a169-0409-47fb-8ca2-126b19196b56"),
        UUID.fromString("87bf456d-7360-407d-8592-5a2583eb948c"),
        UUID.fromString("e836e6c9-355e-49f2-87fc-331fadfdd642")
    };

    @Override
    public int getBaseVolume() {
        EquipmentSlot slot = ((ModularArmorItem) (Object) this).getEquipmentSlot();
        return switch (slot) {
            case HEAD -> PneumaticValues.PNEUMATIC_HELMET_VOLUME;
            case CHEST -> PneumaticValues.PNEUMATIC_CHESTPLATE_VOLUME;
            case LEGS -> PneumaticValues.PNEUMATIC_LEGGINGS_VOLUME;
            case FEET -> PneumaticValues.PNEUMATIC_BOOTS_VOLUME;
            default -> 10000;
        };
    }

    @Override
    public int getVolumeUpgrades(ItemStack stack) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                ? UpgradableItemUtils.getUpgradeCount(stack, ModUpgrades.VOLUME.get())
                : 0;
    }

    @Override
    public int getAir(ItemStack stack) {
        if (!GeoTetraArmorItems.isPneumaticArmor(stack)) return 0;
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(AirHandlerItemStack.AIR_NBT_KEY) : 0;
    }

    @Override
    public float getMaxPressure() {
        return 10F;
    }

    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        AirHandlerItemStack airHandler = new AirHandlerItemStack(stack);
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                return cap == PNCCapabilities.AIR_HANDLER_ITEM_CAPABILITY
                        && GeoTetraArmorItems.isPneumaticArmor(stack)
                        ? airHandler.getCapability(cap, side)
                        : LazyOptional.empty();
            }
        };
    }

    @Inject(method = "getAttributeModifiers", remap = false, at = @At("RETURN"), cancellable = true)
    private void pnc$addArmorUpgradeModifiers(EquipmentSlot slot, ItemStack stack,
                                              CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        if (!GeoTetraArmorItems.isPneumaticArmor(stack)
                || slot != GeoTetraArmorItems.getEquipmentSlot(stack)) return;

        int upgrades = UpgradableItemUtils.getUpgradeCount(stack, ModUpgrades.ARMOR.get());
        UUID modifierId = PNC_ARMOR_MODIFIER_IDS[slot.getIndex()];
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(cir.getReturnValue());
        modifiers.put(Attributes.ARMOR, new AttributeModifier(modifierId,
                "Pneumatic Armor modifier boost", upgrades / 2.0,
                AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifierId,
                "Pneumatic Armor toughness boost", upgrades,
                AttributeModifier.Operation.ADDITION));
        cir.setReturnValue(modifiers);
    }

    @Override
    public float getFOVModifier(ItemStack stack, Player player, EquipmentSlot slot) {
        if (!GeoTetraArmorItems.isPneumaticArmor(stack)) return 1F;
        return pnc$delegate(stack).getFOVModifier(stack, player, slot);
    }

    @Override
    public boolean shouldShowCustomDurabilityBar(ItemStack stack) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                && pnc$delegate(stack).shouldShowCustomDurabilityBar(stack);
    }

    @Override
    public int getCustomDurabilityColour(ItemStack stack) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                ? pnc$delegate(stack).getCustomDurabilityColour(stack)
                : 0;
    }

    @Override
    public float getCustomDurability(ItemStack stack) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                ? pnc$delegate(stack).getCustomDurability(stack)
                : 0F;
    }

    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        if (!GeoTetraArmorItems.isPneumaticArmor(stack) || !(wearer instanceof Player player)) return false;
        EquipmentSlot slot = GeoTetraArmorItems.getEquipmentSlot(stack);
        return CommonArmorHandler.getHandlerForPlayer(player)
                .getUpgradeCount(slot, ModUpgrades.GILDED.get()) > 0;
    }

    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                && GeoTetraArmorItems.getEquipmentSlot(stack) == EquipmentSlot.HEAD
                && CommonArmorHandler.getHandlerForPlayer(player)
                .upgradeUsable(CommonUpgradeHandlers.enderVisorHandler, true);
    }

    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                && GeoTetraArmorItems.getEquipmentSlot(stack) == EquipmentSlot.FEET
                && wearer instanceof Player player
                && CommonArmorHandler.getHandlerForPlayer(player)
                .getUpgradeCount(EquipmentSlot.FEET, ModUpgrades.FLIPPERS.get()) > 0;
    }

    public boolean isDamageable(ItemStack stack) {
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            return UpgradableItemUtils.getUpgradeCount(stack, ModUpgrades.CREATIVE.get()) == 0;
        }
        return ((Item) (Object) this).canBeDepleted();
    }

    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                && GeoTetraArmorItems.getEquipmentSlot(stack) == EquipmentSlot.CHEST
                && entity instanceof Player player
                && CommonArmorHandler.getHandlerForPlayer(player)
                .upgradeUsable(CommonUpgradeHandlers.elytraHandler, true);
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return GeoTetraArmorItems.isPneumaticArmor(stack)
                && GeoTetraArmorItems.getEquipmentSlot(stack) == EquipmentSlot.CHEST
                && ElytraHandler.handleFlightTick(entity, flightTicks);
    }

    @Unique
    private static PneumaticArmorItem pnc$delegate(ItemStack stack) {
        return (PneumaticArmorItem) GeoTetraArmorItems.pneumaticArmorItem(
                GeoTetraArmorItems.getEquipmentSlot(stack));
    }
}
