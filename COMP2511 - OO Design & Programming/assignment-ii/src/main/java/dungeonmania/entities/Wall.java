package dungeonmania.entities;

import dungeonmania.map.GameMap;

import org.json.JSONObject;

import dungeonmania.entities.enemies.Spider;
import dungeonmania.util.Position;

public class Wall extends Entity {
    public Wall(Position position) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Spider;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "wall");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
