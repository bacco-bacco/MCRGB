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
            strings.add(MCRGBClient.rgbToHex(colourinfo.get(i).x,colourinfo.get(i).y,colourinfo.get(i).z)+"  "+weights.get(i)+"%");
        }
        return strings;
    }
    public ArrayList<Integer> getTextColours(){
        ArrayList<Integer> colours = new ArrayList<Integer>();
        colours.add(0xffffff);
        for(int i = 0; i < colourinfo.size(); i++){
            String hex = MCRGBClient.rgbToHex(colourinfo.get(i).x,colourinfo.get(i).y,colourinfo.get(i).z);
            hex = hex.replace("#","");
            int hexint = Integer.parseInt(hex,16);
            colours.add(hexint);
        }
        return colours;
    }
}
