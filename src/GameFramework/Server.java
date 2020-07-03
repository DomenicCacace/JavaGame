package GameFramework;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.io.IOException;

public class Server 
{
    DatagramSocket socket;
    boolean isRunning = false;
    
    public Server(int port)
    {
        try
        {
            socket = new DatagramSocket(port);
            isRunning = true;
            receive();

        } catch(SocketException e){
            e.printStackTrace();
        }
    }
    
    public void receive()
    {
        Thread serverThread = new Thread("server") {
            public void run()
            {
                while(isRunning)
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
        
        serverThread.start();
    }
}
