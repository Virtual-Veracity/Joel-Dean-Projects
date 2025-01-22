package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable {
    private BattleStatistics battleStatistics;
    private int stuck = 0;

    public Enemy(Position position, double health, double attack) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            battle(map.getGame(), (Player) entity);
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        unsubscribeFromMap(map.getGame());
    }

    public abstract void move(Game game);

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void setHealth(double health) {
        battleStatistics.setHealth(health);
    }

    public void battle(Game game, Player player) {
        game.battle(player, this);
    }

    public void unsubscribeFromMap(Game game) {
        game.unsubscribe(getId());
        game.enemyDestroyed();
    }

    /*
     *  =============== Swamp Tile Functions ====================
     */
    public boolean checkStuckSwamp() {
        if (stuck > 0) {
            stuck -= 1;
            return true;
        }
        return false;
    }

    public void setEnemyStuck(int moveFactor, Player player) {
        stuck = moveFactor;
        return;
    }
}
