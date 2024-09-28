package com.bacco.gui;


import com.bacco.*;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.*;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.BiConsumer;

public class ColourGui extends LightweightGuiDescription {
    ColourGui cg = this;
    static int slotsHeight = 7;
    static int slotsWidth = 9;
    ColourVector inputColour = new ColourVector(255,255,255);
    /*int r = 255;
    int g = 255; 
    int b = 255;*/
    //String hex = "#FFFFFF";

    WGridPanel root = new WGridPanel();
    WGridPanel mainPanel = new WGridPanel();

    WLabel label = new WLabel(Text.translatable("ui.mcrgb.header"));

    public WBlockInfoBox infoBox;

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
    Identifier refreshIdentifier = Identifier.of("mcrgb", "refresh.png");
    TextureIcon refreshIcon = new TextureIcon(refreshIdentifier);
    WButton refreshButton = new WButton(refreshIcon){
        @Environment(EnvType.CLIENT)
        @Override
        public void addTooltip(TooltipBuilder tooltip) {
            tooltip.add(Text.translatable("ui.mcrgb.refresh_info"));
            super.addTooltip(tooltip);
        }
    };
    Identifier settingsIdentifier = Identifier.of("mcrgb", "settings.png");
    TextureIcon settingsIcon = new TextureIcon(settingsIdentifier);
    WButton settingsButton = new WButton(settingsIcon);

    WButton rgbButton = new WButton(Text.translatable("ui.mcrgb.rgb"));
    WButton hsvButton = new WButton(Text.translatable("ui.mcrgb.hsv"));
    WButton hslButton = new WButton(Text.translatable("ui.mcrgb.hsl"));
    private ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    private ArrayList<WColourGuiSlot> wColourGuiSlots = new ArrayList<WColourGuiSlot>();
    net.minecraft.client.MinecraftClient client;
    MCRGBClient mcrgbClient;
    ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
    ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
    ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
    ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
    ItemStack horse = new ItemStack(Items.LEATHER_HORSE_ARMOR);
    ItemStack wolf = new ItemStack(Items.WOLF_ARMOR);
    WColourGuiSlot helmSlot = new WColourGuiSlot(helmet, cg);
    WColourGuiSlot chestSlot = new WColourGuiSlot(chestplate, cg);
    WColourGuiSlot legsSlot = new WColourGuiSlot(leggings, cg);
    WColourGuiSlot bootSlot = new WColourGuiSlot(boots, cg);
    WColourGuiSlot horseSlot = new WColourGuiSlot(horse, cg);
    WColourGuiSlot wolfSlot = new WColourGuiSlot(wolf, cg);
    WLabel savedColoursLabel = new WLabel(Text.translatable("ui.mcrgb.saved_colours"));
    Identifier colourIdentifier = Identifier.of("mcrgb", "square.png");

    ArrayList<WColourPreviewIcon> SavedColours = new ArrayList<>();

    Identifier savePaletteIdentifier = Identifier.of("mcrgb", "save.png");
    TextureIcon savePaletteIcon = new TextureIcon(savePaletteIdentifier);
    WButton savePaletteButton = new WButton(savePaletteIcon);
    WListPanel<Palette,WPaletteWidget> paletteList;
    BiConsumer<Palette,WPaletteWidget> configurator = (Palette p, WPaletteWidget pwig) -> {
        pwig.buildPaletteWidget(cg);
        pwig.setIndex(p.getIndex());
        for(int i = 0; i < pwig.SavedColours.size(); i++) {
            String hex = p.getColour(i).getHex().replace("#","");
            int c = Integer.parseInt(hex,16);
            pwig.SavedColours.get(i).setColour(c);
        }
    };
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
        root.add(mainPanel, 0,0);
        mainPanel.setSize(320, 220);
        mainPanel.setInsets(Insets.ROOT_PANEL);
        mainPanel.add(hexInput, 11, 1, 5, 1);
        mainPanel.add(scrollBar,9,1,1,slotsHeight-1);
        mainPanel.add(refreshButton,17,11,1,1);
        refreshButton.setSize(20,20);
        refreshButton.setIconSize(18);
        refreshButton.setAlignment(HorizontalAlignment.LEFT);

        mainPanel.add(settingsButton,17,0,1,1);
        settingsButton.setSize(20,20);
        settingsButton.setIconSize(18);
        settingsButton.setAlignment(HorizontalAlignment.LEFT);

