package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE",
              target = "Lnet/minecraft/entity/player/PlayerEntity;dismountVehicle()V"))
    private void dismountVehicle(PlayerEntity instance) {
        if (MinecraftClient.getInstance().player != null) {
            if (GrossHacksConfig.INSTANCE.fix_mount_desync) {
                if (MinecraftClient.getInstance().player.isSneaking()) instance.dismountVehicle();
            }
            else instance.dismountVehicle();
        }
    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(DDD)V"))
    private void setVelocity(PlayerEntity instance, double x, double y, double z) {
        if (!(GrossHacksConfig.INSTANCE.fix_mount_desync && MinecraftClient.getInstance().player.hasVehicle())) instance.setVelocity(x, y, z);
    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setPos(DDD)V"))
    private void setPos(PlayerEntity instance, double x, double y, double z) {
        if (!(GrossHacksConfig.INSTANCE.fix_mount_desync && MinecraftClient.getInstance().player.hasVehicle())) instance.setPos(x, y, z);
    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;updatePositionAndAngles(DDDFF)V"))
    private void updatePositionAndAngles(PlayerEntity instance, double x, double y, double z, float j, float k) {
        if (!(GrossHacksConfig.INSTANCE.fix_mount_desync && MinecraftClient.getInstance().player.hasVehicle())) instance.updatePositionAndAngles(x, y, z, j, k);
    }
}

