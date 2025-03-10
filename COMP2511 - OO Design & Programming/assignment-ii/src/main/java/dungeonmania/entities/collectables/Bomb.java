package dungeonmania.entities.collectables;

import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.Switch;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;

public class Bomb extends Entity implements InventoryItem {
    public enum State {
        SPAWNED,
        INVENTORY,
        PLACED
    }

    public static final int DEFAULT_RADIUS = 1;
    private State state;
    private int radius;

    private List<Switch> subs = new ArrayList<>();

    public Bomb(Position position, int radius) {
        super(position);
        state = State.SPAWNED;
        this.radius = radius;
    }

    public void subscribe(Switch s) {
        this.subs.add(s);
    }

    public void notify(GameMap map) {
        explode(map);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (state != State.SPAWNED) return;
        if (entity instanceof Player) {
            subs.stream().forEach(s -> s.unsubscribe(this));
            addToInventory(map, this, ((Player) entity).getInventory());
        }
        this.state = State.INVENTORY;
    }

    public void onPutDown(GameMap map, Position p) {
        translate(Position.calculatePositionBetween(getPosition(), p));
        map.addEntity(this);
        this.state = State.PLACED;
        List<Position> adjPosList = getCardinallyAdjacentPositions();
        adjPosList.stream().forEach(node -> {
            List<Entity> entities = map.getEntities(node)
                                        .stream()
                                        .filter(e -> (e instanceof Switch))
                                        .collect(Collectors.toList());
            entities.stream()
                    .map(Switch.class::cast)
                    .forEach(s -> s.subscribe(this, map));
            entities.stream()
                    .map(Switch.class::cast)
                    .forEach(s -> this.subscribe(s));
        });
    }

    /**
     * it destroys all entities in diagonally and cardinally adjacent cells, except for the player
     * @param map
     */
    public void explode(GameMap map) {
        int x = getPosition().getX();
        int y = getPosition().getY();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                List<Entity> entities = map.getEntities(new Position(i, j));
                entities = entities.stream()
                    .filter(e -> !(e instanceof Player))
                    .collect(Collectors.toList());
                for (Entity e: entities) map.destroyEntity(e);
            }
        }
    }

    public State getState() {
        return state;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "bomb");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }

    @Override
    public JSONObject toInvJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "bomb");
        return jsonEntity;
    }
}
