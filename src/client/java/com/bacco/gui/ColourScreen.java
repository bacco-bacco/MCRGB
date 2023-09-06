package com.bacco.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class ColourScreen extends CottonClientScreen{

    public ColourScreen(GuiDescription description) {
        super(description);
    }
    @Override
    public boolean shouldPause() {return false;}
    
}
