package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//config -> hideFishingBobbers
@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/entity/projectile/FishingBobberEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void gh$render(FishingBobberEntity entity, float f, float g, MatrixStack matrices,
                        VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.hideFishingBobbers
                && entity.getHookedEntity() != null
                && entity.getHookedEntity().equals(MinecraftClient.getInstance().player)) {
            ci.cancel();
        }
    }
}
