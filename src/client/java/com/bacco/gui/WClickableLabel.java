package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class WClickableLabel extends WLabel {

    ColourVector colour;
    MCRGBBaseGui gui;

    net.minecraft.client.MinecraftClient client;
    MCRGBClient mcrgbClient;

    Text textUnhovered = text;

    MutableText textHovered = (MutableText) Text.empty();
    public WClickableLabel(Text text, ColourVector colour, MCRGBBaseGui gui) {
        super(text);
        this.colour = colour;
        this.client = gui.client;
        this.mcrgbClient = gui.mcrgbClient;
        this.gui = gui;


        List<Text> components = text.getWithStyle(Style.EMPTY.withItalic(true).withUnderline(true));
        List<Text> componentsBase = text.getWithStyle(Style.EMPTY);
        if(components.size()>0)
            components.removeFirst();
            components.addFirst(componentsBase.getFirst());

        for (Text component : components){
                textHovered.append(component);
        }

    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        gui.SetColour(colour);
        gui.colourDisplay.setOpaqueTint(colour.asInt());
        //client.setScreen(new ColourScreen(gui));
        return super.onClick(x, y, button);
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context,x,y,mouseX,mouseY);
        if(isWithinBounds(mouseX, mouseY)){
            setText(textHovered);
        }else{
            setText(textUnhovered);
        }

    }
}


