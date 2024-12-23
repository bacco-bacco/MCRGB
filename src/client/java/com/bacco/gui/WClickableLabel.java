package com.bacco.gui;

import com.bacco.ColourVector;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.text.Text;

public class WClickableLabel extends WLabel {

    ColourVector colour;
    ColourGui gui;
    public WClickableLabel(Text text, ColourVector colour) {
        super(text);
        this.colour = colour;
        //this.gui = gui;
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        //gui.SetColour(colour);
        return super.onClick(x, y, button);
    }
}
