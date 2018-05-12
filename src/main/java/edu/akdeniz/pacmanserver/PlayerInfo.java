package edu.akdeniz.pacmanserver;

import java.net.InetSocketAddress;

/**
 * Contains device address and player name data.
 */
public  class PlayerInfo
{
    private InetSocketAddress address;
    private byte[] name;

    /**
     * Constructs a container for the player's device {@code InetSocketAddress} 
     * and the player's name data.
     * @param name {@code byte[]} form of the player name decoded from US-ASCII 
     * String.It is maximum 16 byte long.
     * @param address Address of the device
     * @see {@link InetSocketAddress}
     */
    public PlayerInfo(byte[] name , InetSocketAddress address)
    {
        this.name = name;
        this.address = address;
    }
    /**
     * Returns the player's device address
     * @return {@code InetSocketAddress} address of the device.
     */
    public InetSocketAddress getAddress()
    {
        return this.address;
    }
    /**
     * Returns the player's name in represented in {@code byte[]}.
     * @return {@code byte[]} form of the player name decoded from US-ASCII 
     * String.It is maximum 16 byte long.
     */
    public byte[] getNameData()
    {
        return this.name;
    }
}