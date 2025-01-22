package dungeonmania.entities;

import org.json.JSONObject;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends Entity {
    public Exit(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "exit");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
