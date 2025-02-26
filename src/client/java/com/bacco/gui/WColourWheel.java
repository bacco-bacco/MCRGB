package com.bacco.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class WColourWheel extends WPickableTexture{

    int cursorX = (width)/2;
    int cursorY = (height)/2;
    Boolean beenClicked = false;

    public WColourWheel(Identifier image, float u1, float v1, float u2, float v2, MinecraftClient client, MCRGBBaseGui gui) {
        super(image, u1, v1, u2, v2, client, gui);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
        Identifier texture = new Identifier("mcrgb","circle4.png");
        if(!beenClicked){
            cursorX = (width)/2;
            cursorY = (height)/2;
        }

        ScreenDrawing.texturedRect(context, cursorX+x-4, cursorY+y-4, 8, 8, texture, 0xFFFFFFFF);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public InputResult onClick(int containerX, int containerY, int button) {
        beenClicked = true;
        cursorX = containerX;
        cursorY = containerY;
        return super.onClick(containerX,containerY,button);
    }
    @Environment(EnvType.CLIENT)
    @Override
    public InputResult onMouseDrag(int containerX, int containerY, int mouseButton, double deltaX, double deltaY) {
        beenClicked = true;
        cursorX = containerX;
        cursorY = containerY;
        return super.onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);
    }

    public void pickAtCursor(){
        pickColour(cursorX,cursorY);
    }

    /*@Override
    public WSprite setOpaqueTint(int tint){
        //pickColour(cursorX,cursorY);
        return super.setOpaqueTint(tint);
    }*/
}
