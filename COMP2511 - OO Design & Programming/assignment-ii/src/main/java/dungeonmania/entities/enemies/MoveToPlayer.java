package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class MoveToPlayer implements MoveBehaviour {

    public void move(Game game, Enemy enemy, GameMap map) {
        Position nextPos;
        nextPos = map.dijkstraPathFind(enemy.getPosition(), game.getPlayerPosition(), enemy);
        map.moveTo(enemy, nextPos);
    }
}
