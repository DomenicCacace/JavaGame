package GameFramework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class MapHandler
{
    //My map extension will be... map! Praise the creativity!
    private static String serializedObject = "";
        
    public static void saveMap(Tile[][] board, String mapName)
    {
        //Working
        Game.writeToLog("Starting to save the map");
        
        //Creates a .map file (if it does not already exist) to record progress
        String fileName = "./"+mapName+".map";
        
        //Creating the file and the writer.
        File map;
        BufferedWriter writer = null;
        try {
            Game.writeToLog(fileName);
	    map = new File(fileName);
	    /*If file gets created then the createNewFile() 
	     * method would return true or if the file is 
	     * already present it would return false
	     */
            boolean wasCreated = map.createNewFile();
	    if (wasCreated)
            {
                Game.writeToLog("Map file created successfully.");
	    }
	    else
            {
                Game.writeToLog("Map file already exists.");
            }
    	} catch (IOException e) {
            Game.writeToLog("IOException thrown when creating map file.");
	}
        
        // Instantiating a writer to write to the map file
        
        try{
            writer = new BufferedWriter(new FileWriter(fileName));
        } catch (IOException e){
            Game.writeToLog("IOException thrown while instantiating file writer.");
        }
        
        
        // ------------ Writing the map data -------------------
        
        //I serialize each tile to save the map.
        //1 line = 1 row of tile data. It is stored as a Base 64 Encoded String. 
        //I use , as a delimeter
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            
            
            for (int y = 0; y < 50; y++)
            {
                for (int x = 0; x < board[0].length; x++)
                {
                    oos.writeObject(board[y][x]);
                    serializedObject = Base64.getEncoder().encodeToString(baos.toByteArray());
                    
                    writer.write(serializedObject + "\n");
                }
                writer.write("break\n");
            }
            
            writer.close();
            oos.close();
            baos.close();
            
        } catch (IOException e) {
            Game.writeToLog("IOException when saving the map.");
        }        
    }
    
    public static Tile[][] loadMap()
    {   
        //Not working.
        
        //Getting the map file and initializing the scanner.
        File map = null;
        Scanner scanner = null;
        try{
            String path = SettingsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(0, path.indexOf("game1.jar"));
            
            map = new File(path + "test.map");
            scanner = new Scanner(map);
        }catch (FileNotFoundException e){
            Game.writeToLog("FileNotFoundException thrown.");
        }catch (URISyntaxException urise)
        {
            Game.writeToLog("URISynxtaxException thrown.");
        }
        
        //Getting the number of rows/columns.
        int[] dimensions = findMapDimensions(map);
        Tile[][] loadingBoard = new Tile[dimensions[1]][dimensions[0]];
        
        String encodedTile = "";
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        
        //Loading the map 
        
        try{ 
            for (int y = 0; y < dimensions[0]; y++)
            {
                for (int x = 0; x < dimensions[1]; x++)
                {
                    encodedTile = scanner.next();
                    byte[] tile = Base64.getDecoder().decode(encodedTile);
                    
                    bais = new ByteArrayInputStream(tile);
                    ois = new ObjectInputStream(bais);
                    
                    loadingBoard[y][x]  = (Tile)ois.readObject();
                    loadingBoard[y][x].assignTextures();
                }
                scanner.nextLine();
            }
            
            /*
            byte[] tile = Base64.getDecoder().decode(tileEncoded);
            Game.writeToLog("Step 1");
            
            ByteArrayInputStream bais = new ByteArrayInputStream(tile);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Game.writeToLog("Step 2");
            
            Tile loadedTile  = (Tile)ois.readObject();
            Game.writeToLog("Step 3");
            ois.close();
            Game.writeToLog("Step 4");

            b[0][0] = loadedTile;
            */
            ois.close();
            bais.close();
            scanner.close();
                        
        }catch (IOException e){
            Game.writeToLog("IOException thrown when loading map.");
        }catch (ClassNotFoundException cnfe){
            Game.writeToLog("Class not found exception thrown when loading the map");
        }
                
        return loadingBoard;
    }
    
    private static int[] findMapDimensions(File file)
    {
        //Not working
        
        Scanner scanner = null;
        
        try{
            scanner = new Scanner(file);
        }catch (FileNotFoundException e){
            Game.writeToLog("FileNotFoundException whilst finding map dimensions.");
        }
        
        int rows = 0;
        while(scanner.hasNextLine())
        {
            if (scanner.nextLine().equals("break"))
                rows++;
        }
        
        int columns = 0;
        while(scanner.hasNext())
        {
            columns++;
            scanner.next();
        }
        
        Game.writeToLog(rows + ", " + columns);
        int[] ret = {rows, columns};
        return ret;
    }
}
