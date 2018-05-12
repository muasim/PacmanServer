package edu.akdeniz.pacmanserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

import edu.akdeniz.pacmanserver.match.Match;
import edu.akdeniz.pacmanserver.network.Packet;
import edu.akdeniz.pacmanserver.network.PacketBuffer;

/**
 * Checks received data's {@link DataType}s then sends or process them. All 
 * the {@link Match} and {@link Room} instances are managed by this class.
 * <p>
 * If the {@code DataType} of the data is {@code GAME_ROOM} then it is sent to 
 * the according {@code Match} instance.All the other data is processed 
 * within this thread.
 */ 
public class PacketManager implements Runnable
{
    public static final int MAX_STRING_LENGTH = 16;
    public static final int TYPE_LENGTH = 1;
    
    public static final Charset CHARSET = Charset.forName("US-ASCII");

    private PacketBuffer packetReceiver;
    private ByteBuffer receivedBuffer;

    private HashMap<Short, Room> rooms = new HashMap<Short , Room>(5);
    private HashMap<Short, Match> matches = new HashMap<Short , Match>(5);
    
    /**
    * Constructs a new {@code PacketManager} that process received data.
    * @param packetBuffer Instance of {@code PacketBuffer} for getting data from.
    * @see {@link PacketBuffer}
    */ 
    public PacketManager(PacketBuffer packetBuffer)
    {
        this.packetReceiver = packetBuffer;
        this.receivedBuffer = packetBuffer.getFrontBuffer();
    }

	@Override
	public void run() {
        InetSocketAddress address;
        byte type = 0;
        PacketBuffer packetSender = new PacketBuffer(32768 , 0.9);
        ByteBuffer sentBuffer = packetSender.getBackBuffer();
        SocketSend.packetBuffers.add(packetSender);

        while(true)
        {
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            while(packetReceiver.size() > 0)
            {
                address = this.packetReceiver.peek().getAddress();
                type = this.receivedBuffer.get();
                switch(type)
                {
                    case DataType.GAME_PACKET:
                    {
                        matches.get(receivedBuffer.getShort()).addPacket(packetReceiver.peek());
                        this.receivedBuffer.position(this.receivedBuffer.position() + this.packetReceiver.peek().getLength() - (Byte.BYTES + Short.BYTES));
                        break;
                    }
    
                    case DataType.GAME_LOADED:
                    {
                        matches.get(receivedBuffer.getShort()).inform(address);
                        this.receivedBuffer.position(this.receivedBuffer.position() + this.packetReceiver.peek().getLength() - (Byte.BYTES + Short.BYTES));
                        break;
                    }
                    case DataType.GAME_LIST:
                    {
                        System.out.println("Room List Requested by: " + address);
                        int dataLength = sentBuffer.position();
                        rooms.forEach((roomID , room) ->
                        {
                            sentBuffer.putShort(roomID);
                            sentBuffer.put(room.getRoomName());
                        });
                        dataLength += sentBuffer.position();

                        packetSender.offer(new Packet((short)dataLength , address));
                        break;
                    }
                    case DataType.HOST_GAME:
                    {
                        byte[] roomName = new byte[MAX_STRING_LENGTH];
                        byte[] playerName = new byte[MAX_STRING_LENGTH];
                        for(int i = 0 ; i < MAX_STRING_LENGTH ; i++)
                        {
                            roomName[i] = this.receivedBuffer.get();
                            playerName[i] = this.receivedBuffer.get(this.receivedBuffer.position() + MAX_STRING_LENGTH - 1);
                        }
                        this.receivedBuffer.position(this.receivedBuffer.position() + MAX_STRING_LENGTH);
                        short roomID = this.generateRoomID();
                        rooms.put(roomID , new Room(roomName , playerName , address));
                        System.out.println("New Room Initialized! RoomName: " + new String(roomName , CHARSET) + " , HostName: "+ new String(playerName , CHARSET) + " , IP: " + address);
                        break;
                    }
                    case DataType.JOIN_GAME:
                    {
                        byte[] playerName = new byte[MAX_STRING_LENGTH];
                        short roomID = this.receivedBuffer.getShort();
                        this.receivedBuffer.get(playerName, 0, MAX_STRING_LENGTH);
                        
                        Room room = rooms.get(roomID);
                        
                        if(room == null)
                        {
                            System.out.println("Error : RoomID Violation. IP : " + address + " Tries To Join Non-Exist RoomID: " + roomID);
                            break;
                        }
                        if(!room.hasIP(address))
                        {
                            System.out.println("IP: " + address + " joined the room!");
                            if(room.addPlayer(packetSender , playerName , address));
                            {
                                matches.put(roomID , new Match(roomID , rooms.get(roomID).getRoomInfo() , packetReceiver.cloneBackBuffer()));
                            }
                        }
                        else System.out.println("Error : IP: " + address + " is already in the room of " + roomID);
                        break;
                    }
                    case DataType.LEAVE_GAME:
                    {
                        short roomID = this.receivedBuffer.getShort();
                        Room room = rooms.get(roomID);
                        if(room == null)
                        {
                            System.out.println("Error : RoomID Violation. IP : " + address + " Tries To Leave Non-Exist RoomID: " + roomID);
                            break;
                        }
                        if(room.hasIP(address))
                        {
                            if(room.removePlayer(packetSender , address))
                            {
                                rooms.remove(roomID);
                            }                            
                        }
                        else System.out.println("IP: " + address + " No such IP in any room tries to leave!");
                        break;
                    }
                }
                this.packetReceiver.poll();
            }
        }
    }
    /**
     * Returns a unique {@code short} value in order to be used as key to the 
     * {@link Room} and {@link Match} instances.
     * @return Unique short value within the range of 0-2048 
     * */ 
    private Short generateRoomID() 
    {
        short roomID;
        while(this.rooms.containsKey(roomID = (short)(Math.random() * 2048)));
        return roomID;
	}
}