package edu.akdeniz.pacmanserver.match;

import java.awt.Rectangle;
@SuppressWarnings ("all")
public abstract class Tile extends Rectangle
{
    public static final int SIZE = 128;
    public Tile(int xPos , int yPos)
    {
        super(xPos, yPos, Tile.SIZE, Tile.SIZE);
    }
}
