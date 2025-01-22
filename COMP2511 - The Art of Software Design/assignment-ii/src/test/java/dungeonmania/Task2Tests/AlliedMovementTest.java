package dungeonmania.Task2Tests;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import dungeonmania.mvp.TestUtils;
import dungeonmania.util.Position;

import static org.junit.jupiter.api.Assertions.*;

public class AlliedMovementTest {
    @Test
    @Tag("15-1")
    @DisplayName("Test Mercenary follows player when allied")
    public void alliedAwayFromPlayer() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_ally", "c_alliedMovementTest_ally");

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(8, 1), getMercPos(res));

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        assertDoesNotThrow(() -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(new Position(5, 1), getPlayerPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(6, 1), getPlayerPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(6, 1), getMercPos(res));
        assertEquals(new Position(5, 1), getPlayerPos(res));
    }

    @Test
    @Tag("15-2")
    @DisplayName("Test Mercenary follows player when allied")
    public void alliedCloseToPlayer() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_allySameTick", "c_alliedMovementTest_ally");

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        assertDoesNotThrow(() -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));
    }

    @Test
    @Tag("15-3")
    @DisplayName("Test Mercenary fights player when not allied")
    public void notAlliedFightPlayer() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_ally", "c_alliedMovementTest_ally");

        assertEquals(1, TestUtils.countType(res, "mercenary"));

        for (int i = 1; i <= 4; i++)
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.countType(res, "mercenary"));
    }

    @Test
    @Tag("15-3")
    @DisplayName("Test Mercenary movement after player goes in portal")
    public void testAllyMovePortal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_ally", "c_alliedMovementTest_ally");
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }
}
