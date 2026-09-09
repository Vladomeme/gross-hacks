package net.grosshacks.main.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.grosshacks.main.feature.Misc;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

//config -> brightBlight
@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

	@WrapOperation(method = "renderBlockAsEntity", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/block/BlockModelRenderer;render(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/render/model/BakedModel;FFFII)V"))
	private void gh$render(BlockModelRenderer renderer, MatrixStack.Entry entry, VertexConsumer vertexConsumer, BlockState state,
						   BakedModel bakedModel, float red, float green, float blue, int light, int overlay, Operation<Void> original) {
		if (Misc.SiriusDisplay.inSirius && state.getBlock().equals(Blocks.CYAN_STAINED_GLASS))
			original.call(renderer, entry, vertexConsumer, state, bakedModel, red, green, blue, 15728880, overlay);
		else
			original.call(renderer, entry, vertexConsumer, state, bakedModel, red, green, blue, light, overlay);
	}
}
