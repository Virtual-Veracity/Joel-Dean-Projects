package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.map.GameMap;

public class GoalOR implements Goal {

    private Goal goal1;
    private Goal goal2;

    public GoalOR(Goal firstGoal, Goal secondGoal) {
        goal1 = firstGoal;
        goal2 = secondGoal;
    }

    public boolean achieved(Game game, GameMap map) {
        if (game.getPlayer() == null) return false;
        return goal1.achieved(game, map) || goal2.achieved(game, map);
    }

    public String toString(Game game) {
        if (this.achieved(game, game.getMap())) {
            return "";
        }
        return "(" + goal1.toString(game) + " OR " + goal2.toString(game) + ")";
    }
}
