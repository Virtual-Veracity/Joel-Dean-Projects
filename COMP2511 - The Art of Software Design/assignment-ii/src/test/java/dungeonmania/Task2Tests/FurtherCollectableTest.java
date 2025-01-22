package dungeonmania.Task2Tests;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FurtherCollectableTest {

    @Test
    @Tag("17-1")
    @DisplayName("Test achieving a basic treasure goal")
    public void treasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherCollectableTest_goal", "c_furtherCollectableTest_goal");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains("treasure"));

        // collect sunStone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("17-2")
    @DisplayName("Test whether the sunStone can open doors")
    public void openDoorsTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_furtherCollectableTest_openDoor", "c_furtherCollectableTest_openDoor");

        // pick up sunStone
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // check sunStone is still in the player's inventory
        // but the player has walked through the door
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test whether the sunStone can be used when building entities")
    public void buildingTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherCollectableTest_building", "c_furtherCollectableTest_building");

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Wood x2
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // Pick up SunStone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Shield
        assertEquals(0, TestUtils.getInventory(res, "shield").size());
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());

        // Wood disappears from inventory
        // SunStone stays in the inventory
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("17-4")
    @DisplayName("Test whether the sunStone cannot be used to bribe mercenaries and assassins")
    public void bribeTest() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_furtherCollectableTest_bribe", "c_furtherCollectableTest_bribe");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(9, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(8, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up sunStone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // pick up third treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // bribe successful
        // treasure x3 disappears from inventory
        // sunStone remains in inventory
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
