package net.grosshacks.main.feature;

import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class GeneratedTextures {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GrossHacksConfig config = GrossHacksConfig.INSTANCE;

    public static ButtonTextures statsTextures;
    public static ButtonTextures charmsTextures;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void prepareButtonTextures(ResourceManager rm) {
        if (!config.generateTextures) {
            statsTextures = new ButtonTextures(Identifier.of("grosshacks", "stats_unfocused"),
                    Identifier.of("grosshacks", "stats_focused"));
            charmsTextures = new ButtonTextures(Identifier.of("grosshacks", "charms_unfocused"),
                    Identifier.of("grosshacks", "charms_focused"));
            return;
        }

        TextureManager tm = client.getTextureManager();
        try {
            //UNFOCUSED----------------------------------------------------
            BufferedImage sourceUnfocused = ImageIO.read(rm.getResource(
                    Identifier.of("minecraft", "textures/gui/sprites/recipe_book/button.png")).get().getInputStream());
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
            ctxUnfocused.drawImage(ImageIO.read(rm.getResource(Identifier.of("grosshacks", "textures/gui/sprites/charms_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(imageUnfocused, "png", os);
            Identifier charmsUnfocused = tm.registerDynamicTexture("textures/gui/sprites/charms",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //stats
            ctxCopy.drawImage(ImageIO.read(rm.getResource(Identifier.of("grosshacks", "textures/gui/sprites/stats_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageCopy, "png", os);
            Identifier statsUnfocused = tm.registerDynamicTexture("textures/gui/sprites/stats",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //FOCUSED----------------------------------------------------
            BufferedImage sourceFocused = ImageIO.read(rm.getResource(
                    Identifier.of("minecraft", "textures/gui/sprites/recipe_book/button_highlighted.png")).get().getInputStream());
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
            ctxFocused.drawImage(ImageIO.read(rm.getResource(Identifier.of("grosshacks", "textures/gui/sprites/charms_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageFocused, "png", os);
            Identifier charmsFocused = tm.registerDynamicTexture("textures/gui/sprites/charms",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //stats
            ctxCopy.drawImage(ImageIO.read(rm.getResource(Identifier.of("grosshacks", "textures/gui/sprites/stats_clean.png"))
                    .get().getInputStream()), 0, 0, null);
            os = new ByteArrayOutputStream();
            ImageIO.write(imageCopy, "png", os);
            Identifier statsFocused = tm.registerDynamicTexture("textures/gui/sprites/stats",
                    new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(os.toByteArray()))));

            //FINISH------------------------------------------------------
            charmsTextures = new ButtonTextures(charmsUnfocused, charmsFocused);
            statsTextures = new ButtonTextures(statsUnfocused, statsFocused);
        }
        catch (Exception e) {
            GrossHacks.LOGGER.error("Failed to generate Gross Hacks button icons.");
            charmsTextures = new ButtonTextures(Identifier.of("grosshacks", "charms_unfocused"),
                    Identifier.of("grosshacks", "charms_focused"));
            statsTextures = new ButtonTextures(Identifier.of("grosshacks", "stats_unfocused"),
                    Identifier.of("grosshacks", "stats_focused"));
        }
    }
}
