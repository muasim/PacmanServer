package edu.akdeniz.pacmanserver.match;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Player
{
    private Rectangle boundary;
    public static final int DEFAULT_SPEED = 30;
    private ArrayList<Skill> quickBar = new ArrayList<Skill>(3);
    private byte direction;
	private byte point;
    private byte heart;
    private byte health;
    private byte[] keyStates = new byte[6];
    private boolean skillHexaFire;
    private boolean skillIncendiary;
	private boolean skillSteelTrap;
    public Player(int xPos , int yPos)
    {
        this.boundary = new Rectangle(xPos, yPos, Tile.SIZE, Tile.SIZE);
    }
    
    public void setDirection(byte direction) { this.direction = direction; }
    public void move(Tile[][] tiles) 
    {
        byte directionX = this.keyStates[KeyCode.DIRECTION_X];
        byte directionY = this.keyStates[KeyCode.DIRECTION_Y];
        if(directionX != Direction.NONE || directionY != Direction.NONE) this.direction = directionX == Direction.NONE ? directionY : directionX;
        if(!this.isHexaFireActive())
        {
            int xPos = 0;
            int yPos = 0;
            int overlapX = 0; 
            int overlapY = 0;
            if(directionX != Direction.NONE)
            {
                if(directionX == Direction.LEFT)
                {
                    boundary.x -= DEFAULT_SPEED;
                }
                else
                {
                    boundary.x += DEFAULT_SPEED;
                }
                xPos = boundary.x / Tile.SIZE;
                yPos = boundary.y / Tile.SIZE;
                overlapX = boundary.x % Tile.SIZE;
                overlapY = boundary.y % Tile.SIZE;

                if(directionY != Direction.NONE && overlapY < Player.DEFAULT_SPEED) 
                { 
                    boundary.y -= overlapY;
                    overlapY = 0; 
                }
                if(directionX == Direction.RIGHT)
                {
                    if(tiles[yPos][xPos + 1] instanceof Wall || (overlapY != 0 && tiles[yPos + 1][xPos + 1] instanceof Wall))
                    {
                        boundary.x -= overlapX;
                    }
                    else if(overlapY == 0)this.direction = directionX;
                }   
                else if(directionX == Direction.LEFT)
                {
                    if(tiles[yPos][xPos] instanceof Wall || (overlapY != 0 && tiles[yPos + 1][xPos] instanceof Wall))
                    {
                        boundary.x += Tile.SIZE - overlapX;
                    }
                    else if(overlapY == 0)this.direction = directionX;
                }
                
            }
            if(directionY != Direction.NONE)
            {
                if(directionY == Direction.UP)
                {
                    boundary.y -= DEFAULT_SPEED;
                }
                else
                {
                    boundary.y += DEFAULT_SPEED;
                }
                xPos = boundary.x / Tile.SIZE;
                yPos = boundary.y / Tile.SIZE;
                overlapX = boundary.x % Tile.SIZE;
                overlapY = boundary.y % Tile.SIZE;

                if(directionX != Direction.NONE && overlapX < Player.DEFAULT_SPEED) 
                { 
                    boundary.x -= overlapX;
                    overlapX = 0; 
                }
                if(directionY == Direction.DOWN)
                {
                    if(tiles[yPos + 1][xPos] instanceof Wall || (overlapX != 0 && tiles[yPos + 1][xPos + 1] instanceof Wall))
                    {
                        boundary.y -= overlapY;
                    }
                    else if(overlapX == 0)this.direction = directionY;
                }
                else if(directionY == Direction.UP)
                {
                    if(tiles[yPos][xPos] instanceof Wall || (overlapX != 0 && tiles[yPos][xPos + 1] instanceof Wall))
                    {
                        boundary.y += Tile.SIZE -  overlapY;
                        // if(directionX != null)player.setDirection(directionX);
                    }
                    else if(overlapX == 0)this.direction = directionY;
                }
            }
            xPos = boundary.x / Tile.SIZE;
            yPos = boundary.y/ Tile.SIZE;
            Floor onFloor = (Floor)tiles[yPos][xPos];
            if(overlapX > Player.DEFAULT_SPEED && overlapX < Tile.SIZE / 2) { onFloor = (Floor)tiles[yPos][xPos + 1]; }
            else if(overlapY > Player.DEFAULT_SPEED && overlapY < Tile.SIZE / 2) onFloor = (Floor)tiles[yPos + 1][xPos];
            if(onFloor.getType() != Type.NONE && this.boundary.contains(onFloor.getItemBoundary()))
            {
                switch(onFloor.getType())
                {
                    case DOT:
                    {
                        onFloor.removeItem();
                        if(++this.point == 100)
                        {
                            this.heart += 1;
                            this.point = 0;
                        }
                    }
                    case PELLET:
                    {
                        onFloor.removeItem();
                        // TODO Turn Ghosts's Color
                        
                    }
                    case SKILL:
                    {
                        // TODO Skill Pickup
                    }
                    default:
                }
            }
        }
	}
	public void setX(int xPos) { boundary.x = xPos; }
    public void setY(int yPos) { boundary.y = yPos; }
    public int getX() 
    {
		return (int)this.boundary.getX();
	}
	public int getY() {
		return (int)this.boundary.getY();
	}
    public Skill getSkill(int index)
    {
		return quickBar.get(index);
	}
    public void removeSkill(int index) 
    {
        quickBar.remove(index);
    }
    public byte getKeyState(int index) 
    {
		return this.keyStates[index];
	}
    public void setKeyState(byte index, byte value) 
    {
        this.keyStates[index] = value;
    }
    public boolean isHexaFireActive() { return this.skillHexaFire; }
    public boolean isIncendiaryBulletActive() { return this.skillIncendiary; }

	public byte getDirection() {
		return 0;
	}

	public boolean isInvisibilityActive() {
		return false;
	}
	public void setInvisibility(boolean b) {
	}

	public Rectangle getBoundary() {
		return this.boundary;
	}

    public void setTrapped() 
    {

	}

	public boolean isLifeCocoonActive() {
		return false;
	}

	public boolean isImmune() {
		return false;
	}

    public void getHit(byte type) 
    {
        this.health -= type == Bullet.NORMAL_BULLET ? 15 : 30;
	}

	public boolean isTrapped() {
		return skillSteelTrap;
	}

}