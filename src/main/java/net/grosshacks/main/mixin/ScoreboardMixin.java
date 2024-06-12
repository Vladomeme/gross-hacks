package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {

    @Inject(method = "removePlayerFromTeam", at = @At(value = "INVOKE",
            target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V"), cancellable = true)
    private void removePlayerFromTeam(String playerName, Team team, CallbackInfo ci) {
        if (!GrossHacksConfig.INSTANCE.cleanLogs) ci.cancel();
    }
}