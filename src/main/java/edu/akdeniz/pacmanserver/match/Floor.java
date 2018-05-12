package edu.akdeniz.pacmanserver.match;

import java.awt.Rectangle;

public class Floor extends Tile
{
    Rectangle itemBoundary;
    Type item;
    public Floor(int xPos , int yPos , Type item)
    {
        super(xPos, yPos);
        itemBoundary = new Rectangle(Tile.SIZE / 2 - 32 / 2 + xPos, Tile.SIZE / 2 - 32 / 2 + yPos, 32, 32);
        this.item = item;
    }

    public Type getType() 
    {
		return this.item;
	}

    public Rectangle getItemBoundary() 
    {
		return itemBoundary;
	}

    public void removeItem() 
    {
        item = Type.NONE;
	}
}