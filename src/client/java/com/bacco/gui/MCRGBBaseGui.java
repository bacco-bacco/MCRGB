package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MCRGBBaseGui extends LightweightGuiDescription {

    WGridPanel root = new WGridPanel();
    WGridPanel mainPanel = new WGridPanel();

    WSavedPalettesArea savedPalettesArea;

    WTextField hexInput = new WTextField(Text.literal("#FFFFFF"));

    ColourVector inputColour = new ColourVector(255,255,255);

    net.minecraft.client.MinecraftClient client;
    MCRGBClient mcrgbClient;

    WSprite colourDisplay = new WSprite(Identifier.of("mcrgb", "rect.png"));




    MCRGBBaseGui(){
        //colourDisplay.setSize(18,22);
    }


    int GetColour(){
        String hex = inputColour.getHex().replace("#","");
        return Integer.parseInt(hex,16);
    }

    void SetColour(ColourVector colour){

        inputColour = colour;
        hexInput.setText(inputColour.getHex());

    }

}
