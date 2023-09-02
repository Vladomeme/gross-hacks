package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin {

	@Final
	@Shadow
	private SignBlockEntity blockEntity;
	@Unique
	MinecraftClient client = MinecraftClient.getInstance();

	@Inject(method = "canEdit", at = @At(value = "HEAD"), cancellable = true)
	private void canEdit(CallbackInfoReturnable<Boolean> cir) {
		if (GrossHacksConfig.INSTANCE.fix_sign_screens) {
			cir.setReturnValue(this.client != null && this.client.player != null && !this.blockEntity.isRemoved());
		}
	}
}
