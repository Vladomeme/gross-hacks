package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public class MobSpawnerBlockEntityRendererMixin {

	@Redirect(method = "render(Lnet/minecraft/block/entity/MobSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/MobSpawnerLogic;getRenderedEntity(Lnet/minecraft/world/World;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/Entity;"))
	private Entity render(MobSpawnerLogic instance, World world, Random random, BlockPos pos) {
		if(GrossHacksConfig.INSTANCE.break_spawners){
			return null;
		}
		else {
			return instance.getRenderedEntity(world, random, pos);
		}
	}
}
