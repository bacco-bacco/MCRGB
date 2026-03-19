package com.bacco.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class WSmallButton extends WButton {

    //INCREDIBLY MESSY CODE THAT I AM NOT PROUD OF, BUT ITS JUST FOR THE 1.20.1 BACKPORT SO IDC.
    //OLD VERSION OF LIBGUI DIDN'T SUPPORT CUSTOM HEIGHT BUTTONS SO WE TYING THIS UP WITH SPAGHETTI
    private static final int BUTTON_HEIGHT = 5;

    public WSmallButton(@Nullable Icon icon) {
        this.setIcon(icon);
    }
    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        boolean hovered = (mouseX>=0 && mouseY>=0 && mouseX<getWidth() && mouseY<getHeight());
        int state = 1; //1=regular. 2=hovered. 0=disabled.
        if (!isEnabled()) {
            state = 0;
        } else if (hovered || isFocused()) {
            state = 2;
        }

        float px = 1/256f;
        float buttonLeft = 0 * px;
        float buttonTop = (46 + (state*20)) * px;
        int halfWidth = getWidth()/2;
        if (halfWidth>198) halfWidth=198;
        float buttonWidth = halfWidth*px;
        float buttonHeight = 5*px;

        float buttonEndLeft = (200-(getWidth()/2)) * px;

        Identifier texture = ClickableWidget.WIDGETS_TEXTURE;
        ScreenDrawing.texturedRect(context, x, y, getWidth()/2, 10, texture, buttonLeft, buttonTop, buttonLeft+buttonWidth, buttonTop+2*buttonHeight, 0xFFFFFFFF);
        ScreenDrawing.texturedRect(context, x+(getWidth()/2), y, getWidth()/2, 10, texture, buttonEndLeft, buttonTop, 200*px, buttonTop+2*buttonHeight, 0xFFFFFFFF);
        ScreenDrawing.coloredRect(context,x,y+9,10,1,hovered? 0xFFFFFFFF:0xFF000000);

        if (getIcon() != null) {
            getIcon().paint(context, x+2, 2+y+(BUTTON_HEIGHT-iconSize)/2, iconSize);
        }

        if (getLabel()!=null) {
            int color = 0xE0E0E0;
            if (!isEnabled()) {
                color = 0xA0A0A0;
            } /*else if (hovered) {
				color = 0xFFFFA0;
			}*/

            int xOffset = (getIcon() != null && alignment == HorizontalAlignment.LEFT) ? 2+iconSize+2 : 0;
            ScreenDrawing.drawStringWithShadow(context, getLabel().asOrderedText(), alignment, x + xOffset, y + ((10 - 8) / 2), width, color); //LibGuiClient.config.darkMode ? darkmodeColor : color);
        }
    }

    @Override
    public void setSize(int x, int y) {
        this.width = x;
        this.height = y;
    }
}
