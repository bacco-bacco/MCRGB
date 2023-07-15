package com.bacco;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

public class MCRGBClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	
		ClientPickBlockApplyCallback.EVENT.register((player, result, _stack) -> {
			Text text = Text.literal("Hello World");
			player.sendMessage(text);
			return _stack;
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			//var text = Text.literal("Hello World");
			int count = stack.getMaxCount();
			var text = Text.literal(String.valueOf(count));
			var message = text.formatted(Formatting.AQUA);
			lines.add(message);
		});

	}
}