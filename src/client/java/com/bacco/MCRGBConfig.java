package com.bacco;

import com.google.gson.Gson;

import java.io.IOException;

import static com.bacco.MCRGBClient.readJson;
import static com.bacco.MCRGBClient.writeJson;

public final class MCRGBConfig {

    public static MCRGBConfig instance = new MCRGBConfig();
    public boolean alwaysShowToolTips = false;

    public static void save(){
        Gson gson = new Gson();
        String blockColoursJson = gson.toJson(instance);
        try {
            writeJson(blockColoursJson, "./config/mcrgb/", "config.json");
        } catch (IOException e) {
        }
    }

    public static void load(){
        Gson gson = new Gson();
        instance = gson.fromJson(readJson("./config/mcrgb/config.json"), MCRGBConfig.class);
    }
}
