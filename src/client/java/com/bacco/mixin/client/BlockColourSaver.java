package com.bacco.mixin.client;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;

import com.bacco.IItemBlockColourSaver;

import net.minecraft.item.Item;
//Mixin, which adds one string to the Item class, and getter and setter functions. String colour stores the hexcode value of the block.
@Mixin(Item.class)
public abstract class BlockColourSaver implements IItemBlockColourSaver {
    private ArrayList<String> colour = new ArrayList<String>();

    @Override
    public String getColour(int i) {
        if(this.colour.get(i) == null){
            this.colour.add("default");
        }
        return colour.get(i);
    }

    @Override
    public void addColour(String colour) {
        this.colour.add(colour);
    }

    @Override
    public int getLength(){
        if(this.colour == null) return 0;
        return this.colour.size();
    }
}
