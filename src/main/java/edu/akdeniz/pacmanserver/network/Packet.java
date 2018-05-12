package edu.akdeniz.pacmanserver.network;

import java.net.InetSocketAddress;

/**
 * This class is the position and length presentation of the data that is in 
 * the buffers. It also holds the address of the device that is receiving or 
 * sending data. Instances alone means almost nothing since this class has 
 * no access or knowledge of the data.
 * <p> 
 * When an instance put in the {@code PacketBuffer} instances via 
 * {@link PacketBuffer#offer(Packet) offer} method its index got assinged 
 * appropriate value. That way data can be reach or send properly.
 * @see {@link PacketBuffer}
 */
public class Packet
{
    private final InetSocketAddress address; 
    private short index;
    private short length;

    /**
     * Create a new instance that has the address and the length of the 
     * received data or the data that is going to be sent.
     * @param length {@code short} value of the data's length  
     * @param address Address of the device
     */
    public Packet(short length , InetSocketAddress address)
    {
        this.address = address;
        this.length = length;
    }
    /**
     * Returns the address of the device
     * @return {@code InetSocketAddress} address of the device
     * @see {@link InetSocketAddress}
     */
    public InetSocketAddress getAddress()
    {
        return this.address;
    }
    /**
     * Returns the index of the starting position of the data. 
     * <p>
     * The returned index represents the starting position of the data if only 
     * the instance is added the {@code PacketBuffer} object via 
     * {@link PacketBuffer#offer(Packet) offer} method. Otherwise it returns 
     * {@code 0}
     * @return {@code short} Index of the data.
     */
    public short getIndex()
    {
        return this.index;
    }

    /**
     * Sets the index of the starting position of the data that is in the 
     * buffer. This method should only be called by 
     * {@link PacketBuffer#offer(Packet) offer} method.
     * @see {@link PacketBuffer}
     */
    void setIndex(short value)
    {
        this.index = value;
    }
    /**
     * Returns the length of the data that is in the buffer.
     * @return {@code short} The length of the data.
     */
    public short getLength()
    {
        return this.length;
    }
}