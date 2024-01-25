package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@Inject(method = "playSoundFromEntity", at = @At(value = "HEAD"), cancellable = true)
	private void playSoundFromEntity(@Nullable PlayerEntity except, Entity entity, RegistryEntry<SoundEvent> sound,
									 SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.mute_horns) {
			for (RegistryEntry.Reference<SoundEvent> soundEvent : SoundEvents.GOAT_HORN_SOUNDS) {
				if (sound.matchesKey(soundEvent.registryKey())) {
					for (PlayerEntity player : entity.getWorld().getPlayers()) {
						if (player.getMainHandStack().getItem().toString().equals("goat_horn")) {
							MinecraftClient.getInstance().inGameHud.setOverlayMessage(
									Text.of("§e" + player.getEntityName() + " just used a goat horn!"), false);
						}
					}
					ci.cancel();
				}
			}
		}
	}
}
