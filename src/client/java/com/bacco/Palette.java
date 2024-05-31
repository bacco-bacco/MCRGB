package com.bacco;

import java.util.ArrayList;

public class Palette {
    ArrayList<ColourVector> colourList = new ArrayList<>();

    public void Palette(){
    }

    public void setColour(int i, ColourVector colour){
        colourList.set(i,colour);
    }

    public ColourVector getColour(int i){
        return colourList.get(i);
    }

    public void addColour(ColourVector colour){
        colourList.add(colour);
    }

}
