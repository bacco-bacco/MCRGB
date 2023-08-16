package com.bacco.gui;
import java.util.ArrayList;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WItem;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;


public class WColourGuiSlot extends WWidget{
	public static final Identifier SLOT_TEXTURE = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/item_slot.png");

   ItemStack stack;
   public WColourGuiSlot(ItemStack stack){
      this.stack = stack;
   }

   @Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
      context.drawItem(stack, x+1, y+1);
      //context.drawTexture(SLOT_TEXTURE, x,y, 0, 0,0, 18, 18, 128, 128);
   }
}
