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
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
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

    public static Identifier stats;
    public static Identifier charms;

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
                stats = null;
                charms = null;
                if (GrossHacksConfig.INSTANCE.dynamic_textures) generateButtons(manager);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) tick();
        });

        FabricLoader.getInstance().getModContainer("grosshacks").ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("grosshacks","clean_buttons"),
                        container, ResourcePackActivationType.NORMAL));

        unmountKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Dismount", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Vlado's Gross Hacks"));

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

    public static void generateButtons(ResourceManager rm) {
        TextureManager tm = MinecraftClient.getInstance().getTextureManager();
        try {
            BufferedImage source = ImageIO.read(rm.getResource(new Identifier("minecraft", "textures/gui/recipe_button.png")).get().getInputStream());
            BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctx = image.createGraphics();
            ctx.drawImage(source, 0, 0, null);

            //inactive empty
            Color color = new Color(image.getRGB(2, 2), true);
            ctx.setBackground(new Color(0, 0, 0, 0));
            ctx.clearRect(2, 2, 16, 14);
            ctx.setColor(color);
            ctx.fillRect(2, 2, 16, 14);
            //hovered empty
            color = new Color(image.getRGB(2, 21), true);
            ctx.clearRect(2, 21, 16, 14);
            ctx.setColor(color);
            ctx.fillRect(2, 21, 16, 14);

            //make a copy
            BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctxCopy = imageCopy.createGraphics();
            ctxCopy.drawImage(image, 0, 0, null);

            //charms
            ctx.drawImage(ImageIO.read(rm.getResource(new Identifier("grosshacks", "textures/charms_button_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            charms = tm.registerDynamicTexture("charms",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //stats
            ctxCopy.drawImage(ImageIO.read(rm.getResource(new Identifier("grosshacks", "textures/stats_button_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageCopy, "png", os);
            stats = tm.registerDynamicTexture("stats",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

        } catch (Exception e) {
            LOGGER.error("Failed to dynamically generate Gross Hacks button icons.");
        }
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
