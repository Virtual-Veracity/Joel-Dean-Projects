package dungeonmania.entities.logical;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends LogicalEntity {
    private boolean isOpen;

    public SwitchDoor(Position position, String logic) {
        super(position, logic);
        isOpen = false;
    }

    public void open() {
        isOpen = true;
    }

    public void close() {
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return isOpen;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "switch_door");
        jsonEntity.put("logic", getLogic());

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
