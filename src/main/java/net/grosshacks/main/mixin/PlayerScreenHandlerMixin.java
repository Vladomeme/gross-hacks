package net.grosshacks.main.mixin;

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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingRecipeInput, CraftingRecipe> {

    @Unique
    int slot;
    @Unique
    EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;

    @Inject(method = "quickMove", at = @At(value = "HEAD"))
    private void quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (GrossHacksConfig.INSTANCE.offhandEquip) this.slot = slot;
    }

    @SuppressWarnings("DataFlowIssue")
    @Redirect(method = "quickMove", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot getPreferredEquipmentSlot(PlayerEntity player, ItemStack itemStack) {
        if (GrossHacksConfig.INSTANCE.offhandEquip && slot != 45) {
            for (Text line : itemStack.getTooltip(Item.TooltipContext.DEFAULT, MinecraftClient.getInstance().player, TooltipType.BASIC)) {
                if (line.getString().equals("When in Off Hand:")) {
                    MinecraftClient.getInstance().interactionManager.clickSlot(
                            this.syncId, slot, 40, SlotActionType.SWAP, MinecraftClient.getInstance().player);
                    equipmentSlot = EquipmentSlot.OFFHAND;
                    return EquipmentSlot.OFFHAND;
                }
            }
        }
        return player.getPreferredEquipmentSlot(itemStack);
    }

    @Inject(method = "quickMove", at = @At(value = "INVOKE",
            target="Lnet/minecraft/screen/PlayerScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z"), cancellable = true)
    private void quickMove$return(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
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
