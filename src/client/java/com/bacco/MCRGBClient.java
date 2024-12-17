package com.bacco;

import com.bacco.event.KeyInputHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class MCRGBClient implements ClientModInitializer {
	public static final BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson("./mcrgb_colours/file.json"), BlockColourStorage[].class);
	public static final Logger LOGGER = LoggerFactory.getLogger("mcrgb");
	public static final boolean readMode = false;
	public net.minecraft.client.MinecraftClient client;
	int totalBlocks = 0;			
	int fails = 0;
	int successes = 0;
	boolean scanned = false;
	//public static ColourInventoryScreen colourInvScreen;

	@Override
	public void onInitializeClient() {
		KeyInputHandler.register(this);
		MCRGBConfig.load();
		ClientPlayConnectionEvents.JOIN.register((handler, sender, _client) -> {
			client = _client;
			//colourInvScreen = new ColourInventoryScreen(client);
			if (scanned) return;
			//Read from JSON
			try{
			BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson("./mcrgb_colours/file.json"), BlockColourStorage[].class);
			Registries.BLOCK.forEach(block -> {
				for(BlockColourStorage storage : loadedBlockColourArray){
					if(storage.block.equals(block.asItem().getTranslationKey())){
						storage.spriteDetails.forEach(details -> {	
							((IItemBlockColourSaver) block.asItem()).addSpriteDetails(details);
						});
						break;
					};
				}
				
			});
			scanned = true;
			}catch(Exception e){
				RefreshColours();
			}
		});

		//Override item tooltips to display the colour.
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if(!MCRGBConfig.instance.alwaysShowToolTips) return;
			IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			for(int i = 0; i < item.getLength(); i++){
				ArrayList<String> strings = item.getSpriteDetails(i).getStrings();
				ArrayList<Integer> colours = item.getSpriteDetails(i).getTextColours();
				if(strings.size() > 0){
					if(Screen.hasShiftDown()){
						for(int j = 0; j < strings.size(); j++){
							var text = Text.literal(strings.get(j)).formatted(Formatting.GRAY);
							MutableText text2 = (MutableText) Text.literal("⬛").getWithStyle(Style.EMPTY.withColor(colours.get(j))).get(0);
							if(j > 0){
								text2.append(text);
							}else{
								text2 = text.formatted(Formatting.DARK_GRAY);
							}
							
							lines.add(text2);
						}
					}else{
					var text = Text.translatable("tooltip.mcrgb.shift_prompt");
					var message = text.formatted(Formatting.GRAY);
					lines.add(message);
					break;
					}
				}
			}
		});

	}

	public static void writeJson(String str, String path, String fileName)
        throws IOException
    {
        try {
			File dir = new File(path);
			File file = new File(dir, fileName);
			if(!dir.exists()){
				dir.mkdir();
			}
			if(!file.exists()){
				file.createNewFile();
			}
            FileWriter fw = new FileWriter(file);
  
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

	public static String readJson(String path)
    {
        try {
            // FileReader Class used
            FileReader fileReader
                = new FileReader(path);
 
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

	//Calculate the dominant colours in a list of colours
	public static Set<ColourGroup> GroupColours(ArrayList<ColourVector> rgblist){
		Set<ColourGroup> groups = new HashSet<ColourGroup>();
		
		//Loop through every pixel
		for (int i = 0; i < rgblist.size(); i++){
			ColourVector iPix = new ColourVector(rgblist.get(i).r,rgblist.get(i).g,rgblist.get(i).b);

			//check if already in a group
			boolean iInGroup = false;
			for (ColourGroup group : groups){
				if (group.pixels.contains(iPix)){
					iInGroup = true;
					break;
				}
			}

			//if i is not in a group, create a new one and add i to it...
			if(!iInGroup){
				ColourGroup newGroup = new ColourGroup();
				newGroup.pixels.add(iPix);

				//loop through all the pixels after i, and compare them to i
				for (int j = i + 1; j < rgblist.size(); j++){
					//if the distance is less than 100, add j to the group (if it is not already in a group)
					ColourVector jPix = new ColourVector(rgblist.get(j).r,rgblist.get(j).g,rgblist.get(j).b);
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
				//finally, add the new group to the list of groups
				groups.add(newGroup);
			}
		}
		//calculate the average rgb value of each group, convert to hex and calculate weight
		for (ColourGroup group : groups){
			ColourVector sum = new ColourVector(0, 0, 0);
			int counter = 0;
			for (ColourVector colour : group.pixels){
				sum.add(colour);
				counter ++;
			}
			if(counter == 0){return null;}
			ColourVector avg = sum.div(counter);
			group.meanColour = new ColourVector(avg.r, avg.g, avg.b);
			group.meanHex = group.meanColour.getHex();
			group.weight = (int)((float)counter/(float)rgblist.size() * 100);
		}

		return groups;
	}

	public void RefreshColours(){
		if (client == null) return;
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
		ArrayList<BlockColourStorage> blockColourList = new ArrayList<BlockColourStorage>();
		//loop through every block in the game
		Registries.BLOCK.forEach(block -> {
			if(block.asItem().getTranslationKey() == Items.AIR.getTranslationKey()) return;
			((IItemBlockColourSaver) block.asItem()).clearSpriteDetails();
			BlockColourStorage storage = new BlockColourStorage();
			totalBlocks +=1;
			Set<Sprite> sprites = new HashSet<Sprite>();
			//try to get the default top texture sprite. if fails, report error and skip this block
			
			block.getStateManager().getStates().forEach(state -> {
				try{
					var model = client.getBakedModelManager().getBlockModels().getModel(state);						
					sprites.add(model.getQuads(state, Direction.UP, Random.create()).get(0).getSprite());
					sprites.add(model.getQuads(state, Direction.DOWN, Random.create()).get(0).getSprite());
					sprites.add(model.getQuads(state, Direction.NORTH, Random.create()).get(0).getSprite());
					sprites.add(model.getQuads(state, Direction.SOUTH, Random.create()).get(0).getSprite());
					sprites.add(model.getQuads(state, Direction.EAST, Random.create()).get(0).getSprite());
					sprites.add(model.getQuads(state, Direction.WEST, Random.create()).get(0).getSprite());
					successes +=1;
				}catch(Exception e){	
					fails +=1;						
					return;
				}
			});
			if(sprites.size() < 1){
				return;
			}
			sprites.forEach(sprite -> {
				if(sprite.getContents().getId().getPath().equals("block/grass_block_side")) return;
				//get coords of sprite in atlas
				int spriteX = sprite.getX();
				int spriteY = sprite.getY();
				int spriteW = sprite.getContents().getWidth();
				int spriteH = sprite.getContents().getHeight();
				//convert coords to byte position
				int firstPixel = (spriteY*width + spriteX)*4;
				ArrayList<ColourVector> rgbList = new ArrayList<ColourVector>();
				int biomeColour = 0xFFFFFF;
				try{
					biomeColour = client.getBlockColors().getColor(block.getDefaultState(), null, null, 0);
				}catch (Exception e){
					LOGGER.warn("Could not find biome colour for block: " + block.getName() + ". Please report this logfile to https://github.com/bacco-bacco/MCRGB/issues");
				}
				//for each horizontal row in the sprite
				for (int row = 0; row < spriteH; row++){
					int firstInRow = firstPixel + row*width*4;
					//loop from first pixel in row to the sprite width.
					//Note: Looping in increments of 4, because each pixel is 4 bytes. (R,G,B and A)
					for (int pos = firstInRow; pos < firstInRow + 4*spriteW; pos+=4){
						//retrieve bytes for RGBA values
						//"& 0xFF" does logical and with 11111111. this extracts the last 8 bits, converting to unsigned int
						int pixelColour = ColorHelper.Argb.getArgb(pixels[pos+3], pixels[pos] & 0xFF, pixels[pos+1] & 0xFF, pixels[pos+2] & 0xFF);
						int alpha = ColorHelper.Argb.getAlpha(pixelColour);
						if(biomeColour != -1 & (!block.getDefaultState().isOf(Blocks.GRASS_BLOCK) || sprite.getContents().getId().getPath().equals("block/grass_block_top"))){
							pixelColour = ColorHelper.Argb.mixColor(biomeColour, pixelColour);
						}
						//if the pixel is not fully transparent, add to the list
						if(alpha > 0) {
							ColourVector c = new ColourVector(ColorHelper.Argb.getRed(pixelColour), ColorHelper.Argb.getGreen(pixelColour), ColorHelper.Argb.getBlue(pixelColour));
							rgbList.add(c);
						}



					}
				}
				//Calculate the dominant colours
				Set<ColourGroup> colourGroups = GroupColours(rgbList);
				if (colourGroups == null) return;

				//Add sprite name and each dominant colour to the IItemBlockColourSaver
				SpriteDetails spriteDetails = new SpriteDetails();
				String[] namesplit = sprite.getContents().getId().toString().split("/");
				String name = namesplit[namesplit.length-1];
				spriteDetails.name = name;
				colourGroups.forEach(cg -> {	
					spriteDetails.colourinfo.add(cg.meanColour);
					spriteDetails.weights.add(cg.weight);
				});
				storage.block = block.asItem().getTranslationKey();
				storage.spriteDetails.add(spriteDetails);
			});				
			storage.spriteDetails.forEach(details -> {
				((IItemBlockColourSaver) block.asItem()).addSpriteDetails(details);
			});
			blockColourList.add(storage);
		});

		//Write arraylist to json
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String blockColoursJson = gson.toJson(blockColourList);
		try {
			writeJson(blockColoursJson, "./mcrgb_colours/", "file.json");
		} catch (IOException e) {
		}
		client.player.sendMessage(Text.translatable("message.mcrgb.reloaded"), false);
	}
}