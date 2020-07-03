package GameFramework;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class Game extends JPanel
{
    private static Tile[][] board;
    private static int mapSizeX, mapSizeY, thickness, fps, seed;
    private static int selectorX = 256, selectorY = 256, pixelDimension = 64, turnNumber = 1;
    private static int currentTileX = 4, currentTileY = 4, drawTroopX, drawTroopY, originalTroopX, originalTroopY; //CurrentTroopXY are tile locations
    private static boolean isInMenu = false, isMovingTroop = false, devToolsEnabled = false;
    private static BufferedWriter writer = null;
    private static double scale = 1;
    private static Troop currentTroop = null, troopPlaceholder = null;
    private static JButton nextTurnButton = new JButton("Next Turn");
    private static HashMap<Integer, Troop> troopMap = new HashMap<Integer, Troop>();
    private static ActionMap actionMap;
    private static Game game;
    
    //leftmost X, rightmost x, topmost y, bottommost y
    private static int[] currentMapView = {0,19,0,11};

    //Selector Menu Components 
    private static JPanel optionsPanel = new JPanel();
    private static JButton moveButton = new JButton("Move"), closeButton = new JButton("Close"), specialButton = new JButton("Special");;
   
    public Game()
    {   
        //Creates a log.txt (if it does not already exist) to record progress
        File log;
        try {
	     log = new File("./log.txt");
	     /*If file gets created then the createNewFile() 
	      * method would return true or if the file is 
	      * already present it would return false
	      */
             boolean wasCreated = log.createNewFile();
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
            writer = new BufferedWriter(new FileWriter("./log.txt"));
        } catch (IOException e){
            System.out.println("Constructor IOException thrown.");
        }
        
        setSize(1200,700);
        writeToLog("Successfully set Game object size in class Game at line 63.");
        
        SettingsHandler settingsLoader = new SettingsHandler();
        writeToLog("SettingsLoader in class Game at line 72 successfully instantiated.");
        
        fps = settingsLoader.loadFPS();
        writeToLog("SettingsLoader successfully loaded FPS.");
        mapSizeX = settingsLoader.loadMapSizeX();
        writeToLog("SettingsLoader successfully loaded MapSizeX.");
        mapSizeY = settingsLoader.loadMapSizeY();
        writeToLog("SettingsLoader successfully loaded MapSizeY.");
        thickness = settingsLoader.loadThickness();
        writeToLog("SettingsLoader successfully loaded Selector Thickness <settingsLoader.loadThickness()>.");
        seed = settingsLoader.loadSeed();
        writeToLog("SettingsLoader successfully loaded seed: " + seed);
        
        settingsLoader.close();
        settingsLoader = null;
        //It isn't needed anymore, so I delete it.
        
        writeToLog("SettingsLoader successfully loaded all settings.");
        
        //Setting the seed
        Noise.changeSeed(seed);
        Noise.doInit();
        
        //board = MapGenerator.generateDebugMap(mapSizeX, mapSizeY);
        board = MapGenerator.generateMapFromNoise(mapSizeX, mapSizeY);
        writeToLog("Board creation/initialization complete.");
        
        //generating resources
        writeToLog("Beginning resource generation process ------------------------------------------------------.");
        board = MapGenerator.generateResources(board);
        writeToLog("Resource generation process complete.");
        
        //Creating settler for the player.
        createTroop(6, 6, "SettlerTroop", "Blue");
        createTroop(1, 1, "SettlerTroop", "Blue");  
        board[2][2].setBuilding(new Settlement("Blue"));
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        int xDraw = 0, yDraw = 0;
        
        //Graphics 2D Object instantiation for custom selector thickness
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(thickness));
        
        for(int y = currentMapView[2]; y < currentMapView[3]; y++)
        {
            for(int x = currentMapView[0]; x < currentMapView[1]; x++)
            {
                //DrawImage(image, where to print x, where to print y)
                g.drawImage(board[y][x].getTexture(), xDraw, yDraw, pixelDimension, pixelDimension, null);
                
                //Drawing Resources
                if(board[y][x].hasResource())
                {
                    g.drawImage(board[y][x].getResource().getTexture(), xDraw, yDraw, pixelDimension, pixelDimension, null);
                }
                
                //Drawing Buildings
                if(board[y][x].hasBuilding())
                {
                    g.drawImage(board[y][x].getBuilding().getTexture(), xDraw, yDraw, pixelDimension, pixelDimension, null);
                }
                
                //Drawing Troops
                if(board[y][x].hasTroop())
                {
                    g.drawImage(board[y][x].getTroop().getTexture(), xDraw, yDraw, pixelDimension, pixelDimension, null);
                }
                
                xDraw += 64 * scale;
            }
            xDraw = 0;
            yDraw += 64 * scale;
        }
        
        //Drawing the Selector with user specified thickness and associated tile properties
        g.setColor(Color.BLACK);
        g2d.drawRect((int)(scale * selectorX), (int)(scale * selectorY), pixelDimension, pixelDimension);
        
        // Drawing the movement radius for troop, if it exists. (And highlighted by the selector)
        if (board[currentTileY][currentTileX].hasTroop() && !board[currentTileY][currentTileX].getTroop().hasMoved)
        {
            currentTroop = board[currentTileY][currentTileX].getTroop();

            /*
                Here's how this works:
                1) I get the X+Y coords of wherever the character is.
                2) I subtract the moveDistance*pixelDimension to see where I need to start drawing the movement radius
                (In this way, I can get negative coords)
                3) I draw radius at tempX tempY and double the distance so that it makes a perfect rectangle
            */
            int tempX = ((currentTileX - currentMapView[0]) * pixelDimension) - (currentTroop.getMoveDistance() * pixelDimension);
            int tempY = ((currentTileY - currentMapView[2]) * pixelDimension) - (currentTroop.getMoveDistance() * pixelDimension);
            
            g2d.setColor(Color.YELLOW);
            g2d.drawRect(tempX, tempY, ((currentTroop.getMoveDistance() * 2) + 1) * pixelDimension, ((currentTroop.getMoveDistance() * 2) + 1) * pixelDimension);
            g2d.setColor(Color.BLACK);
            
        }
        
        //The player is trying to move the troop. Do the following!
        if (isMovingTroop)
        {   
            //Keep red highlight box around the original troop location
            int originalPosXDraw = ((originalTroopX - currentMapView[0]) * pixelDimension) - (currentTroop.getMoveDistance() * pixelDimension);
            int originalPosYDraw = ((originalTroopY - currentMapView[2]) * pixelDimension) - (currentTroop.getMoveDistance() * pixelDimension);
            
            g2d.setColor(Color.RED);
            g2d.drawRect(((originalTroopX - currentMapView[0]) * pixelDimension), ((originalTroopY - currentMapView[2]) * pixelDimension), pixelDimension, pixelDimension);
            
            //Keep movement radius on the screen
            
            g2d.setColor(Color.YELLOW);
            g2d.drawRect(originalPosXDraw, originalPosYDraw, ((currentTroop.getMoveDistance() * 2) + 1) * pixelDimension, ((currentTroop.getMoveDistance() * 2) + 1) * pixelDimension);
            
            //Get where the troop should be drawn.
            //DrawTroop XY are assigned to the original troop location in the moveButton action listener
            g.drawImage(troopPlaceholder.getTexture(), drawTroopX, drawTroopY, pixelDimension, pixelDimension, null);
        }
        
        if (devToolsEnabled)
        {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0,0,100,700);
            g.setColor(Color.black);
            g.drawString("X: " + currentTileX, 0, 20);
            g.drawString("Y: " + currentTileY, 0, 40);
            g.drawString("MapXL:"+currentMapView[0], 0, 60);
            g.drawString("MapXR:"+currentMapView[1], 0, 80);
            g.drawString("MapYU:"+currentMapView[2], 0, 100);
            g.drawString("MapYB:"+currentMapView[3], 0, 120);
            g.drawString("Scale:"+scale, 0, 140);
            g.drawString("PixDim:"+pixelDimension, 0, 160);
            g.drawString("SelectorX:"+selectorX, 0, 180);
            g.drawString("SelectorY:"+selectorY, 0, 200);
            g.drawString("inMenu:"+isInMenu, 0, 220);
            g.drawString("Biome:"+board[currentTileY][currentTileX].getBiome(), 0, 240);
            g.drawString("Bldg:"+board[currentTileY][currentTileX].hasBuilding(), 0, 260);
            g.drawString("Rsrc:"+board[currentTileY][currentTileX].hasResource(), 0, 280);
            g.drawString("Troop:"+board[currentTileY][currentTileX].hasTroop(), 0, 300);
        }
    }    
    
    public static void main(String args[])
    {
        //Creating the JFrame
        JFrame frame = new JFrame("Game");
        frame.setSize(1220,740);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        
        Sound.DEBUG.stop();
        
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
 
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close the game?", "Exit Game", JOptionPane.YES_NO_OPTION);
 
                if (result == JOptionPane.YES_OPTION)
                {
                    closeLog();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });
        
        //Creating a game object
        game = new Game();
        game.setLocation(0,0);
        game.setLayout(null);
        frame.add(game);
        
        // Keyboard input setup. InputMaps set via Binds class
        Binds binds = new Binds(game);
        //ActionMap object declared here so all ActionMaps can see it.
        actionMap = game.getActionMap();
        
        /*
            Creating mouse listener.
            Fun fact: even though my mouse object is both a motionListener and and listener,
            I still need to tell my frame to look in the same place for both listeners.
            They're two different variables for the frame class, I imagine.
        */
        Mouse mouse = new Mouse();
        frame.addMouseListener(mouse);
        frame.addMouseMotionListener(mouse);
              
        //These are here because the selector menu does not work properly otherwise.
        //Originally, I had them inside the ENTER actionMap, however, this proved faulty.
        
        //Setting up on-screen components 
        nextTurnButton.setBounds(600, 680, 100, 20);
        nextTurnButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Sound.BUTTONPRESS.play();
                endTurn();
            }
        });
        game.add(nextTurnButton);
        
        optionsPanel.setSize(300, 300);
        optionsPanel.setBackground(Color.darkGray);
        optionsPanel.setLayout(new GridLayout(6, 2));
        
        Thread gameInstance = new Thread(){
            @Override
            public void run()
            {
                long oldTime = System.nanoTime();
                double timePerFrame = 1000000000 / fps;
                double deltaTime = 0;
                //int frames = 0;
                
                
                while(true)
                {
                    long currentTime = System.nanoTime();
                    deltaTime += (currentTime - oldTime) / timePerFrame;
                    oldTime = currentTime;
                    if (deltaTime >= 1) 
                    {
                        //System.out.println(frames);
                        deltaTime--;
                        //frames++;
                        game.repaint();
                    }
                }
            }
        }; 
        gameInstance.start();
        
        // --------------------- Action Map Binds -----------------------
        
        // ActionMap binds go here because they need declarations from above.
        
        actionMap.put("ENTER", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                enterBindCode();
            }
        });
        actionMap.put("MOVE_LEFT", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {   
                moveLeftCode();
            }
        });
        actionMap.put("MOVE_RIGHT", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                moveRightCode();
            }
        });
        actionMap.put("MOVE_UP", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                moveUpCode();
            }
        });
        actionMap.put("MOVE_DOWN", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                moveDownCode();
            }
        });
        
        actionMap.put("DEVTOOLS", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                devToolsBind();
            }
        });
        actionMap.put("ZOOM_IN", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                zoomInCode();
            }
        });
        actionMap.put("ZOOM_OUT", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                zoomOutCode();
            }
        });
        
        actionMap.put("ESCAPE", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                escapeCode();
            }
        });
        
        actionMap.put("DEBUG", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                debugBind();
                
            }
        });
    }
    
    // ------------- Game Related Methods -------------------
    
    public static void endTurn()
    {
        writeToLog("Ending turn " + turnNumber);
        
        for (Troop troop : troopMap.values())
        {
            troop.setHasMoved(false);
        }
        
        turnNumber++;
    }
    
    public static void createTroop(int x, int y, String troopType, String player)
    {
        Troop placeholder;
        
        if(troopType.equals("SettlerTroop"))
        {
            placeholder = new SettlerTroop(player);
            troopMap.put(placeholder.getID(), placeholder);
            board[y][x].setTroop(placeholder);
        }
        else if(troopType.equals("WorkerTroop"))
        {
            placeholder = new WorkerTroop(player);
            troopMap.put(placeholder.getID(), placeholder);
            board[y][x].setTroop(placeholder);
        }
    }
    
    public static void killTroop(int x, int y)
    {
        troopMap.remove(board[y][x].getTroop().getID());
        board[y][x].setTroop(null);
    }
    
    public static void writeToLog(String message)
    {
        try{
            writer.write(message+"\n");
        } catch (IOException e){
            System.out.println("Error writing to the log. IOException thrown.");
        }
    }
    
    public static void closeLog()
    {
        try{
            writer.close();
        } catch (IOException e){
            System.out.println("Error closing the log. IOException thrown.");
        }
    }
    
    public static void saveMapToFile()
    {
        //The map file extension is... map. Praise creativity! 
        
    }
    
    // --------------------- Setters -----------------------
    
    public static void setFPS(int n)
    {
        fps = n;
    }
    
    public static void setMouseSelectors(int newSelectorX, int newSelectorY, int newCurrentTileX, int newCurrentTileY)
    {
        selectorX = newSelectorX;
        selectorY = newSelectorY;
        currentTileX = newCurrentTileX;
        currentTileY = newCurrentTileY;
    }
    
    public static void setDrawTroopXY(int x, int y)
    {
        drawTroopX = x;
        drawTroopY = y;
    }
    
    public static void setCurrentTileXY(int x, int y)
    {
        currentTileX = x;
        currentTileY = y;
    }
    
    // ------------------- Boolean Checks / isMethods -------------
    
    public static boolean isInMenu()
    {
        return isInMenu;
    }
    
    public static boolean isMovingTroop()
    {
        return isMovingTroop;
    }
    
    // ------------------- Getters --------------------
    
    public static int[] getMouseSelectorInfo()
    {
        //All required for mouse selector caluclations. 
        int[] ret = {pixelDimension, selectorX, selectorY, currentTileX, currentTileY, currentMapView[0], currentMapView[2]};
        return ret; 
    }
    
    public static JPanel getOptionsPanel()
    {
        return optionsPanel;
    }
    
    public static int[] getMovingTroopInfo()
    {
        int[] ret = {originalTroopX, originalTroopY, troopPlaceholder.getMoveDistance(), currentMapView[0], currentMapView[2], pixelDimension};
        return ret;
    }
    
    public static int getFPS()
    {
        return fps;
    }
    
    // ------------------------ Action Map Code ------------------------
    
    public static void enterBindCode()
    {
        if(board[currentTileY][currentTileX].hasTroop())
        {
            writeToLog("\n\nHasMoved: " + board[currentTileY][currentTileX].getTroop().hasMoved());
        }

        if (isMovingTroop)
        {
            writeToLog("ISMOVINGTROOP IS TRUE!!!!!!!!!!!!!!");
            if(board[currentTileY][currentTileX].hasTroop())
            {
                //Not allowed. Put back in original location, allow it to be moved again.
                writeToLog("Troop placement on a troop. Not allowed. Placement denied.");
                
                board[originalTroopY][originalTroopX].setTroop(troopPlaceholder);
                isMovingTroop = false;
                troopPlaceholder = null;
            }
            else if (originalTroopX == currentTileX && originalTroopY == currentTileY)
            {
                board[originalTroopY][originalTroopX].setTroop(troopPlaceholder);
                troopPlaceholder = null;
                isMovingTroop = false;
            }
            else
            {
                troopPlaceholder.setHasMoved(true);
                board[currentTileY][currentTileX].setTroop(troopPlaceholder);
                isMovingTroop = false;
                troopPlaceholder = null;
            }
        }
        else if (!isInMenu)
        {
            Game.writeToLog("Interacting with tile (" + currentTileX+", " + currentTileY + "). <ENTER action key started>");

            // First removing non-permanent buttons
            optionsPanel.removeAll();
            optionsPanel.add(closeButton);

            //Getting proper display bounds
            if((currentMapView[1] - currentTileX - 1) <= Math.ceil(300 / pixelDimension) && (currentMapView[3] - currentTileY) <= Math.ceil(300 / pixelDimension))
            {
                if (pixelDimension == 64)
                {
                    optionsPanel.setLocation(selectorX - 300, 400);
                }
                else
                {
                    optionsPanel.setLocation((int)(selectorX * scale) - 300, 400);
                }
            }
            else if ((currentMapView[1] - currentTileX - 1) <= Math.ceil(300 / pixelDimension))
            {
                //Only the X is out of bounds
                if (pixelDimension == 64)
                {
                    optionsPanel.setLocation(selectorX - 300, selectorY);
                }
                else
                {
                    optionsPanel.setLocation((int)(selectorX * scale) - 300, (int)(selectorY * scale));
                }
            }
            else if ((currentMapView[3] - currentTileY) <= Math.ceil(300 / pixelDimension))
            {
                if (pixelDimension == 64)
                {
                    optionsPanel.setLocation(selectorX + pixelDimension, 400);
                }
                else
                {
                    optionsPanel.setLocation((int)(selectorX * scale) + pixelDimension, 400);
                }
            }
            else
            {
                if(pixelDimension == 64)
                {
                    optionsPanel.setLocation(selectorX + pixelDimension, selectorY);
                }
                else
                {
                    optionsPanel.setLocation((int)(selectorX * scale) + pixelDimension, (int)(selectorY * scale));
                }
            }

            //Starting to determine what buttons are to be placed in the options window
            //Oh boy. The fun is about to begin.

            /*
                    !!!!!! IMPORTANT NOTE !!!!!!

                I do not add the close button in any If. I add the close
                button regardless, because that will always be an option
            */

            if (board[currentTileY][currentTileX].hasBuilding() && board[currentTileY][currentTileX].hasTroop())
            {
                //idk
            }
            else if (board[currentTileY][currentTileX].hasBuilding())
            {

            }
            else if (board[currentTileY][currentTileX].hasTroop())
            {
                Game.writeToLog("Creating options menu with Troop options.");

                //Determines if that troop has moved. it enables the move button accordingly
                if (board[currentTileY][currentTileX].getTroop().hasMoved())
                {  
                    Game.writeToLog("Troop has moved. Disabling the button.");
                    moveButton.setEnabled(false);
                }
                else
                {
                    Game.writeToLog("Troop has not moved. Enabling button.");
                    moveButton.setEnabled(true);
                }

                moveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        Sound.BUTTONPRESS.play();
                        
                        isMovingTroop = true;
                        originalTroopY = currentTileY;
                        originalTroopX = currentTileX;
                        drawTroopX = currentTileX;
                        drawTroopY = currentTileY;
                        
                        Game.writeToLog("MovingTroop:"+isMovingTroop);
                        Game.writeToLog("originalTroopX: " + originalTroopX);
                        Game.writeToLog("currentTroopY: " + originalTroopY);
                        
                        troopPlaceholder = currentTroop;
                        board[originalTroopY][originalTroopX].setTroop(null);
                        
                        game.remove(optionsPanel);
                        isInMenu = false;
                    }
                });
                
                // Adding special button, which handles troop-specific operations
                if(board[currentTileY][currentTileX].getTroop() instanceof SettlerTroop)
                {
                    specialButton.setText("Place Settlement");
                    if(board[currentTileY][currentTileX].getTroop().hasMoved())
                        specialButton.setEnabled(false);
                    else
                        specialButton.setEnabled(true);

                    specialButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            Sound.BUTTONPRESS.play();
                            Sound.CONSTRUCT.play();

                            Game.writeToLog("Placing settlement at X:" + currentTileX + ", Y: " + currentTileY + " for player --");

                            board[currentTileY][currentTileX].setBuilding(new Settlement("Blue"));
                            killTroop(currentTileX, currentTileY);
                            createTroop(currentTileX, currentTileY, "WorkerTroop", "Blue");

                            game.remove(optionsPanel);
                            isInMenu = false;
                        }
                    });
                }

                optionsPanel.add(moveButton);
                optionsPanel.add(specialButton);

                /*
                //Because I use removeAll(). Here's why from StackOverflow:
                revalidate() just request to layout the container, when you 
                experienced simply call revalidate() works, it could be caused 
                by the updating of child components bounds triggers the repaint() 
                when their bounds are changed during the re-layout.
                */

                game.revalidate();

                //Adding filler spots for close button
                for(int i = 0; i < 9; i++)
                {
                    optionsPanel.add(new JLabel(""));
                }
            }

            //Re-add closeButton
            optionsPanel.add(closeButton);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    Sound.BUTTONPRESS.play();
                    game.remove(optionsPanel);
                    isInMenu = false;
                }
            });

            //Add the finished panel to the game object, game
            game.add(optionsPanel);
            isInMenu = true;
        }
        else
        {
            game.remove(optionsPanel);
            isInMenu =  false;
        }
    }
    
    public static void moveLeftCode()
    {
        if (currentTileX == 0)
        {
            //Do nothing! You're already at the leftmst edge.
        }
        else if (currentTileX != 0 && currentMapView[0] == 0)
        {
            //When mapView is at left map border, but selector is not at border(just move selector left)

            //But, if player is moving troop, check if it is within movement radius
            if (isMovingTroop)
            {
                if(originalTroopX - currentTroop.getMoveDistance() != currentTileX)
                {
                    //It's within bounds! Let them move.
                    selectorX -= 64;
                    currentTileX -= 1;
                }
                //Otherwise, don't let them move.
            }
            else
            {
                //Normal Movement
                selectorX -= 64;
                currentTileX -= 1;
            }
        }
        else if (currentTileX == currentMapView[0])
        {
            //For moving left & changing both currentMapView vals
            if(isMovingTroop && (originalTroopX - currentTroop.getMoveDistance()) == currentTileX)
            {
                //Do nothing!
            }
            else if (isMovingTroop && (originalTroopX - currentTroop.getMoveDistance()) != currentTileX)
            {
                currentMapView[0] -= 1;
                currentMapView[1] -= 1;

                currentTileX -= 1;
            }
            else
            {
                //For normal movement 
                currentMapView[0] -= 1;
                currentMapView[1] -= 1;

                currentTileX -= 1;
            }
        }
        else
        {
            //Normal movement without affecting currentMapView
            if (isMovingTroop)
            {
                //Check if within movement radius
                if (originalTroopX - currentTroop.getMoveDistance() != currentTileX)
                {
                    //Within bounds! Let em' at it.
                    selectorX -= 64;
                    currentTileX -= 1;
                }
            }
            else
            {
                //Normal movement 
                selectorX -= 64;
                currentTileX -= 1;
            }
        }

        if(isInMenu)
        {
            game.remove(optionsPanel);
            isInMenu = false;
        }
    }
    
    public static void moveRightCode()
    {
        if (currentTileX == mapSizeX - 1)
        {
            //Do nothing! At the right of the map.
        }
        else if (currentTileX != mapSizeX - 1 && currentMapView[1] == mapSizeX)
        {
            //When at the right of the screen, just moving selector

            //But, if player is moving troop, check if it is within movement radius
            if(isMovingTroop)
            {
                if (originalTroopX + currentTroop.getMoveDistance() != currentTileX)
                {
                    //Within bounds! Allow movement.
                    selectorX += 64;
                    currentTileX += 1;
                }
                //Otherwise, don't let them move.
            }
            else
            {
                //Normal Movement
                selectorX += 64;
                currentTileX += 1;
            }
        }
        else if (currentTileX == currentMapView[1] - 1)
        {
            //For moving right across the screen & changing both currentMapView vals

            if (isMovingTroop && (originalTroopX + currentTroop.getMoveDistance()) == currentTileX)
            {
                //Do nothing
            }
            else if (isMovingTroop && (originalTroopX + currentTroop.getMoveDistance()) != currentTileX)
            {
                currentMapView[0] += 1;
                currentMapView[1] += 1;

                currentTileX += 1;
            }
            else
            {
                //Normal Movement
                currentMapView[0] += 1;
                currentMapView[1] += 1;

                currentTileX += 1;
            }
        }
        else
        {
            //Normal movement without affecting currentMapView

            //Again, check if within movement radius.
            if (isMovingTroop)
            {
                if (originalTroopX + currentTroop.getMoveDistance() != currentTileX)
                {
                    //Good to go, sarge!
                    selectorX += 64;
                    currentTileX += 1;
                }
            }
            else
            {
                //Normal Movement
                selectorX += 64;
                currentTileX += 1;
            }
        }

        if(isInMenu)
        {
            game.remove(optionsPanel);
            isInMenu = false;
        }
    }
    
    public static void moveUpCode()
    {
        if (currentTileY == 0)
        {
            //Do nothing! At the top of the map.
        }
        else if (currentTileY != 0 && currentMapView[2] == 0)
        {
            //When at the top of the screen, just moving selector

            //But, if player is moving troop, check if it is within movement radius
            if(isMovingTroop)
            {
                if((originalTroopY - currentTroop.getMoveDistance()) != currentTileY)
                {
                    //Good to move! Allow movement.
                    selectorY -= 64;
                    currentTileY -= 1;
                }
            }
            else
            {
                //Normal Movement
                selectorY -= 64;
                currentTileY -= 1;
            }
        }
        else if (currentTileY == currentMapView[2])
        {
            //For moving up the screen & changing both currentMapView vals

            if (isMovingTroop && (originalTroopY - currentTroop.getMoveDistance()) == currentTileY)
            {
                //Do nothing!
            }
            else if (isMovingTroop && (originalTroopY - currentTroop.getMoveDistance()) != currentTileY)
            {
                currentMapView[2] -= 1;
                currentMapView[3] -= 1;

                currentTileY -= 1;
            }
            else
            {
                //Normal Movement
                currentMapView[2] -= 1;
                currentMapView[3] -= 1;

                currentTileY -= 1;
            }
        }
        else
        {
            //Normal movement without affecting currentMapView

            //Don't drop your guard! Check that movement radius!
            if (isMovingTroop)
            {
                if ((originalTroopY - currentTroop.getMoveDistance()) != currentTileY)
                {
                    //Go go go!
                    selectorY -= 64;
                    currentTileY -= 1;
                }
            }
            else
            {
                //Normal Movement
                selectorY -= 64;
                currentTileY -= 1;
            }
        }

        if(isInMenu)
        {
            game.remove(optionsPanel);
            isInMenu = false;
        }
    }
    
    public static void moveDownCode()
    {
        if (currentTileY == mapSizeY - 1)
        {
            //Do nothing! At the bottom of the map.
        }
        else if (currentTileY != mapSizeY - 1 && currentMapView[3] == mapSizeY)
        {
            //When at the bottom of the screen, just moving selector

            //But, if player is moving troop, check if it is within movement radius
            if (isMovingTroop)
            {
                if((originalTroopY + currentTroop.getMoveDistance()) != currentTileY)
                {
                    //Movement Allowed! Proceed.
                    selectorY += 64;
                    currentTileY += 1;
                }
            }
            else
            {
                //Normal Movement
                selectorY += 64;
                currentTileY += 1;
            }
        }
        else if (currentTileY == currentMapView[3] - 1)
        {
            //For moving down the screen & changing both currentMapView vals

            //Checks if moving mapView down AND checks if at the movement radius
            if (isMovingTroop && (originalTroopY + currentTroop.getMoveDistance()) == currentTileY)
            {
                //Do nothing!
            }
            else if (isMovingTroop && (originalTroopY + currentTroop.getMoveDistance()) != currentTileY)
            {
                currentMapView[2] += 1;
                currentMapView[3] += 1;

                currentTileY += 1;
            }
            else
            {
                //Normal Movement
                currentMapView[2] += 1;
                currentMapView[3] += 1;

                currentTileY += 1;
            }
        }
        else
        {
            //Normal movement without affecting currentMapView

            if (isMovingTroop)
            {
                if ((originalTroopY + currentTroop.getMoveDistance()) != currentTileY)
                {
                    //Within bounds! Allow movement
                    selectorY += 64;
                    currentTileY += 1;
                }
            }
            else
            {
                //Normal Movement 
                selectorY += 64;
                currentTileY += 1;
            }
        }

        if(isInMenu)
        {
            game.remove(optionsPanel);
            isInMenu = false;
        }
    }
    
    public static void zoomInCode()
    {
        if(scale < 2)
        {
            scale += .25;
            pixelDimension += 16;

            //X View
            currentMapView[1] = currentMapView[0] + (int)Math.ceil((double)1200 / pixelDimension);

            //Y View
            currentMapView[3] = currentMapView[2] + (int)Math.ceil((double)700/pixelDimension);
       }

        //Moves the selector with the zoom, if necessary
        //X
        if (currentTileX >= currentMapView[1] - 1)
        {
            selectorX -= Math.abs(currentTileX-currentMapView[1] + 3) * 64;
            currentTileX = currentMapView[1] - 3;
        }

        //Y
        if (currentTileY >= currentMapView[3] - 1)
        {
            selectorY -= Math.abs(currentTileY - currentMapView[3] + 3) * 64;
            currentTileY = currentMapView[3] - 3;
        }
        Game.writeToLog("Zooming. Scale: " + pixelDimension);
    }
    
    public static void zoomOutCode()
    {
        if(scale > .25)
        {
            scale -= .25;
            pixelDimension -= 16;

            //X View
            if (Math.ceil(1200 / pixelDimension) >= mapSizeX) //Checks if it will be out of bounds
            {
                currentMapView[0] = 0;
                currentMapView[1] = mapSizeX - 1;
            }
            else if (Math.ceil(1200 / pixelDimension) + currentMapView[0] >= mapSizeX)
            {
                //Checks if the player would zoom out of bounds, and changes mapViewXs/currentX/selectorX to stay inbounds.
                //Possible solution is to generate enough tiles such that if the user were to zoom out it would show more land
                //that just would not be accessbile to the player. Exclude non playable area from resourceGen and block them from accessing it
                currentMapView[0] = mapSizeX - (int)Math.ceil(1200 / pixelDimension) - 1;
                currentMapView[1] = mapSizeX;
                currentTileX = mapSizeX - 1;
                selectorX = (int)(1200 / pixelDimension) * 64;
            }
            else if (Math.ceil(1200 / pixelDimension) + currentMapView[0] <= mapSizeX) // Handle normal cases
            {
                currentMapView[1] = currentMapView[0] + (int)Math.ceil((double)1200 / pixelDimension);
            }                

            //Y View
            if (Math.ceil(700 / pixelDimension) >= mapSizeY) //Checks if it will be out of bounds
            {
                currentMapView[2] = 0;
                currentMapView[3] = mapSizeY;
            }
            else if (Math.ceil(700 / pixelDimension) + currentMapView[2] >= mapSizeY)
            {
                //Checks if the player would zoom out of bounds, and changes mapViewYs/currentY/selectorY to stay inbounds.
                currentMapView[2] = mapSizeY - (int)Math.ceil(700 / pixelDimension) - 1;
                currentMapView[3] = mapSizeY;
                currentTileY = mapSizeY - 1;
                selectorY = (int)(700 / pixelDimension) * 64;
            }
            else if (Math.ceil(700 / pixelDimension) + currentMapView[2] <= mapSizeY) // Handle normal cases
            {
                currentMapView[3] = currentMapView[2] + (int)Math.ceil((double)700 / pixelDimension);
            }                
        }
        Game.writeToLog("Zooming Out. Scale: " + scale);
    }
    
    public static void escapeCode()
    {
        if (isInMenu)
        {
            game.remove(optionsPanel);
            isInMenu = false;
        }
        else if (isMovingTroop)
        {
            board[originalTroopY][originalTroopX].setTroop(troopPlaceholder);
            isMovingTroop = false;
            troopPlaceholder = null;
        }
        Game.writeToLog("---------------------------------Escape");
        MapHandler.saveMap(board, "test");
    }
    
    public static void devToolsBind()
    {
        // Just gonna throw this out there: I have literally no clue what this is.
        //Maybe it is relevant? Who knows... ill look at it later.

        //Update, i think this enables and disables the devtools. which makes a lot of sense.
        if (devToolsEnabled)
            devToolsEnabled = false;
        else
            devToolsEnabled = true;
    }
    
    public static void debugBind()
    {
        MapHandler.saveMap(board, "test");
        Tile[][] b = MapHandler.loadMap();
        board = b;
    }
}