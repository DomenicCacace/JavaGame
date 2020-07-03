package GameFramework;

public class WorkerTroop extends Troop
{
    private static final long serialVersionUID = 1L; 
    
    public WorkerTroop(String player)
    {
        super(player, "workerTroop.png");
        setMoveDistance(6);
        Game.writeToLog("Created worker.");
    }
}
