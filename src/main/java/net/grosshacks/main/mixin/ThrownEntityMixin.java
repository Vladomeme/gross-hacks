package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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
	//todo [Shame] button.
	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void tick(CallbackInfo ci) {
		if (!GrossHacksConfig.INSTANCE.potionInfoEnabled()) return;

		if (((ThrownEntity) (Object) this) instanceof PotionEntity potion && potion.age == 1) {
			Optional<String> owner = getOwner(potion);
			if (owner.isEmpty()) return;

			Text name = potion.getStack().getName();
			if (GrossHacksConfig.INSTANCE.potionInfo == GrossHacksConfig.PotionInfo.Clucking
					&& !(name.getString().equals("Jar of Clucks"))) return;
			if (name.getString().equals("Alchemist's Potion")) return;

			MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
					potion.getStack().getName().copy().append(Text.literal(" used by " + owner.get() + "!")
					.setStyle(STYLE)));
		}
	}

	@Unique
	private Optional<String> getOwner(PotionEntity entity) {
		if (entity.getOwner() != null && entity.getOwner() instanceof PlayerEntity)
			return Optional.of(entity.getOwner().getName().getString());
		return Optional.empty();
	}
}
