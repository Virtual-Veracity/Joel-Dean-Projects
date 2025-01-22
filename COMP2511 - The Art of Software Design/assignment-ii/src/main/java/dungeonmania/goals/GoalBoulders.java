package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Switch;
import dungeonmania.map.GameMap;

public class GoalBoulders implements Goal {

    public boolean achieved(Game game, GameMap map) {
        if (game.getPlayer() == null) return false;
        return map.getEntities(Switch.class).stream().allMatch(s -> s.isActive());
    }

    public String toString(Game game) {
        if (this.achieved(game, game.getMap())) {
            return "";
        }
        return ":boulders";
    }
}
