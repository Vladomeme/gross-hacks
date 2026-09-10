package net.grosshacks.main.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//Doesn't work ATM because server tracks the attack cooldown independently and doesn't have a fix for it
//config -> dropKeyAttackCooldown
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

	@Shadow public abstract ItemStack getMainHandStack();

	@WrapOperation(method = "dropSelectedItem", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerInventory;removeStack(II)Lnet/minecraft/item/ItemStack;"))
	private ItemStack gh$dropSelectedItem(PlayerInventory inventory, int slot, int amount, Operation<ItemStack> original) {
		if (GrossHacksConfig.INSTANCE.dropKeyAttackCooldown)
			return getMainHandStack();
		else
			return original.call(inventory, slot, amount);
	}
}
