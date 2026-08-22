package com.bacco.gui;

import com.bacco.ColourVector;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class WPickableTexture extends WSprite {

    float texU1;
    float texV1;
    float texU2;
    float texV2;
    int atlasWidth;
    int atlasHeight;

    int tint = 0xFFFFFF;
    int pixelColour = 0xFFFFFFFF;

    Boolean isTransparent = false;

    MCRGBBaseGui gui;

    GlTexture gpuTexture;
    int glID;
    public WPickableTexture(Identifier image, float u1, float v1, float u2, float v2, net.minecraft.client.Minecraft client, MCRGBBaseGui gui) {
        super(image, u1, v1, u2, v2);
        gpuTexture = (GlTexture) client.getTextureManager().getTexture(image).getTexture();
        glID = gpuTexture.glId();

        //get width and height from OpenGL by binding texture
        //RenderSystem.bindTexture(glID);
        atlasWidth = gpuTexture.getWidth(0);
        atlasHeight = gpuTexture.getHeight(0);
        //atlasWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        //atlasHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        texU1 = u1*atlasWidth;
        texV1 = v1*atlasHeight;
        texU2 = u2*atlasWidth;
        texV2 = v2*atlasHeight;
        this.gui = gui;
    }

    @Override
    public InputResult onClick(MouseButtonEvent click, boolean doubled) {
        isTransparent = pickColour((int) click.x(), (int) click.y(), click.button());
        return super.onClick(click, doubled);
    }

    @Override
    public InputResult onMouseDrag(MouseButtonEvent click, double deltaX, double deltaY){
        isTransparent = pickColour((int) click.x(), (int) click.y(), click.button());
        return super.onMouseDrag(click, deltaX, deltaY);
    }
    @Override
    public WSprite setOpaqueTint(int tint){
        this.tint = tint;
        return super.setOpaqueTint(tint);
    }

    public Boolean pickColour(int x, int y, int clickButton){

        if(x < 0 || y < 0 || x >= width || y >= height) return false;

        double trueX = texU1+ Math.floor(((float) x / width)*(texU2-texU1));
        double trueY = texV1+ Math.floor(((float) y / height)*(texV2-texV1));
        int size = atlasHeight*atlasWidth;

        GlStateManager._bindTexture(glID);
        //RenderSystem.bindTexture(glID);
        //Make byte buffer and load full atlas into buffer.
        ByteBuffer buffer = BufferUtils.createByteBuffer(size*4);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        //convert buffer to an array of bytes
        byte[] pixels = new byte[size*4];
        buffer.get(pixels);

        int pos = (((int)trueY*atlasWidth + (int)trueX)*4);

        if(pixels[pos+3] == 0){
            //return if fully transparent pixel
            return true;
        }

        int tempColour = ARGB.color(pixels[pos+3], pixels[pos] & 0xFF, pixels[pos+1] & 0xFF, pixels[pos+2] & 0xFF);
        tempColour = ARGB.multiply(tempColour,tint);

        if(pixelColour == tempColour){  return false;   } else pixelColour = tempColour;

        ColourVector pixelColourVector = new ColourVector(pixelColour);

        switch (clickButton){
            case 0:
                gui.SetColour(pixelColourVector);
                break;
            case 1:
                gui.SetColour(pixelColourVector);
                break;
            case 2:
                Minecraft.getInstance().keyboardHandler.setClipboard(pixelColourVector.getHex());
                SystemToast clipboardToast = new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable("toast.mcrgb.generic_toast_title"), Component.translatable("toast.mcrgb.copied_hex_to_clipboard").append(Component.literal("⬛").toFlatList(Style.EMPTY.withColor(pixelColour)).get(0)).append(pixelColourVector.getHex()));
                Minecraft.getInstance().gui.toastManager().addToast(clipboardToast);
                break;
        }
        return false;
    }


    @Override
    public WSprite setUv(float u1, float v1, float u2, float v2){
        texU1 = u1*atlasWidth;
        texV1 = v1*atlasHeight;
        texU2 = u2*atlasWidth;
        texV2 = v2*atlasHeight;
        return super.setUv(u1, v1, u2, v2);
    }

    @Override
    public void paint(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
    }
}
