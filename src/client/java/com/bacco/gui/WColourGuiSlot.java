package com.bacco.gui;

import com.bacco.IItemBlockColourSaver;
import com.bacco.MCRGBConfig;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;


public class WColourGuiSlot extends WWidget{
	public static final Identifier SLOT_TEXTURE = Identifier.fromNamespaceAndPath(LibGuiCommon.MOD_ID, "textures/widget/item_slot.png");
   LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
   ItemStack stack;


   ColourGui gui;
   int hotbarSlot = -1;
   public WColourGuiSlot(ItemStack stack, ColourGui gui){
      super();
      this.stack = stack;
      this.gui = gui;
      this.hotbarSlot = -1;
   }

   public WColourGuiSlot(ItemStack stack, ColourGui gui, int hotbarSlot){
      super();
      this.stack = stack;
      this.gui = gui;
      this.hotbarSlot = hotbarSlot;
   }

   @Override
   public void paint(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY) {
      ScreenDrawing.texturedRect(context, x, y, 18, 18, SLOT_TEXTURE, 0, 0, .28125f, .28125f, 0xFFFFFFFF);
      if (stack != null){
         context.item(stack, x + 1, y + 1);
         context.itemDecorations(Minecraft.getInstance().font, stack, x+1, y+1);
      }
   }

   @Override
   public InputResult onClick(MouseButtonEvent click, boolean doubled) {
      // x & y are the coordinates of the mouse when the event was triggered
      // int button is which button was pressed
      String nbt = "";
      if(stack.has(DataComponents.DYED_COLOR)) {
         nbt = "dyed_color=" + String.valueOf(stack.get(DataComponents.DYED_COLOR).rgb()) ;//stack.getOrCreateNbt().toString();

      }
      String command = MCRGBConfig.instance.command;

      command = command.replace("%c",nbt);
      switch (click.button()){
         case 0:
            switch(MCRGBConfig.instance.creativeGive){
               case CREATIVE_DRAG:
               if(player.isCreative()){
               if(gui.cursorStack == ItemStack.EMPTY){
                  ItemStack stack2 = stack.copy();
                  gui.cursorStack = stack2;
                  if(hotbarSlot >= 0){
                     stack = ItemStack.EMPTY;
                     player.getInventory().setItem(hotbarSlot, stack);
                     Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(stack,hotbarSlot+36);
                  }
               }else{
                  if(hotbarSlot >= 0){
                     ItemStack stack2 = stack.copy();

                     stack = gui.cursorStack.copy();
                     player.getInventory().setItem(hotbarSlot, stack);
                     Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(stack,hotbarSlot+36);

                     gui.cursorStack = stack2;
                  }else{
                     gui.cursorStack = ItemStack.EMPTY;
                  }
               }
               }else{
                  gui.cursorStack = ItemStack.EMPTY;
               }
               break;
               case GIVE_COMMAND:
               if(!((player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.ALL)) && player.isCreative()) || MCRGBConfig.instance.bypassOP)) return InputResult.PROCESSED;
               command = command.replace("%p",player.getName().getString());
               command = command.replace("%i", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
               command = command.replace("%q","1");
               player.connection.sendCommand(command);
               break;
            }
            break;
         case 1:
            IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
            if(item.getLength() <= 0) break;
            /*ArrayList<ColourVector> colours = item.getSpriteDetails(0).colourinfo;
            ColourVector colour = colours.get(0);
            gui.SetColour(colour);*/
            gui.infoBox = new WBlockInfoBox(Axis.VERTICAL,item,gui);

            gui.mainPanel.add(this.gui.infoBox,19,0);
            gui.mainPanel.validate(gui);
            gui.PlaceSlots();
            if(stack.getItem() instanceof BlockItem)
               gui.OpenBlockInfoGui(gui.client, gui.mcrgbClient, stack);
            break;
         case 2:
            switch(MCRGBConfig.instance.creativeGive){
               case CREATIVE_DRAG:

               if(player.isCreative()){
                  if(gui.cursorStack == ItemStack.EMPTY){
                     ItemStack stack2 = stack.copy();
                     stack2.setCount(stack2.getMaxStackSize());
                     gui.cursorStack = stack2;
                     if(hotbarSlot >= 0){
                        stack = ItemStack.EMPTY;
                        player.getInventory().setItem(hotbarSlot, stack);
                        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(stack,hotbarSlot+36);
                     }
                  }else{
                     if(hotbarSlot >= 0){
                        ItemStack stack2 = stack.copy();

                        stack = gui.cursorStack.copy();
                        player.getInventory().setItem(hotbarSlot, stack);
                        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(stack,hotbarSlot+36);

                        gui.cursorStack = stack2;
                     }else{
                        gui.cursorStack = ItemStack.EMPTY;
                     }
                  }
               }else{
                  gui.cursorStack = ItemStack.EMPTY;
               }
               break;
               case GIVE_COMMAND:
               if(!(player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.ALL)) || MCRGBConfig.instance.bypassOP)) return InputResult.PROCESSED;
               command = command.replace("%p",player.getName().getString());
               command = command.replace("%i", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
               command = command.replace("%q",Integer.toString(stack.getMaxStackSize()));
               player.connection.sendCommand(command);
               break;
            }
            break;
      }
      return InputResult.PROCESSED;
    }

   @Environment(EnvType.CLIENT)
   @Override
   public void addTooltip(TooltipBuilder tooltip) {
      int numLines = 0;
      if(stack.isEmpty()) return;
      tooltip.add(stack.getItemName());
      IItemBlockColourSaver item = (IItemBlockColourSaver) stack.getItem();
			for(int i = 0; i < item.getLength(); i++){
                if(numLines >= MCRGBConfig.instance.maxTooltipLines){
                    tooltip.add(Component.literal(" "));
                    tooltip.add(Component.translatable("tooltip.mcrgb.show_more").withStyle(ChatFormatting.GRAY));
                    break;
                }
				ArrayList<String> strings = item.getSpriteDetails(i).getStrings();
					ArrayList<Integer> colours = item.getSpriteDetails(i).getTextColours();
					if(strings.size() > 0){
                  for(int j = 0; j < strings.size(); j++){
                     var text = Component.literal(strings.get(j)).withStyle(ChatFormatting.GRAY);
                     MutableComponent text2 = (MutableComponent) Component.literal("⬛").toFlatList(Style.EMPTY.withColor(colours.get(j))).get(0);
                     if(j > 0){
                        text2.append(text);
                     }else{
                        text2 = text.withStyle(ChatFormatting.DARK_GRAY);
                     }
                     tooltip.add(text2);
                     numLines ++;
                     }
			         }
               }
         }
   }
