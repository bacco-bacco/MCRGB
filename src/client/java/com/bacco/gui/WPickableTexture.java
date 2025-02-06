package com.bacco.gui;

import com.bacco.ColourVector;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
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

    int glID;
    public WPickableTexture(Identifier image, float u1, float v1, float u2, float v2, net.minecraft.client.MinecraftClient client, MCRGBBaseGui gui) {
        super(image, u1, v1, u2, v2);
        glID = client.getTextureManager().getTexture(image).getGlId();
        //get width and height from OpenGL by binding texture
        RenderSystem.bindTexture(glID);
        atlasWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        atlasHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        texU1 = u1*atlasWidth;
        texV1 = v1*atlasHeight;
        texU2 = u2*atlasWidth;
        texV2 = v2*atlasHeight;
        this.gui = gui;
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        isTransparent = pickColour(x,y);
        return super.onClick(x, y, button);
    }

    @Override
    public InputResult onMouseDrag(int x, int y, int button, double deltaX, double deltaY){
        isTransparent = pickColour(x,y);
        return super.onMouseDrag(x, y, button, deltaX, deltaY);
    }
    @Override
    public WSprite setOpaqueTint(int tint){
        this.tint = tint;
        return super.setOpaqueTint(tint);
    }

    public Boolean pickColour(int x, int y){
        if(x < 0 || y < 0 || x >= width || y >= height) return false;

        double trueX = texU1+ Math.floor(((float) x / width)*(texU2-texU1));
        double trueY = texV1+ Math.floor(((float) y / height)*(texV2-texV1));
        int size = atlasHeight*atlasWidth;

        RenderSystem.bindTexture(glID);
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

        pixelColour = ColorHelper.getArgb(pixels[pos+3], pixels[pos] & 0xFF, pixels[pos+1] & 0xFF, pixels[pos+2] & 0xFF);
        pixelColour = ColorHelper.mix(pixelColour,tint);
        gui.SetColour(new ColourVector(pixelColour));
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
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
    }
}
