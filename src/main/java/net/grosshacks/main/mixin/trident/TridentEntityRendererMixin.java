package net.grosshacks.main.mixin.trident;

import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.MixinUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
public abstract class TridentEntityRendererMixin extends EntityRenderer<TridentEntity> implements MixinUtil {

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;" +
            "Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"), cancellable = true)
    private void render(TridentEntity entity, float f, float g, MatrixStack matrices,
                        VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (!GrossHacksConfig.INSTANCE.tridentCIT) return;
        if (((MixinUtil) entity).gh$getTrident() == null) return;

        float scale = ((MixinUtil) entity).gh$getTridentScale();
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, entity.prevYaw, entity.getYaw()) - 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, entity.prevPitch, entity.getPitch()) - 45.0f));
        matrices.translate(-0.5 * scale, -0.5 * scale, 0);
        matrices.scale(scale, scale, scale);

        MinecraftClient.getInstance().getItemRenderer().renderItem(((MixinUtil) entity).gh$getTrident(), ModelTransformationMode.GUI,
                getLight(entity, 1), OverlayTexture.DEFAULT_UV, matrices, vertexConsumerProvider, entity.getWorld(), 0);
        matrices.pop();
        ci.cancel();
    }

    @SuppressWarnings("unused")
    protected TridentEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
}
