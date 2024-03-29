package net.grosshacks.main;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GrossHacksConfigScreen {
        public static Screen create(Screen parent) {

                GrossHacksConfig currentConfig = GrossHacksConfig.INSTANCE, defaultConfig = new GrossHacksConfig();

                ConfigBuilder builder = ConfigBuilder.create()
                        .setParentScreen(parent)
                        .setTitle(Text.of("Vlado's Gross Hacks"))
                        .setSavingRunnable(currentConfig::write);

                //FEATURES
                ConfigCategory category = builder.getOrCreateCategory(Text.of("Features"));
                ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable stats and charms buttons"), currentConfig.enable_extra_buttons)
                        .setTooltip(Text.of("Adds buttons to open player stats and charms in your inventory\n" +
                                "and stops recipe book button from opening stats."))
                        .setSaveConsumer(newConfig -> currentConfig.enable_extra_buttons = newConfig)
                        .setDefaultValue(defaultConfig.enable_extra_buttons)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Dynamic button textures"), currentConfig.dynamic_textures)
                        .setTooltip(Text.of("Attempts to make stats & charms buttons use the same texture as\n" +
                                "current resource pack's recipe book button. If disabled, resource packs\n" +
                                "will be able to replace the textures."))
                        .setSaveConsumer(newConfig -> currentConfig.dynamic_textures = newConfig)
                        .setDefaultValue(defaultConfig.dynamic_textures)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Disable dismounting"), currentConfig.disable_dismouting)
                        .setTooltip(Text.of("Disables dismounting via sneaking. You can dismount\n" +
                                "using a special changeable keybind (U by default.)"))
                        .setSaveConsumer(newConfig -> currentConfig.disable_dismouting = newConfig)
                        .setDefaultValue(defaultConfig.disable_dismouting)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Offhand shift-clicking"), currentConfig.offhand_equip)
                        .setTooltip(Text.of("Allows you to shift click any items with offhand stats into\n" +
                                "the offhand slot."))
                        .setSaveConsumer(newConfig -> currentConfig.offhand_equip = newConfig)
                        .setDefaultValue(defaultConfig.offhand_equip)
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

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Gallery nightmare timer"), currentConfig.nightmare_timer)
                        .setTooltip(Text.of("Shows approximate time left before nightmares arrive in gallery."))
                        .setSaveConsumer(newConfig -> currentConfig.nightmare_timer = newConfig)
                        .setDefaultValue(defaultConfig.nightmare_timer)
                        .build());

                category.addEntry(entryBuilder.startIntField(Text.of("Time until nightmares"), currentConfig.time_remaining)
                        .setTooltip(Text.of("Timer shows up if you have equal or less time left (in seconds)."))
                        .setSaveConsumer(newConfig -> currentConfig.time_remaining = newConfig)
                        .setDefaultValue(defaultConfig.time_remaining)
                        .build());

                //FIXES
                category = builder.getOrCreateCategory(Text.of("Fixes"));

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Disable tool interactions"), currentConfig.remove_interactions)
                        .setTooltip(Text.of("Disables special interactions with blocks when right\n" +
                                "clicking with an axe/shovel/hoe"))
                        .setSaveConsumer(newConfig -> currentConfig.remove_interactions = newConfig)
                        .setDefaultValue(defaultConfig.remove_interactions)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Fix mount desync"), currentConfig.fix_mount_desync)
                        .setTooltip(Text.of("Fixes desync when server tells client to dismount\n" +
                                "without actually dismounting you server side"))
                        .setSaveConsumer(newConfig -> currentConfig.fix_mount_desync = newConfig)
                        .setDefaultValue(defaultConfig.fix_mount_desync)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Mute goat horns"), currentConfig.mute_horns)
                        .setTooltip(Text.of("Mutes goat horns and displays who used one."))
                        .setSaveConsumer(newConfig -> currentConfig.mute_horns = newConfig)
                        .setDefaultValue(defaultConfig.mute_horns)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Keep deleted messages"), currentConfig.undelete)
                        .setTooltip(Text.of("Marks deleted messages instead of hiding them."))
                        .setSaveConsumer(newConfig -> currentConfig.undelete = newConfig)
                        .setDefaultValue(defaultConfig.undelete)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Remove log spam"), currentConfig.clean_logs)
                        .setTooltip(Text.of("Removes logs like \"Received passengers for unknown entity\",\n" +
                                "\"Received packet for unknown team\" and \"Cannot remove\n" +
                                "from team\"."))
                        .setSaveConsumer(newConfig -> currentConfig.clean_logs = newConfig)
                        .setDefaultValue(defaultConfig.clean_logs)
                        .build());

                //PERFORMANCE
                category = builder.getOrCreateCategory(Text.of("Performance"));

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Spawner entity culling"), currentConfig.spawner_culling)
                        .setTooltip(Text.of("Hides the spinny mobs in inactive spawners. Helps with\n" +
                                "performance in spawner heavy areas."))
                        .setSaveConsumer(newConfig -> currentConfig.spawner_culling = newConfig)
                        .setDefaultValue(defaultConfig.spawner_culling)
                        .build());

                category.addEntry(entryBuilder.startIntField(Text.of("Inactive render range"), currentConfig.extra_range)
                        .setTooltip(Text.of("Additional distance on top of spawner's required player\n" +
                                "range before its spinny entity disappears."))
                        .setSaveConsumer(newConfig -> currentConfig.extra_range = newConfig)
                        .setDefaultValue(defaultConfig.extra_range)
                        .build());

                category.addEntry(entryBuilder.startBooleanToggle(Text.of("Range mode"), currentConfig.range_mode)
                        .setTooltip(Text.of("Render all spawner entities in a set range."))
                        .setSaveConsumer(newConfig -> currentConfig.range_mode = newConfig)
                        .setDefaultValue(defaultConfig.range_mode)
                        .build());

                category.addEntry(entryBuilder.startIntField(Text.of("Range"), currentConfig.range)
                        .setTooltip(Text.of("Range used with Range mode. Set to 0 to stop rendering the\n" +
                                "spinning entities completely."))
                        .setSaveConsumer(newConfig -> currentConfig.range = newConfig)
                        .setDefaultValue(defaultConfig.range)
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
