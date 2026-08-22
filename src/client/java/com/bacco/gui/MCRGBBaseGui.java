package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MCRGBBaseGui extends LightweightGuiDescription {

    WGridPanel root = new WGridPanel();
    WGridPanel mainPanel = new WGridPanel();

    WSavedPalettesArea savedPalettesArea;

    WTextField hexInput = new WTextField(Component.literal("#FFFFFF"));

    ColourVector inputColour = new ColourVector(255,255,255);

    net.minecraft.client.Minecraft client;
    MCRGBClient mcrgbClient;
    public ItemStack cursorStack = ItemStack.EMPTY;


    WSprite colourDisplay = new WSprite(Identifier.fromNamespaceAndPath("mcrgb", "rect.png"));




    MCRGBBaseGui(){
    }


    int GetColour(){
        String hex = inputColour.getHex().replace("#","");
        return Integer.parseInt(hex,16);
    }

    void SetColour(ColourVector colour){

        inputColour = colour;
        hexInput.setText(inputColour.getHex());
        colourDisplay.setOpaqueTint(inputColour.asInt());


    }

}
