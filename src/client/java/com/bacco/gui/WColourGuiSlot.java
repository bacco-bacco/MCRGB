package com.bacco.gui;

import com.bacco.IItemBlockColourSaver;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.BufferedWriter;
import java.util.ArrayList;


public class WColourGuiSlot extends WWidget{
	public static final Identifier SLOT_TEXTURE = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/item_slot.png");
   ClientPlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
   ItemStack stack;
   public WColourGuiSlot(ItemStack stack){
      this.stack = stack;
   }

   @Override
	public void paint(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ItemRenderer renderer = mc.getItemRenderer();
      renderer.renderInGui(stack, x+1, y+1);
      ScreenDrawing.texturedRect(matrixStack, x, y, 18, 18, SLOT_TEXTURE, 0, 0, .28125f, .28125f, 0xFFFFFFFF);
   }

   @Override
   public InputResult onClick(int x, int y, int button) {
      // x & y are the coordinates of the mouse when the event was triggered
      // int button is which button was pressed
      if(!player.hasPermissionLevel(2) || !player.isCreative()) return InputResult.PROCESSED;
      String nbt = stack.getOrCreateNbt().toString();
      switch (button){
         case 0:
            player.sendCommand("give @s " + Registry.ITEM.getId(stack.getItem()).toString()+nbt);
            break;
         case 1:
            player.sendCommand("give @s " + Registry.ITEM.getId(stack.getItem()).toString()+nbt);
            break;
         case 2:
            player.sendCommand("give @s " + Registry.ITEM.getId(stack.getItem()).toString()+nbt + " " + stack.getMaxCount());
            break;
      }
      return InputResult.PROCESSED;
    }

   @Environment(EnvType.CLIENT)
   @Override
   public void addTooltip(TooltipBuilder tooltip) {
      tooltip.add(Text.translatable(stack.getTranslationKey()));
      IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			for(int i = 0; i < item.getLength(); i++){
				ArrayList<String> strings = item.getSpriteDetails(i).getStrings();
					ArrayList<Integer> colours = item.getSpriteDetails(i).getTextColours();
					if(strings.size() > 0){
                  for(int j = 0; j < strings.size(); j++){
                     var text = Text.literal(strings.get(j)).formatted(Formatting.GRAY);
                     MutableText text2 = (MutableText) Text.literal("⬛").getWithStyle(Style.EMPTY.withColor(colours.get(j))).get(0);
                     if(j > 0){
                        text2.append(text);
                     }else{
                        text2 = text.formatted(Formatting.DARK_GRAY);
                     }
                     
                     tooltip.add(text2);
                     }
			         }
               }
         }
   }
