package com.bacco;

public class ColourVector {
    public int r, g, b;

    public ColourVector(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
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
