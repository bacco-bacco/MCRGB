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
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlockInfoGui extends MCRGBBaseGui {

    WLabel label = new WLabel(Component.translatable("ui.mcrgb.header"));

    WBlockInfoBox infoBox;

    WScrollPanel infoScrollPanel;

    WPickableTexture blockTexture;

    WGridPanel textureThumbs = new WGridPanel();

    WScrollPanel textureScrollPanel = new WScrollPanel(textureThumbs);

    ArrayList<TextureAtlasSprite> spritesAL = new ArrayList<>();



    public BlockInfoGui(net.minecraft.client.Minecraft client, MCRGBClient mcrgbClient, ItemStack stack, ColourVector launchColour){

        this.client = client;
        this.mcrgbClient = mcrgbClient;

        Identifier backIdentifier = Identifier.fromNamespaceAndPath("mcrgb", "back.png");
        TextureIcon backIcon = new TextureIcon(backIdentifier);
        savedPalettesArea = new WSavedPalettesArea(this, 9, 7, mcrgbClient);
        WButton backButton = new WButton(backIcon){
            @Environment(EnvType.CLIENT)
            @Override
            public void addTooltip(TooltipBuilder tooltip) {
                tooltip.add(Component.translatable("ui.mcrgb.back_info"));
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
        label.setText(stack.getHoverName());

        mainPanel.add(backButton,17,0,1,1);
        backButton.setSize(20,20);
        backButton.setIconSize(18);
        backButton.setAlignment(HorizontalAlignment.LEFT);

        backButton.setOnClick(() -> {
            client.gui.setScreen(new ColourScreen(new ColourGui(client, mcrgbClient,inputColour)));
        });

        infoBox = new WBlockInfoBox(Axis.VERTICAL,(IItemBlockColourSaver) stack.getItem(), this);
        infoScrollPanel = new WScrollPanel(infoBox);

        mainPanel.add(this.infoScrollPanel,11,3,7,9);

        mainPanel.add(savedPalettesArea,0,7);

        SetColour(launchColour);


        BlockItem bi = (BlockItem) stack.getItem();
        Block block = bi.getBlock();

        Set<TextureAtlasSprite> sprites = new HashSet<TextureAtlasSprite>();
        //try to get the default top texture sprite. if fails, report error and skip this block
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST,null};
        block.getStateDefinition().getPossibleStates().forEach(state -> {
            for(int i = 0; i < directions.length; i++){
                try{
                    var model = client.getModelManager().getBlockStateModelSet().get(state);
                    ArrayList<BlockStateModelPart> blockStateList = new ArrayList<BlockStateModelPart>();
                    model.collectParts(RandomSource.create(), blockStateList);
                    sprites.add(blockStateList.getFirst().getQuads(directions[i]).get(0).materialInfo().sprite());
                }catch(Exception e){
                }
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
            WTextureThumbnail thumbnail = new WTextureThumbnail(spritesAL.get(i).atlasLocation(),spritesAL.get(i).getU0(), spritesAL.get(i).getV0(), spritesAL.get(i).getU1(), spritesAL.get(i).getV1(), i, this);
            textureThumbs.add(thumbnail,i%3,Math.floorDiv(i,3));
        }

        blockTexture = new WPickableTexture(spritesAL.get(0).atlasLocation(),spritesAL.get(0).getU0(), spritesAL.get(0).getV0(), spritesAL.get(0).getU1(), spritesAL.get(0).getV1(), client, this);

        mainPanel.add(blockTexture,0,1,6,6);
        mainPanel.add(textureScrollPanel,7,1,4,6);

        root.validate(this);
    }

    public void ChangeSprite(int i){
        //blockTexture = new WSprite(spritesAL.get(i).getAtlasId(),spritesAL.get(i).getMinU(), spritesAL.get(i).getMinV(), spritesAL.get(i).getMaxU(), spritesAL.get(i).getMaxV());
        blockTexture.setImage(spritesAL.get(i).atlasLocation());
        blockTexture.setUv(spritesAL.get(i).getU0(), spritesAL.get(i).getV0(), spritesAL.get(i).getU1(), spritesAL.get(i).getV1());
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
