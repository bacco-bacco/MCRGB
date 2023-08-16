package com.bacco.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.bacco.IItemBlockColourSaver;
import com.bacco.event.KeyInputHandler;
import com.mojang.authlib.minecraft.client.MinecraftClient;

import net.fabricmc.api.Environment;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;

@Environment(value=EnvType.CLIENT)
public class ColourInventoryScreen extends CreativeInventoryScreen {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/hopper.png");
    private final net.minecraft.client.MinecraftClient client;
    private ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    //private ItemStack stack = new ItemStack(Blocks.STONE, 1);
    private static final Identifier STATS_ICONS_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");


    public ColourInventoryScreen(net.minecraft.client.MinecraftClient client) {
        //super(Text.literal("MCRGB"));
        super(client.player, client.player.networkHandler.getEnabledFeatures(), client.options.getOperatorItemsTab().getValue());
        this.client = client;
    }
    public void createButtons() {
        ButtonWidget buttonWidget = this.addDrawableChild(ButtonWidget.builder((Text)Text.translatable((String)"stat.generalButton"), button -> this.client.player.sendMessage(Text.literal("Test"))).dimensions(this.width / 2 - 120, this.height - 52, 80, 20).build());
        //ButtonWidget buttonWidget = this.addDrawableChild(ButtonWidget.builder((Text)Text.translatable((String)"stat.itemsButton"), button -> this.selectStatList(this.itemStats)).dimensions(this.width / 2 - 40, this.height - 52, 80, 20).build());
        //ButtonWidget buttonWidget2 = this.addDrawableChild(ButtonWidget.builder((Text)Text.translatable((String)"stat.mobsButton"), button -> this.selectStatList(this.mobStats)).dimensions(this.width / 2 + 40, this.height - 52, 80, 20).build());
        //this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
        /*if (this.itemStats.children().isEmpty()) {
            buttonWidget.active = false;
        }
        if (this.mobStats.children().isEmpty()) {
            buttonWidget2.active = false;
        }*/
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        PlayerScreenHandler screenHandler = this.client.player.playerScreenHandler;
        if(stacks.isEmpty()){
            stacks.clear();
            Registries.BLOCK.forEach(block -> {
                    if(block.asItem() != null && ((IItemBlockColourSaver) block.asItem()).getLength() > 0){
                    client.player.sendMessage(block.asItem().getName());
                    client.player.sendMessage(Text.literal(Integer.toString(((IItemBlockColourSaver) block.asItem()).getLength())));
                    stacks.add(new ItemStack(block)); 
                    }  
                    
            });
        }
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        Collections.sort(stacks, new Comparator<ItemStack>(){
            @Override
            public int compare(ItemStack is1, ItemStack is2) {
                double x = ((IItemBlockColourSaver)is1.getItem()).getScore();
                double y = ((IItemBlockColourSaver)is2.getItem()).getScore();
                return Double.compare(x,y);
            }
        });
        int x = 20, y = 20;
        for (int i = 0; i < stacks.size(); i++){
            ItemStack stack = stacks.get(i);
            context.drawItem(stack, x+1, y+1);
            context.drawTexture(STATS_ICONS_TEXTURE, x,y, 0, 0,0, 18, 18, 128, 128);
            if(x < this.width*0.99 -20){
                x += 20;
            }else{
                x = 20;
                y +=20;
            }
        } 
        
        //context.drawItemTooltip(textRenderer, stack, mouseX, mouseY);
        //context.drawItemInSlot(textRenderer, stack, 20, 20, null);
        super.render(context, mouseX, mouseY, delta);
         /*if (this.downloadingStats) {
            this.renderBackground(context);
            context.drawCenteredTextWithShadow(this.textRenderer, DOWNLOADING_STATS_TEXT, this.width / 2, this.height / 2, 0xFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer, PROGRESS_BAR_STAGES[(int)(Util.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)], this.width / 2, this.height / 2 + this.textRenderer.fontHeight * 2, 0xFFFFFF);
        } else {
            this.getSelectedStatList().render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }*/
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    
}
