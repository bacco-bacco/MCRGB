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
        Identifier texture = Identifier.of("mcrgb","circle4.png");
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
        InputResult ret = super.onClick(containerX,containerY,button);
        if(isTransparent) return ret;
        if(!(containerX < 0 || containerY < 0 || containerX >= width || containerY >= height)) {
            cursorX = containerX;
            cursorY = containerY;
        }
        return ret;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public InputResult onMouseDrag(int containerX, int containerY, int mouseButton, double deltaX, double deltaY) {
        beenClicked = true;
        InputResult ret = super.onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);
        System.out.println(isTransparent);
        if(isTransparent) return ret;
        if(!(containerX < 0 || containerY < 0 || containerX >= width || containerY >= height)) {
            cursorX = containerX;
            cursorY = containerY;
        }

        return ret;
    }

    public void pickAtCursor(){
        isTransparent = pickColour(cursorX,cursorY);
    }

    /*@Override
    public WSprite setOpaqueTint(int tint){
        //pickColour(cursorX,cursorY);
        return super.setOpaqueTint(tint);
    }*/
}
