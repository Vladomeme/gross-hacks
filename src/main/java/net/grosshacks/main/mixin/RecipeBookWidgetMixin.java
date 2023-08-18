package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {

	@Inject(method = "sendBookDataPacket", cancellable = true, at = @At(value = "INVOKE",
			target = ("Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")))
	private void sendBookDataPacket(CallbackInfo ci) {
		if(GrossHacksConfig.INSTANCE.enable_extra_buttons) {
			ci.cancel();
		}
	}
}
