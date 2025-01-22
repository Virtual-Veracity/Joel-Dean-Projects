package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.entities.enemies.Spawner;

public class GoalEnemies implements Goal {
    private int totalKills;

    public GoalEnemies(int target) {
        totalKills = target;
    }

    public boolean achieved(Game game, GameMap map) {
        if (game.getPlayer() == null) return false;
        return game.getEnemiesDestroyed() >= totalKills
               && game.getSpawnersDestroyed() >= map.getEntities(Spawner.class).size();
    }

    public String toString(Game game) {
        if (this.achieved(game, game.getMap())) {
            return "";
        }
        return ":enemies";
    }
}
