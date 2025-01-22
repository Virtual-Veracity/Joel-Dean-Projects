package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wood extends Entity implements InventoryItem {
    public Wood(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            addToInventory(map, this, ((Player) entity).getInventory());
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "wood");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }

    @Override
    public JSONObject toInvJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "wood");
        return jsonEntity;
    }
}
