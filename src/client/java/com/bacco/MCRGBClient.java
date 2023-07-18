package com.bacco;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.spi.RegisterableService;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class MCRGBClient implements ClientModInitializer {
	public static final BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson(), BlockColourStorage[].class);

	@Override
	public void onInitializeClient() {
		//when client joins (single or multi)
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			//get top sprite of dirt block default state
			var sprite = client.getBakedModelManager().getBlockModels().getModel(Blocks.DIRT.getDefaultState()).getQuads(Blocks.DIRT.getDefaultState(), Direction.UP, Random.create()).get(0).getSprite();
			//get atlas id of above
			var atlas = sprite.getAtlasId();
			//var atlas = client.getBakedModelManager().getBlockModels().getModel(Blocks.STONE.getDefaultState()).getParticleSprite().getAtlasId();
			//use atlas id to get OpenGL ID. Atlas contains ALL blocks
			int glID = client.getTextureManager().getTexture(atlas).getGlId();
			//get width and height from OpenGL by binding texture
			RenderSystem.bindTexture(glID);
			int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
			int size = width * height;
			//Make byte buffer and load full atlas into buffer.
			ByteBuffer buffer = BufferUtils.createByteBuffer(size*4);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			//convert buffer to an array of bytes
			byte[] pixels = new byte[size*4];
			buffer.get(pixels);
			
			//TODO:
			//for each block and blockstate
			//get the UV's of the blockstate - calculate the position of the blockstate in the atlas
			//loop through pixel region defined by UVs & extract RGBA values
		});


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