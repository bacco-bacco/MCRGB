package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.IItemBlockColourSaver;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlockInfoGui extends MCRGBBaseGui {

    WLabel label = new WLabel(Text.translatable("ui.mcrgb.header"));

    WBlockInfoBox infoBox;

    WScrollPanel infoScrollPanel;

    WPickableTexture blockTexture;

    WGridPanel textureThumbs = new WGridPanel();

    ArrayList<Sprite> spritesAL = new ArrayList<>();



    public BlockInfoGui(net.minecraft.client.MinecraftClient client, MCRGBClient mcrgbClient, ItemStack stack, ColourVector launchColour){

        this.client = client;
        this.mcrgbClient = mcrgbClient;

        Identifier backIdentifier = Identifier.of("mcrgb", "back.png");
        TextureIcon backIcon = new TextureIcon(backIdentifier);
        savedPalettesArea = new WSavedPalettesArea(this, 9, 7, mcrgbClient);
        WButton backButton = new WButton(backIcon){
            @Environment(EnvType.CLIENT)
            @Override
            public void addTooltip(TooltipBuilder tooltip) {
                tooltip.add(Text.translatable("ui.mcrgb.back_info"));
                super.addTooltip(tooltip);
            }
        };

        setRootPanel(root);
        root.add(mainPanel, 0,0);
        mainPanel.setSize(320, 220);
        mainPanel.setInsets(Insets.ROOT_PANEL);
        mainPanel.add(hexInput, 11, 1, 5, 1);
        hexInput.setChangedListener((String value) -> HexTyped(value,false));
        mainPanel.add(colourDisplay,16,1,2,2);
        colourDisplay.setLocation(colourDisplay.getAbsoluteX()+1,colourDisplay.getAbsoluteY()-1);



        mainPanel.add(label, 0, 0, 2, 1);
        label.setText(stack.getName());

        mainPanel.add(backButton,17,0,1,1);
        backButton.setSize(20,20);
        backButton.setIconSize(18);
        backButton.setAlignment(HorizontalAlignment.LEFT);

        backButton.setOnClick(() -> {
            client.setScreen(new ColourScreen(new ColourGui(client, mcrgbClient,inputColour)));
        });

        infoBox = new WBlockInfoBox(Axis.VERTICAL,(IItemBlockColourSaver) stack.getItem(), this);
        infoScrollPanel = new WScrollPanel(infoBox);

        mainPanel.add(this.infoScrollPanel,11,3,7,9);

        mainPanel.add(savedPalettesArea,0,7);

        SetColour(launchColour);


        BlockItem bi = (BlockItem) stack.getItem();
        Block block = bi.getBlock();

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
            }catch(Exception e){
                return;
            }
        });
        if(sprites.isEmpty()){
            return;
        }

        sprites.forEach(sprite -> {
            spritesAL.add(sprite);
        });

        int length = sprites.size();
        for (int i = 0; i < length; i++){
            WTextureThumbnail thumbnail = new WTextureThumbnail(spritesAL.get(i).getAtlasId(),spritesAL.get(i).getMinU(), spritesAL.get(i).getMinV(), spritesAL.get(i).getMaxU(), spritesAL.get(i).getMaxV(), i, this);
            textureThumbs.add(thumbnail,i%3,Math.floorDiv(i,3));
        }

        blockTexture = new WPickableTexture(spritesAL.get(0).getAtlasId(),spritesAL.get(0).getMinU(), spritesAL.get(0).getMinV(), spritesAL.get(0).getMaxU(), spritesAL.get(0).getMaxV(), client, this);

        mainPanel.add(blockTexture,0,1,6,6);
        mainPanel.add(textureThumbs,7,1,3,6);


        root.validate(this);
    }

    public void ChangeSprite(int i){
        //blockTexture = new WSprite(spritesAL.get(i).getAtlasId(),spritesAL.get(i).getMinU(), spritesAL.get(i).getMinV(), spritesAL.get(i).getMaxU(), spritesAL.get(i).getMaxV());
        blockTexture.setImage(spritesAL.get(i).getAtlasId());
        blockTexture.setUv(spritesAL.get(i).getMinU(), spritesAL.get(i).getMinV(), spritesAL.get(i).getMaxU(), spritesAL.get(i).getMaxV());
        root.validate(this);
    }


    public void HexTyped(String value, boolean modeChanged){
        try{
            ColourVector colour = new ColourVector(value);
            if(!hexInput.isFocused()){
                return;
            }
            if(value == inputColour.getHex()){
                return;
            }

            inputColour = colour;
            colourDisplay.setOpaqueTint(inputColour.asInt());


        }catch(Exception e){}
    }

}
