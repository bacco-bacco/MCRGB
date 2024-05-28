package com.bacco.gui;

import com.bacco.ColourVector;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.util.Identifier;

public class WColourPreviewIcon extends WSprite {

    int colour = 0xFFFFFF;
    ColourGui gui;
    public WColourPreviewIcon(Identifier image,ColourGui gui) {
        super(image);
        this.gui = gui;
    }

    @Override
    public InputResult onClick(int x, int y, int button) {

        switch (button){
            case 0:
                colour = gui.GetColour();
                setOpaqueTint(colour);
                break;
            case 1:
                colour = 0xFFFFFF;
                setOpaqueTint(colour);
                break;
            case 2:
                gui.SetColour(new ColourVector(colour));
                break;
        }
        return InputResult.PROCESSED;
    }

}
