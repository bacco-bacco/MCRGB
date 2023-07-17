package com.bacco.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.bacco.IItemBlockColourSaver;

import net.minecraft.item.Item;

@Mixin(Item.class)
public abstract class BlockColourSaver implements IItemBlockColourSaver {
    private String colour;

    @Override
    public String getColour() {
        if(this.colour == null){
            this.colour = "default";
        }
        return colour;
    }

    @Override
    public void setColour(String colour) {
        this.colour = colour;
    }
}
