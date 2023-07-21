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
            target = "Lnet/minecraft/entity/player/PlayerEntity;setPosition(DDD)V"))
    private void setPosition(PlayerEntity instance, double x, double y, double z) {
        if (!(GrossHacksConfig.INSTANCE.fix_mount_desync && MinecraftClient.getInstance().player.hasVehicle())) instance.setPosition(x, y, z);
    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setPitch(F)V"))
    private void setPitch(PlayerEntity instance, float v) {
        if (!(GrossHacksConfig.INSTANCE.fix_mount_desync && MinecraftClient.getInstance().player.hasVehicle())) instance.setPitch(v);
    }

    @Redirect(method = "onPlayerPositionLook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setYaw(F)V"))
    private void setYaw(PlayerEntity instance, float v) {
        if (!(GrossHacksConfig.INSTANCE.fix_mount_desync && MinecraftClient.getInstance().player.hasVehicle())) instance.setYaw(v);
    }
}

