package com.bacco;

import java.util.ArrayList;
import java.util.Arrays;

import static com.bacco.MCRGBClient.mean;
import static com.bacco.MCRGBClient.median;


public class ColourGroup {
    public ArrayList<ColourVector> pixels = new ArrayList<ColourVector>();
    public int weight;
    public String repHex;
    public ColourVector repColour;

    public ColourVector CalculateMedian(){
        int size = pixels.size();
        int[] reds = new int[size];
        int[] greens = new int[size];
        int[] blues = new int[size];
        for (int i = 0; i < size; i++){
            ColourVector pixel = pixels.get(i);
            reds[i] = pixel.r;
            greens[i] = pixel.g;
            blues[i] = pixel.b;
        }

        Arrays.sort(reds);
        Arrays.sort(greens);
        Arrays.sort(blues);

        ColourVector result = new ColourVector(median(reds), median(greens), median(blues));
        return result;
    }

    public ColourVector CalculateMean(){
        int size = pixels.size();
        int[] reds = new int[size];
        int[] greens = new int[size];
        int[] blues = new int[size];
        for (int i = 0; i < size; i++){
            ColourVector pixel = pixels.get(i);
            reds[i] = pixel.r;
            greens[i] = pixel.g;
            blues[i] = pixel.b;
        }

        ColourVector result = new ColourVector(mean(reds), mean(greens), mean(blues));
        return result;
    }
}
