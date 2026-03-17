package com.bacco.gui;

import com.bacco.ColourVector;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.util.Identifier;

public class WColourPreviewIcon extends WSprite {

    int colour = 0xFFFFFF;
    MCRGBBaseGui gui;
    boolean interactable = true;
    public WColourPreviewIcon(Identifier image,MCRGBBaseGui gui) {
        super(image);
        this.gui = gui;
    }

    public WColourPreviewIcon(Identifier image) {
        super(image);
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        switch (button){
            case 0:
                if (!interactable) return InputResult.PROCESSED;
                colour = gui.GetColour();
                setOpaqueTint(colour);
                break;
            case 1:
                if (!interactable) return InputResult.PROCESSED;
                colour = 0xFFFFFF;
                setOpaqueTint(colour);
                break;
            case 2:
                gui.SetColour(new ColourVector(colour));
                break;
        }
        return InputResult.PROCESSED;
    }

    public void setColour(int colour) {
        this.colour = colour;
        setOpaqueTint(colour);
    }

    public  void setInteractable(boolean interactable){
        this.interactable = interactable;
    }
}
