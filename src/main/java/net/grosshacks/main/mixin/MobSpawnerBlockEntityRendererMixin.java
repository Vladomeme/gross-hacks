package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public class MobSpawnerBlockEntityRendererMixin {

	@Inject(method = "render(Lnet/minecraft/block/entity/MobSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/MobSpawnerBlockEntity;getLogic()Lnet/minecraft/world/MobSpawnerLogic;"), cancellable = true)
	private void render(MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.spawner_culling &&
				!isPlayerInRange(mobSpawnerBlockEntity.getWorld(), mobSpawnerBlockEntity.getPos(), mobSpawnerBlockEntity.getLogic().requiredPlayerRange)) {
			matrixStack.pop();
			ci.cancel();
		}
	}

	@Unique
	private static boolean isPlayerInRange(World world, BlockPos pos, int requiredPlayerRange) {
		return world.isPlayerInRange((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5,
				GrossHacksConfig.INSTANCE.range_mode ? GrossHacksConfig.INSTANCE.range : requiredPlayerRange + GrossHacksConfig.INSTANCE.extra_range);
	}
}