package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.logical.LogicalConductor;
import dungeonmania.entities.logical.LogicalEntity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends LogicalConductor {
    private List<Bomb> bombs = new ArrayList<>();

    public Switch(Position position) {
        super(position);
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (isActive()) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            bombs.stream().forEach(b -> b.notify(map));
            activateLogicalEntity(map.getGame());
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            deactivate();
            List<LogicalEntity> logicalEntities = findLogicalEntities(getPosition(), map, false);
            for (LogicalEntity logicalEntity : logicalEntities) {
                if (logicalEntity != null) {
                    logicalEntity.deactivate();
                }
            }
        }
    }

    public void activateLogicalEntity(Game game) {
        activate(game.getTick());
        List<LogicalEntity> logicalEntities = findLogicalEntities(getPosition(), game.getMap(), true);
        for (LogicalEntity logicalEntity : logicalEntities) {
            if (logicalEntity != null) {
                logicalEntity.activate(game.getMap(), game.getTick());
            }
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "switch");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
