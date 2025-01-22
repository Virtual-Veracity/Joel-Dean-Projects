package dungeonmania.entities.buildables;


import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class Shield extends Buildable {
    private int durability;
    private double defence;

    public Shield(int durability, double defence) {
        super(null);
        this.durability = durability;
        this.defence = defence;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.getPlayer().remove(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            defence,
            1,
            1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public JSONObject toInvJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "shield");
        jsonEntity.put("durability", durability);
        return jsonEntity;
    }
}
