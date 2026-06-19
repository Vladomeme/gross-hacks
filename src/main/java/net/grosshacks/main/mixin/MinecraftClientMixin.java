package net.grosshacks.main.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Inject(method = "hasOutline", at = @At(value = "TAIL"), cancellable = true)
	private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (GrossHacksConfig.INSTANCE.disableGlowing && entity instanceof PlayerEntity player) {
			if (player.getScore() == 0 || player.getName().getString().charAt(0) == '|') return;
			if (FabricLoader.getInstance().isModLoaded("hph")) {
				if (GrossHacks.glowOverrideEnabled()) cir.setReturnValue(GrossHacks.shouldForceGlow(entity));
			}
			else cir.setReturnValue(false);
		}
	}
}
