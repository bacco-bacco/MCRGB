package com.bacco.gui;
import java.util.ArrayList;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.brigadier.ParseResults;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WItem;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class WColourGuiSlot extends WWidget{
	public static final Identifier SLOT_TEXTURE = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/item_slot.png");
   ClientPlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
   ItemStack stack;
   public WColourGuiSlot(ItemStack stack){
      this.stack = stack;
   }

   @Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
      context.drawItem(stack, x+1, y+1);
      //context.drawTexture(SLOT_TEXTURE, x,y, 0, 0,0, 18, 18, 128, 128);
   }

   @Override
    public InputResult onClick(int x, int y, int button) {
        // x & y are the coordinates of the mouse when the event was triggered
        // int button is which button was pressed
         if(!player.hasPermissionLevel(2)) return InputResult.PROCESSED;
        switch (button){
         case 0:
         player.networkHandler.sendCommand("give @s " + stack.getItem().toString());
         break;
         case 1:
         player.networkHandler.sendCommand("give @s " + stack.getItem().toString());
         break;
         case 2:
         player.networkHandler.sendCommand("give @s " + stack.getItem().toString() + " 64");
         break;
        }
        return InputResult.PROCESSED;
    }

}
