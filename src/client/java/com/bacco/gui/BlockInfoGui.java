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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BlockInfoGui extends MCRGBBaseGui {

    WLabel label = new WLabel(Text.translatable("ui.mcrgb.header"));

    WBlockInfoBox infoBox;

    WScrollPanel infoScrollPanel;


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
        colourDisplay.setOpaqueTint(inputColour.asInt());
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
