package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ThrownEntity.class)
public abstract class ThrownEntityMixin {

	@Unique
    final Style STYLE = Style.EMPTY.withColor(Formatting.GOLD).withBold(false).withItalic(false).withUnderline(false);

	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci) {
		if (!GrossHacksConfig.INSTANCE.potionInfoEnabled()) return;

		if (((ThrownEntity) (Object) this) instanceof PotionEntity potion && potion.age == 1) {
			Optional<String> owner = getOwner(potion);
			if (owner.isEmpty()) return;

			Text textName = potion.getStack().getName();
			String name = textName.getString();
			if (name.equals("Alchemist's Potion") || name.equals("Uncraftable Splash Potion")) return;

			MutableText message = textName.copy().append(Text.literal(" used by " + owner.get() + "! ").setStyle(STYLE));
			if (name.equals("Jar of Clucks")) {
				message.append(Text.literal("[Copy]").setStyle(Style.EMPTY.withColor(Formatting.AQUA)
						.withUnderline(true).withBold(false)
						.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message.getString()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy")))));
				MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
				return;
			}
			if (GrossHacksConfig.INSTANCE.potionInfo == GrossHacksConfig.PotionInfo.All)
				MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
		}
	}

	@Unique
	private Optional<String> getOwner(PotionEntity entity) {
		if (entity.getOwner() != null && entity.getOwner() instanceof PlayerEntity)
			return Optional.of(entity.getOwner().getName().getString());
		return Optional.empty();
	}
}
