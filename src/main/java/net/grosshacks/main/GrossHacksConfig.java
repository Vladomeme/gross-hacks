package net.grosshacks.main;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

public class GrossHacksConfig {

    public boolean enable_extra_buttons = true;
    public boolean break_spawners = false;
    public boolean hide_handheld = false;
    public boolean thrown_trident_texture = true;
    public boolean custom_trident_projectile = true;
    public float trident_size = 1;

    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "grosshacks.json");

    public static final GrossHacksConfig INSTANCE = read();

    public static GrossHacksConfig read() {
        if (!FILE.exists())
            return new GrossHacksConfig().write();

        Reader reader = null;
        try {
            return new Gson().fromJson(reader = new FileReader(FILE), GrossHacksConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public GrossHacksConfig write() {
        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            writer = gson.newJsonWriter(new FileWriter(FILE));
            writer.setIndent("    ");
            gson.toJson(gson.toJsonTree(this, GrossHacksConfig.class), writer);
        } catch (Exception e) {
            GrossHacks.LOGGER.error("Couldn't save config");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return this;
    }

}
