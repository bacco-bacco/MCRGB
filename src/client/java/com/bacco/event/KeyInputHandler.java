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
    public static final String KEY_COLOUR_INV_OPEN = "key.mcrgb.colour_inv_open";

    public static KeyBinding colourInvKey;


    public static void registerKeyInputs(MCRGBClient mcrgbClient){
        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if(colourInvKey.wasPressed()){
                if (client.currentScreen == null) {
					//client.setScreen(MCRGBClient.colourInvScreen);
                    client.setScreen(new ColourScreen(new ColourGui(client, mcrgbClient)));
				} else {
					client.setScreen(null);
				}
            }
        });
    }

    public static void register(MCRGBClient mcrgbClient){
        colourInvKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            KEY_COLOUR_INV_OPEN, 
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY_MCRGB
            ));
            registerKeyInputs(mcrgbClient);
    }
}
