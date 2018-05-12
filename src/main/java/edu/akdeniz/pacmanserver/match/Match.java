package edu.akdeniz.pacmanserver.match;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import edu.akdeniz.pacmanserver.DataType;
import edu.akdeniz.pacmanserver.PlayerInfo;
import edu.akdeniz.pacmanserver.SocketSend;
import edu.akdeniz.pacmanserver.network.Packet;
import edu.akdeniz.pacmanserver.network.PacketBuffer;

public class Match extends Thread
{
    private static final int SERVER_TICK = 100; 
    private final LinkedList<PlayerInfo> playerInfos;
    private final PacketBuffer packetReceiver;
    private final PacketBuffer packetSender = new PacketBuffer(2048, 0.9);
    private byte readyCheck;

    /////////////////////////////////////////////////////////////////////////////
    Tile[][] tiles = new Tile[7][13];
    private Player[] players = new Player[4];
    public Match(Short roomID , LinkedList<PlayerInfo> playerInfos , PacketBuffer packetReceiver)
    {
        super("Match -" + roomID + "- Thread");

        this.playerInfos = playerInfos;
        this.packetReceiver = packetReceiver;
        /////////////////////////////////////////////////////////////////////////////

        for (int y = 0; y < 7 ; y++)
        {
            for (int x = 0; x < 13 ; x++)
            {
                Tile tile;
                if(y == 0 || y == 6 || x == 0 || x == 12)
                {
                    tile = new Wall(x * Tile.SIZE , y * Tile.SIZE); 
                }
                else if(y % 2 == 0 && x % 2 == 0)
                {
                    tile = new Wall(x * Tile.SIZE , y * Tile.SIZE);
                }
                else tile = new Floor(x * Tile.SIZE , y * Tile.SIZE , Type.DOT);
                tiles[y][x] = tile;
            }
        }

        players[0] = new Player(1 * Tile.SIZE, 1 * Tile.SIZE);
        players[1] = new Player(11 * Tile.SIZE, 1 * Tile.SIZE);
        players[2] = new Player(11 * Tile.SIZE, 5 * Tile.SIZE);
        players[3] = new Player(1 * Tile.SIZE, 5 * Tile.SIZE);

    }

    @Override
    public void run() 
    {
        ByteBuffer receivedBuffer = this.packetReceiver.getFrontBuffer();
        ByteBuffer sentBuffer = this.packetSender.getBackBuffer();

        LinkedList<Bullet> bullets = new LinkedList<Bullet>();
        LinkedList<SteelTrap> traps = new LinkedList<SteelTrap>();
        while(true)
        {
            try 
            {
                super.sleep(Match.SERVER_TICK);    
            } catch (InterruptedException e) 
            {
                System.out.println("Match Tick");
            }
            
            while(packetReceiver.size() > 0)
            {
                receivedBuffer.position(this.packetReceiver.poll().getIndex() + Byte.BYTES + Short.BYTES );
                Player player = players[receivedBuffer.get()];
                player.setKeyState(KeyCode.DIRECTION_X , receivedBuffer.get());
                player.setKeyState(KeyCode.DIRECTION_Y , receivedBuffer.get());
            }
            for(int i = 0 ; i < playerInfos.size() ; i++)
            {

                //#region Bullet Collision Check
                for(Bullet bullet:bullets)
                {
                    if(bullet.intersects(players[i].getBoundary()))
                    {
                        if(players[i].isLifeCocoonActive())bullet.ricochet();
                        if(!players[i].isImmune())
                        {
                            players[i].getHit(bullet.getType());
                            //TODO Check Player Health
                        }
                    }
                }
                //#endregion

                if(players[i].isTrapped())

                //#region Trap Collision Check
                for(SteelTrap trap:traps)
                {
                    if(trap.intersects(players[i].getBoundary()) && trap.getOwner() != players[i])
                    {
                        players[i].setTrapped();
                        traps.remove(trap);    
                    }
                }
                //#endregion

                //#region Fire Logic
                if(players[i].isHexaFireActive() || players[i].getKeyState(KeyCode.FIRE_BULLET) > 0)
                {
                    if(players[i].isIncendiaryBulletActive())
                    {
                        bullets.add(new Bullet(players[i].getX(), players[i].getY(), players[i].getDirection() , Bullet.INCENDIARY_BULLET));
                    }
                    else bullets.add(new Bullet(players[i].getX(), players[i].getY(), players[i].getDirection() , Bullet.NORMAL_BULLET));
                    if(players[i].isInvisibilityActive())
                    {
                        players[i].setInvisibility(false);
                    }
                }
                //#endregion

                //#region Skill Usage Logic
                for(int j = 0 ; j < 3 ; j++)
                {
                    if(players[i].getKeyState(KeyCode.SKILL_A + j) > 0 && players[i].getSkill(j) != null)
                    {
                        switch(players[i].getSkill(j))
                        {
                            case BLINK:
                            {

                            }
                            case LIFE_COCOON:
                            {

                            }
                            case INVISIBILITY:
                            {

                            }
                            case TO_MY_SIDE:
                            {

                            }
                            case HEXA_FIRE:
                            {

                            }
                            case STEEL_TRAP:
                            {

                            }
                            case INCENDIARY_BULLET:
                            {

                            }
                        }
                        players[i].removeSkill(i);
                    }
                    else
                    {
                        // TODO Play Sound
                    }
                }
                //endregion


                players[i].move(tiles);
            }

        }
    }
    public void inform(InetSocketAddress address) 
    {
        for(PlayerInfo playerInfo:playerInfos)
        {
            if(playerInfo.getAddress().equals(address))
            {
                System.out.println("Got the Inform");
                if(++this.readyCheck == 4)
                {
                    System.out.println("READY TO BEGIN");
                    SocketSend.packetBuffers.add(this.packetSender);
                    this.packetSender.getBackBuffer().put(DataType.GAME_LOADED);
                }
                break;
            }
            else System.out.println("IP Violation! Match doesn't contains the address : " + address);    
        }
        playerInfos.forEach(player ->
        {
            packetSender.offer(new Packet((short) Byte.BYTES, player.getAddress()));
        });
	}

    public void addPacket(Packet packet) 
    {
        this.packetReceiver.addPacket(packet);
	}
}