package net.grosshacks.main.feature;

import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.feature.wallet.WalletManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class Misc {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GrossHacksConfig config = GrossHacksConfig.INSTANCE;

    public static void tick() {
        if (config.nightmareTimer) NightmareTimer.tick();
        if (config.withdrawMenu) WalletManager.tick();
        if (config.brightBlight) SiriusDisplay.tick();
    }

    public static class NightmareTimer {

        static int nightmareTicks = 1200;

        public static void setTicks(int ticks) {
            nightmareTicks = ticks;
        }

        public static void tick() {
            if (client.player != null && client.player.getWorld().getRegistryKey().getValue().toString().endsWith("gallery")) {
                if (nightmareTicks > 0) nightmareTicks--;
                if (nightmareTicks / 20 <= GrossHacksConfig.INSTANCE.timeRemaining) {
                    client.inGameHud.setOverlayMessage(
                            Text.of("§3Nightmares arrive in: " + (nightmareTicks / 20)), false);
                }
            }
        }
    }

    public static class SiriusDisplay {

        public static boolean inSirius = false;

        private static void tick() {
            if (client.player == null) return;

            Vec3d pos = client.player.getPos();
            inSirius = pos.getX() > 270 && pos.getZ() > 950 && pos.getX() < 380 && pos.getZ() < 1060;
        }
    }
}
