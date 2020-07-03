package GameFramework;

import java.io.Serializable;

public class MapNode implements Serializable
{
    private static final long serialVersionUID = 1L; 
    private int x;
    private int y;
    MapNode left, right, up, down;
    private boolean genResource = false; //For resource generation. Tells me whether I've tried generating for this tile/node before.

    public MapNode(int x, int y)
    {
            this.y = y;
            this.x = x;
    }

    //Setters
    public void setGenResource(boolean bool)
    {
            genResource = bool;
    }

    public MapNode setRight(Tile[][] board, int x, int y){
            right = board[y][x].getNode();
            right.setLeft(this);

            return this;
    }

    public void setRight(MapNode mn){
            right = mn;
    }

    public MapNode setLeft(Tile[][] board, int x, int y){
            left = board[y][x].getNode();
            left.setRight(this);

            return this;
    }

    public void setLeft(MapNode mn){
            left = mn;
    }

    public MapNode setUp(Tile[][] board, int x, int y){
            up = board[y][x].getNode();
            up.setDown(this);

            return this;
    }

    public void setUp(MapNode mn){
            up = mn;
    }

    public MapNode setDown(Tile[][] board, int x, int y){
            down = board[y][x].getNode();
            down.setUp(this);

            return this;
    }

    public void setDown(MapNode mn){
            down = mn;
    }

    //Getters

    public boolean hasGenResource(){
            return genResource;
    }

    public MapNode getRight(){
            return right;
    }

    public MapNode getLeft(){
            return left;
    }

    public MapNode getUp(){
            return up;
    }

    public MapNode getDown(){
            return down;
    }

    public int getX(){
            return x;
    }

    public int getY(){
            return y;
    }

    // Detection methods

    public boolean detectHasLeft(Tile[][] board, String biome, int x, int y)
    {
            //If out of bounds, will short circuit. If same biome type, return true.
            if ((x-1 > -1) && board[y][x - 1].equals(biome))
                    return true;
            else
                    return false;
    }

    public boolean detectHasRight(Tile[][] board, String biome, int x, int y){
            if ((x+1 < board[0].length) && board[y][x + 1].equals(biome))
            {
                    return true;
            }
            else
                    return false;
    }

    public boolean detectHasUp(Tile[][] board, String biome, int x, int y){
            if ((y - 1 > -1) && board[y - 1][x].equals(biome))
            {
                    return true;
            }
            return false;
    }

    public boolean detectHasDown(Tile[][] board, String biome, int x, int y){
            if ((y + 1 < board.length) && board[y + 1][x].equals(biome))
            {
                    return true;
            }
            else 
                    return false;
    }

    // Condition Checkers

    public boolean hasLeft()
    {
            if (left != null)
                    return true;
            else
                    return false;
    }

    public boolean hasRight()
    {
            if (right != null)
                    return true;
            else
                    return false;
    }

    public boolean hasUp()
    {
            if (up != null)
                    return true;
            else
                    return false;
    }

    public boolean hasDown()
    {
            if (down != null)
                    return true;
            else
                    return false;
    }
}
