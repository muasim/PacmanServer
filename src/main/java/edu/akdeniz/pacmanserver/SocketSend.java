package edu.akdeniz.pacmanserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.akdeniz.pacmanserver.network.Packet;
import edu.akdeniz.pacmanserver.network.PacketBuffer;

/**
 * This class is responsible for sending data.In each pulse, the data of every 
 * {@code PacketBuffer} instances assigned to this class is sent to according addresses.
 * The {@code DatagramChannel} instance that is responsible for sending data is 
 * the same instance that is used for receiving data.
 * <p>
 * The thread running this class and the thread that runs {@code PacketManager} 
 * instance should pulse synchronously.
 */
public class SocketSend implements Runnable
{
    public static Set<PacketBuffer> packetBuffers = Collections.synchronizedSet(new LinkedHashSet<PacketBuffer>()); 
    private DatagramChannel channel;
    /**
     * Creates an {@code SocketSend} instance. To construct this class the instance 
     * of {@code DatagramChannel} that is used for sending data is required. 
     * @param channel Same channel object that is used for receiving data.
     */
    public SocketSend(DatagramChannel channel)
    {
        this.channel = channel;
    }
    @Override
    public void run() {
        while(true)
        {
            try 
            {
                Thread.sleep(100);
            } catch (InterruptedException e1) 
            {
				e1.printStackTrace();
			}
            for(PacketBuffer packetBuffer:packetBuffers)
            {
                ByteBuffer buffer = packetBuffer.getFrontBuffer();
                while(packetBuffer.size() > 0)  
                {
                    try 
                    {
                        Packet packet = packetBuffer.poll();
                        buffer.limit(buffer.position() + packet.getLength() +1 );
                        this.channel.send(buffer, packet.getAddress());
                    } catch (IOException e) 
                    {
						e.printStackTrace();
					}
                }
            }
            
        }
    }
}