package com.bacco;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.spi.RegisterableService;

import com.google.gson.Gson;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.block.Block;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

public class MCRGBClient implements ClientModInitializer {
	public static final BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson(), BlockColourStorage[].class);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	
		ClientPickBlockApplyCallback.EVENT.register((player, result, _stack) -> {
			/*Text text = Text.literal("Hello World");
			player.sendMessage(text);

			Registries.BLOCK.forEach(block -> {
				var state = block.getDefaultState();
				Text text2 = Text.literal(state.toString());
				player.sendMessage(text2);

			});*/
			BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson(), BlockColourStorage[].class);
				Registries.ITEM.forEach(item -> {
					for(BlockColourStorage storage : loadedBlockColourArray){
						//Text text = Text.literal(item.getTranslationKey());
						//player.sendMessage(text);
						if(storage.block.equals(item.getTranslationKey())){
							Text text = Text.literal("Match");
							player.sendMessage(text);
							((IItemBlockColourSaver) item).setColour(storage.colour);
							//Text text2 = Text.literal(storage.colour);
							//player.sendMessage(text);
							break;
						};
					}
					
				});
			return _stack;
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			//var text = Text.literal("Hello World");
			//int count = stack.getMaxCount();
			IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			String colour = item.getColour();
			//var text = Text.literal(String.valueOf(count));
			var text = Text.literal(colour);
			var message = text.formatted(Formatting.AQUA);
			lines.add(message);
		});

		
		ArrayList<BlockColourStorage> blockColourList = new ArrayList<BlockColourStorage>();
		Registries.BLOCK.forEach(block -> {

		BlockColourStorage storage = new BlockColourStorage();
		storage.block = block.getTranslationKey();
		storage.colour = "defaulto";
		blockColourList.add(storage);

		});

		/*blockColourList.forEach(block -> {
			var id = ((Item) block).getName();
			var colour = block.getColour();
			LOGGER.info(id.toString());
			LOGGER.info(colour);
		});*/

		Gson gson = new Gson();
		String blockColoursJson = gson.toJson(blockColourList);
		try {
			writeJson(blockColoursJson);
		} catch (IOException e) {
		}

	}

	public static void writeJson(String str)
        throws IOException
    {
        try {
  
            // attach a file to FileWriter
            FileWriter fw
                = new FileWriter("D:/data/file.json");
  
            // read each character from string and write
            // into FileWriter
            for (int i = 0; i < str.length(); i++)
                fw.write(str.charAt(i));
  
  
            // close the file
            fw.close();
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }

	public static String readJson()
    {
        try {
            // FileReader Class used
            FileReader fileReader
                = new FileReader("D:/data/custom_file.json");
 
            int i;
			String str = "";
            // Using read method
            while ((i = fileReader.read()) != -1) {
				str += (char)i;
            }
 
            // Close method called
            fileReader.close();
			return str;
        }
        catch (Exception e) {
			return "";
        }
    }
}