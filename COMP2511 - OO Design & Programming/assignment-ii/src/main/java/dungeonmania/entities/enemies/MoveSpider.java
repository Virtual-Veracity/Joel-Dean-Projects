package dungeonmania.entities.enemies;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class MoveSpider implements MoveBehaviour {

    private List<Position> movementTrajectory;
    private int nextPositionElement = 1;
    private boolean forward = true;

    public MoveSpider(List<Position> initialMovementTrajectory) {
        movementTrajectory = initialMovementTrajectory;
    }

    @Override
    public void move(Game game, Enemy enemy, GameMap map) {
        Position nextPos = movementTrajectory.get(nextPositionElement);
        List<Entity> entities = map.getEntities(nextPos);
        if (entities != null && entities.size() > 0 && entities.stream().anyMatch(e -> e instanceof Boulder)) {
            forward = !forward;
            updateNextPosition();
            updateNextPosition();
        }
        nextPos = movementTrajectory.get(nextPositionElement);
        entities = map.getEntities(nextPos);
        if (entities == null
                || entities.size() == 0
                || entities.stream().allMatch(e -> e.canMoveOnto(game.getMap(), enemy))) {
            map.moveTo(enemy, nextPos);
            updateNextPosition();
        }
    }

    private void updateNextPosition() {
        if (forward) {
            nextPositionElement++;
            if (nextPositionElement == 8) {
                nextPositionElement = 0;
            }
        } else {
            nextPositionElement--;
            if (nextPositionElement == -1) {
                nextPositionElement = 7;
            }
        }
    }
}
