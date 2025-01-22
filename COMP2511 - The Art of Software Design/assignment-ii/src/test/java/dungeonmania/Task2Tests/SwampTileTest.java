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

public class SwampTileTest {

    @Test
    @Tag("15-1")
    @DisplayName("Test Swamp Tile doesn't affect player")
    public void testSwampTilePlayer() {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        DungeonResponse dungeonResponse = controller.newGame("d_swampTileTest_player",  "c_swampTileTest_main");


        assertEquals(new Position(4, 1),  TestUtils.getPlayerPos(dungeonResponse));
        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1),  TestUtils.getPlayerPos(dungeonResponse));
        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1),  TestUtils.getPlayerPos(dungeonResponse));
        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1),  TestUtils.getPlayerPos(dungeonResponse));
    }

    @Test
    @Tag("15-2")
    @DisplayName("Test Swamp Tile doesn't affect adjacent allies")
    public void testSwampTileAdjacentAlly() {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        DungeonResponse dungeonResponse = controller.newGame("d_swampTileTest_ally",  "c_swampTileTest_main");

        String mercId = TestUtils.getEntitiesStream(dungeonResponse,  "mercenary").findFirst().get().getId();

        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        assertEquals(1,  TestUtils.countEntityOfTypeInInventory(dungeonResponse,  "treasure"));

        assertDoesNotThrow(() -> controller.interact(mercId));
        assertEquals(new Position(4, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));

        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));

    }

    @Test
    @Tag("15-3")
    @DisplayName("Test Swamp Tile affects mercenary correctly")
    public void testSwampTileMercenary() {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        DungeonResponse dungeonResponse = controller.newGame("d_swampTileTest_mercenary",  "c_swampTileTest_main");


        assertEquals(new Position(5, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
    }

    @Test
    @Tag("15-4")
    @DisplayName("Test Swamp Tile affects zombietoast correctly")
    public void testSwampTileZombie() {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        DungeonResponse dungeonResponse = controller.newGame("d_swampTileTest_zombieToast",  "c_swampTileTest_main");

        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "zombie_toast"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "zombie_toast"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "zombie_toast"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "zombie_toast"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "zombie_toast"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "zombie_toast"));
    }

    @Test
    @Tag("15-5")
    @DisplayName("Test Swamp Tile affects spider correctly")
    public void testSwampTileSpider() {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        DungeonResponse dungeonResponse = controller.newGame("d_swampTileTest_spider",  "c_swampTileTest_main");

        assertEquals(new Position(6, 2),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 2),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 3),  TestUtils.getEntityPos(dungeonResponse,  "spider"));
    }

    @Test
    @Tag("15-6")
    @DisplayName("Test 2 SwampTile's in succession")
    public void testSwampTileMultiple() {
        DungeonManiaController controller;
        controller = new DungeonManiaController();
        DungeonResponse dungeonResponse = controller.newGame("d_swampTileTest_multiple",  "c_swampTileTest_main");

        assertEquals(new Position(5, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(6, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(7, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
        dungeonResponse = controller.tick(Direction.NONE);
        assertEquals(new Position(8, 1),  TestUtils.getEntityPos(dungeonResponse,  "mercenary"));
    }
}
