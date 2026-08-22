package com.bacco;

import com.bacco.event.KeyInputHandler;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ClothConfigIntegration {
    public static Screen getConfigScreen(Screen parent) {
        return Internal.getConfigScreen();
    }

    public enum ColourFindMode{
        MCRGB,
        MEAN,
        MEDIAN
    }

    public enum ItemSpawningMode{
        CREATIVE_DRAG,
        GIVE_COMMAND,
        VIEW_ONLY
    }

    static Function<ItemSpawningMode, Component> ismNameProvider = mode -> switch (mode) {
        case CREATIVE_DRAG -> Component.translatable("options.mcrgb.creative_drag");
        case GIVE_COMMAND -> Component.translatable("options.mcrgb.give_command");
        case VIEW_ONLY -> Component.translatable("options.mcrgb.view_only");
    };

    private static class Internal {
        private static final Function<Boolean, Component> alwaysShowToolTipsTextSupplier = bool -> {
            if (bool) return Component.translatable("options.mcrgb.all_contexts");
            else return Component.translatable("options.mcrgb.picker_only");
        };
        private static final Function<Boolean, Component> sliderConstantUpdateTextSupplier = bool -> {
            if (bool) return Component.translatable("options.mcrgb.while_scrolling");
            else return Component.translatable("options.mcrgb.after_scrolling");
        };
        protected static Screen getConfigScreen() {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(Minecraft.getInstance().gui.screen())
                    .setTitle(Component.translatable("title.mcrgb.config"))
                    .setDoesConfirmSave(true);

            ConfigCategory configs = builder.getOrCreateCategory(Component.translatable("options.mcrgb.category.configs"));
            ConfigCategory keybinds = builder.getOrCreateCategory(Component.translatable("Keybinds"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            configs.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.mcrgb.always_show_in_tooltips"), MCRGBConfig.instance.alwaysShowToolTips)
                    .setDefaultValue(false)
                    .setYesNoTextSupplier(alwaysShowToolTipsTextSupplier)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.alwaysShowToolTips = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.always_show_in_tooltips"))
                    .build());

            configs.addEntry(entryBuilder.startIntField(Component.translatable("option.mcrgb.maxTooltipLines"), MCRGBConfig.instance.maxTooltipLines)
                    .setDefaultValue(15)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.maxTooltipLines = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.maxTooltipLines"))
                    .build());

            configs.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.mcrgb.slider_constant_update"), MCRGBConfig.instance.sliderConstantUpdate)
                    .setDefaultValue(true)
                    .setYesNoTextSupplier(sliderConstantUpdateTextSupplier)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.sliderConstantUpdate = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.slider_constant_update"))
                    .build());


            configs.addEntry(entryBuilder.startSelector(Component.translatable("option.mcrgb.colour_mode"),ColourFindMode.values(), MCRGBConfig.instance.mode)
                    .setDefaultValue(ColourFindMode.MCRGB)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.mode = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.colour_mode"))
                    .build());

            @NotNull SelectionListEntry<ItemSpawningMode> itemSpawningMode = entryBuilder.startSelector(Component.translatable("option.mcrgb.creative_give"),ItemSpawningMode.values(), MCRGBConfig.instance.creativeGive)
                    .setDefaultValue(ItemSpawningMode.CREATIVE_DRAG)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.creativeGive = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.creative_give"))
                    .setNameProvider(ismNameProvider)
                    .build();
            configs.addEntry(itemSpawningMode);

            configs.addEntry(entryBuilder.startStrField(Component.literal("    ").append(Component.translatable("option.mcrgb.give_command")),MCRGBConfig.instance.command)
                    .setRequirement(Requirement.isValue(itemSpawningMode,ItemSpawningMode.GIVE_COMMAND))
                    .setDefaultValue("give %p %i[%c] %q")
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.command = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.give_command"))
                    .build());

            configs.addEntry(entryBuilder.startBooleanToggle(Component.literal("    ").append(Component.translatable("option.mcrgb.bypass_op")),MCRGBConfig.instance.bypassOP)
                    .setRequirement(Requirement.isValue(itemSpawningMode,ItemSpawningMode.GIVE_COMMAND))
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.bypassOP = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.bypass_op"))
                    .build());

            configs.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.mcrgb.readJsonFile"), MCRGBConfig.instance.readJsonFile)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.readJsonFile = newValue)
                    .setTooltip(Component.translatable("tooltip.mcrgb.readJsonFile"))
                    .build());

            keybinds.addEntry(entryBuilder.fillKeybindingField(Component.translatable("key.mcrgb.colour_inv_open"), KeyInputHandler.colourInvKey).build());
            keybinds.addEntry(entryBuilder.fillKeybindingField(Component.translatable("key.mcrgb.quick_search_from_clipboard"), KeyInputHandler.quickSearchKey).build());


            builder.setSavingRunnable(MCRGBConfig::save);


            return builder.build();
        }
    }
}

