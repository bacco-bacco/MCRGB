package com.bacco.gui;

import com.bacco.IItemBlockColourSaver;
import com.bacco.MCRGBClient;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
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

public class BlockInfoGui extends LightweightGuiDescription {

    WGridPanel root = new WGridPanel();
    WGridPanel mainPanel = new WGridPanel();

    WLabel label = new WLabel(Text.translatable("ui.mcrgb.header"));

    WBlockInfoBox infoBox;

    WScrollPanel infoScrollPanel;

    public BlockInfoGui(net.minecraft.client.MinecraftClient client, MCRGBClient mcrgbClient, ItemStack stack){

        Identifier backIdentifier = Identifier.of("mcrgb", "back.png");
        TextureIcon backIcon = new TextureIcon(backIdentifier);
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


        mainPanel.add(label, 0, 0, 2, 1);
        label.setText(stack.getName());

        mainPanel.add(backButton,17,0,1,1);
        backButton.setSize(20,20);
        backButton.setIconSize(18);
        backButton.setAlignment(HorizontalAlignment.LEFT);

        backButton.setOnClick(() -> {
            client.setScreen(new ColourScreen(new ColourGui(client, mcrgbClient)));
        });

        infoBox = new WBlockInfoBox(Axis.VERTICAL,(IItemBlockColourSaver) stack.getItem(),this);
        infoScrollPanel = new WScrollPanel(infoBox);



        //gui.mainPanel.add(this.gui.infoBox,this.getAbsoluteX()/18+1,this.getAbsoluteY()/18+1);
        mainPanel.add(this.infoScrollPanel,10,0,7,12);

        root.validate(this);
    }
}
