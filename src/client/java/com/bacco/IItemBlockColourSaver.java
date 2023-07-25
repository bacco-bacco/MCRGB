package com.bacco;
//Definition of IItemBlockColourSaver class. See BlockColourSaver for implementation.
public interface IItemBlockColourSaver {
    String getColour(int i);
    void addColour(String name);
    int getLength();
}
