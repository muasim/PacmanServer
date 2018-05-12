package edu.akdeniz.pacmanserver.match;

import java.awt.Rectangle;

public class SteelTrap
{
    private Rectangle boundary;
    private Player owner;
    public SteelTrap(int xPos , int yPos , Player owner)
    {
        this.boundary = new Rectangle(xPos, yPos, Tile.SIZE / 2, Tile.SIZE / 2);
        this.owner = owner;
    }
    public boolean intersects(Rectangle targetRectangle)
    {
        return this.boundary.intersects(targetRectangle);
    }
    public Player getOwner() 
    {
		return this.owner;
	}
}