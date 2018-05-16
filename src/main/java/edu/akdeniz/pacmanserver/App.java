package edu.akdeniz.pacmanserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;

import edu.akdeniz.pacmanserver.network.*;


public class App {
    /**
     * Program's entry point. If an IP address is supplied as an 
     * command line argument, sockets bind to it. If the address is invalid 
     * or not supplied then sockets bind to localhost. Running the program 
     * initializes and starts listening and sending sockets immediately.
     */ 
    public static void main(String[] args) 
    {
        InetAddress bindAddress = null;
        if(args.length > 0)
        {
            try
            {
                bindAddress = InetAddress.getByName(args[0]);
            } catch(UnknownHostException e)
            {
                System.out.println("The IP address is not valid. Using localhost");
                bindAddress = resolveLocalAddress();
            }
        }
        else bindAddress = resolveLocalAddress();

        DatagramChannel channel;
        while(true)
        {
            try 
            {
                channel = DatagramChannel.open();
                channel.bind(new InetSocketAddress(bindAddress, 2242));
                break;
            } catch (IOException e) 
            {
                e.printStackTrace();
                System.exit(127);
            } catch (UnsupportedAddressTypeException e)
            {
                System.out.println("Cannot bind to the given IP address :" + bindAddress + " . Using localhost");
                bindAddress = resolveLocalAddress();
            }
        }
        try
        {
            PacketBuffer packetReceiver = new PacketBuffer(32768 , 0.95 );
            
            PacketManager packetManager = new PacketManager(packetReceiver);
            Thread packetManagerThread = new Thread(packetManager , "PacketManager Thread");
            SocketSend send = new SocketSend(channel);
            Thread sendThread = new Thread(send , "PacketSend Thread");
            
            sendThread.start();
            packetManagerThread.start();

            InetSocketAddress address;
            ByteBuffer buffer = packetReceiver.getBackBuffer();
            while(true)
            {
                address = (InetSocketAddress)channel.receive(buffer);
                packetReceiver.offer(new Packet((short)buffer.position() , address));
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(127);
        } 
        
    }
    /**
     * Returns InetAddress resolved from 
     * <a href="https://www.techopedia.com/definition/26064/localhost">
     * localhost</a>. If JVM can't determine the ip address program 
     * exits with the code 1.
     * @return      Resolved Ip address from the String "localhost"
     * @see         {@link InetAddress}
     */ 
    private static InetAddress resolveLocalAddress()
    {
        InetAddress address = null;
        try 
        {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e1) 
        {
            System.out.println("JVM cannot find any network drivers! Terminating...");
            System.exit(1);
        }
        return address;
    }
}