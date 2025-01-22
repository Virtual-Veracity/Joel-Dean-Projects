package dungeonmania.entities.logical.rules;

import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logical.LogicalConductor;

public abstract class LogicalRule {

    public boolean satisfied(List<Entity> entitiesCA) {
        return false;
    }

    public int numActivatedConductorsCA(List<Entity> entitiesCA) {
        int counter = 0;
        for (Entity curr : entitiesCA) {
            if (curr instanceof LogicalConductor) {
                if (((LogicalConductor) curr).isActive()) {
                    counter++;
                }
            }
        }
        return counter;
    }
}
