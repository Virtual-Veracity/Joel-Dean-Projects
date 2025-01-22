package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class Sceptre extends Buildable {
    public Sceptre() {
        super(null);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            1,
            1));
    }

    @Override
    public void use(Game game) {
        return;
    }

    @Override
    public int getDurability() {
        return 1;
    }

    @Override
    public JSONObject toInvJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "sceptre");
        return jsonEntity;
    }
}
