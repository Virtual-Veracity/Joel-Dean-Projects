package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Treasure extends Entity implements InventoryItem {
    public Treasure(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            entity.addToInventory(map, this, ((Player) entity).getInventory());
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "treasure");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }

    @Override
    public JSONObject toInvJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "treasure");
        return jsonEntity;
    }
}
