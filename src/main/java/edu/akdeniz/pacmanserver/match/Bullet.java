package edu.akdeniz.pacmanserver.match;

import java.awt.Rectangle;

public class Bullet
{
    public static final byte NORMAL_BULLET = 0x01;
    public static final byte INCENDIARY_BULLET = 0x02;
    private static final int DEFAULT_SPEED = Player.DEFAULT_SPEED * 3;
    private byte direction;
    private final byte type;
    private final Rectangle boundary;

    public Bullet(int xPos , int yPos , byte direction , byte type)
    {
        this.boundary = new Rectangle(xPos, yPos, 32, 16);
        this.direction = direction;
        this.type = type;
    }
    public boolean intersects(Rectangle targetRectangle)
    {
        return this.boundary.intersects(targetRectangle);
    }
    public void ricochet() 
    {
        this.direction = this.direction > Direction.DOWN ? (this.direction == Direction.RIGHT ? Direction.RIGHT : Direction.LEFT) : (this.direction == Direction.UP ? Direction.UP : Direction.DOWN); 
	}
	public byte getType() {
		return this.type;
	}
}