package com.bacco.gui;

import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class WColourGuiHotbar extends WPlainPanel {
    ArrayList<WColourGuiSlot> hotbarSlots = new ArrayList<WColourGuiSlot>();
    Minecraft client = Minecraft.getInstance();
    ColourGui gui;
    public WColourGuiHotbar(ColourGui gui){
        super();
        this.gui = gui;
        for (int i = 0; i < 9; i++){
            hotbarSlots.add(new WColourGuiSlot(client.player.getInventory().getItem(i),gui,i));
            this.add(hotbarSlots.get(i),i*18,0);
        }
        this.validate(gui);
        SyncHotbar();
    }

    void SyncHotbar(){
        for (int i = 0; i < 9; i++){
            hotbarSlots.get(i).stack = client.player.getInventory().getItem(i);
        }
    }

    @Override
    public void tick(){
        super.tick();
        SyncHotbar();
    }

}
