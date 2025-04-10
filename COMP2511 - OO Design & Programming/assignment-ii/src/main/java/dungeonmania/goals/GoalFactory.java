package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalFactory {

    public static Goal createGoal(JSONObject jsonGoal, JSONObject config) {
        JSONArray subgoals;
        switch (jsonGoal.getString("goal")) {
        case "AND":
            subgoals = jsonGoal.getJSONArray("subgoals");
            return new GoalAND(
                createGoal(subgoals.getJSONObject(0), config),
                createGoal(subgoals.getJSONObject(1), config)
            );
        case "OR":
            subgoals = jsonGoal.getJSONArray("subgoals");
            return new GoalOR(
                createGoal(subgoals.getJSONObject(0), config),
                createGoal(subgoals.getJSONObject(1), config)
            );
        case "exit":
            return new GoalExit();
        case "boulders":
            return new GoalBoulders();
        case "treasure":
        case "sun_stone":
            int treasureGoal = config.optInt("treasure_goal", 1);
            return new GoalTreasure(treasureGoal);
        case "enemies":
            int enemyGoal = config.optInt("enemy_goal", 1);
            return new GoalEnemies(enemyGoal);
        default:
            return null;
        }
    }
}
