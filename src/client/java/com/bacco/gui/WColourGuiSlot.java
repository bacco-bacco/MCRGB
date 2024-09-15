package com.bacco.gui;

import com.bacco.ColourVector;
import com.bacco.IItemBlockColourSaver;
import com.bacco.MCRGBConfig;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;


public class WColourGuiSlot extends WWidget{
	public static final Identifier SLOT_TEXTURE = Identifier.of(LibGuiCommon.MOD_ID, "textures/widget/item_slot.png");
   ClientPlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
   ItemStack stack;

   ColourGui gui;
   public WColourGuiSlot(ItemStack stack, ColourGui gui){

      this.stack = stack;
      this.gui = gui;
   }

   @Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
      context.drawItem(stack, x+1, y+1);
      //context.drawTexture(SLOT_TEXTURE, x,y, 0, 0,0, 18, 18, 64, 64);
      ScreenDrawing.texturedRect(context, x, y, 18, 18, SLOT_TEXTURE, 0, 0, .28125f, .28125f, 0xFFFFFFFF);
      //context.drawTexture();
   }

   @Override
   public InputResult onClick(int x, int y, int button) {
      // x & y are the coordinates of the mouse when the event was triggered
      // int button is which button was pressed
      if(!((player.hasPermissionLevel(2) && player.isCreative()) || MCRGBConfig.instance.bypassOP)) return InputResult.PROCESSED;
      String command = MCRGBConfig.instance.command;
      command = command.replace("%p",player.getName().getString());
      command = command.replace("%i",Registries.ITEM.getId(stack.getItem()).toString());

      String nbt = "";
      if(stack.contains(DataComponentTypes.DYED_COLOR)) {
         nbt = "dyed_color=" + String.valueOf(stack.get(DataComponentTypes.DYED_COLOR).rgb()) ;//stack.getOrCreateNbt().toString();

      }
      command = command.replace("%c",nbt);
      switch (button){
         case 0:
            command = command.replace("%q","1");
            //player.networkHandler.sendCommand("give @s " + Registries.ITEM.getId(stack.getItem()).toString()+nbt);
            break;
         case 1:
            //player.networkHandler.sendCommand("give @s " + Registries.ITEM.getId(stack.getItem()).toString()+nbt);
            IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
            if(item.getLength() <= 0) break;
            ArrayList<ColourVector> colours = item.getSpriteDetails(0).colourinfo;
            ColourVector colour = colours.get(0);
            gui.SetColour(colour);
            break;
         case 2:
            command = command.replace("%q",Integer.toString(stack.getMaxCount()));
            //player.networkHandler.sendCommand("give @s " + Registries.ITEM.getId(stack.getItem()).toString()+nbt + " " + stack.getMaxCount());
            break;
      }
      player.networkHandler.sendCommand(command);
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
