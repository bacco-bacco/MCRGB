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

		//Read the json file for block colours. Load into an array.
		BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson(), BlockColourStorage[].class);
		//For each item in the game, loop through the array to search for an entry
		Registries.ITEM.forEach(item -> {
			for(BlockColourStorage storage : loadedBlockColourArray){
				if(storage.block.equals(item.getTranslationKey())){
					//Set the colour of the block's IItemBlockColourSaver to the value from the json
					((IItemBlockColourSaver) item).setColour(storage.colour);
					break;
				};
			}
			
		});

		//Override item tooltips to display the colour.
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			String colour = item.getColour();
			var text = Text.literal(colour);
			var message = text.formatted(Formatting.AQUA);
			lines.add(message);
		});

		//Add all blocks to a new array list. 
		ArrayList<BlockColourStorage> blockColourList = new ArrayList<BlockColourStorage>();
		Registries.BLOCK.forEach(block -> {

		BlockColourStorage storage = new BlockColourStorage();
		storage.block = block.getTranslationKey();
		storage.colour = "defaulto";	//replace this once functionality for calculating block colours exists.
		blockColourList.add(storage);

		});

		//Write arraylist to json
		Gson gson = new Gson();
		String blockColoursJson = gson.toJson(blockColourList);
		try {
			writeJson(blockColoursJson);
		} catch (IOException e) {
		}

	}

	//Read and write functions, shamelessly stolen from a Java tutorial.
	public static void writeJson(String str)
        throws IOException
    {
        try {
  
            // attach a file to FileWriter
            FileWriter fw
                = new FileWriter("./mcrgb_colours/file.json");
  
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
                = new FileReader("./mcrgb_colours/custom_file.json");
 
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