/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameFramework;

public class MapGenerator 
{
    private static double goldRSR = .02, treeRSR = .03, fishRSR = .02, coalRSR = .02;
    private static int treeNum = 0, treeMax = 100, goldNum = 0, goldMax = 100, fishNum = 0, fishMax = 10, coalNum = 0, coalMax = 10;
    private static double resourceSpawnRate = .05; //temp, from 0 - 1
    private static int stackDepth = 0;
    
    public static Tile[][] generateMapFromNoise(int mapSizeX, int mapSizeY)
    {
        Game.writeToLog("Beginning terrain generation. MapGenerator.generateMapFromNoise()");
        
        Tile board[][] = new Tile[mapSizeY][mapSizeX];
        double currentLowest = 0, currentHighest = 0;
        
        Game.writeToLog("Starting terrain gen loop.");
        double nx = -.5, ny = -.5;
        
	for (double y = 0; y < mapSizeY; y++)
        {
            for (double x = 0; x < mapSizeX; x++)
            {
                nx += .05;  
                
                double noise = Noise.noise(nx, ny);

		noise += Noise.noise(2 * nx, 2 * ny);
                noise += .5* Noise.noise(4 * nx, 4 * ny);
		noise += .25* Noise.noise(8 * nx, 8 * ny);
                
                
                if (currentLowest > noise)
                    currentLowest = noise;
                if (currentHighest < noise)
                    currentHighest = noise;

                if (noise <= -.4)
                    board[(int)y][(int)x] = new Tile((int)x, (int)y,"water.png", "water");// Water
                else if (noise <= -0.3)
                    board[(int)y][(int)x] = new Tile((int)x, (int)y, "basicSand.png", "sand"); // Beach
                else if (noise <= .35)
                    board[(int)y][(int)x] = new Tile((int)x, (int)y, "testGrass.png", "land"); // Land
                else
                    board[(int)y][(int)x] = new Tile((int)x, (int)y, "mountain.png", "mountain"); // Mountain
            }
            ny += .05;
            nx = -.5;
            
        }
        
        Game.writeToLog("Current Lowest: " + currentLowest);
        Game.writeToLog("Current highest " + currentHighest);
        
        return board;
    }
    
    public static Tile[][] generateDebugMap(int mapSizeX, int mapSizeY)
    {
        Tile board[][] = new Tile[mapSizeX][mapSizeY];
        
        for(int i = 0; i < mapSizeY; i++)
        {
            for (int j = 0; j < mapSizeX; j++)
            {
                board[i][j] = new Tile();
            }
        }
        
        return board;
    }
    
    public static Tile[][] generateResources(Tile[][] board)
    {
        //to prevent nodeClusters from being generated everytime

        //Begin Map Iteration for resource generation
        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[0].length; x++)
            {
                Tile tile = board[y][x];
                String biome = tile.getBiome();

                if (!tile.hasNode())
                {
                    Game.writeToLog("Tile at "+ x +", " + y + " has no node. Generating node cluster.");
                    board = createNodeCluster(tile, board, biome);
                }
                
                if (!tile.getNode().hasGenResource())
                {
                    if (board[y][x].getBiome().equals("land"))
                    {
                        board = resourceIteration(tile, board, biome, "tree");
                    }
                    else if (board[y][x].getBiome().equals("mountain"))
                    {
                        // Note: COAL IS NEVER GENERATED.
                        board = resourceIteration(tile, board, biome, "gold");
                    }
                    else if (board[y][x].getBiome().equals("water"))
                    {
                        board = resourceIteration(tile, board, biome, "fish");
                    }
                }
                stackDepth = 0;
            }
        }
        return board;
    }

    public static Tile[][] resourceIteration(Tile tile, Tile[][] board, String biome, String resource)
    {        
        //stackDepth++;
        //Game.writeToLog("StackDepth: "+stackDepth);
        
        int x = tile.getNode().getX();
        int y = tile.getNode().getY();

        //I basically just reiterate through said node cluster the same way I identified it. Generate on the fly.

        board[y][x].getNode().setGenResource(true);
        double spawnNum = Math.random();
        
        //I want to check what biome it is for proper assigning.
        /*The following ifs gets the proper resource via checking biome, 
        checks if the spawn num reaches the RSR, 
        and if the max num of that resource has been placed.
        */
        
        if (board[y][x].getBiome().equals("land") && (spawnNum < treeRSR) && (treeNum < treeMax))
        {
            //Game.writeToLog("Tree successfully generated at "+ x + ", " + y);
            board[y][x].setResource(x, y, "tree.png", "tree");
            treeNum++;
        }
        else if (board[y][x].getBiome().equals("mountain") && (spawnNum < goldRSR) && (goldNum < goldMax))
        {
            //Game.writeToLog("Gold successfully generated at " + x + ", " + y);
            board[y][x].setResource(x, y, "goldOre.png", "gold");
            goldNum++;
        }
        else if (board[y][x].getBiome().equals("water") && (spawnNum < fishRSR) && (fishNum < fishMax))
        {
            //Game.writeToLog("Fish successfully generated at "+ x + ", "+ y);
            board[y][x].setResource(x, y, "fish.png", "fish");
            fishNum++;
        }
        else if (board[y][x].getBiome().equals("mountain") && (spawnNum < coalRSR) && (coalNum < coalMax))
        {
            //Game.writeToLog("Coal successfully generated at "+ x + ", "+ y);
            board[y][x].setResource(x, y, "coal.png", "coal");
            coalNum++;
        }
        return board;
    }

    public static Tile[][] createNodeCluster(Tile tile, Tile[][] board, String biome)
    {
        int x = tile.getX();
        int y = tile.getY();
        //Is iterating correctly.

        //NOTE TO SELF: 
        /*
        the fact that I don't assign the node to a variable is not inefficient. it is merely to avoid the missing of an update. Doing it this way ensures that I have the most up-to-date version of the node, always. otherwise I don't know if the version of the tile I am working with is the up-to-date version, or if it is a past copy. 
        */

        if (board[y][x].getNode().detectHasLeft(board, biome, x, y))
        {
            if (!board[y][x].getNode().hasLeft())
            {
                //original: node.setLeft(board, x - 1, y);
                board[y][x].setNode(tile.getNode().setLeft(board, x - 1, y));

                //Originally did NOT update board. I simply called the recursive method 
                board = createNodeCluster(board[y][x-1], board, biome);
            }
        }
        if (board[y][x].getNode().detectHasRight(board, biome, x, y))
        {
            if (!board[y][x].getNode().hasRight())
            {
                board[y][x].setNode(tile.getNode().setRight(board, x + 1, y));
                board = createNodeCluster(board[y][x+1], board, biome);
            }
        }
        if (board[y][x].getNode().detectHasUp(board, biome, x, y))
        {
            if (!board[y][x].getNode().hasUp())
            {
                board[y][x].setNode(tile.getNode().setUp(board, x, y - 1));
                board = createNodeCluster(board[y-1][x], board, biome);
            }
        }
        if (board[y][x].getNode().detectHasDown(board, biome, x, y))
        {
            if (!board[y][x].getNode().hasDown())
            {
                board[y][x].setNode(tile.getNode().setDown(board, x, y + 1));
                board = createNodeCluster(board[y+1][x], board, biome);
            }
        }
        return board;
    }
}