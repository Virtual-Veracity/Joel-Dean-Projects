package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class MidnightArmour extends Buildable {
    private static final double DEFAULT_ATTACK_DAMAGE = 0;
    private static final double DEFAULT_PROTECTION = 0;

    private double extraAttackDamage = MidnightArmour.DEFAULT_ATTACK_DAMAGE;
    private double protection = MidnightArmour.DEFAULT_PROTECTION;

    public MidnightArmour(double extraAttackDamage, double protection) {
        super(null);
        this.extraAttackDamage = extraAttackDamage;
        this.protection = protection;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            extraAttackDamage,
            protection,
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

    public double getExtraAttackDamage() {
        return extraAttackDamage;
    }

    public double getProtection() {
        return protection;
    }

    @Override
    public JSONObject toInvJSON() {
        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("type", "midnight_armour");
        return jsonEntity;
    }
}
