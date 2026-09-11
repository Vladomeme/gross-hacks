package net.grosshacks.main.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//config -> disableGlowing, masterworkGlowing
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

	@Inject(method = "hasOutline", at = @At(value = "TAIL"), cancellable = true)
	private void gh$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (GrossHacksConfig.INSTANCE.disableGlowing && entity instanceof PlayerEntity player) {
			if (player.getScore() == 0 || player.getName().getString().charAt(0) == '|') return;
			if (FabricLoader.getInstance().isModLoaded("hph")) {
				if (GrossHacks.glowOverrideEnabled()) cir.setReturnValue(GrossHacks.shouldForceGlow(entity));
			}
			else cir.setReturnValue(false);
		}
		if (GrossHacksConfig.INSTANCE.masterworkGlowing) {
			if (interactionManager != null && interactionManager.getCurrentGameMode() == GameMode.SURVIVAL) {
				if (entity.getName().getString().equals("MasterworkAnvil")) cir.setReturnValue(false);
				else if (entity instanceof DisplayEntity.BlockDisplayEntity display
						&& display.getScoreboardTeam() != null && display.getScoreboardTeam().getName().equals("Aqua")
						&& display.getData() != null && display.getData().blockState().getBlock().equals(Blocks.ANVIL)) {
					cir.setReturnValue(false);
				}
			}
		}
		if (GrossHacksConfig.INSTANCE.disableLucidityGlowing && entity instanceof DisplayEntity.BlockDisplayEntity display) {
			if (display.getData() != null && display.getData().blockState().getBlock().equals(Blocks.TINTED_GLASS)
					&& display.getRenderState() != null	&& display.getRenderState().glowColorOverride() == -12996274) {
				cir.setReturnValue(false);
			}
		}
	}
}
