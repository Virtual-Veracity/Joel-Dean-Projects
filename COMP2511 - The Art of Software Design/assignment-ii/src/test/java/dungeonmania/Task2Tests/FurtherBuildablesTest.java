package dungeonmania.Task2Tests;

import dungeonmania.DungeonManiaController;
import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class FurtherBuildablesTest {

    @Test
    @Tag("18-1")
    @DisplayName("Test building a sceptre with wood, key and sun stone")
    public void buildSceptreWithWoodKeySunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_buildSceptreWithWoodKeySunStone",
                                           "c_furtherBuildablesTest_buildSceptreWithWoodKeySunStone");

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up Key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // Pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-2")
    @DisplayName("Test building a sceptre with wood, treasure and sun stone")
    public void buildSceptreWithWoodTreasureSunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_buildSceptreWithWoodTreasureSunStone",
                                           "c_furtherBuildablesTest_buildSceptreWithWoodTreasureSunStone");

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up Treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-3")
    @DisplayName("Test building a sceptre with wood and 2 sun stone")
    public void buildSceptreWithWoodSunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_buildSceptreWithWoodSunStone",
                                           "c_furtherBuildablesTest_buildSceptreWithWoodSunStone");

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up 2 Sun Stones
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Materials used in construction disappear from inventory
        // except for one of the sunStones since it was used instead of a key or treasure
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-4")
    @DisplayName("Test building a sceptre with arrows, key and sun stone")
    public void buildSceptreWithArrowsKeySunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_buildSceptreWithArrowsKeySunStone",
                                           "c_furtherBuildablesTest_buildSceptreWithArrowsKeySunStone");

        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up 2 Arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Pick up Key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // Pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-5")
    @DisplayName("Test building a sceptre with arrows, treasure and sun stone")
    public void buildSceptreWithArrowsTreasureSunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_buildSceptreWithArrowsTreasureSunStone",
                                           "c_furtherBuildablesTest_buildSceptreWithArrowsTreasureSunStone");

        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up 2 Arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Pick up Treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-6")
    @DisplayName("Test building a sceptre with wood and 2 sun stone")
    public void buildSceptreWithArrowsSunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_buildSceptreWithArrowsSunStone",
                                           "c_furtherBuildablesTest_buildSceptreWithArrowsSunStone");

        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up 2 Arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "arrow").size());

        // Pick up Sun Stones
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Materials used in construction disappear from inventory
        // except for one of the sunStones since it was used instead of a key or treasure
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-7")
    @DisplayName("Test mind controlling mercenary and assassin with sceptre")
    public void mindControl() {
        // Create variables
        Game game = new Game("d_mindControl");
        Position playPos = new Position(0, 0);
        Position mercPos = new Position(4, 4);
        Player player = new Player(playPos, 10, 10, game.getMap());
        Mercenary merc = new Mercenary(mercPos, 5, 5, 1, 1, 2);
        game.getMap().addEntity(merc);

        Sceptre sceptre = new Sceptre();
        player.addToInventory(game.getMap(), sceptre, player.getInventory());

        // Check if sceptre can be used to mind control mercenary
        assertTrue(!merc.isAllied());
        merc.mindControl();
        assertTrue(merc.isAllied());

        // Mind control only lasts for a certain number of ticks (2 for this test)
        game.tick();
        assertTrue(merc.isAllied());
        game.tick();
        assertTrue(!merc.isAllied());
    }

    @Test
    @Tag("18-8")
    @DisplayName("Test that the player can craft the midnight armour when there are no zombies")
    public void craftNoZombies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_craftArmourNoZombies",
                                           "c_furtherBuildablesTest_craftArmourNoZombies");

        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Midnight Armour
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-9")
    @DisplayName("Test that the player cannot craft the midnight armour when there are zombies")
    public void craftZombies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_craftArmourZombies",
                                           "c_furtherBuildablesTest_craftArmourZombies");

        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, getZombies(res).size());

        // Pick up Sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, getZombies(res).size());

        // Pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(2, getZombies(res).size());

        // Attempt to build Midnight Armour but fail
        // because there are zombies
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        assertThrows(InvalidActionException.class, () ->
                dmc.build("midnight_armour")
        );
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());

        // Materials weren't used so they are still in the inventory
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("18-10")
    @DisplayName("Test whether the armour provides extra attack damage as well as protection")
    public void armourDamageProtectionTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        String config = "c_furtherBuildablesTest_armourDamageProtectionTest";
        DungeonResponse res = dmc.newGame("d_furtherBuildablesTest_armourDamageProtectionTest", config);

        // Pick up Sword
        res = dmc.tick(Direction.RIGHT);

        // Pick up SunStone
        res = dmc.tick(Direction.RIGHT);

        // Build Midnight Armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT); // battle happens after this tick

        // Get the Battle Response
        BattleResponse battle = res.getBattles().get(0);
        RoundResponse firstRound = battle.getRounds().get(0);

        // check the armour adds extra damage to the attack
        // and has some protection
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", config));
        double extraDamage = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_attack", config));
        double spiderAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("spider_attack", config));
        double protection = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_defence", config));
        double attackEnemy = (playerAttack + extraDamage) / 5;
        double attackPlayer = (spiderAttack - protection) / 10;

        // Delta health is negative so take negative here
        assertEquals(attackEnemy, -firstRound.getDeltaEnemyHealth(), 0.001);
        assertEquals(attackPlayer, -firstRound.getDeltaCharacterHealth(), 0.001);
    }

    private List<EntityResponse> getZombies(DungeonResponse res) {
        return TestUtils.getEntities(res, "zombie_toast");
    }

    public void assertBattleCalculations(
            BattleResponse battle, boolean enemyDies, String configFilePath, String enemyType) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = battle.getInitialPlayerHealth(); // Should come from config
        double enemyHealth = battle.getInitialEnemyHealth(); // Should come from config
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double
                .parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(-enemyAttack / 10, round.getDeltaCharacterHealth(), 0.001);
            assertEquals(-playerAttack / 5, round.getDeltaEnemyHealth(), 0.001);
            // Delta health is negative
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }
}
