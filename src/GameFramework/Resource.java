package GameFramework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Resource
{
    private static final long serialVersionUID = 1L; 
    String resourceName;
    BufferedImage texture; 
    int x, y;
    
    public Resource(int x, int y, String fileName, String resourceName)
    {
        this.x = x;
        this.y = y;
        texture = pullTexture(fileName);
        this.resourceName = resourceName;
    }
    
    public Resource()
    {
        resourceName = "";
    }
    
    public BufferedImage getTexture()
    {
        return texture;
    }
    
    public void setResource(String resourceN)
    {
        resourceName = resourceN;
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
}