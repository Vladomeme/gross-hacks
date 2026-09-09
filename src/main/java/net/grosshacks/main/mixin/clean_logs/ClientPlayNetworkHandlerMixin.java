package net.grosshacks.main.mixin.clean_logs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityPassengersSet", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"),
            cancellable = true)
    private void gh$onEntityPassengersSet(EntityPassengersSetS2CPacket packet, CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.cleanLogs) ci.cancel();
    }

    @WrapOperation(method = "setPublicSession", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void gh$setPublicSession(Logger logger, String string, Object o, Operation<Void> original) {
        if (!GrossHacksConfig.INSTANCE.cleanLogs) original.call(logger, string, o);
    }

    @Inject(method = "onTeam", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"),
            cancellable = true)
    private void gh$onTeam(TeamS2CPacket packet, CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.cleanLogs) ci.cancel();
    }

    @Inject(method = "onPlayerList", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            cancellable = true)
    private void gh$onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (GrossHacksConfig.INSTANCE.cleanLogs) ci.cancel();
    }
}