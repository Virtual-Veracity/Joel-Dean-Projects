package dungeonmania.Task2Tests;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import dungeonmania.mvp.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class GoalEnemiesTest {
    @Test
    @Tag("14-1")
    @DisplayName("Test achieving an enemies goal - No Spawners")
    public void noSpawners() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalTest_zombie", "c_enemiesGoalTest_noSpawner");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Pick Up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        for (int i = 0; i <= 10; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-2")
    @DisplayName("Test achieving an enemies goal - Multiple Enemies")
    public void multipleZombies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalTest_3Zombies", "c_enemiesGoalTest_3Zombies");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Pick Up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        for (int i = 0; i < 2; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        for (int i = 0; i <= 7; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-3")
    @DisplayName("Test achieving an enemies goal - Multiple Enemy Types")
    public void multipleTypes() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalTest_multipleTypes", "c_enemiesGoalTest_multipleTypes");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Pick Up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        for (int i = 0; i <= 9; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("14-4")
    @DisplayName("Test achieving an enemies goal - With Spawner's")
    public void withSpawner() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalTest_withSpawner", "c_enemiesGoalTest_withSpawner");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Pick Up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        for (int i = 0; i < 4; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        // cardinally adjacent: true, has sword: true
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }
}
