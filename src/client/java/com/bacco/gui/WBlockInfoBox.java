package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.IItemBlockColourSaver;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class WBlockInfoBox extends WBox {

    /**
     * Constructs a box.
     *
     * @param axis the box axis
     * @throws NullPointerException if the axis is null
     */

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        setBackgroundPainter(BackgroundPainter.VANILLA);
        super.paint(context, x, y, mouseX, mouseY);
        //context.getMatrices().translate(0,0,-1000f);
    }
    public WBlockInfoBox(Axis axis, IItemBlockColourSaver item, MCRGBBaseGui gui) {
        super(axis);
        setInsets(Insets.ROOT_PANEL);
        int lineCount = 0;
        for(int i = 0; i < item.getLength(); i++){
            ArrayList<String> strings = item.getSpriteDetails(i).getStrings();
            ArrayList<Integer> colours = item.getSpriteDetails(i).getTextColours();
            if(strings.size() > 0){
                for(int j = 0; j < strings.size(); j++){
                    var text = Text.literal(strings.get(j));//.getWithStyle(Style.EMPTY.withColor(0x707070)).get(0);//.withColor(0x707070);
                    MutableText text2 = (MutableText) Text.literal("⬛").getWithStyle(Style.EMPTY.withColor(colours.get(j))).get(0);
                    if(j > 0){
                        text2.append(text.getWithStyle(Style.EMPTY.withColor(0x707070)).get(0));
                    }else{
                        text2 = (MutableText) text.getWithStyle(Style.EMPTY.withColor(0x444444)).get(0);//.withColor(0x444444);
                    }
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    int width = textRenderer.getWidth(text2);
                    WClickableLabel newLabel = new WClickableLabel(text2,new ColourVector(colours.get(j)), gui);
                    newLabel.hoveredProperty();
                    add(newLabel,width,1);
                    lineCount++;
                }
            }
        }

        setSize(10,this.getWidth());
    }
}