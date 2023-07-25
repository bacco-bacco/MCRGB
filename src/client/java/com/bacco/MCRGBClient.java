package com.bacco;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.spi.RegisterableService;
import javax.swing.GroupLayout.Group;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.minecraft.client.texture.Sprite;
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
	public static final Logger LOGGER = LoggerFactory.getLogger("mcrgb");
	int totalBlocks = 0;			
	int fails = 0;
	int successes = 0;
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		//when client joins (single or multi)
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			//get top sprite of stone block default state
			var defSprite = client.getBakedModelManager().getBlockModels().getModel(Blocks.STONE.getDefaultState()).getQuads(Blocks.STONE.getDefaultState(), Direction.UP, Random.create()).get(0).getSprite();
			//get id of the atlas containing above
			var atlas = defSprite.getAtlasId();
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

			//loop through every block in the game
			Registries.BLOCK.forEach(block -> {
				totalBlocks +=1;
				Sprite sprite;
				//try to get the default top texture sprite. if fails, report error and skip this block
				try{
				sprite = client.getBakedModelManager().getBlockModels().getModel(block.getDefaultState()).getQuads(block.getDefaultState(), Direction.DOWN, Random.create()).get(0).getSprite();
				successes +=1;
				}catch(Exception e){	
					Text text = Text.literal("Error wit da: "+block.asItem().getName().getString());
					client.player.sendMessage(text);	
					fails +=1;						
					return;
				}
				//get coords of sprite in atlas
				int spriteX = sprite.getX();
				int spriteY = sprite.getY();
				int spriteW = sprite.getContents().getWidth();
				int spriteH = sprite.getContents().getHeight();
				//convert coords to byte position
				int firstPixel = (spriteY*width + spriteX)*4;
				ArrayList<Integer> red = new ArrayList<Integer>();
				ArrayList<Integer> grn = new ArrayList<Integer>();
				ArrayList<Integer> blu = new ArrayList<Integer>();
				//for each horizontal row in the sprite
				for (int row = 0; row < spriteH; row++){
					int firstInRow = firstPixel + row*width*4;
					//loop from first pixel in row to the sprite width.
					//Note: Looping in increments of 4, because each pixel is 4 bytes. (R,G,B and A)
					for (int pos = firstInRow; pos < firstInRow + 4*spriteW; pos+=4){
						//retrieve bytes for RGBA values
						//"& 0xFF" does logical and with 11111111. this extracts the last 8 bits, converting to unsigned int
						int a = pixels[pos+3];
						a = a & 0xFF;
						//if the pixel is fully transparent, skip it and don't count it
						if(a > 0) {
							red.add(pixels[pos] & 0xFF);
							grn.add(pixels[pos+1] & 0xFF);
							blu.add(pixels[pos+2] & 0xFF);
						}
					}
				}
				//ColourGroup[] colourGroups = CalculateDominantColours(red, grn, blu);
				ArrayList<ColourGroup> colourGroups = GroupColours(red, grn, blu);
				
				for(int i = 0; i < colourGroups.size(); i++){
					ColourGroup cg = colourGroups.get(i);
					((IItemBlockColourSaver) block.asItem()).addColour(cg.meanHex + " " + cg.weight + "%");
				}
				
				//Text text = Text.literal(block.asItem().getTranslationKey()+" ("+sprite.getContents().getId() + "): " + hexColour);
				//client.player.sendMessage(text);
			});
			Text text = Text.literal("Blocks Analysed: "+Integer.toString(totalBlocks)+" Fails: "+Integer.toString(fails)+" Successes: "+Integer.toString(successes));
			client.player.sendMessage(text);
		});

		//Add all blocks to a new array list. 
		ArrayList<BlockColourStorage> blockColourList = new ArrayList<BlockColourStorage>();
		Registries.BLOCK.forEach(block -> {

		BlockColourStorage storage = new BlockColourStorage();
		storage.block = block.getTranslationKey();
		for(int i = 0; i < ((IItemBlockColourSaver) block.asItem()).getLength(); i++){
			storage.colour = ((IItemBlockColourSaver) block.asItem()).getColour(i);
		}		
		blockColourList.add(storage);

		});

		//Write arraylist to json
		Gson gson = new Gson();
		String blockColoursJson = gson.toJson(blockColourList);
		try {
			writeJson(blockColoursJson);
		} catch (IOException e) {
		}
