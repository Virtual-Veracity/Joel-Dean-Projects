package dungeonmania.entities.logical;

import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logical.rules.*;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LogicalEntity extends Entity {
    private LogicalRule rule;
    private String logic;

    public LogicalEntity(Position position, String logic) {
        super(position);
        this.logic = logic;
        switch (logic) {
            case "and":
                rule = new RuleAND();
                break;
            case "or":
                rule = new RuleOR();
                break;
            case "xor":
                rule = new RuleXOR();
                break;
            case "co_and":
                rule = new RuleCOAND();
                break;
            default:
                rule = null;
                break;
        }
    }

    public void activate(GameMap map, int tick) {
        List<Entity> validEntities = map.getCardinallyAdjacentEntities(getCardinallyAdjacentPositions());
        boolean valid = false;
        if (rule instanceof RuleCOAND) {
            valid = ((RuleCOAND) rule).satisfied(validEntities, tick);
        } else {
            valid = rule.satisfied(validEntities);
        }

        if (valid) {
            if (this instanceof LightBulb) {
                ((LightBulb) this).turnOn();
            } else if (this instanceof SwitchDoor) {
                ((SwitchDoor) this).open();
            }
        }
    }

    public void deactivate() {
        if (this instanceof LightBulb) {
            ((LightBulb) this).turnOff();
        } else if (this instanceof SwitchDoor) {
            ((SwitchDoor) this).close();
        }
    }

    public String getLogic() {
        return logic;
    }
}
