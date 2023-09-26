package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
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

import java.util.function.BooleanSupplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@Inject(method = "playSoundFromEntity", at = @At(value = "HEAD"))
	private void playSoundFromEntity(@Nullable PlayerEntity except, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.mute_horns) {
			for (RegistryEntry.Reference<SoundEvent> soundEvent : SoundEvents.GOAT_HORN_SOUNDS) {
				if (sound.matchesKey(soundEvent.registryKey())) {
					for (PlayerEntity player : entity.getWorld().getPlayers()) {
						if (player.getMainHandStack().getItem().toString().equals("goat_horn")) {
							MinecraftClient.getInstance().inGameHud.setOverlayMessage(
									Text.of("§e" + player.getEntityName() + " just used a goat horn!"), false);
						}
					}
					return;
				}
			}
		}
	}

	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.nightmare_timer &&
				MinecraftClient.getInstance().player.getWorld().getRegistryKey().getValue().toString().endsWith("gallery")) {
			GrossHacks.tick();
			if (GrossHacks.getTicks() / 20 <= GrossHacksConfig.INSTANCE.time_remaining) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(
						Text.of("§3Nightmares arrive in: " + (GrossHacks.getTicks() / 20)), false);
			}
		}
	}
}
