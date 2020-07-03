package GameFramework;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Client 
{
    private int port = 1000;
    private InetAddress ip;
    private static DatagramSocket socket;
    private boolean isReceiving = false;
    
    public Client(InetAddress ip, int port)
    {
        this.port = port;
        this.ip = ip;
        isReceiving = true;
        
        try{
            socket = new DatagramSocket();
        }catch (SocketException e){
            e.printStackTrace();
        }
        
        //receive();
    }
    
    public static void send(String message, InetAddress ip, int port)
    {
        //Note - I think that 1 char is one byte. Thus 1024 chars can be sent per packet.
        if(message.getBytes().length <= 1024)
        {
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
            try{
                socket.send(packet);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public void receive()
    {
        Thread clientThread = new Thread("client") {
            public void run()
            {
                while(isReceiving)
                {
                    byte[] rawData = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(rawData, rawData.length);
                    
                    try{
                        socket.receive(packet);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    
                    String message = new String(rawData);
                    
                    Game.writeToLog(packet.getAddress().getHostAddress() + ":" + packet.getPort() + " >> " + message);
                    
                }
            }
        }; 
        
        clientThread.start();
    }
}
