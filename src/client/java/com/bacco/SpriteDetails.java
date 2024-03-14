package com.bacco;

import java.util.ArrayList;


public class SpriteDetails {
    public String name;
    public ArrayList<ColourVector> colourinfo = new ArrayList<ColourVector>();
    public ArrayList<Integer> weights = new ArrayList<Integer>();

    public ArrayList<String> getStrings(){
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(name+":");
        for(int i = 0; i < colourinfo.size(); i++){
            strings.add(colourinfo.get(i).getHex()+"  "+weights.get(i)+"%");
        }
        return strings;
    }
    public ArrayList<Integer> getTextColours(){
        ArrayList<Integer> colours = new ArrayList<Integer>();
        colours.add(0xffffff);
        for(int i = 0; i < colourinfo.size(); i++){
            String hex = colourinfo.get(i).getHex();
            hex = hex.replace("#","");
            int hexint = Integer.parseInt(hex,16);
            colours.add(hexint);
        }
        return colours;
    }
}