        mainPanel.add(rgbButton,10,11,1,1);
        rgbButton.setLocation(201,205);
        rgbButton.setSize(26,20);
        rgbButton.setEnabled(false);
        rgbButton.setAlignment(HorizontalAlignment.CENTER);
        mainPanel.add(hsvButton,13,11,1,1);
        hsvButton.setLocation(237,205);
        hsvButton.setSize(26,20);
        hsvButton.setAlignment(HorizontalAlignment.CENTER);
        mainPanel.add(hslButton,15,11,1,1);
        hslButton.setLocation(273,205);
        hslButton.setSize(26,20);
        hslButton.setAlignment(HorizontalAlignment.CENTER);

        mainPanel.add(label, 0, 0, 2, 1);
        mainPanel.add(savedColoursLabel, 0,slotsHeight,2,1);
        savedColoursLabel.setVerticalAlignment(VerticalAlignment.BOTTOM);

        for(int i = 0; i < slotsWidth; i++) {
            SavedColours.add(new WColourPreviewIcon(colourIdentifier,cg));

            mainPanel.add(SavedColours.get(i), i, slotsHeight + 1, 1, 1);
        }

        mainPanel.add(savePaletteButton,slotsWidth,slotsHeight+1,1,1);
        savePaletteButton.setSize(20,20);
        savePaletteButton.setIconSize(18);
        savePaletteButton.setAlignment(HorizontalAlignment.LEFT);

        paletteList = new WListPanel<>(mcrgbClient.palettes, WPaletteWidget::new, configurator);

        paletteList.setBackgroundPainter(BackgroundPainter.createColorful(0x999999));
        paletteList.setListItemHeight(19);
        mainPanel.add(paletteList,0,slotsHeight+2, 10, 3);
        paletteList.setLocation(9,(int)(18*(slotsHeight+2.7)));
        paletteList.setSize(10*18,(int)(2.8f*18));

        mainPanel.add(labels, 11,2,6,1);

        labels.add(rLabel, 6, 7, 1, 1);
        labels.add(gLabel, 42, 7, 1, 1);
        labels.add(bLabel, 78, 7, 1, 1);

        mainPanel.add(rSlider, 11, 3, 1, 6);
        rSlider.setValue(inputColour.r);
        mainPanel.add(gSlider, 13, 3, 1, 6);
        gSlider.setValue(inputColour.g);
        mainPanel.add(bSlider, 15, 3, 1, 6);
        bSlider.setValue(inputColour.b);

        mainPanel.add(inputs,10,9,2,1);
        inputs.add(rInput,14,9,26,1);
        inputs.add(gInput,50,9,26,1);
        inputs.add(bInput,86,9,26,1);

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

