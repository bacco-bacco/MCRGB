package com.bacco.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class WGradientSlider extends WSlider {
    Identifier valueSliderIdentifier = Identifier.of("mcrgb", "value_slider.png");

    public WGradientSlider(int min, int max, Axis axis) {
        super(min, max, axis);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        //super.paint(context, x, y, mouseX, mouseY);
        ScreenDrawing.texturedRect(context,x+5,y,8,128,valueSliderIdentifier,0xFFFFFFFF);

        float px = 1 / 32f;
        // thumbX/Y: thumb position in widget-space
        int thumbX, thumbY;
        // thumbXOffset: thumb texture x offset in pixels
        int thumbXOffset;
        Identifier texture = new Identifier("mcrgb","circle4.png");

        if (axis == Axis.VERTICAL) {
            thumbX = width / 2 - THUMB_SIZE / 2;
            thumbY = direction == Direction.UP
                    ? (height - THUMB_SIZE) + 1 - (int) (coordToValueRatio * (value - min))
                    : Math.round(coordToValueRatio * (value - min));
        } else {
            thumbX = direction == Direction.LEFT
                    ? (width - THUMB_SIZE) - (int) (coordToValueRatio * (value - min))
                    : Math.round(coordToValueRatio * (value - min));
            thumbY = height / 2 - THUMB_SIZE / 2;
        }
        ScreenDrawing.texturedRect(context, x + thumbX, y + thumbY+3, THUMB_SIZE, THUMB_SIZE, texture, 0xFFFFFFFF);

    }

    @Override
    protected int getThumbWidth() {
        return 1;
    }

}
