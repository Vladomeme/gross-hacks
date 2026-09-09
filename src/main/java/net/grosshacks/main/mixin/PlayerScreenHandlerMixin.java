package net.grosshacks.main.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//config -> offhandEquip
@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> {

    @Unique int slot;
    @Unique EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;

    @Inject(method = "quickMove", at = @At(value = "HEAD"))
    private void gh$quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (GrossHacksConfig.INSTANCE.offhandEquip) this.slot = slot;
    }

    @SuppressWarnings("DataFlowIssue")
    @WrapOperation(method = "quickMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot gh$getPreferredEquipmentSlot(PlayerEntity player, ItemStack stack, Operation<EquipmentSlot> original) {
        if (GrossHacksConfig.INSTANCE.offhandEquip && slot != 45) {
            for (Text line : stack.getTooltip(Item.TooltipContext.DEFAULT, MinecraftClient.getInstance().player, TooltipType.BASIC)) {
                if (line.getString().equals("When in Off Hand:")) {
                    MinecraftClient.getInstance().interactionManager.clickSlot(
                            this.syncId, slot, 40, SlotActionType.SWAP, MinecraftClient.getInstance().player);
                    equipmentSlot = EquipmentSlot.OFFHAND;
                    return EquipmentSlot.OFFHAND;
                }
            }
        }
        return original.call(player, stack);
    }

    @Inject(method = "quickMove", at = @At(value = "INVOKE",
            target="Lnet/minecraft/screen/PlayerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"), cancellable = true)
    private void gh$quickMove$Return(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (equipmentSlot == EquipmentSlot.OFFHAND) {
            equipmentSlot = EquipmentSlot.MAINHAND;
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("unused")
    public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }
}