        savePaletteButton.setOnClick(() -> {SavePalette();});

        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            settingsButton.setOnClick(() -> {
                MinecraftClient.getInstance().setScreen(ClothConfigIntegration.getConfigScreen(client.currentScreen));
            });
        }else{
            settingsButton.setOnClick(() -> {
                client.player.sendMessage(Text.translatable("warning.mcrgb.noclothconfig"), false);
            });
        }
        UpdateArmour();
        
        mainPanel.add(helmSlot, 17, 3);
        mainPanel.add(chestSlot, 17, 4);
        mainPanel.add(legsSlot, 17, 5);
        mainPanel.add(bootSlot, 17, 6);
        mainPanel.add(horseSlot, 17, 7);
        mainPanel.add(wolfSlot, 17, 8);


        mainPanel.validate(this);
    }

    public  void SetColourMode(ColourMode cm){
        enableSliderListeners = false;
        mode = cm;
        ColourVector colour = new ColourVector(inputColour.getHex());

        switch (mode){
            case RGB:
                rLabel.setText(Text.translatable("ui.mcrgb.r_for_red"));
                rLabel.setColor(0xFF0000);
                gLabel.setText(Text.translatable("ui.mcrgb.g_for_green"));
                gLabel.setColor(0x00FF00);
                bLabel.setText(Text.translatable("ui.mcrgb.b_for_blue"));
                bLabel.setColor(0x0000FF);

                rSlider.setMinValue(0);
                gSlider.setMinValue(0);
                bSlider.setMinValue(0);
                rSlider.setMaxValue(255);
                gSlider.setMaxValue(255);
                bSlider.setMaxValue(255);
                rgbButton.setEnabled(false);
                hsvButton.setEnabled(true);
                hslButton.setEnabled(true);
                break;
            case HSV:
                rLabel.setText(Text.translatable("ui.mcrgb.h_for_hue_hsv"));
                rLabel.setColor(0x3F3F3F);
                gLabel.setText(Text.translatable("ui.mcrgb.s_for_sat_hsv"));
                gLabel.setColor(0x3F3F3F);
                bLabel.setText(Text.translatable("ui.mcrgb.v_for_val_hsv"));
                bLabel.setColor(0x3F3F3F);
                rSlider.setMinValue(0);
                gSlider.setMinValue(0);
                bSlider.setMinValue(0);
                rSlider.setMaxValue(360);
                gSlider.setMaxValue(100);
                bSlider.setMaxValue(100);
                rgbButton.setEnabled(true);
                hsvButton.setEnabled(false);
                hslButton.setEnabled(true);
                break;
            case HSL:
                rLabel.setText(Text.translatable("ui.mcrgb.h_for_hue_hsl"));
                rLabel.setColor(0x3F3F3F);
                gLabel.setText(Text.translatable("ui.mcrgb.s_for_sat_hsl"));
                gLabel.setColor(0x3F3F3F);
                bLabel.setText(Text.translatable("ui.mcrgb.l_for_lit_hsl"));
                bLabel.setColor(0x3F3F3F);
                rSlider.setMinValue(0);
                gSlider.setMinValue(0);
                bSlider.setMinValue(0);
                rSlider.setMaxValue(360);
                gSlider.setMaxValue(100);
                bSlider.setMaxValue(100);
                rgbButton.setEnabled(true);
                hsvButton.setEnabled(true);
                hslButton.setEnabled(false);
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
        int hexint = GetColour();
        DyedColorComponent dyedColorComponent = new DyedColorComponent(hexint,true);
        helmet.set(DataComponentTypes.DYED_COLOR,dyedColorComponent);
        chestplate.set(DataComponentTypes.DYED_COLOR,dyedColorComponent);
        leggings.set(DataComponentTypes.DYED_COLOR,dyedColorComponent);
        boots.set(DataComponentTypes.DYED_COLOR,dyedColorComponent);
        horse.set(DataComponentTypes.DYED_COLOR,dyedColorComponent);
        wolf.set(DataComponentTypes.DYED_COLOR,dyedColorComponent);
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

        scrollBar.setMaxValue(stacks.size()/ slotsWidth + slotsWidth);

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
        mainPanel.remove(slot);
        });
        int index = slotsWidth *scrollBar.getValue();
        for(int j = 1; j< slotsHeight; j++) {
            for(int i = 0; i< slotsWidth; i++) {
                if(index >= stacks.size()) break;
                WColourGuiSlot colourGuiSlot = new WColourGuiSlot(stacks.get(index), cg);
                
                if(wColourGuiSlots.size() <= index){
                wColourGuiSlots.add(colourGuiSlot);
                }else{
                wColourGuiSlots.set(index, colourGuiSlot);
                }
                mainPanel.add(colourGuiSlot, i, j);
                index ++;
                
            }
        }
        mainPanel.validate(this);
    }

    int GetColour(){
        String hex = inputColour.getHex().replace("#","");
        return Integer.parseInt(hex,16);
    }

    void SetColour(ColourVector colour){
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
        hexInput.setText(inputColour.getHex());
        UpdateArmour();
        ColourSort();
        scrollBar.setValue(0);
        PlaceSlots();
    }

    Palette CreatePalette(){
        Palette newPallet = new Palette();
        for(int i = 0; i < SavedColours.size(); i++){
            newPallet.addColour(new ColourVector(SavedColours.get(i).colour));
        }
        newPallet.setIndex(mcrgbClient.palettes.size());
        return  newPallet;
    }

    void SavePalette(){
        mcrgbClient.palettes.add(CreatePalette());
        mcrgbClient.SavePalettes();
        mainPanel.validate(this);
    }

    public void DeletePalette(int i){
        mcrgbClient.palettes.remove(i);
        mcrgbClient.palettes.forEach(palette -> {
            palette.setIndex(mcrgbClient.palettes.indexOf(palette));
            System.out.println(palette.getIndex());
        });
        paletteList.layout();
        mainPanel.validate(this);
    }

}
