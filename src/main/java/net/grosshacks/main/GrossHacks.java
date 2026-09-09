package net.grosshacks.main;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.grosshacks.main.feature.Commands;
import net.grosshacks.main.feature.GeneratedTextures;
import net.grosshacks.main.feature.Misc;
import net.grosshacks.main.feature.TridentProperties;
import net.grosshacks.main.mixin.KeyBindingAccessor;
import net.grosshacks.main.feature.wallet.WithdrawalIO;
import net.grosshacks.main.util.MixinUtil;
import net.hph.main.WhitelistManager;
import net.hph.main.config.HPHConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class GrossHacks implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("grosshacks");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GrossHacksConfig config = GrossHacksConfig.INSTANCE;

    public static KeyBinding dismountKey;
    public static KeyBinding toggleGlowingKey;
    public static KeyBinding interactionKey;

    public static boolean handbookLoaded = false;
    public static boolean citResewnLoaded = false;

    @Override
    public void onInitializeClient() {
        registerEvents();
        registerKeybinds();
        if (config.chatCommands) Commands.register();

        FabricLoader.getInstance().getModContainer("grosshacks").ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("grosshacks","clean_buttons"),
                        container, ResourcePackActivationType.NORMAL));

        citResewnLoaded = FabricLoader.getInstance().isModLoaded("citresewn");
        handbookLoaded = FabricLoader.getInstance().isModLoaded("handbook");
        LOGGER.info("Ahhh hell no");
    }

    private static void registerEvents() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return Identifier.of("grosshacks", "resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                TridentProperties.collect(manager);
                GeneratedTextures.prepareButtonTextures(manager);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Misc.tick();
            tickKeybinds();
            if (client.interactionManager != null) ((MixinUtil) client.interactionManager).gh$tick();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> WithdrawalIO.write());
    }

    private static void registerKeybinds() {
        dismountKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Dismount", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Vlado's Gross Hacks"));
        toggleGlowingKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Toggle player glowing", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Vlado's Gross Hacks"));
        interactionKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Allow block interactions", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Vlado's Gross Hacks"));
    }

    private static void tickKeybinds() {
        if (toggleGlowingKey.wasPressed()) {
            client.inGameHud.setOverlayMessage(Text.of("§ePlayer glowing is now " + (config.disableGlowing ? "enabled" : "disabled")), false);
            config.disableGlowing = !config.disableGlowing;
            ((KeyBindingAccessor) toggleGlowingKey).gh$reset();
        }
    }

    //HPH
    public static boolean glowOverrideEnabled() {
        return HPHConfig.INSTANCE.overrideGrossHacksGlowing;
    }

    //HPH
    public static boolean shouldForceGlow(Entity entity) {
        return WhitelistManager.shouldForceGlow(entity);
    }
}
