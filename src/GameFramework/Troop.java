package GameFramework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import java.io.Serializable;

public class Troop implements Serializable
{
    //Honestly, this is just here for the hierarchy. Don't know what I'll need yet. Might make this an interface.
    private static final long serialVersionUID = 1L; 
    String player = "", fileName = "";
    transient BufferedImage texture;
    int moveDistance = 0, id;
    boolean hasMoved = false;
    
    //ID assigner
    private static int identification = 0;
    
    public Troop(String player, String fileName)
    {    
        Game.writeToLog("Creating a troop for " + player + ".");
        
        this.player = player;
        this.fileName = fileName;
                
        id = identification++;
        
        String path = "";
        
        pullTexture(fileName);
    }
    
    // Getters ------------------------------------------------------------
    
    public String getPlayer()
    {
        return player;
    }
    
    public int getMoveDistance()
    {
        return moveDistance;
    }
    
    public BufferedImage getTexture()
    {
        return texture;
    }
    
    public int getID()
    {
        return id;
    }
    
    // Setters ------------------------------------------------------------
    public void setPlayer(String player)
    {
        this.player = player;
    }
    
    public void setMoveDistance(int dist)
    {
        moveDistance = dist;
    }
    
    public BufferedImage pullTexture(String fileName)
    {
        String path = "";
        BufferedImage ret = null;
        
        try{
            path = SettingsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(0, path.indexOf("game1.jar")) + fileName;
        }catch (URISyntaxException uriE){
            Game.writeToLog("URISyntaxException Thrown. Current path "+ path +" could not be acquired.");
        }
        
        try{    
            ret = ImageIO.read(new File(path));
        } catch (IOException e){
            Game.writeToLog("Failed to load " + path + ". IOException thrown.");
        }
        
        return ret;
    }
    
    public void setHasMoved(boolean b)
    {
        hasMoved = b;
    }
    
    // Has methods --------------------------------------------------------------------------------------
    
    public boolean hasMoved()
    {
        if (hasMoved == true)
            return true;
        else
            return false;
    }
}
