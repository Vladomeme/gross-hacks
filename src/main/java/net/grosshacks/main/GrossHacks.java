package net.grosshacks.main;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

import java.nio.file.Path;
import java.util.ArrayList;

public class GrossHacks implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("grosshacks");

    public static ArrayList<String> projectileList = new ArrayList<>();

    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return new Identifier("grosshacks", "resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                findProjectiles(manager);
            }
        });

        FabricLoader.getInstance().getModContainer("grosshacks").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("grosshacks", "clean_buttons"), container, ResourcePackActivationType.NORMAL);
        });

        LOGGER.info("Ahhh hell no");
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
}
