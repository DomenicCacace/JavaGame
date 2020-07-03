
package GameFramework;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import javax.imageio.*;
import java.io.Serializable;
import java.awt.Graphics;
import java.util.ArrayList;

public class Tile implements Serializable
{
    private static final long serialVersionUID = 1L; 
    transient static int identification = 0;
    transient ArrayList<BufferedImage> textures = null;
    int id;
    int x, y = 0;
    String biome, textureName;
    boolean hasBeenModified = false;
    
    //Animation variables
    long oldTime = System.nanoTime(), currentTime;
    int animationFPS = 1, frame = 0;
    double timePerFrame = 1000000000 / animationFPS, deltaTime = 0;
    
    //Custom Class Objects
    Resource resource;
    Troop troop = null;
    MapNode node;
    Building building = null;
    
    public Tile()
    {
        id = identification++;
        node = new MapNode(x, y);
        biome = "land";
        
        //This may cause problems with loading if serialization calls default constructor
        assignTextures();
    }
    
    public Tile(int x, int y, String fileName, String biome)
    {
        // Is currently functional.
        
        node = new MapNode(x, y);
        this.x = x;
        this.y = y;
        this.biome = biome;
        textureName = fileName;
        textures = assignTextures();
    }
    
    public String toString()
    {
        return("ID: "+this.id+"\n");
    }
    
    public String showConnections()
    {
        String ret = "";
        ret += ("Tile X: " + x + "	Y: " + y + "\n");
        ret += ("Has Left: " + node.hasLeft() + "\n");
        ret += ("Has Right: " + node.hasRight() + "\n");
        ret += ("Has Up: " + node.hasUp() + "\n");
        ret += ("Has Down: " + node.hasDown() + "\n");
        
        return ret;
    }
    
    // OTHER METHODS ========================================================
    
    public boolean equals(String biome)
    {
        if (this.biome.equals(biome))
            return true;
        else
            return false;
    }
    
    //SETTERS ===============================================================
    
    public void setNode(MapNode node)
    {
        this.node = node;
    }

    public void setBiome(String biome)
    {
        this.biome = biome;
    }
    
    public void setResource(int x, int y, String fileName, String resourceName)
    {
        resource = new Resource(x, y, fileName, resourceName);
    }
    
    public void setTroop(Troop troop)
    {
        this.troop = troop;
    }
    
    public void setTroopHasMoved(boolean b)
    {
        troop.setHasMoved(b);
    }
    
    public void setBuilding(Building building)
    {
        this.building = building;
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
    
    public ArrayList<BufferedImage> assignTextures()
    {
        ArrayList<BufferedImage> ret = new ArrayList<BufferedImage>();
        
        if (biome.equals("land"))
        {
            ret.add(pullTexture("testGrass.png"));
            ret.add(pullTexture("testGrass2.png"));
        }
        else if (biome.equals("water"))
        {
            ret.add(pullTexture("water.png"));
        }
        else if (biome.equals("mountain"))
        {
            ret.add(pullTexture("mountain.png"));
        }
        else if (biome.equals("sand"))
        {
            ret.add(pullTexture("basicSand.png"));
        }
        else if (biome.equals("sword"))
        {
            //Temporary
            ret.add(pullTexture("sword.png"));
        }
        
        return ret;
    }
    
    //This does work!
    
    public static void render(Graphics g)
    {
        g.drawRect(0,0,200,200);
    }
    
    // GETTERS ===========================================================

    public BufferedImage getTexture()
    {
        //Get texture will return different textures based upon when it was last updated.
        //Essentially, this is where I will animate.
        currentTime = System.nanoTime();
        deltaTime += (currentTime - oldTime) / timePerFrame;
        oldTime = currentTime;

        if (deltaTime >= 1)
        {
            deltaTime = 0;
            frame++;
            
            if (frame >= textures.size())
                frame = 0;
        }
        
        return textures.get(frame);
    }
    
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
    
    public Resource getResource()
    {
        return resource;
    }
    
    public MapNode getNode()
    {
        return node;
    }
    
    public String getBiome()
    {
        return biome;
    }
    
    public Troop getTroop()
    {
        return troop;
    }
    
    public Building getBuilding()
    {
        return building;
    }
    
    public String getTextureName()
    {
        return textureName;
    }
    
    //HAS METHODS ==========================================================
    
    public boolean hasResource()
    {
        if(resource == null)
            return false;
        else
            return true;
    }
    
    public boolean hasTexture()
    {
        if (textures == null || textures.get(0) == null)
            return false;
        else
            return true;
    }
    
    public boolean hasNode()
    {
        if (node == null)
            return false;
        else
            return true;
    }
    
    public boolean hasTroop()
    {
        if (troop == null)
            return false;
        else
            return true;
    }
    
    public boolean hasBuilding()
    {
        if (building == null)
            return false;
        else
            return true;
    }
}
