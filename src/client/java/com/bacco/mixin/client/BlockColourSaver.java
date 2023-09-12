package com.bacco.mixin.client;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;

import com.bacco.IItemBlockColourSaver;
import com.bacco.SpriteDetails;

import net.minecraft.item.Item;
//Mixin, which adds one string to the Item class, and getter and setter functions. String colour stores the hexcode value of the block.
@Mixin(Item.class)
public abstract class BlockColourSaver implements IItemBlockColourSaver, Comparable<BlockColourSaver> {
    private ArrayList<SpriteDetails> spriteDetails = new ArrayList<SpriteDetails>();
    private double score = 0;

    @Override
    public SpriteDetails getSpriteDetails(int i) {
        if(this.spriteDetails.get(i) == null){
            this.spriteDetails.add(new SpriteDetails());
        }
        return spriteDetails.get(i);
    }

    @Override
    public void addSpriteDetails(SpriteDetails spriteDetails) {
        this.spriteDetails.add(spriteDetails);
    }

    @Override
    public void clearSpriteDetails(){
        this.spriteDetails.clear();
    }

    @Override
    public int getLength(){
        if(this.spriteDetails == null) return 0;
        return this.spriteDetails.size();
    }

    @Override
    public double getScore(){
        return this.score;
    }

    @Override
    public void setScore(double score){
        this.score = score;
    }
}
