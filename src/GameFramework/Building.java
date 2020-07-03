package GameFramework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import java.io.Serializable;

public class Building implements Serializable
{
    private static final long serialVersionUID = 1L; 
    String player = "", fileName = "";
    transient BufferedImage texture;
    
    public Building(String player, String fileName)
    {
        this.player = player;
        this.fileName = fileName;
        
        Game.writeToLog("Creating a building for " + player + ".");
        
        String path = "";
        
        pullTexture(fileName);
    }
    
    // Getters ------------------------------------------------------------
    
    public String getPlayer()
    {
        return player;
    }
    
    public BufferedImage getTexture()
    {
        return texture;
    }
    
    // Setters ------------------------------------------------------------
    
    public void setPlayer(String player)
    {
        this.player = player;
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
    
    // Has methods --------------------------------------------------------------------------------------
    
}
