package net.grosshacks.main;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

public class GrossHacksConfig {

    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "grosshacks.json");

    public static final GrossHacksConfig INSTANCE = read();

    public static GrossHacksConfig read() {
        if (!FILE.exists())
            return new GrossHacksConfig().write();

        Reader reader = null;
        try {
            return new Gson().fromJson(reader = new FileReader(FILE), GrossHacksConfig.class);
        } catch (Exception e) {
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
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return this;
    }

    //Trident CIT
    public boolean tridentCIT = true;
    public boolean customProjectiles = true;
    public float tridentScale = 1;
    public boolean customScaling = true;

    //Performance
    public boolean spawnerCulling = true;
    public int extraRange = 4;
    public boolean rangeMode = false;
    public int range = 16;
    public boolean hideHandheld = false;

    //Gameplay
    public boolean extraButtons = true;
    public boolean dailiesButton = true;
    public boolean withdrawMenu = true;
    public boolean disableToolInteractions = false;
    public boolean disableBlockInteractions = false;
    public boolean useInteractionExceptions = true;
    public boolean barrelInteractions = true;
    public boolean interactionKeyArmMode = false;
    public boolean disablePotionMining = false;
    public boolean loomAntighost = false;
    public boolean rebindDismounting = false;
    public boolean nightmareTimer = false;
    public int timeRemaining = 60;

    //QOL
    public boolean offhandEquip = false;
    public boolean muteHorns = false;
    public boolean hideFishingBobbers = false;
    public boolean disableGlowing = false;
    public boolean masterworkGlowing = false;
    public boolean chatCommands = false;
    public boolean blightAlert = false;
    public boolean brightBlight = false;
    public PotionInfo potionInfo = PotionInfo.Disabled;
    public boolean cleanLogs = false;
    public boolean chatPacketFix = true;

    //Other
    public boolean generateTextures = true;

    public Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .save(this::write)
                .title(Text.literal("Vlado's Gross Hacks"))

                //TRIDENT CIT
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Trident CIT"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Render custom textures"))
                                .description(OptionDescription.of(Text.literal(
                                        "Render tridents with custom textures instead of vanilla model.")))
                                .binding(true, () -> tridentCIT, newVal -> tridentCIT = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Custom projectiles"))
                                .description(OptionDescription.of(Text.literal(
                                        "If there's a separate texture for trident projectile it will be " +
                                                "rendered instead. Instructions on how to add a projectile could be " +
                                                "found on github.")))
                                .binding(true, () -> customProjectiles, newVal -> customProjectiles = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Float>createBuilder()
                                .name(Text.literal("Size scaling"))
                                .description(OptionDescription.of(Text.literal(
                                        "Trident size scaling. Default - 1.")))
                                .binding(1f, () -> tridentScale, newVal -> tridentScale = newVal)
                                .controller(FloatFieldControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Custom size scaling"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables size scaling for tridents depending on their name. " +
                                                "Instructions on how to set scaling for a trident could be " +
                                                "found on github.")))
                                .binding(true, () -> customScaling, newVal -> customScaling = newVal)
                                .controller(TickBoxControllerBuilder::create).build())
                        .build())

                //PERFORMANCE
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Performance"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Spawner entity \"culling\""))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables rendering of mini-entities in inactive spawners.")))
                                .binding(true, () -> spawnerCulling, newVal -> spawnerCulling = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Extra range"))
                                .description(OptionDescription.of(Text.literal(
                                        "Additional distance over spawner's player range before it's \"culled\".")))
                                .binding(4, () -> extraRange, newVal -> extraRange = newVal)
                                .controller(IntegerFieldControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Range mode"))
                                .description(OptionDescription.of(Text.literal(
                                        "Renders all spawner entities in a set range instead of checking for inactivity.")))
                                .binding(false, () -> rangeMode, newVal -> rangeMode = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Range"))
                                .description(OptionDescription.of(Text.literal(
                                        "Range used if Range mode is enabled.")))
                                .binding(16, () -> range, newVal -> range = newVal)
                                .controller(IntegerFieldControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide player handheld items"))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables rendering of player held items.")))
                                .binding(false, () -> hideHandheld, newVal -> hideHandheld = newVal)
                                .controller(TickBoxControllerBuilder::create).build())
                        .build())

                //GAMEPLAY
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Gameplay"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Add stats and charms buttons"))
                                .description(OptionDescription.of(Text.literal(
                                        "Adds buttons to open player stats and charms to your inventory.")))
                                .binding(true, () -> extraButtons, newVal -> extraButtons = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Add dailies button"))
                                .description(OptionDescription.of(Text.literal(
                                        "Adds button to your inventory to open dailies screen.")))
                                .binding(true, () -> dailiesButton, newVal -> dailiesButton = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Wallet withdraw menu"))
                                .description(OptionDescription.of(Text.literal(
                                        "Adds a menu with wallet shortcuts to your inventory screen.")))
                                .binding(true, () -> withdrawMenu, newVal -> withdrawMenu = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Disable tool interactions"))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables right click interactions with blocks when using an axe/shovel/hoe.")))
                                .binding(false, () -> disableToolInteractions, newVal -> disableToolInteractions = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Disable block interactions"))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables right click block interactions in survival mode areas when holding any item. " +
                                                "Works best with '/bi' disabled.\n" +
                                                "Chests and shulker boxes are excluded by default, for others - see interaction exceptions in the option below.")))
                                .binding(false, () -> disableBlockInteractions, newVal -> disableBlockInteractions = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable interaction exceptions"))
                                .description(OptionDescription.of(Text.literal(
                                        "Allows interactions with following blocks: doors, trapdoors, fence gates, buttons, levers and anvils.")))
                                .binding(true, () -> useInteractionExceptions, newVal -> useInteractionExceptions = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable barrel interactions"))
                                .description(OptionDescription.of(Text.literal(
                                        "Allows interactions with barrels.")))
                                .binding(true, () -> barrelInteractions, newVal -> barrelInteractions = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Interaction key arm mode"))
                                .description(OptionDescription.of(Text.literal(
                                        "Pressing the interaction key will allow the next block interaction within 5 seconds.")))
                                .binding(false, () -> interactionKeyArmMode, newVal -> interactionKeyArmMode = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Disable mining with splash potions"))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables mining when holding a splash potion (intended for Alchemists).")))
                                .binding(false, () -> disablePotionMining, newVal -> disablePotionMining = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Loom anti-ghost"))
                                .description(OptionDescription.of(Text.literal(
                                        "Prevents Worldshaper's Loom from going into ghost mode when placed on the ground.")))
                                .binding(false, () -> loomAntighost, newVal -> loomAntighost = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Change dismount keybind"))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables dismounting by sneaking and lets you use a keybind instead (U by default).")))
                                .binding(false, () -> rebindDismounting, newVal -> rebindDismounting = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable nightmare timer"))
                                .description(OptionDescription.of(Text.literal(
                                        "Shows (badly approximated) time left before nightmares arrive in Gallery.")))
                                .binding(false, () -> nightmareTimer, newVal -> nightmareTimer = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Time remaining"))
                                .description(OptionDescription.of(Text.literal(
                                        "Time left on the nightmare timer for it to show up.")))
                                .binding(60, () -> timeRemaining, newVal -> timeRemaining = newVal)
                                .controller(IntegerFieldControllerBuilder::create).build())
                        .build())

                //QOL
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Quality of Life"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Shift-click offhands"))
                                .description(OptionDescription.of(Text.literal(
                                        "Allows you to equip offhands by shift-clicking on them in inventory.")))
                                .binding(false, () -> offhandEquip, newVal -> offhandEquip = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Mute goat horns"))
                                .description(OptionDescription.of(Text.literal(
                                        "Mutes goat horns and displays who used one in actionbar.")))
                                .binding(false, () -> muteHorns, newVal -> muteHorns = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Hide fishing bobbers"))
                                .description(OptionDescription.of(Text.literal(
                                        "Hides fishing bobbers if they're stuck on you.")))
                                .binding(false, () -> hideFishingBobbers, newVal -> hideFishingBobbers = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Disable player glowing"))
                                .description(OptionDescription.of(Text.literal(
                                        "Can be toggled with a keybind (Unset by default).")))
                                .binding(false, () -> disableGlowing, newVal -> disableGlowing = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Disable Masterwork anvil glowing"))
                                .description(OptionDescription.of(Text.literal(
                                        "Disables masterwork anvil glowing in survival mode areas.")))
                                .binding(false, () -> masterworkGlowing, newVal -> masterworkGlowing = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable chat commands"))
                                .description(OptionDescription.of(Text.literal(
                                        "Adds commands for sending hoverable info in chat: /mainhand (/show), /charms, /equipment")))
                                .binding(false, () -> chatCommands, newVal -> chatCommands = newVal)
                                .flag(OptionFlag.GAME_RESTART)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable blight alert"))
                                .description(OptionDescription.of(Text.literal(
                                        "Show a warning on the screen when Blight Wave is casted.")))
                                .binding(false, () -> blightAlert, newVal -> blightAlert = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Bright Blight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Blight Wave is always rendered at max brightness.")))
                                .binding(false, () -> brightBlight, newVal -> brightBlight = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<PotionInfo>createBuilder()
                                .name(Text.literal("Potion throw info"))
                                .description(OptionDescription.of(Text.literal(
                                        "Whenever a potion is thrown, you'll see who threw it.")))
                                .binding(PotionInfo.Disabled, () -> potionInfo, newVal -> potionInfo = newVal)
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(PotionInfo.class)).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Clean logs"))
                                .description(OptionDescription.of(Text.literal(
                                        "Removes useless error/warning spam from game logs.")))
                                .binding(false, () -> cleanLogs, newVal -> cleanLogs = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Fix chat equipment packets"))
                                .description(OptionDescription.of(Text.literal(
                                        "Fixes decoding of packets containing show_item hover events. " +
                                                "Should be disabled after 1.21 update.")))
                                .binding(true, () -> chatPacketFix, newVal -> chatPacketFix = newVal)
                                .controller(TickBoxControllerBuilder::create).build())
                        .build())
                //OTHER
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Other"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Generated button textures"))
                                .description(OptionDescription.of(Text.literal(
                                        "Attempts to make stats & charms buttons use the same texture as " +
                                                "current resource pack's recipe book button. If disabled, resource packs " +
                                                "will be able to replace the textures.")))
                                .binding(true, () -> generateTextures, newVal -> generateTextures = newVal)
                                .controller(TickBoxControllerBuilder::create).build())
                        .build())
                .build()
                .generateScreen(parent);
    }

    public enum PotionInfo implements NameableEnum {
        Disabled,
        All,
        Clucking;

        @Override
        public Text getDisplayName() {
            return Text.literal(name().toLowerCase());
        }
    }

    public boolean potionInfoEnabled() {
        return INSTANCE.potionInfo != PotionInfo.Disabled;
    }
}
