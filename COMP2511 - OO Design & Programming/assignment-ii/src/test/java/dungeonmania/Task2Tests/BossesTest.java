package dungeonmania.Task2Tests;

import dungeonmania.DungeonManiaController;
import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.enemies.Assassin;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class BossesTest {

    @Test
    @Tag("16-1")
    @DisplayName("Class-level Unit Test for randomness in bribe failure")
    public void classLevelTest() {
        // Create variables
        Game game = new Game("d_assassinTest");
        Position assPos = new Position(5, 5);
        Position playPos = new Position(4, 4);
        double bribeFailRate = 0.3;
        Assassin assassin = new Assassin(assPos, 10, 10, 1, 10, bribeFailRate, 1);
        Player player = new Player(playPos, 10, 10, game.getMap());

        // Test bribeAssassin method with given random number
        Treasure treasure = new Treasure(playPos);
        Random randGen = new Random(5);
        int totalAttempts = 9;
        int fail = 0;
        for (int i = 0; i < totalAttempts; i++) {
            // Add payment for the assassin
            player.addToInventory(game.getMap(), treasure, player.getInventory());
            assertTrue(player.getInventory().getEntities().size() == 1);

            double random = randGen.nextDouble();
            assassin.bribeAssassin(player, random);

            if (random >= bribeFailRate) {
                assertTrue(assassin.isAllied());
            } else {
                fail++;
                assertTrue(!assassin.isAllied());
            }
            assertTrue(player.getInventory().getEntities().size() == 0);
        }

        assertTrue(totalAttempts / bribeFailRate == fail * 10);
    }

    @Test
    @Tag("16-2")
    @DisplayName("Test assassin in line with Player moves towards them")
    public void simpleMovement() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      A4      A3      A2      A1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_simpleMovement", "c_assassinTest_simpleMovement");

        assertEquals(new Position(8, 1), getAssPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getAssPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getAssPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getAssPos(res));
    }

    @Test
    @Tag("16-3")
    @DisplayName("Test assassin stops if they cannot move any closer to the player")
    public void stopMovement() {
        //                  Wall     Wall    Wall
        // P1       P2      Wall      A1     Wall
        //                  Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_stopMovement", "c_assassinTest_stopMovement");

        Position startingPos = getAssPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getAssPos(res));
    }

    @Test
    @Tag("16-4")
    @DisplayName("Test assassins can not move through closed doors")
    public void doorMovement() {
        //                  Wall     Door    Wall
        // P1       P2      Wall      A1     Wall
        // Key              Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_doorMovement", "c_assassinTest_doorMovement");

        Position startingPos = getAssPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getAssPos(res));
    }

    @Test
    @Tag("16-5")
    @DisplayName("Test assassin moves around a wall to get to the player")
    public void evadeWall() {
        //                  Wall      A2
        // P1       P2      Wall      A1
        //                  Wall      A2
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_evadeWall", "c_assassinTest_evadeWall");

        res = dmc.tick(Direction.RIGHT);
        assertTrue(new Position(4, 1).equals(getAssPos(res))
            || new Position(4, 3).equals(getAssPos(res)));
    }

    private Position getAssPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "assassin").get(0).getPosition();
    }
}
