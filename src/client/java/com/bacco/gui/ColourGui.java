package com.bacco.gui;


import com.bacco.*;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ColourGui extends LightweightGuiDescription {
    static int height = 12;
    static int width = 9;
    ColourVector inputColour = new ColourVector(255,255,255);
    /*int r = 255;
    int g = 255; 
    int b = 255;*/
    //String hex = "#FFFFFF";
    WGridPanel root = new WGridPanel();
    WLabel label = new WLabel(Text.translatable("ui.mcrgb.header"));
    WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL){
        @Environment(EnvType.CLIENT)
        @Override
        public InputResult onMouseDrag(int x, int y, int button, double deltaX, double deltaY) {
            PlaceSlots();
            return super.onMouseDrag(x, y, button, deltaX, deltaY);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public InputResult onMouseScroll(int x, int y, double hAmount, double vAmount) {
            PlaceSlots();
            setValue(getValue() + (int) -vAmount);
		    return InputResult.PROCESSED;
            //return super.onMouseScroll(x, y, hAmount, vAmount);
        }
    };
    WTextField hexInput = new WTextField(Text.literal("#FFFFFF"));
    WPlainPanel labels = new WPlainPanel();
    WLabel rLabel = new WLabel(Text.translatable("ui.mcrgb.r_for_red"),0xFF0000);
    WLabel gLabel = new WLabel(Text.translatable("ui.mcrgb.g_for_green"),0x00FF00);
    WLabel bLabel = new WLabel(Text.translatable("ui.mcrgb.b_for_blue"),0x0000FF);
    WSlider rSlider = new WSlider(0, 255, Axis.VERTICAL);
    WSlider gSlider = new WSlider(0, 255, Axis.VERTICAL);
    WSlider bSlider = new WSlider(0, 255, Axis.VERTICAL);
    WPlainPanel inputs = new WPlainPanel();
    WTextField rInput = new WTextField(Text.literal(Integer.toString(inputColour.r)));
    WTextField gInput = new WTextField(Text.literal(Integer.toString(inputColour.g)));
    WTextField bInput = new WTextField(Text.literal(Integer.toString(inputColour.b)));
    Identifier refreshIdentifier = new Identifier("mcrgb", "refresh.png");
    TextureIcon refreshIcon = new TextureIcon(refreshIdentifier);
    WButton refreshButton = new WButton(refreshIcon){
        @Environment(EnvType.CLIENT)
        @Override
        public void addTooltip(TooltipBuilder tooltip) {
            tooltip.add(Text.translatable("ui.mcrgb.refresh_info"));
            super.addTooltip(tooltip);
        }
    };
    Identifier settingsIdentifier = new Identifier("mcrgb", "settings.png");
    TextureIcon settingsIcon = new TextureIcon(settingsIdentifier);
    WButton settingsButton = new WButton(settingsIcon);

    WButton rgbButton = new WButton(Text.literal("RGB"));
    WButton hsvButton = new WButton(Text.literal("HSV"));
    WButton hslButton = new WButton(Text.literal("HSL"));
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
    boolean enableSliderListeners = true;

    enum ColourMode {
        RGB,
        HSV,
        HSL
    }
    ColourMode mode = ColourMode.RGB;

    @Environment(value=EnvType.CLIENT)
    public ColourGui(net.minecraft.client.MinecraftClient client, MCRGBClient mcrgbClient){
        this.client = client;
        this.mcrgbClient = mcrgbClient;
        ColourSort();
        setRootPanel(root);
        root.setSize(320, 220);
        root.setInsets(Insets.ROOT_PANEL);
        root.add(hexInput, 11, 1, 5, 1);
        root.add(scrollBar,9,1,1,11);
        root.add(refreshButton,17,11,1,1);
        refreshButton.setSize(20,20);
        refreshButton.setIconSize(18);
        refreshButton.setAlignment(HorizontalAlignment.LEFT);

        root.add(settingsButton,17,0,1,1);
        settingsButton.setSize(20,20);
        settingsButton.setIconSize(18);
        settingsButton.setAlignment(HorizontalAlignment.LEFT);

        root.add(rgbButton,17,3,1,1);
        rgbButton.setSize(20,10);
        root.add(hsvButton,17,4,1,1);
        hsvButton.setSize(20,10);
        root.add(hslButton,17,5,1,1);
        hslButton.setSize(20,10);
        
        root.add(label, 0, 0, 2, 1);
        
        root.add(labels, 11,2,6,1);

        labels.add(rLabel, 6, 7, 1, 1);
        labels.add(gLabel, 42, 7, 1, 1);
        labels.add(bLabel, 78, 7, 1, 1);

        root.add(rSlider, 11, 3, 1, 6);
        rSlider.setValue(inputColour.r);
        root.add(gSlider, 13, 3, 1, 6);
        gSlider.setValue(inputColour.g);
        root.add(bSlider, 15, 3, 1, 6);
        bSlider.setValue(inputColour.b);

        root.add(inputs,10,9,2,1);
        inputs.add(rInput,13,9,27,1);
        inputs.add(gInput,49,9,27,1);
        inputs.add(bInput,85,9,27,1);

        rSlider.setValueChangeListener((int value) -> {if(enableSliderListeners) SliderAdjust('r', value);});
        gSlider.setValueChangeListener((int value) -> {if(enableSliderListeners) SliderAdjust('g', value);});
        bSlider.setValueChangeListener((int value) -> {if(enableSliderListeners) SliderAdjust('b', value);});

        rSlider.setDraggingFinishedListener((int value) -> {if(!MCRGBConfig.instance.sliderConstantUpdate) ColourSort();});
        gSlider.setDraggingFinishedListener((int value) -> {if(!MCRGBConfig.instance.sliderConstantUpdate) ColourSort();});
        bSlider.setDraggingFinishedListener((int value) -> {if(!MCRGBConfig.instance.sliderConstantUpdate) ColourSort();});


        rInput.setChangedListener((String value) -> RGBTyped('r',value));
        gInput.setChangedListener((String value) -> RGBTyped('g',value));
        bInput.setChangedListener((String value) -> RGBTyped('b',value));

        hexInput.setChangedListener((String value) -> HexTyped(value,false));

        refreshButton.setOnClick(() -> {mcrgbClient.RefreshColours(); ColourSort();});

        rgbButton.setOnClick(() -> {SetColourMode(ColourMode.RGB);});
        hsvButton.setOnClick(() -> {SetColourMode(ColourMode.HSV);});
        hslButton.setOnClick(() -> {SetColourMode(ColourMode.HSL);});

        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            settingsButton.setOnClick(() -> {
                MinecraftClient.getInstance().setScreen(ClothConfigIntegration.getConfigScreen(client.currentScreen));
            });
        }else{
            settingsButton.setOnClick(() -> {
                client.player.sendMessage(Text.translatable("warning.mcrgb.noclothconfig"));
            });
        }
        UpdateArmour();
        
        root.add(helmSlot, 11, 11);
        root.add(chestSlot, 12, 11);
        root.add(legsSlot, 13, 11);
        root.add(bootSlot, 14, 11);
        root.add(horseSlot, 15, 11);


        root.validate(this);
    }

    public  void SetColourMode(ColourMode cm){
        enableSliderListeners = false;
        mode = cm;
        ColourVector colour = new ColourVector(inputColour.getHex());

        switch (mode){
            case RGB:
                rLabel.setText(Text.literal("R"));
                rLabel.setColor(0xFF0000);
                gLabel.setText(Text.literal("G"));
                gLabel.setColor(0x00FF00);
                bLabel.setText(Text.literal("B"));
                bLabel.setColor(0x0000FF);

                rSlider.setMinValue(0);
                gSlider.setMinValue(0);
                bSlider.setMinValue(0);
                rSlider.setMaxValue(255);
                gSlider.setMaxValue(255);
                bSlider.setMaxValue(255);
                break;
            case HSV:
                rLabel.setText(Text.literal("H"));
                rLabel.setColor(0x3F3F3F);
                gLabel.setText(Text.literal("S"));
                gLabel.setColor(0x3F3F3F);
                bLabel.setText(Text.literal("V"));
                bLabel.setColor(0x3F3F3F);
                rSlider.setMinValue(0);
                gSlider.setMinValue(0);
                bSlider.setMinValue(0);
                rSlider.setMaxValue(360);
                gSlider.setMaxValue(100);
                bSlider.setMaxValue(100);
                break;
            case HSL:
                rLabel.setText(Text.literal("H"));
                rLabel.setColor(0x3F3F3F);
                gLabel.setText(Text.literal("S"));
                gLabel.setColor(0x3F3F3F);
                bLabel.setText(Text.literal("L"));
                bLabel.setColor(0x3F3F3F);
                rSlider.setMinValue(0);
                gSlider.setMinValue(0);
                bSlider.setMinValue(0);
                rSlider.setMaxValue(360);
                gSlider.setMaxValue(100);
                bSlider.setMaxValue(100);
                break;
        }
        HexTyped(colour.getHex(),true);
        inputColour = colour;
        enableSliderListeners = true;
    }
    public void SliderAdjust(char d, int value) {
        switch (mode){
            case RGB:
                if (d == 'r'){
                    if (inputColour.r == value) return;
                    inputColour.r = value;
                    rInput.setText(Integer.toString(inputColour.r));
                }
                if (d == 'g'){
                    if (inputColour.g == value) return;
                    inputColour.g = value;
                    gInput.setText(Integer.toString(inputColour.g));
                }
                if (d == 'b'){
                    if (inputColour.b == value) return;
                    inputColour.b = value;
                    bInput.setText(Integer.toString(inputColour.b));
                }
                break;
            case HSV:
                inputColour.fromHSV(rSlider.getValue(),gSlider.getValue(),bSlider.getValue());
                rInput.setText(Integer.toString(rSlider.getValue()));
                gInput.setText(Integer.toString(gSlider.getValue()));
                bInput.setText(Integer.toString(bSlider.getValue()));
                break;
            case HSL:
                inputColour.fromHSL(rSlider.getValue(),gSlider.getValue(),bSlider.getValue());
                rInput.setText(Integer.toString(rSlider.getValue()));
                gInput.setText(Integer.toString(gSlider.getValue()));
                bInput.setText(Integer.toString(bSlider.getValue()));
                break;
        }
        hexInput.setText(inputColour.getHex());
        UpdateArmour();
        if(MCRGBConfig.instance.sliderConstantUpdate) ColourSort();
    }
    public void RGBTyped(char d, String value){
        try{
            switch (mode){
                case RGB:
                    if(!rInput.isFocused() & !gInput.isFocused() & !bInput.isFocused()) return;
                    if(Integer.valueOf(value) > 255 || Integer.valueOf(value) < 0) return;

                    if (d == 'r'){
                        if (inputColour.r == Integer.valueOf(value)) return;
                        inputColour.r = Integer.valueOf(value);
                        rSlider.setValue(inputColour.r);
                    }
                    if (d == 'g'){
                        if (inputColour.g == Integer.valueOf(value)) return;
                        inputColour.g = Integer.valueOf(value);
                        gSlider.setValue(inputColour.g);
                    }
                    if (d == 'b'){
                        if (inputColour.b == Integer.valueOf(value)) return;
                        inputColour.b = Integer.valueOf(value);
                        bSlider.setValue(inputColour.b);
                    }
                    break;
                case HSV:
                    if(!rInput.isFocused() & !gInput.isFocused() & !bInput.isFocused()) return;
                    if(d == 'r'){
                        if(Integer.valueOf(value) > 360 || Integer.valueOf(value) < 0) return;
                    }else{
                        if(Integer.valueOf(value) > 100 || Integer.valueOf(value) < 0) return;
                    }


                    if (d == 'r'){
                        if (inputColour.r == Integer.valueOf(value)) return;
                        rSlider.setValue(Integer.valueOf(value));
                    }
                    if (d == 'g'){
                        if (inputColour.g == Integer.valueOf(value)) return;
                        gSlider.setValue(Integer.valueOf(value));
                    }
                    if (d == 'b'){
                        if (inputColour.b == Integer.valueOf(value)) return;
                        bSlider.setValue(Integer.valueOf(value));
                    }
                    inputColour.fromHSV(rSlider.getValue(),gSlider.getValue(),bSlider.getValue());
                    break;
                case HSL:
                    if(!rInput.isFocused() & !gInput.isFocused() & !bInput.isFocused()) return;
                    if(d == 'r'){
                        if(Integer.valueOf(value) > 360 || Integer.valueOf(value) < 0) return;
                    }else{
                        if(Integer.valueOf(value) > 100 || Integer.valueOf(value) < 0) return;
                    }


                    if (d == 'r'){
                        if (inputColour.r == Integer.valueOf(value)) return;
                        rSlider.setValue(Integer.valueOf(value));
                    }
                    if (d == 'g'){
                        if (inputColour.g == Integer.valueOf(value)) return;
                        gSlider.setValue(Integer.valueOf(value));
                    }
                    if (d == 'b'){
                        if (inputColour.b == Integer.valueOf(value)) return;
                        bSlider.setValue(Integer.valueOf(value));
                    }
                    inputColour.fromHSL(rSlider.getValue(),gSlider.getValue(),bSlider.getValue());
                    break;
            }
            hexInput.setText(inputColour.getHex());
            UpdateArmour();
        }catch(Exception e){

        }
    }
    public void HexTyped(String value, boolean modeChanged){
        enableSliderListeners = false;
        try{
            ColourVector colour = new ColourVector(value);
            if(!modeChanged && !hexInput.isFocused()){
                enableSliderListeners = true;
                return;
            }
            if(!modeChanged && value == inputColour.getHex()){
                enableSliderListeners = true;
                return;
            }
            switch(mode) {
                case RGB:
                    rSlider.setValue(colour.r);
                    rInput.setText(Integer.toString(colour.r));

                    gSlider.setValue(colour.g);
                    gInput.setText(Integer.toString(colour.g));

                    bSlider.setValue(colour.b);
                    bInput.setText(Integer.toString(colour.b));
                    break;
                case HSV:
                    rSlider.setValue(colour.getHue());
                    rInput.setText(Integer.toString(colour.getHue()));

                    gSlider.setValue(colour.getSatV());
                    gInput.setText(Integer.toString(colour.getSatV()));

                    bSlider.setValue(colour.getVal());
                    bInput.setText(Integer.toString(colour.getVal()));
                    break;
                case HSL:
                    rSlider.setValue(colour.getHue());
                    rInput.setText(Integer.toString(colour.getHue()));

                    gSlider.setValue(colour.getSatL());
                    gInput.setText(Integer.toString(colour.getSatL()));

                    bSlider.setValue(colour.getLight());
                    bInput.setText(Integer.toString(colour.getLight()));
                    break;
            }
            inputColour = colour;
            UpdateArmour();
            ColourSort();
        }catch(Exception e){}
        enableSliderListeners = true;
    }

    public void UpdateArmour(){
        String hex = inputColour.getHex().replace("#","");
        int hexint = Integer.parseInt(hex,16);
        helmet.getOrCreateSubNbt("display").putInt("color", hexint);
        chestplate.getOrCreateSubNbt("display").putInt("color", hexint);
        leggings.getOrCreateSubNbt("display").putInt("color", hexint);
        boots.getOrCreateSubNbt("display").putInt("color", hexint);
        horse.getOrCreateSubNbt("display").putInt("color", hexint);
    }

    public void ColourSort(){
        stacks.clear();
        ColourVector query = inputColour;

        Registries.BLOCK.forEach(block -> {
            try{
                for(int j = 0; j < ((IItemBlockColourSaver) block.asItem()).getLength(); j++){
                    double distance = 0;
                    double weightless = 0;
                    double weight = 0;
                    SpriteDetails sprite = ((IItemBlockColourSaver) block.asItem()).getSpriteDetails(j);
                    for (int i = 0; i < sprite.colourinfo.size(); i++){
                        ColourVector colour = sprite.colourinfo.get(i);
                        if(colour == null) return;
                        weightless = query.distance(colour)+0.000001;
                        weight = Double.valueOf(sprite.weights.get(i));
                        if(weightless/Math.pow(weight,5) < distance || i == 0){  
                            distance = weightless/Math.pow(weight,5);
                        }
                    }

                    if(distance < ((IItemBlockColourSaver) block.asItem()).getScore() || j == 0){
                        ((IItemBlockColourSaver) block.asItem()).setScore(distance);
                    }
                }
                if(block.asItem() != null && ((IItemBlockColourSaver) block.asItem()).getLength() > 0 && block.isEnabled(client.world.getEnabledFeatures())){
                    stacks.add(new ItemStack(block)); 
                }  
                                                
            }catch(Exception e){
                return;
            }   
        });

        scrollBar.setMaxValue(stacks.size()/width+width);

        //sort list
        Collections.sort(stacks, new Comparator<ItemStack>(){
            @Override
            public int compare(ItemStack is1, ItemStack is2) {
                double x = ((IItemBlockColourSaver)is1.getItem()).getScore();
                double y = ((IItemBlockColourSaver)is2.getItem()).getScore();
                return Double.compare(x,y);
            }
        });
        PlaceSlots();
    }

    public void PlaceSlots(){
        wColourGuiSlots.forEach(slot -> {
        root.remove(slot);
        });
        int index = width*scrollBar.getValue();
        for(int j=1; j<height; j++) {
            for(int i=0; i<width; i++) {
                if(index >= stacks.size()) break;
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