/*
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
*/
		//Override item tooltips to display the colour.
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			for(int i = 0; i < item.getLength(); i++){
				String colour = item.getColour(i);
				var text = Text.literal(colour);
				var message = text.formatted(Formatting.AQUA);
				lines.add(message);
			}
		});

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

	public static String rgbToHex(int r, int g, int b){
		String hexR = r < 0x10 ? "0" + Integer.toHexString(r) : Integer.toHexString(r);
		String hexG = g < 0x10 ? "0" + Integer.toHexString(g) : Integer.toHexString(g);
		String hexB = b < 0x10 ? "0" + Integer.toHexString(b) : Integer.toHexString(b);
		return ("#" + hexR + hexG + hexB).toUpperCase();
	}

	public static ArrayList<ColourGroup> GroupColours(ArrayList<Integer> redlist, ArrayList<Integer> grnlist, ArrayList<Integer> blulist){
		ArrayList<ColourGroup> groups = new ArrayList<>();

		for (int i = 0; i < redlist.size(); i++){
			Vector3i iPix = new Vector3i(redlist.get(i),grnlist.get(i),blulist.get(i));
			//x = r; y = g; z = b

			boolean iInGroup = false;
			for (ColourGroup group : groups){
				if (group.pixels.contains(iPix)){
					iInGroup = true;
					break;
				}
			}

			if(!iInGroup){
				ColourGroup newGroup = new ColourGroup();
				newGroup.pixels.add(iPix);


				for (int j = i + 1; j < redlist.size(); j++){
					Vector3i jPix = new Vector3i(redlist.get(j),grnlist.get(j),blulist.get(j));
					if(jPix.distance(iPix) < 100){
						boolean jInGroup = false;
						for (ColourGroup group : groups){
							if (group.pixels.contains(jPix)){
								jInGroup = true;
								break;
							}
						}

						if(!jInGroup){
							newGroup.pixels.add(jPix);
						}
					}

				}
				groups.add(newGroup);
			}
		}

		for (ColourGroup group : groups){
			Vector3i sum = new Vector3i(0, 0, 0);
			int counter = 0;
			for (Vector3i colour : group.pixels){
				sum.add(colour);
				counter ++;
			}
			Vector3i avg = sum.div(counter);
			group.meanHex = rgbToHex(avg.x, avg.y, avg.z);
			group.weight = (int)((float)counter/(float)redlist.size() * 100);
		}

		return groups;
	}
/*
	public static ColourGroup[] CalculateDominantColours(ArrayList<Integer> red, ArrayList<Integer> grn, ArrayList<Integer> blu){
		ColourGroup[] colourGroups = new ColourGroup[16];
		for (int i = 0; i < red.size(); i++){
			for (int j = 0; j < red.size(); j++){
				double rds = Math.pow((red.get(i)-red.get(j)),2);
				double gds = Math.pow((grn.get(i)-grn.get(j)),2);
				double bds = Math.pow((blu.get(i)-blu.get(j)),2);
				double distance = Math.sqrt(rds + gds + bds);				
				if(distance < 100){
					for (int n = 0; n < colourGroups.length; n++){
						if(colourGroups[n] == null){
							colourGroups[n] = new ColourGroup();
							colourGroups[n].red.add(red.get(i));
							colourGroups[n].green.add(grn.get(i));
							colourGroups[n].blue.add(blu.get(i));

							colourGroups[n].red.add(red.get(j));
							colourGroups[n].green.add(grn.get(j));
							colourGroups[n].blue.add(blu.get(j));
							break;
						}else if(colourGroups[n].red.contains(red.get(i))){
							if(colourGroups[n].red.contains(red.get(j))) break;
							colourGroups[n].red.add(red.get(j));
							colourGroups[n].green.add(grn.get(j));
							colourGroups[n].blue.add(blu.get(j));
							break;
						}
					}
				}
			}
		}
		for (int n = 0; n < colourGroups.length; n++){
			colourGroups[n].weight = colourGroups[n].red.size()/red.size();
		}
		return colourGroups;
	}*/

	public static int arrayListAverage(ArrayList<Integer> al){
		int total = 0;
		for (int i = 0; i < al.size(); i++){
			total += al.get(i);
		}
		return total/al.size();
	}
}