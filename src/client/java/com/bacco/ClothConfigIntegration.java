package com.bacco;

import com.bacco.event.KeyInputHandler;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
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

    static Function<ItemSpawningMode, Text> ismNameProvider = mode -> switch (mode) {
        case CREATIVE_DRAG -> Text.translatable("options.mcrgb.creative_drag");
        case GIVE_COMMAND -> Text.translatable("options.mcrgb.give_command");
        case VIEW_ONLY -> Text.translatable("options.mcrgb.view_only");
    };

    private static class Internal {
        private static final Function<Boolean, Text> alwaysShowToolTipsTextSupplier = bool -> {
            if (bool) return Text.translatable("options.mcrgb.all_contexts");
            else return Text.translatable("options.mcrgb.picker_only");
        };
        private static final Function<Boolean, Text> sliderConstantUpdateTextSupplier = bool -> {
            if (bool) return Text.translatable("options.mcrgb.while_scrolling");
            else return Text.translatable("options.mcrgb.after_scrolling");
        };

        protected static Screen getConfigScreen() {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle(Text.translatable("title.mcrgb.config"))
                    .setDoesConfirmSave(true);

            ConfigCategory configs = builder.getOrCreateCategory(Text.translatable("options.mcrgb.category.configs"));
            ConfigCategory keybinds = builder.getOrCreateCategory(Text.translatable("Keybinds"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            configs.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.mcrgb.always_show_in_tooltips"), MCRGBConfig.instance.alwaysShowToolTips)
                    .setDefaultValue(false)
                    .setYesNoTextSupplier(alwaysShowToolTipsTextSupplier)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.alwaysShowToolTips = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.always_show_in_tooltips"))
                    .build());

            configs.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.mcrgb.slider_constant_update"), MCRGBConfig.instance.sliderConstantUpdate)
                    .setDefaultValue(true)
                    .setYesNoTextSupplier(sliderConstantUpdateTextSupplier)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.sliderConstantUpdate = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.slider_constant_update"))
                    .build());


            configs.addEntry(entryBuilder.startSelector(Text.translatable("option.mcrgb.colour_mode"),ColourFindMode.values(), MCRGBConfig.instance.mode)
                    .setDefaultValue(ColourFindMode.MCRGB)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.mode = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.colour_mode"))
                    .build());

            @NotNull SelectionListEntry<ItemSpawningMode> itemSpawningMode = entryBuilder.startSelector(Text.translatable("option.mcrgb.creative_give"),ItemSpawningMode.values(), MCRGBConfig.instance.creativeGive)
                    .setDefaultValue(ItemSpawningMode.CREATIVE_DRAG)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.creativeGive = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.creative_give"))
                    .setNameProvider(ismNameProvider)
                    .build();
            configs.addEntry(itemSpawningMode);

            configs.addEntry(entryBuilder.startStrField(Text.literal("    ").append(Text.translatable("option.mcrgb.give_command")),MCRGBConfig.instance.command)
                    .setRequirement(Requirement.isValue(itemSpawningMode,ItemSpawningMode.GIVE_COMMAND))
                    .setDefaultValue("give %p %i[%c] %q")
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.command = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.give_command"))
                    .build());

            configs.addEntry(entryBuilder.startBooleanToggle(Text.literal("    ").append(Text.translatable("option.mcrgb.bypass_op")),MCRGBConfig.instance.bypassOP)
                    .setRequirement(Requirement.isValue(itemSpawningMode,ItemSpawningMode.GIVE_COMMAND))
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.bypassOP = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.bypass_op"))
                    .build());



            keybinds.addEntry(entryBuilder.fillKeybindingField(Text.translatable("key.mcrgb.colour_inv_open"), KeyInputHandler.colourInvKey).build());
            keybinds.addEntry(entryBuilder.fillKeybindingField(Text.translatable("key.mcrgb.quick_search_from_clipboard"), KeyInputHandler.quickSearchKey).build());


            builder.setSavingRunnable(MCRGBConfig::save);


            return builder.build();
        }
    }
}

