package com.bacco.gui;

import com.bacco.Palette;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class WPaletteWidget extends WPlainPanel {

    ArrayList<WColourPreviewIcon> SavedColours = new ArrayList<>();
    Identifier colourIdentifier = Identifier.of("mcrgb", "square.png");
    int slotsWidth = 9;
    Palette palette;
    Identifier editIdentifier = Identifier.of("mcrgb", "edit.png");
    TextureIcon editIcon = new TextureIcon(editIdentifier);
    WButton editButton = new WButton(editIcon){
        @Environment(EnvType.CLIENT)
        @Override
        public void addTooltip(TooltipBuilder tooltip) {
            tooltip.add(Text.translatable("ui.mcrgb.edit_palette_info"));
            super.addTooltip(tooltip);
        }
    };
    Identifier deleteIdentifier = Identifier.of("mcrgb", "delete.png");
    TextureIcon deleteIcon = new TextureIcon(deleteIdentifier);

    WButton deleteButton = new WButton(deleteIcon){
        @Environment(EnvType.CLIENT)
        @Override
        public void addTooltip(TooltipBuilder tooltip) {
            tooltip.add(Text.translatable("ui.mcrgb.delete_palette_info"));
            super.addTooltip(tooltip);
        }
    };

    public WPaletteWidget(){

    }

    public void buildPaletteWidget(ColourGui cg){

        this.setBackgroundPainter(BackgroundPainter.createColorful(0xFFFFFF));
        for(int i = 0; i < slotsWidth; i++) {
            SavedColours.add(new WColourPreviewIcon(colourIdentifier,cg));
            SavedColours.get(i).setInteractable(false);
            this.add(SavedColours.get(i), i*17, 0, 18, 18);
        }
        this.add(editButton,(int)(8.6f*18),0,1,1);
        editButton.setSize(10,10);
        editButton.setIconSize(9);
        editButton.setAlignment(HorizontalAlignment.LEFT);

        this.add(deleteButton,(int)(8.6f*18),9,1,1);
        deleteIcon.setColor(0xFF_FC5454);
        deleteButton.setSize(10,10);
        deleteButton.setIconSize(9);
        deleteButton.setAlignment(HorizontalAlignment.LEFT);
        deleteButton.setOnClick(() -> {cg.DeletePalette(this);});

    }

}
