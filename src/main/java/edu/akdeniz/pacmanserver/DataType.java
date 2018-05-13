package edu.akdeniz.pacmanserver;
/**
 * Constant byte representation of the receiving or sending data. 
 * Every data's first byte should only be one of these values otherwise 
 * the data will be discarded.
 */
public class DataType
{
    public static final byte HOST_GAME = 0x01;
    public static final byte JOIN_GAME = 0x02;
    public static final byte LEAVE_GAME = 0x03;
    public static final byte ROOM_INFO = 0x04;
    public static final byte GAME_LIST = 0x05;
    public static final byte GAME_LOADED = 0x06;
    public static final byte GAME_PACKET = 0x07;
}