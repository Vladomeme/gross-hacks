package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

	@Shadow @Final
	private BlockModelRenderer blockModelRenderer;

	@Redirect(method = "renderBlockAsEntity", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/block/BlockModelRenderer;render(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/render/model/BakedModel;FFFII)V"))
	private void render(BlockModelRenderer instance, MatrixStack.Entry entry, VertexConsumer vertexConsumer, BlockState state,
						BakedModel bakedModel, float red, float green, float blue, int light, int overlay) {
		if (GrossHacksConfig.INSTANCE.brightBlight && GrossHacks.inSirius && state.getBlock().equals(Blocks.CYAN_STAINED_GLASS))
			light = 15728880;
		blockModelRenderer.render(entry, vertexConsumer, state, bakedModel, red, green, blue, light, overlay);

	}
}
