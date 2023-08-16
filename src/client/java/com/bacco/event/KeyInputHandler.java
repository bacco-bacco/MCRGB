package com.bacco.event;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import com.bacco.IItemBlockColourSaver;
import com.bacco.MCRGBClient;
import com.bacco.SpriteDetails;
import com.bacco.gui.ColourGui;
import com.bacco.gui.ColourScreen;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents.Register;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_MCRGB = "key.category.mcrgb.mcrgb";
    public static final String KEY_REFRESH_COLOURS = "key.mcrgb.refresh_colours";
    public static final String KEY_COLOUR_INV_OPEN = "key.mcrgb.colour_inv_open";
    public static final String KEY_COLOUR_SORT = "key.mcrgb.colour_sort";

    public static KeyBinding refreshKey;
    public static KeyBinding colourInvKey;
    public static KeyBinding colourSortKey;

    public static void registerKeyInputs(MCRGBClient mcrgbClient){
        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if(refreshKey.wasPressed()){
                client.player.sendMessage(Text.literal("keyprresed"));
                mcrgbClient.RefreshColours();
            }
            if(colourInvKey.wasPressed()){
                if (client.currentScreen == null) {
					//client.setScreen(MCRGBClient.colourInvScreen);
                    client.setScreen(new ColourScreen(new ColourGui(client)));
				} else {
					client.setScreen(null);
				}
            }
            if(colourSortKey.wasPressed()){
                Vector3i query = new Vector3i(120, 120, 120);
                Registries.BLOCK.forEach(block -> {   
                    try{
                        for(int j = 0; j < ((IItemBlockColourSaver) block.asItem()).getLength(); j++){
                            double distance = 0;
                            SpriteDetails sprite = ((IItemBlockColourSaver) block.asItem()).getSpriteDetails(j);
                            for (int i = 0; i < sprite.colourinfo.size(); i++){
                                Vector3i colour = sprite.colourinfo.get(i);
                                if(colour == null) return;
                                distance += query.distance(colour) * sprite.weights.get(i);
                            }
                            if(distance < ((IItemBlockColourSaver) block.asItem()).getScore() || j == 0){
                                ((IItemBlockColourSaver) block.asItem()).setScore(distance);
                            }
                        }
                                       
                    }catch(Exception e){
                        return;
                    }
                    
                });
            }
        });
    }

    public static void register(MCRGBClient mcrgbClient){
        refreshKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_REFRESH_COLOURS, 
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            KEY_CATEGORY_MCRGB
            ));
        colourInvKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_COLOUR_INV_OPEN, 
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY_MCRGB
            ));
        colourSortKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_COLOUR_SORT, 
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            KEY_CATEGORY_MCRGB
            ));

            registerKeyInputs(mcrgbClient);
    }
}
