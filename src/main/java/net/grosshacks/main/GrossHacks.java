package net.grosshacks.main;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.grosshacks.main.mixin.KeyBindingAccessor;
import net.grosshacks.main.wallet.WalletManager;
import net.grosshacks.main.wallet.WithdrawalIO;
import net.hph.main.WhitelistManager;
import net.hph.main.config.HPHConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Environment(EnvType.CLIENT)
public class GrossHacks implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("grosshacks");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static final HashSet<String> projectileList = new HashSet<>();
    public static final HashMap<String, Float> tridentScales = new HashMap<>();

    public static KeyBinding unmountKey;
    public static KeyBinding toggleGlowing;

    public static ButtonTextures stats;
    public static ButtonTextures charms;

    private static final List<String> chats = List.of("g", "l", "wc", "tr", "lfg", "gc");

    static int nightmareTicks = 1200;

    public static boolean handbookAvailable = false;
    public static boolean inSirius = false;

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
                if (GrossHacksConfig.INSTANCE.generateTextures) generateButtons(manager);
                else {
                    stats = new ButtonTextures(new Identifier("grosshacks", "stats_unfocused"),
                            new Identifier("grosshacks", "stats_focused"));
                    charms = new ButtonTextures(new Identifier("grosshacks", "charms_unfocused"),
                            new Identifier("grosshacks", "charms_focused"));
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) tick();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> WithdrawalIO.write());

        FabricLoader.getInstance().getModContainer("grosshacks").ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("grosshacks","clean_buttons"),
                        container, ResourcePackActivationType.NORMAL));

        unmountKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Dismount", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Vlado's Gross Hacks"));
        toggleGlowing = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Toggle player glowing", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Vlado's Gross Hacks"));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("show").executes(ctx -> showCommand(null))
                        .then(argument("chat", StringArgumentType.string())
                                        .suggests((ctx, builder) -> getSuggestions(builder)).executes(ctx ->
                                        showCommand(StringArgumentType.getString(ctx, "chat"))))
        ));

        if (FabricLoader.getInstance().isModLoaded("handbook")) handbookAvailable = true;
        LOGGER.info("Ahhh hell no");
    }

    @SuppressWarnings("SameReturnValue")
    private static int showCommand(String chat) {
        ClientPlayerEntity player = client.player;
        if (player == null) return 1;

        if (chat == null) player.networkHandler.sendChatMessage("<mainhand>");
        else player.networkHandler.sendCommand(chat + " <mainhand>");
        return 1;
    }

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
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                BufferedReader reader = new BufferedReader(new InputStreamReader(manager.getResource(id).get().getInputStream()));

                while ((line = reader.readLine()) != null) {
                    String[] entry = line.split(":", 2);
                    tridentScales.put(entry[0], Float.valueOf(entry[1]));
                }
            }
            catch (IOException e) {
                throw new RuntimeException("An error occured while trying to read " + id.getPath());
            }
        });
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void generateButtons(ResourceManager rm) {
        TextureManager tm = MinecraftClient.getInstance().getTextureManager();
        try {
            //UNFOCUSED----------------------------------------------------
            BufferedImage sourceUnfocused = ImageIO.read(rm.getResource(
                    new Identifier("minecraft", "textures/gui/sprites/recipe_book/button.png")).get().getInputStream());
            BufferedImage imageUnfocused = new BufferedImage(20, 18, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctxUnfocused = imageUnfocused.createGraphics();
            ctxUnfocused.drawImage(sourceUnfocused, 0, 0, null);

            Color color = new Color(imageUnfocused.getRGB(2, 2), true);
            ctxUnfocused.setBackground(new Color(0, 0, 0, 0));
            ctxUnfocused.clearRect(2, 2, 16, 14);
            ctxUnfocused.setColor(color);
            ctxUnfocused.fillRect(2, 2, 16, 14);

            BufferedImage imageCopy = new BufferedImage(20, 18, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctxCopy = imageCopy.createGraphics();
            ctxCopy.drawImage(imageUnfocused, 0, 0, null);

            //charms
            ctxUnfocused.drawImage(ImageIO.read(rm.getResource(new Identifier("grosshacks", "textures/gui/sprites/charms_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(imageUnfocused, "png", os);
            Identifier charmsUnfocused = tm.registerDynamicTexture("textures/gui/sprites/charms",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //stats
            ctxCopy.drawImage(ImageIO.read(rm.getResource(new Identifier("grosshacks", "textures/gui/sprites/stats_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageCopy, "png", os);
            Identifier statsUnfocused = tm.registerDynamicTexture("textures/gui/sprites/stats",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //FOCUSED----------------------------------------------------
            BufferedImage sourceFocused = ImageIO.read(rm.getResource(
                    new Identifier("minecraft", "textures/gui/sprites/recipe_book/button_highlighted.png")).get().getInputStream());
            BufferedImage imageFocused = new BufferedImage(20, 18, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctxFocused = imageFocused.createGraphics();
            ctxFocused.drawImage(sourceFocused, 0, 0, null);

            color = new Color(imageFocused.getRGB(2, 2), true);
            ctxFocused.setBackground(new Color(0, 0, 0, 0));
            ctxFocused.clearRect(2, 2, 16, 14);
            ctxFocused.setColor(color);
            ctxFocused.fillRect(2, 2, 16, 14);

            imageCopy = new BufferedImage(20, 18, BufferedImage.TYPE_INT_ARGB);
            ctxCopy = imageCopy.createGraphics();
            ctxCopy.drawImage(imageFocused, 0, 0, null);

            //charms
            ctxFocused.drawImage(ImageIO.read(rm.getResource(new Identifier("grosshacks", "textures/gui/sprites/charms_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageFocused, "png", os);
            Identifier charmsFocused = tm.registerDynamicTexture("textures/gui/sprites/charms",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //stats
            ctxCopy.drawImage(ImageIO.read(rm.getResource(new Identifier("grosshacks", "textures/gui/sprites/stats_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageCopy, "png", os);
            Identifier statsFocused = tm.registerDynamicTexture("textures/gui/sprites/stats",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //FINISH------------------------------------------------------
            charms = new ButtonTextures(charmsUnfocused, charmsFocused);
            stats = new ButtonTextures(statsUnfocused, statsFocused);
        }
        catch (Exception e) {
            LOGGER.error("Failed to dynamically generate Gross Hacks button icons.");
            charms = new ButtonTextures(new Identifier("grosshacks", "charms_unfocused"),
                    new Identifier("grosshacks", "charms_focused"));
            stats = new ButtonTextures(new Identifier("grosshacks", "stats_unfocused"),
                    new Identifier("grosshacks", "stats_focused"));
        }
    }

    public static void setTicks(int ticks) {
        nightmareTicks = ticks;
    }

    public static int getTicks() {
        return nightmareTicks;
    }

    public static void tick() {
        if (client.player == null) return;
        if (GrossHacksConfig.INSTANCE.nightmareTimer &&
                client.player.getWorld().getRegistryKey().getValue().toString().endsWith("gallery")) {
            if (nightmareTicks > 0) nightmareTicks--;
            if (GrossHacks.getTicks() / 20 <= GrossHacksConfig.INSTANCE.timeRemaining) {
                client.inGameHud.setOverlayMessage(
                        Text.of("§3Nightmares arrive in: " + (GrossHacks.getTicks() / 20)), false);
            }
        }
        if (GrossHacksConfig.INSTANCE.withdrawMenu) WalletManager.tick();
        if (client.player.clientWorld.getTime() % 20 == 0) WalletManager.checkWallet();
        if (toggleGlowing.wasPressed()) {
            MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.of("§ePlayer glowing is now " + (
                    GrossHacksConfig.INSTANCE.disableGlowing ? "enabled" : "disabled")), false);
            GrossHacksConfig.INSTANCE.disableGlowing = !GrossHacksConfig.INSTANCE.disableGlowing;
            ((KeyBindingAccessor) toggleGlowing).reset();
        }
        inSiriusCheck();
    }

    private static void inSiriusCheck() {
        if (client.player == null) return;
        Vec3d pos = client.player.getPos();
        inSirius = pos.getX() > 270 && pos.getZ() > 950 && pos.getX() < 380 && pos.getZ() < 1060;
    }

    private CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
        for (String chat : chats) builder.suggest(chat);
        return builder.buildFuture();
    }

    public static boolean glowOverrideEnabled() {
        return HPHConfig.INSTANCE.overrideGrossHacksGlowing;
    }

    public static boolean shouldForceGlow(Entity entity) {
        return WhitelistManager.shouldForceGlow(entity);
    }
}
