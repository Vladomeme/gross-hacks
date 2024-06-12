package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlyingItemEntityRenderer.class)
public abstract class FlyingItemEntityRendererMixin<T extends Entity> {

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.potionInfo == GrossHacksConfig.PotionInfo.Disabled ||
				GrossHacks.potionInfoSent || !(entity instanceof PotionEntity) || entity.age != 1) return;

		MutableText name = ((PotionEntity) entity).getStack().getName().copy();
		if (!nameCheck(name.getString())) return;

		String thrower;
		if (((PotionEntity) entity).getOwner() != null && ((PotionEntity) entity).getOwner() instanceof PlayerEntity)
			thrower = ((PotionEntity) entity).getOwner().getName().getString();
		else return;

		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
				name.append(Text.literal(" used by " + thrower + "!")
					.setStyle(Style.EMPTY.withColor(Formatting.GOLD)
							.withBold(false).withItalic(false).withUnderline(false))));
		GrossHacks.potionInfoSent = true;
	}

	@Unique
	private boolean nameCheck(String name) {
		if (GrossHacksConfig.INSTANCE.potionInfo == GrossHacksConfig.PotionInfo.Clucking)
			return (name.equals("Jar of Clucks"));

		return !(name.equals("Alchemist's Potion") || name.contains("Splash") || name.contains("Lingering"));
    }
}
