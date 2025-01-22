package dungeonmania.entities;

import dungeonmania.map.GameMap;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.util.Position;

public class Door extends Entity {
    private boolean open = false;
    private int number;

    public Door(Position position, int number) {
        super(position.asLayer(Entity.DOOR_LAYER));
        this.number = number;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return entity instanceof Player && canOpen(((Player) entity).getInventory());
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (!(entity instanceof Player))
            return;

        Inventory inventory = getInventory((Player) entity);
        Key key = getKey(inventory);

        if (hasKey(inventory)) {
            inventory.remove(key);
            open();
        } else if (hasSunStone(inventory)) {
            open();
        }
    }

    private boolean canOpen(Inventory inventory) {
        return hasKey(inventory) || hasSunStone(inventory);
    }

    private boolean hasKey(Inventory inventory) {
        Key key = getKey(inventory);

        return (key != null && key.getnumber() == number);
    }

    private boolean hasSunStone(Inventory inventory) {
        return getSunStone(inventory) != null;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }

    public Inventory getInventory(Player player) {
        return player.getInventory();
    }

    public Key getKey(Inventory inventory) {
        return inventory.getFirst(Key.class);
    }

    public SunStone getSunStone(Inventory inventory) {
        return inventory.getFirst(SunStone.class);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "door");
        jsonEntity.put("key", number);

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
