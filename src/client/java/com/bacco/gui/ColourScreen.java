package com.bacco.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.gui.DrawContext;

public class ColourScreen extends CottonClientScreen{

    MCRGBBaseGui description;

    public ColourScreen(GuiDescription description) {
        super(description);
        this.description = (MCRGBBaseGui) description;
    }




    @Override
    public boolean shouldPause() {return false;}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); // draws your components

        // Draw the cursor stack above everything
        if (!description.cursorStack.isEmpty()) {
            context.drawItem(description.cursorStack, mouseX - 8, mouseY - 8);
        }
    }
    
}
