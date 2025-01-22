package dungeonmania.entities;

import dungeonmania.util.Position;
import dungeonmania.map.GameMap;

import org.json.JSONObject;

import dungeonmania.entities.enemies.Enemy;

public class Swamp extends Entity {

    private int movementFactor;

    public Swamp(Position pos, int moveFactor) {
        super(pos);
        movementFactor = moveFactor;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            Enemy enemy = (Enemy) entity;
            enemy.setEnemyStuck(movementFactor, map.getPlayer());
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "swamp_tile");
        jsonEntity.put("movement_factor", movementFactor);

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }

    /*
     * ============================= Get + Set Methods ======================================
     */
    public int getMovementFactor() {
        return movementFactor;
    }
}
