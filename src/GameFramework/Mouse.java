
package GameFramework;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Point;
import javax.swing.JPanel;
        
public class Mouse implements MouseListener, MouseMotionListener
{
    //Getting mouse position accounts for the window, which the buffer removes. The unit is pixels.
    
    //iOS Screen Buffer constant
    //Windows Screen Buffer constant
    public static int IOSSCREENBUFFER = 26, WINSCREENBUFFERY = 30, WINSCREENBUFFERX = 7;
    int currentTileX, currentTileY, updatedX, updatedY, districtNum = 0;
    private int[] screenVals;
    private boolean troopOutOfBounds = false;
    
    
    String os = System.getProperty("os.name").toLowerCase();
    
    boolean isWindows = isWindows();
    
    public Mouse()
    {
        //Nothing to report, Sarge.
        //I have to have an object to assign the mouse listeners. There's nothing to put in the constructor, though.
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        Game.writeToLog("LOOK HERE. MY X: "+e.getPoint().getX() + ", Y: "+e.getPoint().getY()+", District: "+districtNum);
        
        if (Game.isMovingTroop())
        {
            if (troopOutOfBounds)
            {
                //Instead of rewriting placement method, I temporarily assign currentTileXY to where I WANT the troop.
                //I assign them back afterwards to avoid errors.
                int goodX = currentTileX, goodY = currentTileY;
                
                //Need updated screenVals
                screenVals = Game.getMouseSelectorInfo();
                
                //We need to know what district the mouse is in by 'case' number.
                /*
                |-----------------|
                |  4  |  7  |  2  |
                |-----------------|
                |  6  |  0  |  3  | // 0 represent being inside movement radius
                |-----------------|
                |  5  |  8  |  1  |
                |-----------------|
                */
                
                switch(districtNum)
                {
                    case 1:
                    case 2: 
                    case 4: 
                    case 5:
                        //All corners
                        Game.setCurrentTileXY((int)((updatedX / screenVals[0]) + screenVals[5]), (int)((updatedY / screenVals[0]) + screenVals[6]));
                        Game.enterBindCode();
                        Game.setCurrentTileXY(goodX, goodY);
                    case 6:
                    case 3:
                        Game.setCurrentTileXY((int)((updatedX / screenVals[0]) + screenVals[5]), currentTileY);
                        Game.enterBindCode();
                        Game.setCurrentTileXY(goodX, goodY);
                    case 7:
                    case 8:
                        Game.setCurrentTileXY(currentTileX, (int)((updatedY / screenVals[0]) + screenVals[6]));
                        Game.enterBindCode();
                        Game.setCurrentTileXY(goodX, goodY);
                }
            }
            else
            {
                //Normal Case! District 0
                Game.enterBindCode();
            }
        }
        else if (!Game.isInMenu())
        {
            //Not in the menu
            Game.enterBindCode();
        }
        else if (Game.isInMenu())
        {
            //Is in the menu
            
            JPanel optionsPanel = Game.getOptionsPanel();
            Point point = e.getPoint();
            
            //This checks if the mouse is clicked outside the options panel
            Game.writeToLog("("+point.getX()+" < "+optionsPanel.getX()+") || "+point.getX()+" > ("+optionsPanel.getX()+" + 300)");
            Game.writeToLog(point.getY()+" < "+optionsPanel.getY()+" || "+point.getY()+" > ("+optionsPanel.getY()+" + 300)");
            if ((point.getX() < optionsPanel.getX()) || point.getX() > (optionsPanel.getX() + 300))
            {
                Game.enterBindCode();
            }
            //I could have consolidated this into one if, however, this looked nicer.
            else if(point.getY() < optionsPanel.getY() || point.getY() > (optionsPanel.getY() + 300))
            {
                Game.enterBindCode();
            }
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {   
        Point point = e.getPoint();
        screenVals = Game.getMouseSelectorInfo();
        
        //The 26 for the Y is a constant for the Mac OS because it is slightly off, due to the window, and that corrects it.
        //My fears have been realized, and it is different for different OS'. We presume if it isn't windows, then it is iOS

        if (Game.isMovingTroop())
        {            
            //Working.
            
            updateMouseMovement(point);
            int[] movingTroopInfo = Game.getMovingTroopInfo();
            if(currentTileX > (movingTroopInfo[0] + movingTroopInfo[2]))
            {
                //The cursor is right of the move distance edge(rightmost)
                if (currentTileY > (movingTroopInfo[1] + movingTroopInfo[2]))
                {
                    //The cursor is below bottom of the moveDistance edge
                    districtNum = 1;
                    //(OriginalTroopX - currentMapView[0] - moveDistance) * pixelDimension
                    updatedX = (movingTroopInfo[0] - movingTroopInfo[3] + movingTroopInfo[2]) * movingTroopInfo[5];
                    
                    //(OriginalTroopY - currentMapView[0] - moveDistance) * pixelDimension
                    updatedY = (movingTroopInfo[1] - movingTroopInfo[4] + movingTroopInfo[2]) * movingTroopInfo[5];
                    
                    troopOutOfBounds = true;
                    
                    Game.setDrawTroopXY(updatedX, updatedY);
                }
                else if (currentTileY < (movingTroopInfo[1] - movingTroopInfo[2]))
                {
                    districtNum = 2;
                    //The cursor is lower than bottom edge of moveDistance
                    updatedX = (movingTroopInfo[0] - movingTroopInfo[3] + movingTroopInfo[2]) * movingTroopInfo[5];
                    updatedY = (movingTroopInfo[1] - movingTroopInfo[4] - movingTroopInfo[2]) * movingTroopInfo[5];
                    
                    troopOutOfBounds = true;
                    
                    Game.setDrawTroopXY(updatedX, updatedY);
                }
                else
                {
                    //Just to the right.
                    districtNum = 3;
                    updatedX = (movingTroopInfo[0] - movingTroopInfo[3] + movingTroopInfo[2]) * movingTroopInfo[5];
                    
                    troopOutOfBounds = true;
                    
                    Game.setDrawTroopXY(updatedX, currentTileY * movingTroopInfo[5]);
                }
            }
            else if (currentTileX < (movingTroopInfo[0] - movingTroopInfo[2]))
            {
                //The cursor is left bound of moveDistance
                if (currentTileY < (movingTroopInfo[1] - movingTroopInfo[2]))
                {
                    districtNum = 4;
                    //The cursor is above top of the moveDistance edge
                    updatedX = (movingTroopInfo[0] - movingTroopInfo[3] - movingTroopInfo[2]) * movingTroopInfo[5];
                    updatedY = (movingTroopInfo[1] - movingTroopInfo[4] - movingTroopInfo[2]) * movingTroopInfo[5];
                    
                    troopOutOfBounds = true;
                    
                    Game.setDrawTroopXY(updatedX, updatedY);
                }
                else if (currentTileY > (movingTroopInfo[1] + movingTroopInfo[2]))
                {
                    //The cursor is lower than bottom edge of moveDistance
                    districtNum = 5;
                    updatedX = (movingTroopInfo[0] - movingTroopInfo[3] - movingTroopInfo[2]) * movingTroopInfo[5];
                    updatedY = (movingTroopInfo[1] - movingTroopInfo[4] + movingTroopInfo[2]) * movingTroopInfo[5];    
                    
                    troopOutOfBounds = true;
                    
                    Game.setDrawTroopXY(updatedX, updatedY);
                }
                else
                {
                    //Just to the left.
                    districtNum = 6;
                    updatedX = (movingTroopInfo[0] - movingTroopInfo[3] - movingTroopInfo[2]) * movingTroopInfo[5];
                    
                    troopOutOfBounds = true;
                    
                    Game.setDrawTroopXY(updatedX, currentTileY * movingTroopInfo[5]);
                }
            }
            else if (currentTileY < movingTroopInfo[1] - movingTroopInfo[2])
            {
                //Just above the bounds
                districtNum = 7;
                updatedY = (movingTroopInfo[1] - movingTroopInfo[4] - movingTroopInfo[2]) * movingTroopInfo[5];    
                
                troopOutOfBounds = true;
                
                Game.setDrawTroopXY(currentTileX * movingTroopInfo[5], updatedY);
            }
            else if (currentTileY > movingTroopInfo[1] + movingTroopInfo[2])
            {
                //Just below the bounds
                districtNum = 8;
                updatedY = (movingTroopInfo[1] - movingTroopInfo[4] + movingTroopInfo[2]) * movingTroopInfo[5];    
                
                troopOutOfBounds = true;
                
                Game.setDrawTroopXY(currentTileX * movingTroopInfo[5], updatedY);
            }
            else
            {
                districtNum = 0;
                Game.setDrawTroopXY(currentTileX * movingTroopInfo[5], currentTileY * movingTroopInfo[5]);
                troopOutOfBounds = false;
            }
        }
        else if (!Game.isInMenu())
        {
            updateMouseMovement(point);
        }
        else
        {
            //Don't change that currentTileX! No selectorY! No touch! 
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        
    }
    
    private void updateMouseMovement(Point point)
    {
        screenVals = Game.getMouseSelectorInfo();

        if (isWindows)
        {
            currentTileX = screenVals[5] + (int)((point.getX() - WINSCREENBUFFERX) / screenVals[0]);
            currentTileY = screenVals[6] + (int)((point.getY() - WINSCREENBUFFERY) / screenVals[0]);
        }
        else
        {
            currentTileX = screenVals[5] + (int)(point.getX() / screenVals[0]);
            currentTileY = screenVals[6] + (int)((point.getY() - IOSSCREENBUFFER) / screenVals[0]);
        }

        //Takes mouse x, divides by pixelDimension, then subtracts currentTileX minus currentMapView[0]
        int distX = currentTileX - screenVals[3];

        //Takes mouse y, divides by pixelDimension, then subtracts currentTileY minus currentMapView[2]
        int distY = currentTileY - screenVals[4];

        Game.setMouseSelectors((distX * 64 + screenVals[1]), (distY * 64 + screenVals[2]) , currentTileX, currentTileY);
    }
    
    private boolean isWindows()
    {
        if (os.indexOf("win") >= 0)
            return true;
        else
            return false;
    }
}
