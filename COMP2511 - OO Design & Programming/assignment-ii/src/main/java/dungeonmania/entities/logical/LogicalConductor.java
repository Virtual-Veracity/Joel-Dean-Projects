package dungeonmania.entities.logical;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class LogicalConductor extends Entity {
    private boolean isActive;
    private int tickActivated = -1;

    public LogicalConductor(Position position) {
        super(position);
        isActive = false;
    }

    public void activate(int tick) {
        isActive = true;
        tickActivated = tick;
    }

    public void deactivate() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getTickActivated() {
        return tickActivated;
    }
}
