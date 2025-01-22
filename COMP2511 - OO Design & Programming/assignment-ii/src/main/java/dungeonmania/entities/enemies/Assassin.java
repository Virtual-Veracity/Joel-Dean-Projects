package dungeonmania.entities.enemies;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    public static final double DEFAULT_BRIBE_FAIL = 0.3;

    private double bribeFail = DEFAULT_BRIBE_FAIL;

    public Assassin(Position position, double health, double attack, int bribeAmount,
                    int bribeRadius, double rate, int duration) {
        super(position, health, attack, bribeAmount, bribeRadius, duration);
        this.bribeFail = rate;
    }

    public void bribeAssassin(Player player, double random) {
        if (random >= bribeFail) {
            setAllied(true);
        } else {
            setAllied(false);
        }
        for (int i = 0; i < getBribeAmount(); i++) {
            player.bribe();
        }
    }

    @Override
    public void interact(Player player, Game game) {
        Random randGen = new Random();
        if (hasSpectre(player.getInventory())) {
            mindControl();
        } else {
            bribeAssassin(player, randGen.nextDouble());
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "assasin");

        Position entityPosition = getPosition();
        jsonEntity.put("x", entityPosition.getX());
        jsonEntity.put("y", entityPosition.getY());

        return jsonEntity;
    }
}
