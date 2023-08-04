package com.bacco;

import java.util.ArrayList;

import com.ibm.icu.impl.UResource.Array;

import net.minecraft.block.Block;
//A simple class with no functions. Holds the block name and colour for (de)serialisation to json.
import net.minecraft.client.texture.Sprite;
public class BlockColourStorage {
    public String block;
    //public ArrayList<String> colourinfo = new ArrayList<String>();
    public ArrayList<SpriteDetails> spriteDetails = new ArrayList<SpriteDetails>();
}
