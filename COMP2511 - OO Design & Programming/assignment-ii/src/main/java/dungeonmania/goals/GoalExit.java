package dungeonmania.goals;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Entity;

public class GoalExit implements Goal {

    public boolean achieved(Game game, GameMap map) {
        if (game.getPlayer() == null) return false;
        Position playerPosition = game.getPlayerPosition();
        List<Exit> currentExits = map.getEntities(Exit.class);
        if (currentExits == null || currentExits.size() == 0) return false;

        // If the player is on any exit return true
        return currentExits
            .stream()
            .map(Entity::getPosition)
            .anyMatch(playerPosition::equals);
    }

    public String toString(Game game) {
        if (this.achieved(game, game.getMap())) {
            return "";
        }
        return ":exit";
    }
}
