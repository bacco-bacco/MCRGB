package com.bacco;

import com.google.gson.Gson;

import java.io.IOException;

import static com.bacco.MCRGBClient.readJson;
import static com.bacco.MCRGBClient.writeJson;

public final class MCRGBConfig {

    public static MCRGBConfig instance = new MCRGBConfig();
    public boolean alwaysShowToolTips = false;
    public boolean sliderConstantUpdate = true;
    public boolean readJsonFile = false;
    public String command = "give %p %i[%c] %q";

    public boolean bypassOP = false;

    public int maxTooltipLines = 15;
    public ClothConfigIntegration.ItemSpawningMode creativeGive = ClothConfigIntegration.ItemSpawningMode.CREATIVE_DRAG;



    public ClothConfigIntegration.ColourFindMode mode = ClothConfigIntegration.ColourFindMode.MCRGB;

    public static void save(){
        Gson gson = new Gson();
        String blockColoursJson = gson.toJson(instance);
        try {
            writeJson(blockColoursJson, "./config/mcrgb/", "config.json");
        } catch (IOException e) {
        }

        MCRGBClient.RefreshColours();

    }

    public static void load(){
        Gson gson = new Gson();
        instance = gson.fromJson(readJson("./config/mcrgb/config.json"), MCRGBConfig.class);
        if(instance == null){
            instance = new MCRGBConfig();
            save();
        }

    }
}
