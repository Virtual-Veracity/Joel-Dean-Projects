package dungeonmania.entities.logical.rules;

import java.util.List;

import dungeonmania.entities.Entity;

public class RuleOR extends LogicalRule {

    @Override
    public boolean satisfied(List<Entity> entitiesCA) {
        return numActivatedConductorsCA(entitiesCA) >= 1;
    }
}
