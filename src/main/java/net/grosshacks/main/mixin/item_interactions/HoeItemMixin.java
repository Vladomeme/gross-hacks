package net.grosshacks.main.mixin.item_interactions;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

	@Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
	private void gh$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (GrossHacksConfig.INSTANCE.disableToolInteractions)
			cir.setReturnValue(ActionResult.PASS);
	}
}
