package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class WClickableLabel extends WLabel {

    ColourVector colour;
    MCRGBBaseGui gui;

    net.minecraft.client.MinecraftClient client;
    MCRGBClient mcrgbClient;

    Text textUnhovered = text;

    MutableText textHovered = (MutableText) Text.empty();
    public WClickableLabel(Text text, ColourVector colour, MCRGBBaseGui gui) {
        super(text);
        this.colour = colour;
        this.client = gui.client;
        this.mcrgbClient = gui.mcrgbClient;
        this.gui = gui;


        List<Text> components = text.getWithStyle(Style.EMPTY.withItalic(true).withUnderline(true));
        List<Text> componentsBase = text.getWithStyle(Style.EMPTY);
        if(components.size()>0)
            components.removeFirst();
            components.addFirst(componentsBase.getFirst());

        for (Text component : components){
                textHovered.append(component);
        }

    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        switch (button){
            case 0:
                gui.SetColour(colour);
                break;
            case 1:
                gui.SetColour(colour);
                break;
            case 2:
                client.keyboard.setClipboard(colour.getHex());
                SystemToast clipboardToast = new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable("toast.mcrgb.generic_toast_title"), Text.translatable("toast.mcrgb.copied_hex_to_clipboard").append(Text.literal("⬛").getWithStyle(Style.EMPTY.withColor(colour.asInt())).get(0)).append(colour.getHex()));
                MinecraftClient.getInstance().getToastManager().add(clipboardToast);
                break;
        }

        return super.onClick(x, y, button);
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context,x,y,mouseX,mouseY);
        if(isWithinBounds(mouseX, mouseY)){
            setText(textHovered);
        }else{
            setText(textUnhovered);
        }

    }
}


