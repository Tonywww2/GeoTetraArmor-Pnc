package com.yi_ran.geotetraarmor_pnc.compat.pneumaticcraft;

import com.yi_ran.geotetraarmor_pnc.api.GeoTetraArmorItems;
import me.desht.pneumaticcraft.common.item.PneumaticArmorItem;
import me.desht.pneumaticcraft.common.util.NBTUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PneumaticArmorItem.class)
public abstract class PneumaticArmorItemMixin {

    /**
     * The drone debugger checks {@code helmet.getItem() == PNEUMATIC_HELMET} (one call per
     * method), so a drone being debugged through an attachment helmet was never recognised.
     */
    @Redirect(method = {
            "isPlayerDebuggingDrone(Lnet/minecraft/world/entity/player/Player;Lme/desht/pneumaticcraft/common/entity/drone/AbstractDroneEntity;)Z",
            "isPlayerDebuggingDrone(Lnet/minecraft/world/entity/player/Player;Lme/desht/pneumaticcraft/common/drone/IDroneBase;)Z",
            "getDebuggedDrone()Lme/desht/pneumaticcraft/common/drone/IDroneBase;"
    }, remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private static Item pnc$acceptAttachmentHelmetForDroneDebug(ItemStack stack) {
        return GeoTetraArmorItems.pneumaticItemView(stack);
    }

    @Inject(method = "isPneumaticArmorPiece", remap = false, at = @At("HEAD"), cancellable = true)
    private static void pnc$isPneumaticArmorPiece(Player player, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (GeoTetraArmorItems.isPneumaticArmor(player.getItemBySlot(slot))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getIntData(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;I)I", remap = false, at = @At("HEAD"), cancellable = true)
    private static void pnc$getIntData(ItemStack stack, String key, int def, CallbackInfoReturnable<Integer> cir) {
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            cir.setReturnValue(stack.hasTag() && stack.getTag().contains(key, Tag.TAG_INT) ? stack.getTag().getInt(key) : def);
        }
    }

    @Inject(method = "getIntData(Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;III)I", remap = false, at = @At("HEAD"), cancellable = true)
    private static void pnc$getIntDataClamped(ItemStack stack, String key, int def, int min, int max, CallbackInfoReturnable<Integer> cir) {
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            int value = stack.hasTag() && stack.getTag().contains(key, Tag.TAG_INT) ? stack.getTag().getInt(key) : def;
            cir.setReturnValue(Mth.clamp(value, min, max));
        }
    }

    @Inject(method = "getBooleanData", remap = false, at = @At("HEAD"), cancellable = true)
    private static void pnc$getBooleanData(ItemStack stack, String key, boolean def, CallbackInfoReturnable<Boolean> cir) {
        if (GeoTetraArmorItems.isPneumaticArmor(stack)) {
            cir.setReturnValue(stack.hasTag() && stack.getTag().contains(key, Tag.TAG_BYTE) ? stack.getTag().getByte(key) == 1 : def);
        }
    }

    @Inject(method = "setSearchedItem", remap = false, at = @At("HEAD"), cancellable = true)
    private static void pnc$setSearchedItem(ItemStack helmetStack, Item searchedItem, CallbackInfo ci) {
        if (GeoTetraArmorItems.isPneumaticArmor(helmetStack)) {
            NBTUtils.setString(helmetStack, PneumaticArmorItem.NBT_SEARCH_ITEM, ForgeRegistries.ITEMS.getKey(searchedItem).toString());
            ci.cancel();
        }
    }
}
