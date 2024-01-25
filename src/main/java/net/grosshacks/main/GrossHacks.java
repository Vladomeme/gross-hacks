package net.grosshacks.main;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.grosshacks.main.util.ChatBlocker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class GrossHacks implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("grosshacks");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static final ArrayList<String> projectileList = new ArrayList<>();
    public static final Map<String, Float> tridentScales = new HashMap<>();

    public static KeyBinding unmountKey;
    public static boolean shouldDismount = false;

    private static final List<String> chats = List.of("g", "l", "wc", "tr", "lfg", "gc");

    static int nightmareTicks = 1200;

    @Override
    public void onInitializeClient() {

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return new Identifier("grosshacks", "resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                findProjectiles(manager);
                findScales(manager);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) tick();
        });

        FabricLoader.getInstance().getModContainer("grosshacks").ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("grosshacks","clean_buttons"),
                        container, ResourcePackActivationType.NORMAL));

        unmountKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Unmount", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Vlado's Gross Hacks"));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("show").executes(ctx -> show(null))
                        .then(argument("chat", StringArgumentType.string())
                                        .suggests(this::getSuggestions).executes(ctx ->
                                        show(StringArgumentType.getString(ctx, "chat"))))
        ));

        LOGGER.info("Ahhh hell no");
    }

    private static int show(String chat) {
        ClientPlayerEntity player = client.player;
        if (player == null) return 1;

        if (chat == null) player.networkHandler.sendChatMessage("<mainhand>");
        else player.networkHandler.sendCommand(chat + " <mainhand>");
        return 1;
    }

    /*
    Gets a list of tridents with custom projectiles, runs on resource reload.
    */
    public static void findProjectiles(ResourceManager manager) {

        projectileList.clear();

        manager.findResources("optifine", id -> id.getPath().endsWith("projectile.png")).keySet().forEach(id -> {
            String name = Path.of(id.getPath()).getFileName().toString()
                    .replace("_projectile.png", "")
                    .replace("_", " ");
            projectileList.add(name);
        });
    }

    public static void findScales(ResourceManager manager) {

        tridentScales.clear();

        manager.findResources("optifine", id -> id.getPath().endsWith("trident_scaling.txt")).keySet().forEach(id -> {
            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(manager.getResource(id).get().getInputStream()));

                while ((line = reader.readLine()) != null) {
                    String[] entry = line.split(":", 2);
                    tridentScales.put(entry[0], Float.valueOf(entry[1]));
                }
            } catch (IOException e) {
                throw new RuntimeException("An error occured while trying to read "+id.getPath());
            }
        });
    }

    public static void setTicks(int ticks) {
        nightmareTicks = ticks;
    }

    public static int getTicks() {
        return nightmareTicks;
    }

    public static void tick() {
        if (GrossHacksConfig.INSTANCE.nightmare_timer &&
                client.player.getWorld().getRegistryKey().getValue().toString().endsWith("gallery")) {
            if (nightmareTicks > 0) nightmareTicks--;
            if (GrossHacks.getTicks() / 20 <= GrossHacksConfig.INSTANCE.time_remaining) {
                client.inGameHud.setOverlayMessage(
                        Text.of("§3Nightmares arrive in: " + (GrossHacks.getTicks() / 20)), false);
            }
        }
        if (((ChatBlocker) client.inGameHud.getChatHud()).isBlocked())
            ((ChatBlocker) client.inGameHud.getChatHud()).unblockChat();
    }

    private CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        for (String chat : chats) builder.suggest(chat);
        return builder.buildFuture();
    }
}
