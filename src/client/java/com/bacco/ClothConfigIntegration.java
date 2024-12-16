package com.bacco;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Function;

public class ClothConfigIntegration {
    public static Screen getConfigScreen(Screen parent) {
        return Internal.getConfigScreen();
    }

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

            configs.addEntry(entryBuilder.startStrField(Text.translatable("option.mcrgb.give_command"),MCRGBConfig.instance.command)
                    .setDefaultValue("give %p %i[%c] %q")
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.command = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.give_command"))
                    .build());

            configs.addEntry(entryBuilder.startBooleanToggle(Text.translatable("Bypass Operator Check"),MCRGBConfig.instance.bypassOP)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> MCRGBConfig.instance.bypassOP = newValue)
                    .setTooltip(Text.translatable("tooltip.mcrgb.bypass_op"))
                    .build());


            builder.setSavingRunnable(MCRGBConfig::save);


            return builder.build();
        }
    }
}

