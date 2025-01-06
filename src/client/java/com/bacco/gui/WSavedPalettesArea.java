package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.MCRGBClient;
import com.bacco.Palette;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class WSavedPalettesArea extends WPlainPanel {
    WLabel savedColoursLabel = new WLabel(Text.translatable("ui.mcrgb.saved_colours"));
    Identifier colourIdentifier = Identifier.of("mcrgb", "square.png");

    ArrayList<WColourPreviewIcon> SavedColours = new ArrayList<>();

    Identifier savePaletteIdentifier = Identifier.of("mcrgb", "save.png");
    TextureIcon savePaletteIcon = new TextureIcon(savePaletteIdentifier);
    WButton savePaletteButton = new WButton(savePaletteIcon);

    WPaletteWidget editingPalette = null;
    WListPanel<Palette,WPaletteWidget> paletteList;

    MCRGBBaseGui cg;
    MCRGBClient mcrgbClient;

    WSavedPalettesArea(MCRGBBaseGui gui, int slotsWidth, int slotsHeight, MCRGBClient mcrgbClient){
        this.cg = gui;
        this.mcrgbClient = mcrgbClient;
        BiConsumer<Palette,WPaletteWidget> configurator = (Palette p, WPaletteWidget pwig) -> {
            pwig.cg = cg;
            pwig.palette = p;
            pwig.buildPaletteWidget(cg);
            for(int i = 0; i < pwig.SavedColours.size(); i++) {
                String hex = p.getColour(i).getHex().replace("#","");
                int c = Integer.parseInt(hex,16);
                pwig.SavedColours.get(i).setColour(c);
            }
        };


        this.add(savedColoursLabel, 0,slotsHeight,2,1);
        savedColoursLabel.setVerticalAlignment(VerticalAlignment.BOTTOM);

        for(int i = 0; i < slotsWidth; i++) {
            SavedColours.add(new WColourPreviewIcon(colourIdentifier,cg));

            this.add(SavedColours.get(i), i*17, slotsHeight + 5, 18, 18);
        }

        this.add(savePaletteButton,slotsWidth*18,slotsHeight+1,20,20);
        savePaletteButton.setSize(20,20);
        savePaletteButton.setIconSize(18);
        savePaletteButton.setAlignment(HorizontalAlignment.LEFT);

        paletteList = new WListPanel<>(mcrgbClient.palettes, WPaletteWidget::new, configurator);

        paletteList.setBackgroundPainter(BackgroundPainter.createColorful(0x999999));
        paletteList.setListItemHeight(19);
        this.add(paletteList,0,2, 10, 3);
        paletteList.setLocation(0,36);
        paletteList.setSize(10*18,(int)(2.8f*18));

        savePaletteButton.setOnClick(() -> {SavePalette();});

    }

    Palette CreatePalette(){
        Palette newPallet = new Palette();
        for(int i = 0; i < SavedColours.size(); i++){
            newPallet.addColour(new ColourVector(SavedColours.get(i).colour));
        }
        return  newPallet;
    }

    WPaletteWidget UpdatePalette(WPaletteWidget updatingPalette){
        for(int i = 0; i < SavedColours.size(); i++){
            updatingPalette.SavedColours.get(i).setColour(SavedColours.get(i).colour);
            updatingPalette.palette.setColour(i,new ColourVector(SavedColours.get(i).colour));
        }
        return updatingPalette;
    }

    void SavePalette(){
        if(editingPalette == null) {
            mcrgbClient.palettes.add(CreatePalette());
        }else{
            UpdatePalette(editingPalette);
            editingPalette = null;
        }
        for(int i = 0; i < SavedColours.size(); i++){
            SavedColours.get(i).setColour(0xffffffff);
        }
        mcrgbClient.SavePalettes();
        cg.mainPanel.validate(cg);
    }

    public void DeletePalette(WPaletteWidget pwig){

        mcrgbClient.palettes.remove(pwig.palette);
        editingPalette = null;
        cg.root.validate(cg);
        mcrgbClient.SavePalettes();
    }

    public void EditPalette(WPaletteWidget pwig){
        if(editingPalette == pwig){
            editingPalette = null;
            for(int i = 0; i < SavedColours.size(); i++){
                SavedColours.get(i).setColour(0xffffffff);
            }
            return;
        }
        for(int i = 0; i < SavedColours.size(); i++){
            SavedColours.get(i).setColour(pwig.SavedColours.get(i).colour);
        }
        editingPalette = pwig;
    }
}
