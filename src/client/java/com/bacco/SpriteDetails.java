package com.bacco;
import java.util.ArrayList;

import org.joml.Vector3i;


public class SpriteDetails {
    public String name;
    public ArrayList<Vector3i> colourinfo = new ArrayList<Vector3i>();
    public ArrayList<Integer> weights = new ArrayList<Integer>();
    public ArrayList<String> getStrings(){
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(name+":");
        for(int i = 0; i < colourinfo.size(); i++){
            strings.add("  " + MCRGBClient.rgbToHex(colourinfo.get(i).x,colourinfo.get(i).y,colourinfo.get(i).z)+"  "+weights.get(i)+"%");
        }
        return strings;
    }
}
