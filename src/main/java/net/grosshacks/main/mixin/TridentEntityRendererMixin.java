package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.ItemDataAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
public abstract class TridentEntityRendererMixin extends EntityRenderer<TridentEntity> implements ItemDataAccessor {

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void render(TridentEntity tridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {

        NbtCompound tridentItemData = ((ItemDataAccessor) tridentEntity).getTridentItemData();
        if (GrossHacksConfig.INSTANCE.thrown_trident_texture && tridentItemData.contains("TridentItemData")
                && tridentItemData.getCompound("TridentItemData").getCompound("tag").contains("plain")) {

            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, tridentEntity.prevYaw, tridentEntity.getYaw()) - 90.0f));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, tridentEntity.prevPitch, tridentEntity.getPitch()) - 45.0f));

            Float tridentScale = ((ItemDataAccessor) tridentEntity).getTridentScale();
            if (tridentScale == null) tridentScale = GrossHacksConfig.INSTANCE.trident_size;
            matrixStack.translate(-0.5 * tridentScale, -0.5 * tridentScale, 0);
            matrixStack.scale(tridentScale, tridentScale, tridentScale);

            MinecraftClient.getInstance().getItemRenderer().renderItem(ItemStack.fromNbt(tridentItemData.getCompound("TridentItemData")),
                    ModelTransformationMode.GUI, getLight(tridentEntity, 1), OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, tridentEntity.getWorld(), 0);
            matrixStack.pop();
            ci.cancel();
        }
    }

    protected TridentEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }
}
