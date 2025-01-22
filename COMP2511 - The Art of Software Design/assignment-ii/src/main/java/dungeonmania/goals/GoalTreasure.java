package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.map.GameMap;

public class GoalTreasure implements Goal {

    private int target;

    public GoalTreasure(int goalTarget) {
        target = goalTarget;
    }

    public boolean achieved(Game game, GameMap map) {
        if (game.getPlayer() == null) return false;
        return game.getInitialTreasureCount() - map.getEntities(Treasure.class).size() >= target;
    }

    public String toString(Game game) {
        if (this.achieved(game, game.getMap())) {
            return "";
        }
        return ":treasure";
    }
}
