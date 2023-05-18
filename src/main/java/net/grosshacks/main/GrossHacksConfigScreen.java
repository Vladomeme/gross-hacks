package net.grosshacks.main;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GrossHacksConfigScreen {
        public static Screen create(Screen parent) {

                GrossHacksConfig currentConfig = GrossHacksConfig.INSTANCE, defaultConfig = new GrossHacksConfig();

                ConfigBuilder builder = ConfigBuilder.create()
                        .setParentScreen(parent)
                        .setTitle(Text.of("Vlado's Gross Hacks"))
                        .setSavingRunnable(currentConfig::write);

                ConfigCategory category = builder.getOrCreateCategory(Text.empty());
                ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable stats and charms buttons"), currentConfig.enable_extra_buttons)
                        .setTooltip(Text.of("Adds buttons to open player stats and charms in your inventory\n" +
                                "and stops recipe book button from opening stats."))
                        .setSaveConsumer(newConfig -> currentConfig.enable_extra_buttons = newConfig)
                        .setDefaultValue(defaultConfig.enable_extra_buttons)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Render custom texture on thrown tridents"), currentConfig.thrown_trident_texture)
                        .setTooltip(Text.of("Changes thrown tridents to use a corresponding custom texture.\n" +
                                "Extra hacky and may tank performance"))
                        .setSaveConsumer(newConfig -> currentConfig.thrown_trident_texture = newConfig)
                        .setDefaultValue(defaultConfig.thrown_trident_texture)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Custom trident projectiles"), currentConfig.custom_trident_projectile)
                        .setTooltip(Text.of("If there's a separate texture for trident projectile it will be\n" +
                                "rendered instead. Instructions on how to add a projectile could be\n" +
                                "found in the mod file or on github."))
                        .setSaveConsumer(newConfig -> currentConfig.custom_trident_projectile = newConfig)
                        .setDefaultValue(defaultConfig.custom_trident_projectile)
                        .build());

                category.addEntry(entryBuilder.startFloatField(Text.of("Overall trident size scaling"), currentConfig.trident_size)
                        .setTooltip(Text.of("Default - 1."))
                        .setSaveConsumer(newConfig -> currentConfig.trident_size = newConfig)
                        .setDefaultValue(defaultConfig.trident_size)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Trident size scaling per item"), currentConfig.per_trident_scaling)
                        .setTooltip(Text.of("Enables size scaling for tridents depending on their name.\n" +
                                "Instructions on how to set scaling for a trident could be found\n" +
                                "in the mod file or on github."))
                        .setSaveConsumer(newConfig -> currentConfig.per_trident_scaling = newConfig)
                        .setDefaultValue(defaultConfig.per_trident_scaling)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Disable tool interactions"), currentConfig.remove_interactions)
                        .setTooltip(Text.of("Disables special interactions with blocks when right\n" +
                                "clicking with an axe/shovel/hoe"))
                        .setSaveConsumer(newConfig -> currentConfig.remove_interactions = newConfig)
                        .setDefaultValue(defaultConfig.remove_interactions)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Spawner entity culling"), currentConfig.spawner_culling)
                        .setTooltip(Text.of("Hides the spinny mobs in inactive spawners. Helps with\n" +
                                "performance in spawner heavy areas."))
                        .setSaveConsumer(newConfig -> currentConfig.spawner_culling = newConfig)
                        .setDefaultValue(defaultConfig.spawner_culling)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Hide handheld items on players"), currentConfig.hide_handheld)
                        .setTooltip(Text.of("Hides the handheld items on players. Could help with\n" +
                                "performance. Also looks silly :)."))
                        .setSaveConsumer(newConfig -> currentConfig.hide_handheld = newConfig)
                        .setDefaultValue(defaultConfig.hide_handheld)
                        .build());

                return builder.build();
        }
}
