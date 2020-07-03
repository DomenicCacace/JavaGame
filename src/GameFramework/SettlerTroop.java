package GameFramework;

public class SettlerTroop extends Troop
{   
    private static final long serialVersionUID = 1L; 
    
    public SettlerTroop(String player)
    {
        super(player, "settlerTroop.png");
        setMoveDistance(5);
        Game.writeToLog("Created settler.");
    }
}
