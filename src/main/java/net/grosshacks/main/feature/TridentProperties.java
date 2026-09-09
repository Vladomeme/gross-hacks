package net.grosshacks.main.feature;

import net.minecraft.resource.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

public class TridentProperties {

    public static final HashSet<String> projectileList = new HashSet<>();
    public static final HashMap<String, Float> tridentScales = new HashMap<>();
    public static final HashSet<String> modelledTridents = new HashSet<>();

    public static void collect(ResourceManager manager) {
        findProjectiles(manager);
        findScales(manager);
        findModelledTridents(manager);
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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
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
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void findModelledTridents(ResourceManager manager) {
        modelledTridents.clear();
        manager.findResources("optifine", id -> id.getPath().endsWith("trident.properties")).forEach((id, resource) -> {
            try {
                resource.getReader().lines().forEach(line -> {
                    if (line.startsWith("name.")) {
                        int index = line.indexOf('=');
                        if (index != -1) modelledTridents.add(line.substring(index + 1));
                    }
                });
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
