package com.bacco.event;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import com.bacco.gui.ColourGui;
import com.bacco.gui.ColourScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_MCRGB = "key.category.mcrgb.mcrgb";
    public static final String KEY_COLOUR_INV_OPEN = "key.mcrgb.colour_inv_open";

    public static final String KEY_QUICK_SEARCH_FROM_CLIPBOARD = "key.mcrgb.quick_search_from_clipboard";

    public static KeyBinding colourInvKey;

    public static KeyBinding quickSearchKey;

    //public static final KeyBinding.Category mcrgbKeyCategory = KeyBinding.Category.create(Identifier.of("mcrgb",KEY_CATEGORY_MCRGB));

    public static void registerKeyInputs(MCRGBClient mcrgbClient){
        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if(colourInvKey.wasPressed()){
                if (client.currentScreen == null) {
                    client.setScreen(new ColourScreen(new ColourGui(client, mcrgbClient, new ColourVector(0xFFFFFFFF))));
				} else {
					client.setScreen(null);
				}
            }

            if(quickSearchKey.wasPressed()){
                if (client.currentScreen == null) {
                    client.setScreen(new ColourScreen(new ColourGui(client, mcrgbClient, new ColourVector(client.keyboard.getClipboard()))));
                }else{
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

        quickSearchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_QUICK_SEARCH_FROM_CLIPBOARD,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY_MCRGB
        ));
            registerKeyInputs(mcrgbClient);
    }
}
