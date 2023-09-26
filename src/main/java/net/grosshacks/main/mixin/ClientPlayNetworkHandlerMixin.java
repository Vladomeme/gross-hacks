package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"))
    private void onEntityPassengersSet$warn(Logger instance, String s) {
        if (GrossHacksConfig.INSTANCE.clean_logs) return;
        GrossHacks.LOGGER.warn("Received passengers for unknown entity");
    }

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;removeAllPassengers()V"))
    private void onEntityPassengersSet$removeAllPassengers(Entity instance) {
        if (!GrossHacksConfig.INSTANCE.fix_mount_desync) {
            instance.removeAllPassengers();
            return;
        }
        if (!instance.hasPassengerDeep(MinecraftClient.getInstance().player)) return;

        if (MinecraftClient.getInstance().player.isSneaking() || GrossHacks.shouldDismount || GrossHacks.unmountKey.isPressed()) {
            instance.removeAllPassengers();
            GrossHacks.shouldDismount = false;
        }
    }

    @Redirect(method = "onTeam", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private void onTeam$warn(Logger instance, String s, Object[] objects) {
        if (GrossHacksConfig.INSTANCE.clean_logs) return;
        GrossHacks.LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", objects);
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