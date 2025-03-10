package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.logical.LogicalConductor;
import dungeonmania.entities.logical.LogicalEntity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONObject;

public class Entity {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private Position position;
    private Position previousPosition;
    private Position previousDistinctPosition;
    private Direction facing;
    private String entityId;

    public Entity(Position position) {
        this.position = position;
        this.previousPosition = position;
        this.previousDistinctPosition = null;
        this.entityId = UUID.randomUUID().toString();
        this.facing = null;
    }

    public boolean canMoveOnto(GameMap map, Entity entity) {
        return false;
    }

    // use setPosition
    @Deprecated(forRemoval = true)
    public void translate(Direction direction) {
        previousPosition = this.position;
        this.position = Position.translateBy(this.position, direction);
        if (!previousPosition.equals(this.position)) {
            previousDistinctPosition = previousPosition;
        }
    }

    // use setPosition
    @Deprecated(forRemoval = true)
    public void translate(Position offset) {
        this.position = Position.translateBy(this.position, offset);
    }

    public void onOverlap(GameMap map, Entity entity) {
        return;
    }

    public void onMovedAway(GameMap map, Entity entity) {
        return;
    }

    public void onDestroy(GameMap gameMap) {
        return;
    }

    public Position getPosition() {
        return position;
    }

    public Position getPreviousPosition() {
        return previousPosition;
    }

    public Position getPreviousDistinctPosition() {
        return previousDistinctPosition;
    }

    public String getId() {
        return entityId;
    }

    public void setPosition(Position position) {
        previousPosition = this.position;
        this.position = position;
        if (!previousPosition.equals(this.position)) {
            previousDistinctPosition = previousPosition;
        }
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public List<Position> getCardinallyAdjacentPositions() {
        return position.getCardinallyAdjacentPositions();
    }

    public void addToInventory(GameMap map, Entity entity, Inventory inventory) {
        if (!inventory.add((InventoryItem) entity)) return;
        map.destroyEntity(entity);
    }

    public List<LogicalEntity> findLogicalEntities(Position pos, GameMap map, boolean activate) {
        List<Entity> entities = map.getCardinallyAdjacentEntities(pos.getCardinallyAdjacentPositions());
        List<LogicalEntity> returnEntities = new ArrayList<>();
        for (Entity curr : entities) {
            if (curr instanceof LogicalEntity) {
                returnEntities.add((LogicalEntity) curr);
            } else if (curr instanceof LogicalConductor && !(curr instanceof Switch)) {
                LogicalConductor currLC = (LogicalConductor) curr;
                if (currLC.isActive()) continue;
                if (activate) {
                    activateLogicalConductor(currLC, map.getGame());
                } else {
                    currLC.deactivate();
                }
                returnEntities.addAll(findLogicalEntities(curr.getPosition(), map, activate));
            }
        }
        return returnEntities;
    }

    public void activateLogicalConductor(LogicalConductor logicalConductor, Game game) {
        logicalConductor.activate(game.getTick());
    }

    public JSONObject toJSON() {
        return null;
    }
}
