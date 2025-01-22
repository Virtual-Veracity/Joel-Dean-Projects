package dungeonmania.Task2Tests;

import dungeonmania.DungeonManiaController;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogicSwitchesTest {
    @Test
    @Tag("19-1")
    @DisplayName("Test whether a XOR light bulb turns on from an activated cardinally adjacent (CA) switch")
    public void lightBulbOnFromSwitchXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_lightBulbOnFromSwitchXOR", "c_LogicSwitchesTest_lightBulbOnFromSwitchXOR");

        // Light bulb should start off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Light bulb should be on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("19-2")
    @DisplayName("Test whether a XOR light bulb turns off from a deactivated CA switch")
    public void lightBulbOffFromSwitchXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_lightBulbOffFromSwitchXOR", "c_LogicSwitchesTest_lightBulbOffFromSwitchXOR");

        // Light bulb should start off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Light bulb should be on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push boulder off switch
        res = dmc.tick(Direction.RIGHT);

        // Light bulb should be off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("19-3")
    @DisplayName("Test whether a XOR light bulb turns on when connected to a CA wire which is connected to a CA switch")
    public void lightBulbOnFromWireXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_lightBulbOnFromWireXOR", "c_LogicSwitchesTest_lightBulbOnFromWireXOR");

        // Light bulb should start off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Light bulb should be on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("19-4")
    @DisplayName("Test whether a XOR switch door opens from an activated CA switch")
    public void switchDoorOpenFromSwitchXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_switchDoorOpenFromSwitchXOR", "c_LogicSwitchesTest_switchDoorOpenFromSwitchXOR");
        Position doorPos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();

        // Push boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // walk through the open switch door
        res = dmc.tick(Direction.RIGHT);
        assertEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-5")
    @DisplayName("Test whether a XOR switch door closes from a deactivated CA switch")
    public void switchDoorClosedFromSwitchXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_switchDoorClosedFromSwitchXOR", "c_LogicSwitchesTest_switchDoorClosedFromSwitchXOR");
        Position doorPos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();

        // Push boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // walk through the open switch door
        res = dmc.tick(Direction.RIGHT);
        assertEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // Push boulder off switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        // cannot walk through a closeed switch door
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-6")
    @DisplayName("Test whether a XOR switch door opens when connected to a CA wire which is connected to a CA switch")
    public void switchDoorOpenFromWireXOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_switchDoorOpenFromWireXOR", "c_LogicSwitchesTest_switchDoorOpenFromWireXOR");
        Position doorPos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();

        // Push boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Put player next to door
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // walk through the open switch door
        res = dmc.tick(Direction.DOWN);
        assertEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-7")
    @DisplayName("Test whether an AND light bulb turns on when connected to 2 CA active conductors")
    public void lightBulbOnFromWireAND() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_lightBulbOnFromWireAND", "c_LogicSwitchesTest_lightBulbOnFromWireAND");

        // Light bulb should start off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push first boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Light bulb should still be off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push second boulder onto switch
        res = dmc.tick(Direction.DOWN);

        // Light bulb should be on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("19-8")
    @DisplayName("Test whether an AND switch door opens when connected to 2 CA active conductors")
    public void switchDoorOpenFromWireAND() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_switchDoorOpenFromWireAND", "c_LogicSwitchesTest_switchDoorOpenFromWireAND");
        Position doorPos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();

        // Push first boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Put player next to door
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Switch door should still be closed
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // Push second boulder onto switch
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Put player next to door
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Switch door is open and then player can move onto it
        res = dmc.tick(Direction.UP);
        assertEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-9")
    @DisplayName("Test whether an OR light bulb turns on when connected to 2 CA active conductors")
    public void lightBulbOnFromWireOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_lightBulbOnFromWireOR", "c_LogicSwitchesTest_lightBulbOnFromWireOR");

        // Light bulb should start off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push first boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Light bulb should be on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push second boulder onto switch
        res = dmc.tick(Direction.DOWN);

        // Light bulb should still be on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("19-10")
    @DisplayName("Test whether an OR switch door opens when connected to 2 CA active conductors")
    public void switchDoorOpenFromWireOR() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_switchDoorOpenFromWireOR", "c_LogicSwitchesTest_switchDoorOpenFromWireOR");
        Position doorPos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();

        // Push first boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Put player next to door
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Switch door should be open
        res = dmc.tick(Direction.DOWN);
        assertEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // Push second boulder onto switch
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Put player next to door
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Switch door should still be open
        res = dmc.tick(Direction.UP);
        assertEquals(doorPos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-11")
    @DisplayName("Test whether a CO_AND light bulb turns on when connected to 2 CA active conductors")
    public void lightBulbOnFromWireCOAND() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_lightBulbOnFromWireCOAND", "c_LogicSwitchesTest_lightBulbOnFromWireCOAND");

        // Both light bulbs should start off
        assertEquals(2, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push first boulder onto switch
        res = dmc.tick(Direction.LEFT);

        // Both light bulbs should still be off
        assertEquals(2, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push second boulder onto switch
        res = dmc.tick(Direction.DOWN);

        // Both light bulbs should still be off
        // This is because the two cardinally adjacent wires
        // to the first light bulb were activated at different times
        assertEquals(2, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Move player back to starting position
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // Push third boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Only 1 light bulb should be on
        // since the two cardinally adjacent wires to the
        // second light bulb were activated at the same time
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("19-12")
    @DisplayName("Test whether an AND switch door opens when connected to 2 CA active conductors")
    public void switchDoorOpenFromWireCOAND() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(
            "d_LogicSwitchesTest_switchDoorOpenFromWireCOAND", "c_LogicSwitchesTest_switchDoorOpenFromWireCOAND");
        Position door1Pos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();
        Position door2Pos = TestUtils.getEntities(res, "switch_door").get(1).getPosition();

        // Push first boulder onto switch
        res = dmc.tick(Direction.LEFT);

        // Move player to be next to the first door
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        // The first switch door is still closed
        res = dmc.tick(Direction.UP);
        assertNotEquals(door1Pos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // Move player back to spot
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);

        // Push second boulder onto switch
        res = dmc.tick(Direction.DOWN);

        // Move player to be next to the first door
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // The first switch door is still closed
        // because the 2 wires cardinally acjacent to
        // the switch door were activated at different times
        res = dmc.tick(Direction.UP);
        assertNotEquals(door1Pos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // Move player back to starting position
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);

        // Push third boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // Move player to be right next to the second door
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        // The second switch door is open
        // because the 2 wires cardinally acjacent to
        // the switch door were activated at the same time
        res = dmc.tick(Direction.RIGHT);
        assertEquals(door2Pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }
}
