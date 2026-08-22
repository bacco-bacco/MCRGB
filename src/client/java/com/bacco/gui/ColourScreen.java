package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.event.KeyInputHandler;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;

public class ColourScreen extends CottonClientScreen{

    MCRGBBaseGui description;

    public ColourScreen(GuiDescription description) {
        super(description);
        this.description = (MCRGBBaseGui) description;
    }

    public MCRGBBaseGui getGuiDescription(){
        return  description;
    }


    @Override
    public boolean isPauseScreen() {return false;}

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta); // draws your components

        // Draw the cursor stack above everything
        if (!description.cursorStack.isEmpty()) {
            context.item(description.cursorStack, mouseX - 8, mouseY - 8);
            context.itemDecorations(font, description.cursorStack, mouseX - 8, mouseY - 8);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent input){
        if(KeyInputHandler.quickSearchKey.matches(input)){
            WWidget focused = description.getFocus();
            if(focused instanceof WTextField){
                return super.keyPressed(input);
            }
            description.SetColour(new ColourVector(minecraft.keyboardHandler.getClipboard()));
        }
        return super.keyPressed(input);
    }
    
}
