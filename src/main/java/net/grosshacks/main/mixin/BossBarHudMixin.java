package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

	@Inject(method = "handlePacket", at = @At(value = "TAIL"))
	private void handlePacket(BossBarS2CPacket packet, CallbackInfo ci) {
		if (!GrossHacksConfig.INSTANCE.blightAlert) return;

		packet.accept(new BossBarS2CPacket.Consumer() {

			@Override
			public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style,
							boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
				if (!name.getString().equals("Charging Blight Wave")) return;
				MinecraftClient.getInstance().inGameHud.setTitle(Text.of(""));
				MinecraftClient.getInstance().inGameHud.setSubtitle(Text.of("§c! Blight Wave Incoming !"));
			}
		});
	}
}
