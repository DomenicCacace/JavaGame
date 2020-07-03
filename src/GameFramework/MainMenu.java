package GameFramework;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainMenu extends JPanel 
{
    private static Tile sword = new Tile(76, 75, "sword.png", "sword");
    private static boolean inOptions = false, errorOccurred = false;
    private static JFrame frame;
    private static BufferedWriter writer = null;
    private static JButton optionsButton = new JButton("Options"), multiplayerButton = new JButton("Multiplayer"), startButton = new JButton("Start Game");
    private static JButton clientButton, hostButton;
    private static JTextArea textField;
    private static Client client;
    private static Server server;
    
    public static void main(String args[])
    {   
        //Creating the MainMenu Log
        //Creates a mainMenuLog.txt (if it does not already exist) to record progress
        File mainMenuLog;
        try {
	     mainMenuLog = new File("./mainMenuLog.txt");
	     /*If file gets created then the createNewFile() 
	      * method would return true or if the file is 
	      * already present it would return false
	      */
             boolean wasCreated = mainMenuLog.createNewFile();
	     if (wasCreated)
             {
	          System.out.println("File has been created successfully");
	     }
	     else
             {
	          System.out.println("File already present at the specified location");
	     }
    	} catch (IOException e) {
    		System.out.println("Exception Occurred:");
	        e.printStackTrace();
	}
        
        // Instantiating a writer to write to the log
        try{
            writer = new BufferedWriter(new FileWriter("./mainMenuLog.txt"));
        } catch (IOException e){
            System.out.println("Constructor IOException thrown.");
        }
        
        // -------------------------------------------------------------------------------
        
        //set inOptions to false for reinitialization
        inOptions = false;
        
        // Creating the JFrame
        frame = new JFrame("Game");
        frame.setResizable(false);
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        writeMainLog("Finished instantiating frame");
        
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                closeMainLog();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        
        //Playing mainmenu music
        Sound.DEBUG.play();
        Sound.DEBUG.setVolume(.25);
        
        // Creating the JPanel
        MainMenu menu = new MainMenu();
        menu.setBackground(new Color(176, 237, 242));
        menu.setSize(400,400);
        menu.setLayout(null);
        writeMainLog("Finished instantiating JPanel");
        
        startButton.setBounds(50,30,100,50);
        startButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                writeMainLog("Starting main game.");
                Sound.BUTTONPRESS.play();
                frame.dispose();
                Game.main(null);
            }
        });
        
        multiplayerButton.setBounds(150, 100, 100, 50);
        multiplayerButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                writeMainLog("Opening multiplayer menu...");
                
                Sound.BUTTONPRESS.play();
                
                //Re-using of old JPanel
                menu.remove(startButton);
                menu.remove(optionsButton);
                menu.remove(multiplayerButton);
                inOptions = true;
                
                //Adding Debug Components
                textField = new JTextArea("Welcome, Dom!", 30, 30);
                textField.setBounds(10, 10, 300, 300);
                menu.add(textField);
                
                clientButton = new JButton("Client");
                clientButton.setBounds(320, 10, 70, 20);
                clientButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e)
                    {
                        try{
                            writeMainLog("Attempting to create client object");
                            textField.append("\nAttempting to create client object");
                            client = new Client(InetAddress.getByName("localhost"), 9876);
                            textField.append("\nSuccess");
                        } catch (UnknownHostException uhe)
                        {
                            textField.append("\nUnknownHostException thrown. Client object failed to create.");
                            writeMainLog("UnknownHostException thrown. Client object failed to create.");
                        }
                        
                        try{
                            textField.append("\nSending packet");
                            client.send("I am a packet", InetAddress.getByName("localhost"), 9876);
                            textField.append("\nSuccess");
                        } catch(UnknownHostException uhe){
                            textField.append("\nFailed.");
                            writeMainLog("UnknownHostException occurred. Sending packet failed.");
                        }
                    }
                });
                menu.add(clientButton);
                
                hostButton = new JButton("Host");
                hostButton.setBounds (320, 40, 70, 20);
                hostButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e)
                    {
                        server = new Server(9876);
                        textField.append("\nServer started.");
                    }
                });
                menu.add(hostButton);
                
                //Refreshing screen
                frame.revalidate();
                
                writeMainLog("Finished removing buttons from JPanel.");
            }
        });
        
        optionsButton.setBounds(230,30,100,50);
        optionsButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {   
                writeMainLog("Opening Options Menu");
                
                //playing sound
                Sound.DEBUG.setVolume(.05);
                Sound.BUTTONPRESS.play();
                
                //Sets up an option for SettingsHandler for later
                SettingsHandler settingsInput = new SettingsHandler();
                writeMainLog("Successfully instantiated SettingsHandler, settingsInput open.");
                
                //Set inOptions to true
                inOptions = true;
                
                //Re-using of old JPanel
                menu.remove(startButton);
                menu.remove(optionsButton);
                menu.remove(multiplayerButton);
                writeMainLog("Finished removing buttons from JPanel.");
                
                // Menu Components
                JRadioButton fps60 = new JRadioButton("60 FPS");
                JRadioButton fps120 = new JRadioButton("120 FPS");
                JButton saveButton = new JButton("Save & Exit");
                JTextField mapSizeX = new JTextField();
                JTextField mapSizeY = new JTextField();
                JTextField thickness = new JTextField();
                JLabel mapYLabel = new JLabel("Map Size Y (Min:14)");
                JLabel mapXLabel = new JLabel("Map Size X (Min:24)");
                JLabel mapWarning = new JLabel("Warning: The larger the map, the longer the loading time.");
                JLabel thicknessLabel = new JLabel("Thickness (1-5)");
                JLabel warning = new JLabel("Game MUST restart for changes to take effect.");
                JLabel fpsLabel = new JLabel("FPS Options");
                JTextField seed = new JTextField();
                JLabel seedLabel = new JLabel("Seed");
                JTextField imgScale = new JTextField();
                JLabel imgScaleLabel = new JLabel("Img Scale");
                
                writeMainLog("Finished creating menu components");
                
                // Menu Component initialization
                fps60.setBounds(10, 30, 100, 20);
                fps60.setBackground(new Color(176, 237, 242));
                
                fps120.setBounds(10, 50, 100, 20);
                fps120.setBackground(new Color(176, 237, 242));
                if (settingsInput.loadFPS() == 60)
                    fps60.setSelected(true);
                else
                    fps120.setSelected(true);
                
                fpsLabel.setBounds(10,0,100,30);
                
                warning.setBounds(10,330,300,30);
                
                mapXLabel.setBounds(10, 70, 120, 20);
                mapSizeX.setBounds(10, 90, 70, 20);
                mapSizeX.setText(""+settingsInput.loadMapSizeX());
                mapYLabel.setBounds(10, 110, 120, 20);
                mapSizeY.setBounds(10, 130, 70, 20);
                mapSizeY.setText(""+settingsInput.loadMapSizeY());
                mapWarning.setBounds(10,150,350, 20);
                thicknessLabel.setBounds(10, 170, 100, 20);
                thickness.setBounds(10, 190, 40, 20);
                thickness.setText(""+settingsInput.loadThickness());
                seedLabel.setBounds(10, 210, 50, 20);
                seed.setBounds(10, 230, 70, 20);
                seed.setText("" + settingsInput.loadSeed());
                
                imgScaleLabel.setBounds (10, 250, 100, 20);
                imgScale.setBounds(10, 270, 100, 20);
                imgScale.setText("" + settingsInput.loadImgScale());
                
                writeMainLog("Finished component initialization and customization.");

                //Done loading. Closing the settingsInput and setting it to null.
                settingsInput.close();
                settingsInput = null;
                
                saveButton.setBounds(282,330,100,30);
                saveButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {   
                        writeMainLog("Beginning to save settings.");
                        
                        //This is created specifically for SettingsOutput
                        SettingsHandler settingsOutput = new SettingsHandler("config.ini");
                        writeMainLog("Instantiated settingsOuput successfully.");
                        
                        //Scans through current options and sends the current 
                        //settings
                        if(fps60.isSelected())
                        {
                            settingsOutput.saveSetting("fps:", "60");
                            writeMainLog("Saved 60 FPS Setting.");
                        }
                        else if (fps120.isSelected())
                        {
                            settingsOutput.saveSetting("fps:", "120");
                            writeMainLog("Saved 120 FPS Setting.");
                        }
                        //Saving the Map X Size
                        try{
                            int temp = Integer.parseInt(mapSizeX.getText());
                            if(temp <= 23)
                                settingsOutput.saveSetting("mapSizeX:", "24");
                            else
                                settingsOutput.saveSetting("mapSizeX:",mapSizeX.getText());
                        }catch (NumberFormatException ne){
                            writeMainLog("Number Format Exception thrown for Map X Size.");
                            if  (!errorOccurred)
                            {
                                errorOccurred = true;
                                errorNote();
                            }
                        }
                        writeMainLog("Saved Map X Size.");
                        
                        //Saving the Map Y Size
                        try{
                            int temp = Integer.parseInt(mapSizeY.getText());
                            if(temp <= 13)
                                settingsOutput.saveSetting("mapSizeY:", "14");
                            else
                                settingsOutput.saveSetting("mapSizeY:",mapSizeX.getText());
                        }catch (NumberFormatException ne){
                            writeMainLog("Number Format Exception thrown for Map Y Size.");
                            if  (!errorOccurred)
                            {
                                errorOccurred = true;
                                errorNote();
                            }
                        }
                        writeMainLog("Saved MapY Size.");
                        
                        
                        // Saving the thickness 
                        try{
                            int num = Integer.parseInt(thickness.getText());
                            if(num <= 0)
                                settingsOutput.saveSetting("thickness:", "1");
                            else if (num >= 6)
                                settingsOutput.saveSetting("thickness:", "5");
                            else
                                settingsOutput.saveSetting("thickness:", thickness.getText());
                        }catch (NumberFormatException ne){
                            writeMainLog("Number Format Exception wasthrown for the thickness.");
                            if (!errorOccurred)
                            {
                                errorOccurred = true;
                                errorNote();
                            }
                        }
                        writeMainLog("Saved selector thickness.");
                        
                        // Saving the seed 
                        try{
                            int num = Integer.parseInt(seed.getText());
                            if(num <= 0)
                                settingsOutput.saveSetting("seed:", "1");
                            else if (num >= Integer.MAX_VALUE)
                                settingsOutput.saveSetting("seed:", "" + (Integer.MAX_VALUE - 1));
                            else
                                settingsOutput.saveSetting("seed:", seed.getText());
                        }catch (NumberFormatException ne){
                            writeMainLog("Number Format Exception was thrown for the seed.");
                        }
                        
                        // Saving the imgScale 
                        try{
                            int num = Integer.parseInt(imgScale.getText());
                            if(num <= 0)
                                settingsOutput.saveSetting("imgScale:", "1");
                            else if (num >= 3)
                                settingsOutput.saveSetting("imgScale:", "2");
                            else
                                settingsOutput.saveSetting("imgScale:", imgScale.getText());
                        }catch (NumberFormatException ne){
                            writeMainLog("Number Format Exception was thrown for the imgScale.");
                        }
                        writeMainLog("Saved image scale.");
                        
                        writeMainLog("Beginning to recreate main menu and closing writer.");
                        
                        //Starts main window & closes the writer
                        errorOccurred = false;
                        inOptions = false;
                        settingsOutput.closeWriter();
                        
                        menu.removeAll();
                        menu.add(startButton);
                        menu.add(optionsButton);
                        menu.add(multiplayerButton);
                        menu.repaint();
                        
                        Sound.DEBUG.setVolume(.25);
                    }
                });
                
                //Adding stuff to other stuff
                ButtonGroup fpsGroup = new ButtonGroup();
                fpsGroup.add(fps60);
                fpsGroup.add(fps120);
                
                menu.add(fpsLabel);
                menu.add(fps60);
                menu.add(fps120);
                menu.add(saveButton);
                menu.add(warning);
                menu.add(mapXLabel);
                menu.add(mapYLabel);
                menu.add(mapSizeX);
                menu.add(mapSizeY);
                menu.add(mapWarning);
                menu.add(thickness);
                menu.add(thicknessLabel);
                menu.add(seed);
                menu.add(seedLabel);
                menu.add(imgScaleLabel);
                menu.add(imgScale);
                
                frame.add(menu);
                
                menu.repaint();
                
                writeMainLog("Finished adding all components to menu JPanel.");
            }
        });
        
        // Add components to the JFrame & Panel
        menu.add(startButton);
        menu.add(optionsButton);
        menu.add(multiplayerButton);
        frame.add(menu);
        
        menu.repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (!inOptions)
            g.drawImage(sword.getTexture(), 150, 200, null);
    }
    
    public static void errorNote()
    {
        JPanel p = new JPanel();
        p.setSize(500,100);
        
        JFrame popup = new JFrame();
        JLabel errorNote = new JLabel("1 or more errors occurred while saving settings");
        
        JButton exitB = new JButton("Exit");
        exitB.setBounds(100,50,50,50);
        exitB.setLocation(200,50);
        exitB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                popup.dispose();
            }
        });
        
        p.add(errorNote);
        p.add(exitB);
        popup.add(p);
        popup.setSize(500,100);
        popup.setVisible(true);   
    }
    
    public static void writeMainLog(String s)
    {
        try{
            writer.write(s+"\n");
        }catch(IOException ioe){
            //Not like its going to do anything if it fails.
        }
    }
    
    public static void closeMainLog()
    {
        try{
            writer.close();
        }catch(IOException ioe){
            //Not like its going to do anything if it fails.
        }
    }
}