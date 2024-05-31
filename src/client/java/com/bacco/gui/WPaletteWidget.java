package com.bacco.gui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class WPaletteWidget extends WPlainPanel {

    ArrayList<WColourPreviewIcon> SavedColours = new ArrayList<>();
    Identifier colourIdentifier = new Identifier("mcrgb", "square.png");
    int slotsWidth = 9;

    public WPaletteWidget(){
        this.setBackgroundPainter(BackgroundPainter.createColorful(0xFFFFFF));
        for(int i = 0; i < slotsWidth; i++) {
            SavedColours.add(new WColourPreviewIcon(colourIdentifier));

            this.add(SavedColours.get(i), i*18, 0, 18, 18);
        }
    }
}
