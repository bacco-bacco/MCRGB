package com.bacco.event;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import com.bacco.gui.ColourGui;
import com.bacco.gui.ColourScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_MCRGB = "key.category.mcrgb.mcrgb";
    public static final String KEY_COLOUR_INV_OPEN = "key.mcrgb.colour_inv_open";

    public static final String KEY_QUICK_SEARCH_FROM_CLIPBOARD = "key.mcrgb.quick_search_from_clipboard";

    public static KeyMapping colourInvKey;

    public static KeyMapping quickSearchKey;

    public static final KeyMapping.Category mcrgbKeyCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("mcrgb",KEY_CATEGORY_MCRGB));

    public static void registerKeyInputs(MCRGBClient mcrgbClient){
        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if(colourInvKey.consumeClick()){
                if (client.gui.screen() == null) {
                    client.setScreenAndShow(new ColourScreen(new ColourGui(client, mcrgbClient, new ColourVector(0xFFFFFFFF))));
				} else {
					client.setScreenAndShow(null);
				}
            }

            if(quickSearchKey.consumeClick()){
                if (client.gui.screen() == null) {
                    client.setScreenAndShow(new ColourScreen(new ColourGui(client, mcrgbClient, new ColourVector(client.keyboardHandler.getClipboard()))));
                }else{
                    client.setScreenAndShow(null);
                }
            }
        });
    }

    public static void register(MCRGBClient mcrgbClient){
        colourInvKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_COLOUR_INV_OPEN,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                mcrgbKeyCategory
        ));

        quickSearchKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_QUICK_SEARCH_FROM_CLIPBOARD,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                mcrgbKeyCategory
        ));
            registerKeyInputs(mcrgbClient);
    }
}
