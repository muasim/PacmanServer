package edu.akdeniz.pacmanserver.network;

import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class directly stores the received or sending data in its buffers 
 * and responsible for buffers management. Data input (write) is provided 
 * via {@code backBuffer} and output (read) via {@code frontBuffer}.
 * <p>
 * After write or read operation to these buffers, according {@code Packet} 
 * object that represents the data must be added or removed via 
 * {@link #offer(Packet) offer} or {@link #poll() pool} methods.
 * 
 * <p>Example of writing data:</p>
 * <pre>
InetSocketAddress destinationAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 5555);  
PacketBuffer packetBuffer = new PacketBuffer(512 , 0.9);
ByteBuffer backBuffer = packetBuffer.getBackBuffer();

//Writing some data to the back buffer
byte[] data = "Send me!".getBytes();
backBuffer.put(data);
// After writing add an instance of Packet object
// to the packetBuffer via offer(Packet) method
packetBuffer.offer(new Packet(data.length , destinationAddress));
 * </pre>
 * 
 * <p>Example of reading data:</p>
 * <pre>
ByteBuffer backBuffer = packetBuffer.getFrontBuffer();

//Check if any data is received
if(packetBuffer.size() > 0)
{
    Packet packet = packetBuffer.peek();
    //Get the data from the front buffer
    byte[] receivedData = new byte[packet.getLength()];
    backBuffer.get(receivedData , 0 , packet.getLength());
    System.out.println(new String(receivedData));
    //After done reading packet must be 
    //removed from the packetBuffer
    packetBuffer.poll();
}
 * <pre>
 */
public class PacketBuffer 
{
    final Queue<Packet> packets = new LinkedList<Packet>();
    final ByteBuffer frontBuffer;
    final ByteBuffer backBuffer;
    final int limit;
    /**
     * Constructs an instance by assigning given buffer object to its 
     * {@link #backBuffer} without allocating new {@code ByteBuffer} 
     * object and sets {@link #limit} to the given one. This constructor 
     * method is to read from the same back buffer with multiple different 
     * front buffers.
     * <p>
     * Buffers are <b>not Thread-safe!</b> Reading same part of the buffer 
     * by multiple front buffers concurrently might cause concurrent problems. 
     */
    private PacketBuffer(ByteBuffer backBuffer , int limit)
    {
        this.backBuffer = backBuffer;
        this.frontBuffer = backBuffer.duplicate();
        this.limit = limit;
    }
    /**
     *  Constructs an instance by allocating a back buffer with given 
     *  {@code capacity} and sets its limit to the {@code loadFactor}
     */
    public PacketBuffer(int capacity , double loadFactor)
    {
        this.frontBuffer = ByteBuffer.allocateDirect(capacity);
        this.backBuffer = this.frontBuffer.duplicate();
        this.limit = (int) (capacity * loadFactor);
    }
    /**
     * Queue an packet to this instance. If the data that is written to 
     * the {@code backBuffer} exceeds {@code limit}, position of 
     * {@code backBuffer} is set to {@code 0}. If there is a data waiting 
     * to be read exists at the {@code 0} location of {@code backBuffer}, 
     * {@code BufferOverflowException} is thrown.
     * <p>
     * This method also sets the index of the given {@code packet} to according value.
     * @param packet {@code Packet} instance that represents the last added 
     * data to the {@code backBuffer}.
     */
    public void offer(Packet packet) throws BufferOverflowException 
    {
        if(this.backBuffer.position() > this.limit)
        {
            this.backBuffer.position(0);
            if(this.packets.peek() != null && this.backBuffer.capacity() - this.limit > this.packets.peek().getIndex())
            {
                throw new BufferOverflowException();
            }
        }
        packet.setIndex((short)backBuffer.position());
        this.packets.offer(packet);
    }
    
    /**
     * Dequeue first packet from this instance and returns it. If the packet that 
     * represents the data that exceed the limit is read, {@code frontBuffer} 
     * position is set to {@code 0}.
     *  @return the last read {@code Packet} instance.
     */
    public Packet poll()
    {
        Packet packet = this.packets.poll();
        if(this.frontBuffer.position() > this.limit)
        {
            this.frontBuffer.position(0);
        }
        return packet; 
    }
    /**
     * Returns the first packet from the queue.
     * @return {@code Packet} instance that represent the data that added last.
     */
    public Packet peek()
    {
        return this.packets.peek();
    }
    /**
     * Queue an packet that is already initialized by {@code PacketBuffer} 
     * instance and represents data to this instance. Since it is offered 
     * to a different {@code PacketBuffer} that is holding the same data 
     * ({@code backBuffer}) as this instance, this method differs from 
     * {@link #pool} by adding {@code packet} directly without any check operation.
     * @param packet {@code Packet} instance that represents the last added 
     * data to the {@code backBuffer}.
     * @see {@link Packet}
     */
    public void addPacket(Packet packet)
    {
        packets.add(packet);
    }
    /**
     * Returns the amount of {@code Packet} that is in this instance.
     * @return Number of packets.
     * @see {@link Packet}
     */
    public final int size()
    {
        return this.packets.size();
    }
    
    /**
     * Returns {@code frontBuffer}.
     * @return {@code ByteBuffer} that is used for reading data.
     * @see {@link ByteBuffer}
     */
    public final ByteBuffer getFrontBuffer()
    {
        return this.frontBuffer;
    }

    /**
     * Returns {@code backBuffer}.
     * @return {@code ByteBuffer} that is used for writing data.
     * @see {@link ByteBuffer}
     */
    public final ByteBuffer getBackBuffer()
    {
        return this.backBuffer;
    }

    /**
     * Returns a clone of this instance. Clone instance has different 
     * {@code frontBuffer} object. {@code frontBuffer} gets duplicated 
     * from {@code backBuffer}.
     * @return A duplicate of this instance.  
     */
    public final PacketBuffer cloneBackBuffer()
    {
        return new PacketBuffer(this.backBuffer , this.limit);
    }
}