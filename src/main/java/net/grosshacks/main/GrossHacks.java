package net.grosshacks.main;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
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
import java.util.Map;

@Environment(EnvType.CLIENT)
public class GrossHacks implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("grosshacks");

    public static final ArrayList<String> projectileList = new ArrayList<>();
    public static final Map<String, Float> tridentScales = new HashMap<>();

    public static KeyBinding unmountKey;

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

        FabricLoader.getInstance().getModContainer("grosshacks").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("grosshacks", "clean_buttons"), container, ResourcePackActivationType.NORMAL);
        });

        unmountKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Unmount", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Vlado's Gross Hacks"));

        LOGGER.info("Ahhh hell no");
    }

    /*
    Gets a list of tridents with custom projectiles, runs on resource reload.
    */
    public static void findProjectiles(ResourceManager manager) {

        projectileList.clear();

        for (Identifier id : manager.findResources("optifine", id -> id.endsWith("projectile.png"))) {
            String name = Path.of(id.getPath()).getFileName().toString()
                    .replace("_projectile.png", "")
                    .replace("_", " ");
            projectileList.add(name);
        }
    }

    public static void findScales(ResourceManager manager) {

        tridentScales.clear();

        for (Identifier id : manager.findResources("optifine", id -> id.endsWith("trident_scaling.txt"))) {
            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(manager.getResource(id).getInputStream()));

                while ((line = reader.readLine()) != null) {
                    String[] entry = line.split(":", 2);
                    tridentScales.put(entry[0], Float.valueOf(entry[1]));
                }
            } catch (IOException e) {
                throw new RuntimeException("An error occured while trying to read "+id.getPath());
            }
        }
    }
}
