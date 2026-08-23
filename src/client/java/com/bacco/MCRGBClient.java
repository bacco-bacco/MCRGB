package com.bacco;

import com.bacco.event.KeyInputHandler;
import com.bacco.gui.ColourScreen;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.toast.SystemToast;
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
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class MCRGBClient implements ClientModInitializer {
	//public static final BlockColourStorage[] loadedBlockColourArray = new Gson().fromJson(readJson("./mcrgb_colours/file.json"), BlockColourStorage[].class);

	static Type listType = new TypeToken<ArrayList<Palette>>() {}.getType();

	//public static final ArrayList<Palette> loadedPalettes = new Gson().fromJson(readJson("./mcrgb_colours/palettes.json"), listType);

	public static final Logger LOGGER = LoggerFactory.getLogger("mcrgb");
	public static final boolean readMode = false;
	public static net.minecraft.client.MinecraftClient client;

	static boolean scanned = false;
	public ArrayList<Palette> palettes = new ArrayList<>();


	//public static ColourInventoryScreen colourInvScreen;

	@Override
	public void onInitializeClient() {
		KeyInputHandler.register(this);
		MCRGBConfig.load();
		ClientPlayConnectionEvents.JOIN.register((handler, sender, _client) -> {
			client = _client;
			//colourInvScreen = new ColourInventoryScreen(client);
			if (scanned) return;
			if(MCRGBConfig.instance.readJsonFile){
				//Read from JSON
				try{
				long parseStartTime = System.nanoTime();
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

				LOGGER.info("Parsed JSON in: " + (System.nanoTime()-parseStartTime)/1000000 + " ms");
				}catch(Exception e){
					RefreshColours();

				}
			}else{
				RefreshColours();
			}
			scanned = true;
		});
		LoadPalettes();
		//Override item tooltips to display the colour.
		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			if(!MCRGBConfig.instance.alwaysShowToolTips) return;
			IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			for(int i = 0; i < item.getLength(); i++){
				if(lines.size() >= MCRGBConfig.instance.maxTooltipLines){
					lines.add(Text.literal(" "));
					lines.add(Text.translatable("tooltip.mcrgb.item_show_more").formatted(Formatting.GRAY));
					break;
				}
				ArrayList<String> strings = item.getSpriteDetails(i).getStrings();
				ArrayList<Integer> colours = item.getSpriteDetails(i).getTextColours();
				if(strings.size() > 0){
					if(MinecraftClient.getInstance().isShiftPressed()){
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


	public static Set<ColourGroup> Algo_MCRGB(ArrayList<ColourVector> rgblist){
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
			group.repColour = group.CalculateMean();
			group.repHex = group.repColour.getHex();
			group.weight = (int)((float)group.pixels.size()/(float)rgblist.size() * 100);
		}

		return groups;
	}

	public static Set<ColourGroup> Algo_Mean(ArrayList<ColourVector> rgblist){
		Set<ColourGroup> groups = new HashSet<ColourGroup>();
		ColourGroup group = new ColourGroup();
		for (int i = 0; i < rgblist.size(); i++){
			group.pixels.add(new ColourVector(rgblist.get(i).r,rgblist.get(i).g,rgblist.get(i).b));
		}
		group.repColour = group.CalculateMean();
		group.repHex = group.repColour.getHex();
		group.weight = (int)((float)group.pixels.size()/(float)rgblist.size() * 100);
		groups.add(group);
		return groups;
	}

	public static Set<ColourGroup> Algo_Median(ArrayList<ColourVector> rgblist){
		Set<ColourGroup> groups = new HashSet<ColourGroup>();
		ColourGroup group = new ColourGroup();
		for (int i = 0; i < rgblist.size(); i++){
			group.pixels.add(new ColourVector(rgblist.get(i).r,rgblist.get(i).g,rgblist.get(i).b));
		}

		group.repColour = group.CalculateMedian();
		group.repHex = group.repColour.getHex();
		group.weight = (int)((float)group.pixels.size()/(float)rgblist.size() * 100);
		groups.add(group);
		return groups;
	}



/*
	public static Set<ColourGroup> Algo_MeanShift(ArrayList<ColourVector> rgblist){
		int window = 10;
		int counter = 0;
		int threshold = 1;
		ArrayList<ColourVector> means = new ArrayList<>();
		ArrayList<ColourGroup> groups = new ArrayList<ColourGroup>();
		ColourVector newMean = new ColourVector(0xFFFFFF);
		do {
			for (int i = 0; i < rgblist.size(); i++) {
				counter = 0;
				ColourVector iPix = rgblist.get(i);
				ColourGroup tempGroup = new ColourGroup();
				for (int j = 0; j < rgblist.size(); j++) {
					ColourVector jPix = rgblist.get(j);
					if (iPix.distance(jPix) < window) {
						tempGroup.pixels.add(jPix);
					}
				}
				newMean = tempGroup.CalculateMean();
				if (newMean.distance(iPix) < threshold) break;
				counter += 1;
				tempGroup = new ColourGroup();
				tempGroup.pixels.add(newMean);
				tempGroup.pixels.add(iPix);
				rgblist.set(i,tempGroup.CalculateMean());
			}
		}while(counter > 0);
		rgblist.forEach(shiftedPixel -> {
			for(int i = 0; i < groups.size(); i++){
				if(groups.get(i).repColour.distance(shiftedPixel) < threshold){
					break;
				}

			}
			ColourGroup newGroup = new ColourGroup();
			newGroup.repColour = shiftedPixel;
			groups.add(newGroup);
		});
		return new HashSet<>(groups);
	}
*/
	public static Set<ColourGroup> GroupColours(ArrayList<ColourVector> rgblist){
		switch (MCRGBConfig.instance.mode){
			case MCRGB:
				return Algo_MCRGB(rgblist);
			case MEAN:
				return Algo_Mean(rgblist);
			case MEDIAN:
				return Algo_Median(rgblist);
			default:
				return Algo_MCRGB(rgblist);
		}
	}

	public static void RefreshColours(){
		long refreshStartTime = System.nanoTime();
		if (client == null) return;
		//get top sprite of stone block default state
		var defSprite = client.getBakedModelManager().getBlockModels().getModel(Blocks.STONE.getDefaultState()).getParts(Random.create()).get(0).getQuads(Direction.UP).getFirst().sprite();
		//get id of the atlas containing above
		var atlas = defSprite.getAtlasId();
		//use atlas id to get OpenGL ID. Atlas contains ALL blocks
		GlTexture glTexture = (GlTexture) client.getTextureManager().getTexture(atlas).getGlTexture();
		//get width and height from OpenGL by binding texture
		int width = glTexture.getWidth(0);
		int height = glTexture.getHeight(0);
		int size = width * height;
		//Make byte buffer and load full atlas into buffer.
		GlStateManager._bindTexture(glTexture.getGlId());
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
			Set<Sprite> sprites = new HashSet<Sprite>();
			//try to get the default top texture sprite. if fails, report error and skip this block
			Direction[] directions = {Direction.UP,Direction.DOWN,Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST,null};
			block.getStateManager().getStates().forEach(state -> {
				for(int i = 0; i < directions.length; i++){
					try{
						var model = client.getBakedModelManager().getBlockModels().getModel(state);
						sprites.add(model.getParts(Random.create()).getFirst().getQuads(directions[i]).get(0).sprite());
					}catch(Exception e){
					}
				}
			});
			if(sprites.size() < 1){
				return;
			}
			sprites.forEach(sprite -> {
				if(sprite.getContents().getId().getPath().equals("block/grass_block_side")) return;
				//get coords of sprite in atlas

				//x and y Buffer of 17 required as workaround for Minecraft 1.21.11 bug: MC-303675
				int xBuffer = 17;
				int yBuffer = 17;

				int spriteX = sprite.getX()+xBuffer;
				int spriteY = sprite.getY()+yBuffer;
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
						int pixelColour = ColorHelper.getArgb(pixels[pos+3], pixels[pos] & 0xFF, pixels[pos+1] & 0xFF, pixels[pos+2] & 0xFF);
						int alpha = ColorHelper.getAlpha(pixelColour);
						if(biomeColour != -1 & (!block.getDefaultState().isOf(Blocks.GRASS_BLOCK) || sprite.getContents().getId().getPath().equals("block/grass_block_top"))){
							pixelColour = ColorHelper.mix(biomeColour, pixelColour);
						}
						//if the pixel is not fully transparent, add to the list
						if(alpha > 0) {
							ColourVector c = new ColourVector(ColorHelper.getRed(pixelColour), ColorHelper.getGreen(pixelColour), ColorHelper.getBlue(pixelColour));
							rgbList.add(c);
						}



					}
				}
				//Calculate the dominant colours
				if(rgbList.isEmpty()){
					return;
				}
				Set<ColourGroup> colourGroups = GroupColours(rgbList);
				if (colourGroups == null) return;

				//Add sprite name and each dominant colour to the IItemBlockColourSaver
				SpriteDetails spriteDetails = new SpriteDetails();
				String[] namesplit = sprite.getContents().getId().toString().split("/");
				String name = namesplit[namesplit.length-1];
				spriteDetails.name = name;
				colourGroups.forEach(cg -> {	
					spriteDetails.colourinfo.add(cg.repColour);
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

		if(MCRGBConfig.instance.readJsonFile) {
			long saveStartTime = System.nanoTime();
			//Write arraylist to json
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String blockColoursJson = gson.toJson(blockColourList);
			try {
				writeJson(blockColoursJson, "./mcrgb_colours/", "file.json");
			} catch (IOException e) {
			}
			LOGGER.info("Saved to JSON in: " + (System.nanoTime() - saveStartTime)/1000000 + " ms");
		}
		if(client.currentScreen instanceof ColourScreen){
			SystemToast clipboardToast = new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable("toast.mcrgb.generic_toast_title"), Text.translatable("toast.mcrgb.reloaded"));
			MinecraftClient.getInstance().getToastManager().add(clipboardToast);
		}
		LOGGER.info("Refreshed colours in: " + (System.nanoTime()-refreshStartTime)/1000000 + " ms");
	}

	public void SavePalettes(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String blockColoursJson = gson.toJson(palettes);
		try {
			writeJson(blockColoursJson, "./mcrgb_colours/", "palettes.json");
		} catch (IOException e) {
		}
	}

	public void LoadPalettes(){
		ArrayList<Palette> loadedPalettes = new ArrayList<Palette>();
		try{
			loadedPalettes = new Gson().fromJson(readJson("./mcrgb_colours/palettes.json"), listType);
		}catch (Exception e){
			loadedPalettes = new ArrayList<Palette>();
		}
		if(loadedPalettes == null){
			loadedPalettes = new ArrayList<Palette>();
		}

		palettes = loadedPalettes;

	}

	public static int median(int[] values){
		int mid = values.length/2;
		if (values.length % 2 == 0){
			return (values[mid-1]+values[mid])/2;
		}else{
			return values[mid];
		}
	}

	public static int mean(int[] values){
		int sum = IntStream.of(values).sum();
		return sum/values.length;
	}


}