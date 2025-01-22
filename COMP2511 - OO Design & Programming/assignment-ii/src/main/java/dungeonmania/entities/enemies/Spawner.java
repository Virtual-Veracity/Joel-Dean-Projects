package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Spawner extends Entity implements Interactable {
    public static final int DEFAULT_SPAWN_INTERVAL = 0;

    public Spawner(Position position, int spawnInterval) {
        super(position);
    }

    public abstract void spawn(Game game);

    @Override
    public void onDestroy(GameMap map) {
        Game game = map.getGame();
        game.unsubscribe(getId());
        game.spawnerDestroyed();
        map.removeNode(this);
    }

    @Override
    public void interact(Player player, Game game) {
        player.useWeapon(game);
        game.spawnerDestroyed();
    }

    @Override
    public boolean isInteractable(Player player) {
        return Position.isAdjacent(player.getPosition(), getPosition()) && player.hasWeapon();
    }
}
