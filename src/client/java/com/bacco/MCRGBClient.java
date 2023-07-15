package com.bacco;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class MCRGBClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	
		ClientPickBlockApplyCallback.EVENT.register((player, result, _stack) -> {
			Text text = Text.literal("Hello World");
			player.sendMessage(text);
			return _stack;
		});

	}
}