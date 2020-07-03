package GameFramework;

import java.io.BufferedWriter;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

public class SettingsHandler 
{   
    private static Scanner file;
    private static String line;
    private static BufferedWriter writer;
    
    //Note to self: DO NOT MESS WITH THE BUILD FOLDER! BAD BAD HAPPEN
    
    public SettingsHandler()
    {
        //New version
        String path = "";
        
        try{
            path = SettingsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            //Game.writeToLog("SettingsHandler path:"+path);
            path = path.substring(0, path.indexOf("game1.jar"));
        }catch (URISyntaxException uriE){
            System.out.println("URISyntaxException Thrown. Current path "+ path +" could not be acquired.");
        }
        
        //If broken check the config file. NO SPACES ALLOWED!
        File configFile = new File(path+"config.ini");
        try{
            file = new Scanner(configFile);
        }catch (FileNotFoundException e){
            System.out.println("Config.ini not found");
        }
    }
    
    public SettingsHandler(String fileSuffix)
    {   
        //The difference is that this opens a writer too.
        
        //New version
        String path = "";
        
        try{
            path = SettingsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(0, path.indexOf("game1.jar"));
        }catch (URISyntaxException uriE){
            System.out.println("URISyntaxException Thrown while instantiating SettingsHandler Output Object. Current path "+ path +" could not be acquired.");
        }
        
        File configFile = new File(path + fileSuffix);
        try{
            file = new Scanner(configFile);
        }catch (FileNotFoundException e){
            System.out.println("Config.ini not found");
        }
        
        try{
            writer = new BufferedWriter(new FileWriter(path + fileSuffix));
        } catch (IOException e){
            System.out.println("Constructor IOException thrown.");
        }
    }
    
    public int loadFPS()
    {
        line = file.nextLine();
        line = line.replaceAll(" ", "");
        return Integer.parseInt(line.substring(4, line.length()));
    }
    
    public int loadMapSizeX()
    {
        // ONLY ONCE because it is on the line that contains fps
        line = file.nextLine();
        line= line.replaceAll(" ", "");
        return Integer.parseInt(line.substring(9, line.length()));
    }
    
    public int loadMapSizeY()
    {
        // ONLY ONCE because it is on the line that contains mapSizeX:
        line = file.nextLine();
        line = line.replaceAll(" ", "");
        return Integer.parseInt(line.substring(9, line.length()));
    }
    
    public int loadThickness()
    {
        // ONLY ONCE because it is on the line that contains mapSizeY
        line = file.nextLine();
        line = line.replaceAll(" ", "");
        return Integer.parseInt(line.substring(10, line.length()));
    }
    
    public int loadSeed()
    {
        // ONLY ONCE because it is on the line that contains mapSizeY
        line = file.nextLine();
        line = line.replaceAll(" ", "");
        return Integer.parseInt(line.substring(5, line.length()));
    }
    
    public int loadImgScale()
    {
        line = file.nextLine();
        line = line.replaceAll(" ","");
        return Integer.parseInt(line.substring(9, line.length()));
    }
    
    public void close()
    {
        file.close();
    }
    
    public void saveSetting(String setting, String value)
    {
        try{
            writer.write(setting+value);
            writer.newLine();
        } catch (IOException e){
            System.out.println("Error when saving settings.");
        }
    }
    
    public void nextLine(int n)
    {
        for (int i = 0; i < n; i++)
            file.nextLine();
    }
    
    public void closeWriter()
    {
        try{
            writer.close();
        }catch (IOException e){
            System.out.println("IOException thrown while closing the writer.");
        }
    }
}