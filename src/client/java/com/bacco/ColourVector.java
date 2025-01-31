package com.bacco;

import io.github.cottonmc.cotton.gui.widget.data.Color.RGB;

import java.awt.*;

public class ColourVector {
    public int r, g, b;

    public ColourVector(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColourVector(String hex){
        hex = "#" + hex.replace("#","");
        Color c = Color.decode(hex);
        this.r = c.getRed();
        this.g = c.getGreen();
        this.b = c.getBlue();
    }

    public ColourVector(int colour){
        String hex = String.format("#%06X", (0xFFFFFF & colour));
        Color c = Color.decode(hex);
        this.r = c.getRed();
        this.g = c.getGreen();
        this.b = c.getBlue();
    }

    public void fromHSV(int hue, int sat, int val){
        float s = sat/100f;
        float v = val/100f;
        float h = hue/60f;
        float chroma = v*s;
        float X = chroma * (1-Math.abs((h%2)-1));
        float r=0,g=0,b=0;
        if(h<1){
            r=chroma;   g=X;
        }else
        if(h<2){
            r=X;   g=chroma;
        }else
        if(h<3){
            g=chroma;   b=X;
        }else
        if(h<4){
            g=X;   b=chroma;
        }else
        if(h<5){
            r=X;   b=chroma;
        }else
        if(h<=6){
            r=chroma;  b=X;
        }
        float m = v-chroma;
        r+=m;g+=m;b+=m;
        this.r = Math.round(r*255);
        this.g = Math.round(g*255);
        this.b = Math.round(b*255);
    }

    public void fromHSL(int hue, int sat, int light){
        float s = sat/100f;
        float l = light/100f;
        float h = hue/60f;
        float chroma = (1-Math.abs(2*l-1))*s;
        float X = chroma * (1-Math.abs((h%2)-1));
        float r=0,g=0,b=0;
        if(h<1){
            r=chroma;   g=X;
        }else
        if(h<2){
            r=X;   g=chroma;
        }else
        if(h<3){
            g=chroma;   b=X;
        }else
        if(h<4){
            g=X;   b=chroma;
        }else
        if(h<5){
            r=X;   b=chroma;
        }else
        if(h<=6){
            r=chroma;  b=X;
        }
        float m = l-chroma/2;
        r+=m;g+=m;b+=m;
        this.r = Math.round(r*255);
        this.g = Math.round(g*255);
        this.b = Math.round(b*255);
    }

    public String getHex(){
        String hexR = r < 0x10 ? "0" + Integer.toHexString(r) : Integer.toHexString(r);
        String hexG = g < 0x10 ? "0" + Integer.toHexString(g) : Integer.toHexString(g);
        String hexB = b < 0x10 ? "0" + Integer.toHexString(b) : Integer.toHexString(b);
        return ("#" + hexR + hexG + hexB).toUpperCase();
    }

    public int getHue(){
        float max = Math.max(Math.max(this.r,this.g),this.b);
        float min = Math.min(Math.min(this.r,this.g),this.b);
        float chroma = max-min;
        float h;

        if(chroma==0){h = 0;}
        else if(max==this.r){ h = (((this.g-this.b)/chroma) % 6);}
        else if(max==this.g){ h = (((this.b-this.r)/chroma) + 2);}
        else if(max==this.b){ h = (((this.r-this.g)/chroma) + 4);}
        else h = 0;
        h = h*60;
        if(h < 0) h+=360;

        return Math.round(h);
    }

    public int getSatV(){
        return Math.round(new RGB(this.r,this.g,this.b).getHSVSaturation()*100f);
    }

    public int getVal(){
        return Math.round(100f*(new RGB(this.r,this.g,this.b).getValue()/255f));
    }

    public int getVal255(){
        return new RGB(this.r,this.g,this.b).getValue();
    }

    public int getSatL(){
        float r = this.r/255f;
        float g = this.g/255f;
        float b = this.b/255f;
        float max = Math.max(r,Math.max(g,b));
        float min = Math.min(r,Math.min(g,b));
        float c = max - min;
        float s = c/(1-Math.abs(2*max-c-1));
        return Math.round(s*100f);
    }
    public int getLight(){return Math.round(100f*(new RGB(this.r,this.g,this.b).getLightness()/255f));}

    public int asInt() {
        // Ensure each component is within the 0-255 range
        this.r = Math.max(0, Math.min(255, this.r));
        this.g = Math.max(0, Math.min(255, this.g));
        this.b = Math.max(0, Math.min(255, this.b));
        return (255 << 24) | (this.r << 16) | (this.g << 8) | this.b;
    }

    public double distance(ColourVector otherVector){
        return Math.sqrt(Math.pow((this.r - otherVector.r),2) + Math.pow((this.g - otherVector.g),2) + Math.pow((this.b - otherVector.b),2));
    }

    public ColourVector add(ColourVector otherVector){
        this.r += otherVector.r;
        this.g += otherVector.g;
        this.b += otherVector.b;

        return this;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        ColourVector other = (ColourVector) obj;
        if(this.r != other.r || this.g != other.g || this.b != other.b) return false;
        return true;
    }

    public ColourVector div(int i){
        this.r = this.r/i;
        this.g = this.g/i;
        this.b = this.b/i;

        return this;
    }
}
