package dungeonmania.entities.enemies;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class Spider extends Enemy {

    public static final int DEFAULT_SPAWN_RATE = 0;
    public static final double DEFAULT_ATTACK = 5;
    public static final double DEFAULT_HEALTH = 10;

    private MoveBehaviour moveBehaviour;

    public Spider(Position position, double health, double attack) {
        super(position.asLayer(Entity.DOOR_LAYER + 1), health, attack);
        /**
         * Establish spider movement trajectory Spider moves as follows:
         *  8 1 2       10/12  1/9  2/8
         *  7 S 3       11     S    3/7
         *  6 5 4       B      5    4/6
         */
        moveBehaviour = new MoveSpider(position.getAdjacentPositions());
    };

    @Override
    public void move(Game game) {

        if (checkStuckSwamp()) {
            return;
        }

        moveBehaviour.move(game, this, game.getMap());
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "spider");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
