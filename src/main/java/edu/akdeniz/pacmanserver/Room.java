package edu.akdeniz.pacmanserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import static edu.akdeniz.pacmanserver.PacketManager.SUCCEED;
import static edu.akdeniz.pacmanserver.PacketManager.MAX_STRING_LENGTH;
import edu.akdeniz.pacmanserver.match.Match;
import edu.akdeniz.pacmanserver.network.Packet;
import edu.akdeniz.pacmanserver.network.PacketBuffer;

/** This class manages up to four players until there is no player left or all 
 * the four players are present. Players address and names are held in this 
 * class within a list of {@link PlayerInfo} and every address must be unique.
 * <p>
 * If the four players is complete then the room instance gets destroyed and player 
 * informations are transferred to a new {@link Match} instance
 */
public class Room
{
    private static final int MAX_PLAYER = 4;
    
    private final byte[] roomName;
    private LinkedList<PlayerInfo> players;

    /**
     * Constructs a new {@code Room} object with a name and then stores the host's 
     * information
     * @param roomName {@code byte[]} form of the room name decoded from US-ASCII 
     * String. It is maximum 16 byte long.
     * @param playerName {@code byte[]} form of the player name decoded from US-ASCII 
     * String. It is maximum 16 byte long.
     * @param address IP and port information of the player
     * @see {@link InetSocketAddress}
     */
    public Room(byte[] roomName , byte[] playerName , InetSocketAddress address)
    {
        this.roomName = roomName;
        this.players = new LinkedList<PlayerInfo>();
        this.players.add(new PlayerInfo(playerName , address));
    }
    /**
     * Stores the player information then informs other players that are present in the 
     * room a player is joined then sends other players information to the joined player.
     * If all the four player are present returns {@code true}
     * @param packetSender Instance of {@code PacketBuffer} for sending data to. 
     * @param playerName {@code byte[]} form of the player name decoded from US-ASCII 
     * String.It is maximum 16 byte long.
     * @param address IP and port information of the player
     * @return If four players are present returns {@code true} otherwise {@code false}
     * @see {@link PacketBuffer} , {@link InetSocketAddress}
     */
    public boolean addPlayer(PacketBuffer packetSender , byte[] playerName , InetSocketAddress address)
    {
        ByteBuffer buffer = packetSender.getBackBuffer();
        for(int i = 0 ; i < players.size() ; i++)
        {
            buffer.put(DataType.JOIN_GAME);
            buffer.put(players.size() == MAX_PLAYER - 1 ? (byte)0x01 : (byte)0x00);
            buffer.put(playerName);
            
            packetSender.offer(new Packet((short) (Byte.BYTES * 2 + MAX_STRING_LENGTH) , players.get(i).getAddress()));
            
        }
        
        buffer.put(DataType.ROOM_INFO);
        buffer.put(SUCCEED);
        buffer.put((byte)players.size());
        players.forEach(player ->
        {
            buffer.put(player.getNameData());
        });
        packetSender.offer(new Packet((short) (Byte.BYTES * 3 + MAX_STRING_LENGTH * players.size()) , address));
        this.players.add(new PlayerInfo(playerName , address));
        return players.size() == MAX_PLAYER;
    }
    /**
     * Finds the player by the address and removes the player information then 
     * informs other players that are present in the room the player is left
     * If no other player is left in the room return {@code true}
     * @param packetSender Instance of {@code PacketBuffer} for sending data to.
     * @param address IP and port information of the player
     * @return If no other player is left in the room return {@code true} 
     * otherwise {@code false}
     * @see {@link PacketBuffer} , {@link InetSocketAddress}
     */
    public boolean removePlayer(PacketBuffer packetSender , InetSocketAddress address)
    {
        if(players.size() == 1)return true;
        ByteBuffer buffer = packetSender.getBackBuffer();

        buffer.put(DataType.LEAVE_GAME);

        byte playerIndex = 0;
        for(int i = 0 ; i < players.size() ; i++)
        {
            if(players.get(i).getAddress().equals(address))
            {
                playerIndex = (byte)i;
                players.remove(playerIndex);
            }
        }
        for(PlayerInfo player:players)
        {
            buffer.put(playerIndex);
            packetSender.offer(new Packet((short) (Byte.SIZE * 2) , player.getAddress()));
        }
        return false;
    }

    /**
     * Checks if a player is present in the room by comparing addresses
     * @param address IP and port information of the player
     * @return If same address with the paramater is available in the room 
     * returns {@code true} otherwise {@code false}
     * @see {@link InetSocketAddress}
     */
    public boolean hasIP(InetSocketAddress address)
    {
        for(PlayerInfo player:players)
        {
            if(player.getAddress().equals(address)) return true;
        }
        return false;
    }
    /**
     * Returns the room name in {@code byte[]} form.
     * @return {@code byte[]} form of the player name decoded from US-ASCII 
     * String. It is maximum 16 byte long.
     */
    public byte[] getRoomName()
    {
        return this.roomName;
    }
    /**
     * Returns player informations.
     * @return {@code LinkedList<PlayerInfo>}  List of the players that are 
     * present in the room. Informations resides in {@code PlayerInfo} instances
     * @see {@link PlayerInfo}
     */
    public LinkedList<PlayerInfo> getRoomInfo() 
    {
		return this.players;
	}
}