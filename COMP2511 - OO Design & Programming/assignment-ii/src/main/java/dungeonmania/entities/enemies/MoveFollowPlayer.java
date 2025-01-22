package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Player;

public class MoveFollowPlayer implements MoveBehaviour {
    public void move(Game game, Enemy enemy, GameMap map) {
        Player player = game.getPlayer();
        Position previousPosition = player.getPreviousPosition();

        // If player Moves -> mercenary follows
        if (!player.getPosition().equals(player.getPreviousPosition())) {
            map.moveTo(enemy, previousPosition);
        }
    }
}
