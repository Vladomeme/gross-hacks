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

		String name = ((PotionEntity) entity).getStack().getName().getString();
		if (name.equals("Alchemist's Potion") || name.equals("Splash Uncraftable Potion")) return;
		if (GrossHacksConfig.INSTANCE.potionInfo == GrossHacksConfig.PotionInfo.Clucking
			&& !(name.equals("Jar of Clucks"))) return;

		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(
				((PotionEntity) entity).getStack().getName().getString() +
						" used " + getThrower((PotionEntity) entity) + "!")
					.setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
		GrossHacks.potionInfoSent = true;
	}

	@Unique
	private String getThrower(PotionEntity entity) {
		if (entity.getOwner() != null && entity.getOwner() instanceof PlayerEntity)
			return "by " + entity.getOwner().getName().getString();

		PlayerEntity player;
		if ((player = entity.getWorld().getClosestPlayer(entity, 10)) != null)
			return "near " + player.getName().getString();

		return "by unknown player";
	}
}
