package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;

public class WClickableLabel extends WLabel {

    ColourVector colour;
    MCRGBBaseGui gui;

    net.minecraft.client.Minecraft client;
    MCRGBClient mcrgbClient;

    Component textUnhovered = text;

    MutableComponent textHovered = (MutableComponent) Component.empty();
    public WClickableLabel(Component text, ColourVector colour, MCRGBBaseGui gui) {
        super(text);
        this.colour = colour;
        this.client = gui.client;
        this.mcrgbClient = gui.mcrgbClient;
        this.gui = gui;


        List<Component> components = text.toFlatList(Style.EMPTY.withItalic(true).withUnderlined(true));
        List<Component> componentsBase = text.toFlatList(Style.EMPTY);
        if(components.size()>0)
            components.removeFirst();
            components.addFirst(componentsBase.getFirst());

        for (Component component : components){
                textHovered.append(component);
        }

    }

    @Override
    public InputResult onClick(MouseButtonEvent click, boolean doubled) {
        switch (click.button()){
            case 0:
                gui.SetColour(colour);
                break;
            case 1:
                gui.SetColour(colour);
                break;
            case 2:
                client.keyboardHandler.setClipboard(colour.getHex());
                SystemToast clipboardToast = new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable("toast.mcrgb.generic_toast_title"), Component.translatable("toast.mcrgb.copied_hex_to_clipboard").append(Component.literal("⬛").toFlatList(Style.EMPTY.withColor(colour.asInt())).get(0)).append(colour.getHex()));
                Minecraft.getInstance().gui.toastManager().addToast(clipboardToast);
                break;
        }

        return super.onClick(click,doubled);
    }


    @Environment(EnvType.CLIENT)
    @Override
    public void paint(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY) {
        super.paint(context,x,y,mouseX,mouseY);
        if(isWithinBounds(mouseX, mouseY)){
            setText(textHovered);
        }else{
            setText(textUnhovered);
        }

    }
}


