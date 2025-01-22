package dungeonmania.entities.logical.rules;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logical.LogicalConductor;

public class RuleCOAND extends LogicalRule {

    public boolean satisfied(List<Entity> entitiesCA, int tick) {
        return numActivatedConductorsCA(entitiesCA, tick) >= 2;
    }

    public int numActivatedConductorsCA(List<Entity> entitiesCA, int tick) {
        List<LogicalConductor> allConductors = new ArrayList<>();
        for (Entity curr : entitiesCA) {
            if (curr instanceof LogicalConductor) {
                LogicalConductor currLC = (LogicalConductor) curr;
                if (currLC.isActive()) {
                    allConductors.add(currLC);
                }
            }
        }

        List<LogicalConductor> validConductors = new ArrayList<>();
        for (LogicalConductor curr : allConductors) {
            if (curr.getTickActivated() == tick) {
                validConductors.add(curr);
            }
        }

        return validConductors.size();
    }
}
