package com.bacco.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector3i;

import com.bacco.IItemBlockColourSaver;
import com.bacco.MCRGBClient;
import com.bacco.SpriteDetails;
import com.mojang.authlib.minecraft.client.MinecraftClient;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItem;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ColourGui extends LightweightGuiDescription {
    int r = 255; 
    int g = 255; 
    int b = 255;
    String hex = "#FFFFFF";
    WGridPanel root = new WGridPanel();
    WLabel label = new WLabel(Text.literal("MCRGB"));
    WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL);
    WTextField hexInput = new WTextField(Text.literal("#cebbed"));
    WPlainPanel labels = new WPlainPanel();
    WLabel rLabel = new WLabel(Text.literal("R"),0xFF0000);
    WLabel gLabel = new WLabel(Text.literal("G"),0x00FF00);
    WLabel bLabel = new WLabel(Text.literal("B"),0x0000FF);
    WSlider rSlider = new WSlider(0, 255, Axis.VERTICAL);
    WSlider gSlider = new WSlider(0, 255, Axis.VERTICAL);
    WSlider bSlider = new WSlider(0, 255, Axis.VERTICAL);
    WPlainPanel inputs = new WPlainPanel();
    WTextField rInput = new WTextField(Text.literal(Integer.toString(r)));
    WTextField gInput = new WTextField(Text.literal(Integer.toString(g)));
    WTextField bInput = new WTextField(Text.literal(Integer.toString(b)));
    WButton refreshButton = new WButton(Text.literal("Refresh"));
    private ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    private ArrayList<WColourGuiSlot> wColourGuiSlots = new ArrayList<WColourGuiSlot>();
    net.minecraft.client.MinecraftClient client;
    MCRGBClient mcrgbClient;
    ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
    ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
    ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
    ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
    ItemStack horse = new ItemStack(Items.LEATHER_HORSE_ARMOR);
    WColourGuiSlot helmSlot = new WColourGuiSlot(helmet);
    WColourGuiSlot chestSlot = new WColourGuiSlot(chestplate);
    WColourGuiSlot legsSlot = new WColourGuiSlot(leggings);
    WColourGuiSlot bootSlot = new WColourGuiSlot(boots);
    WColourGuiSlot horseSlot = new WColourGuiSlot(horse);

    @Environment(value=EnvType.CLIENT)
    public ColourGui(net.minecraft.client.MinecraftClient client, MCRGBClient mcrgbClient){
        
        this.client = client;
        this.mcrgbClient = mcrgbClient;
        ColourSort(9,12);
        PlayerInventory inventory = client.player.getInventory();
        setRootPanel(root);
        root.setSize(320, 240);
        root.setInsets(Insets.ROOT_PANEL);
        WItemSlot itemSlot = WItemSlot.of(inventory, 1, 9, 11);
        root.add(refreshButton,16,13,3,1);
        
        root.add(label, 0, 0, 2, 1);
        root.add(itemSlot, 0, 1);
        root.add(hexInput, 11, 1, 5, 1);
        root.add(labels, 11,2,6,1);

        labels.add(rLabel, 6, 7, 1, 1);
        labels.add(gLabel, 42, 7, 1, 1);
        labels.add(bLabel, 78, 7, 1, 1);

        root.add(rSlider, 11, 3, 1, 6);
        rSlider.setValue(r);
        root.add(gSlider, 13, 3, 1, 6);
        gSlider.setValue(g);
        root.add(bSlider, 15, 3, 1, 6);
        bSlider.setValue(b);

        root.add(inputs,10,9,2,1);
        inputs.add(rInput,13,9,27,1);
        inputs.add(gInput,49,9,27,1);
        inputs.add(bInput,85,9,27,1);


        rSlider.setValueChangeListener((int value) -> SliderAdjust('r', value));
        gSlider.setValueChangeListener((int value) -> SliderAdjust('g', value));
        bSlider.setValueChangeListener((int value) -> SliderAdjust('b', value));

        rInput.setChangedListener((String value) -> RGBTyped('r',value));
        gInput.setChangedListener((String value) -> RGBTyped('g',value));
        bInput.setChangedListener((String value) -> RGBTyped('b',value));

        hexInput.setChangedListener((String value) -> HexTyped(value));

        refreshButton.setOnClick(() -> {mcrgbClient.RefreshColours(); ColourSort(9,12);});
        /*int index = 0;
        for(int j=1; j<12; j++) {
            for(int i=0; i<9; i++) {
                WColourGuiSlot colourGuiSlot = new WColourGuiSlot(stacks.get(index));
                wColourGuiSlots.add(colourGuiSlot);
                root.add(colourGuiSlot, i, j);
                index ++;
            }
        }*/

        //WItem item = new WItem(stacks);
        //root.add(item, 0,1,9,10);

        UpdateArmour();
        
        root.add(helmSlot, 11, 11);
        root.add(chestSlot, 12, 11);
        root.add(legsSlot, 13, 11);
        root.add(bootSlot, 14, 11);
        root.add(horseSlot, 15, 11);


        root.validate(this);
    }
    public void SliderAdjust(char d, int value) {
        if (d == 'r'){
            if (r == value) return;
            r = value;
            rInput.setText(Integer.toString(r));
        }
        if (d == 'g'){
            if (g == value) return;
            g = value;
            gInput.setText(Integer.toString(g));
        }
        if (d == 'b'){
            if (b == value) return;
            b = value;
            bInput.setText(Integer.toString(b));
        }
        hex = MCRGBClient.rgbToHex(r, g, b);
        hexInput.setText(hex);
        UpdateArmour();
        ColourSort(9,12);
    }
    public void RGBTyped(char d, String value){
        try{
            if(!rInput.isFocused() & !gInput.isFocused() & !bInput.isFocused()) return;
            if(Integer.valueOf(value) > 255 || Integer.valueOf(value) < 0) return;

            if (d == 'r'){
                if (r == Integer.valueOf(value)) return;
                r = Integer.valueOf(value);
                rSlider.setValue(r);
            }
            if (d == 'g'){
                if (g == Integer.valueOf(value)) return;
                g = Integer.valueOf(value);
                gSlider.setValue(g);
            }
            if (d == 'b'){
                if (b == Integer.valueOf(value)) return;
                b = Integer.valueOf(value);
                bSlider.setValue(b);
            }
            hex = MCRGBClient.rgbToHex(r, g, b);
            hexInput.setText(hex);
            UpdateArmour();
        }catch(Exception e){

        }
    }
    public void HexTyped(String value){
        try{
            hex = value;
            if(!hexInput.isFocused()) return;
            if(MCRGBClient.hexToRGB(value) == new Vector3i(r,g,b)) return;
            Vector3i rgb = MCRGBClient.hexToRGB(value);
            r = rgb.x;
            rSlider.setValue(r);
            rInput.setText(Integer.toString(r));

            g = rgb.y;
            gSlider.setValue(g);
            gInput.setText(Integer.toString(g));

            b = rgb.z;
            bSlider.setValue(b);
            bInput.setText(Integer.toString(b));

            UpdateArmour();
            ColourSort(9,12);
        }catch(Exception e){}
    }

    public void UpdateArmour(){
        hex = hex.replace("#","");
        int hexint = Integer.parseInt(hex,16);
        helmet.getOrCreateSubNbt("display").putInt("color", hexint);
        chestplate.getOrCreateSubNbt("display").putInt("color", hexint);
        leggings.getOrCreateSubNbt("display").putInt("color", hexint);
        boots.getOrCreateSubNbt("display").putInt("color", hexint);
        horse.getOrCreateSubNbt("display").putInt("color", hexint);
    }

    public void ColourSort(int width, int height){
        stacks.clear();
        Vector3i query = new Vector3i(r, g, b);
        Registries.BLOCK.forEach(block -> {   
            try{
                for(int j = 0; j < ((IItemBlockColourSaver) block.asItem()).getLength(); j++){
                    double distance = 0;
                    SpriteDetails sprite = ((IItemBlockColourSaver) block.asItem()).getSpriteDetails(j);
                    for (int i = 0; i < sprite.colourinfo.size(); i++){
                        Vector3i colour = sprite.colourinfo.get(i);
                        if(colour == null) return;
                        distance += query.distance(colour) * sprite.weights.get(i);
                    }
                    if(distance < ((IItemBlockColourSaver) block.asItem()).getScore() || j == 0){
                    ((IItemBlockColourSaver) block.asItem()).setScore(distance);
                    }
                }
                if(block.asItem() != null && ((IItemBlockColourSaver) block.asItem()).getLength() > 0){
                    stacks.add(new ItemStack(block)); 
                }  
                                                
            }catch(Exception e){
                return;
            }   
        });

        //sort list
        Collections.sort(stacks, new Comparator<ItemStack>(){
            @Override
            public int compare(ItemStack is1, ItemStack is2) {
                double x = ((IItemBlockColourSaver)is1.getItem()).getScore();
                double y = ((IItemBlockColourSaver)is2.getItem()).getScore();
                return Double.compare(x,y);
            }
        });
        wColourGuiSlots.forEach(slot -> {
        root.remove(slot);
        });
        int index = 0;
        for(int j=1; j<height; j++) {
            for(int i=0; i<width; i++) {
                WColourGuiSlot colourGuiSlot = new WColourGuiSlot(stacks.get(index));
                
                if(wColourGuiSlots.size() <= index){
                wColourGuiSlots.add(colourGuiSlot);
                }else{
                wColourGuiSlots.set(index, colourGuiSlot);
                }
                root.add(colourGuiSlot, i, j);
                index ++;
            }
        }
        root.validate(this);
    }

}
