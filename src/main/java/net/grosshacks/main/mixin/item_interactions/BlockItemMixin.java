package net.grosshacks.main.mixin.item_interactions;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

	@WrapWithCondition(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrementUnlessCreative(ILnet/minecraft/entity/LivingEntity;)V"))
	private boolean gh$place(ItemStack itemStack, int amount, LivingEntity entity) {
		if (GrossHacksConfig.INSTANCE.loomAntighost) {
            return !itemStack.getName().getString().equals("Worldshaper's Loom");
		}
		return true;
	}
}
