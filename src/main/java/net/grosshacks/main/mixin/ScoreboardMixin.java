package net.grosshacks.main.mixin;

import com.google.common.collect.Maps;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin {

    @Final
    @Shadow
    private final Map<String, Team> teamsByPlayer = Maps.newHashMap();

    @Inject(method = "removePlayerFromTeam", at = @At(value = "HEAD"), cancellable = true)
    private void removePlayerFromTeam(String playerName, Team team, CallbackInfo ci) {
        if (!GrossHacksConfig.INSTANCE.clean_logs && this.getPlayerTeam(playerName) != team) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
        }
        this.teamsByPlayer.remove(playerName);
        team.getPlayerList().remove(playerName);
        ci.cancel();
    }

    @Shadow
    public Team getPlayerTeam(String playerName) {
        return null;
    }
}