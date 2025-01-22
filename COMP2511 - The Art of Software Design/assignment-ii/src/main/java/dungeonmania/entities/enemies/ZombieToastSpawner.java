package dungeonmania.entities.enemies;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends Spawner {

    public ZombieToastSpawner(Position position, int spawnInterval) {
        super(position, spawnInterval);
    }

    public void spawn(Game game) {
        game.spawnZombie(this);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "zombie_toast_spawner");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
