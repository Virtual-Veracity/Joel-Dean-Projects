package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExampleTests {
    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
        

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"), controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"), controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void testSomeExceptionsForSend() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "RelaySatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "DesktopDevice", Angle.fromDegrees(300));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class, () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("NonExistentFile", "Satellite1", "DeviceB"));
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class, () -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
    }

    @Test
    public void testMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate(57);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(221.33), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));

    }

    @Test
    public void testWrapAround() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(1));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(1), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(358.96), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testExample() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        // Hints for further testing:
        // - What about checking about the progress of the message half way through?
        // - Device/s get out of range of satellite
        // ... and so on.
    }

    @Test
    public void testRelayMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                                Angle.fromDegrees(180));

        // moves in negative direction
        assertEquals(
                        new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER,
                                        "RelaySatellite"),
                        controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        // edge case
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        // coming back
        controller.simulate(1);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testRelayMovementOutsideBoundary() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "RelaySatellite", 9000 + RADIUS_OF_JUPITER,
                                Angle.fromDegrees(345));

        // Move ANTI_CLOCKWISE to boundary
        assertEquals(
                        new EntityInfoResponse("Satellite1", Angle.fromDegrees(345), 9000 + RADIUS_OF_JUPITER,
                                        "RelaySatellite"),
                        controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(346.09), 9000 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(25);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(13.29), 9000 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        
        // Move CLOCKWISE to boundary
        controller.createSatellite("Satellite2", "RelaySatellite", 9000 + RADIUS_OF_JUPITER,
        Angle.fromDegrees(344));
        assertEquals(
                        new EntityInfoResponse("Satellite2", Angle.fromDegrees(344), 9000 + RADIUS_OF_JUPITER,
                                        "RelaySatellite"),
                        controller.getInfo("Satellite2"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(342.91), 9000 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite2"));
    }

    @Test
    public void testTeleportingMovement() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(0));

        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);

        // Ensure direction is swapped after teleport 
        controller.simulate();
        Angle antiClockwiseOnThirdMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle antiClockwiseOnFourthMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(antiClockwiseOnThirdMovement.compareTo(antiClockwiseOnFourthMovement) == -1);
    }

    @Test
    public void testTeleportingSatelliteCustom() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        // Test: Teleport moves ANTI-CLOCKWISE at first
        controller.createSatellite("Satellite1", "TeleportingSatellite", 9000 + RADIUS_OF_JUPITER, Angle.fromDegrees(160));
        // Move ANTI_CLOCKWISE
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(160), 9000 + RADIUS_OF_JUPITER, "TeleportingSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(160.73), 9000 + RADIUS_OF_JUPITER, "TeleportingSatellite"), controller.getInfo("Satellite1"));
        
        controller.simulate(525);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(0), 9000 + RADIUS_OF_JUPITER, "TeleportingSatellite"), controller.getInfo("Satellite1"));
    }

    @Test 
    public void testInRangeSatellites() {
        BlackoutController controller = new BlackoutController();

        // Devices
        // Standard
        controller.createSatellite("Satellite1", "StandardSatellite", 100000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(175));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(175));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(175));
        controller.createSatellite("Satellite2", "StandardSatellite", 200000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 225000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite5", "TeleportingSatellite", 300000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "Satellite2", "Satellite3"), controller.communicableEntitiesInRange("Satellite1"));

        controller = new BlackoutController();

        // Relay
        controller.createSatellite("Satellite1", "RelaySatellite", 200000 + RADIUS_OF_JUPITER, Angle.fromDegrees(5));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(5));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(5));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(5));
        controller.createSatellite("Satellite2", "StandardSatellite", 600000 + RADIUS_OF_JUPITER, Angle.fromDegrees(5));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 450000 + RADIUS_OF_JUPITER, Angle.fromDegrees(5));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC", "Satellite3"), controller.communicableEntitiesInRange("Satellite1"));

        controller = new BlackoutController();

        // Teleporting
        controller.createSatellite("Satellite1", "TeleportingSatellite", 300000 + RADIUS_OF_JUPITER, Angle.fromDegrees(240));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(240));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(240));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(240));
        controller.createSatellite("Satellite2", "StandardSatellite", 700000 + RADIUS_OF_JUPITER, Angle.fromDegrees(240));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 400000 + RADIUS_OF_JUPITER, Angle.fromDegrees(240));
        
        
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite3"), controller.communicableEntitiesInRange("Satellite1"));
    }

    @Test
    public void testRelayWithDevices() {
        BlackoutController controller = new BlackoutController();
        // With Relay
        controller.createSatellite("Satellite1", "StandardSatellite", 35000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite4", "StandardSatellite", 300000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite5", "StandardSatellite", 370000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(175));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite4"), controller.communicableEntitiesInRange("DeviceA"));
        
        controller = new BlackoutController();
        // With Relay
        controller.createSatellite("Satellite1", "StandardSatellite", 35000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite4", "RelaySatellite", 340000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite5", "StandardSatellite", 400000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(175));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite4", "Satellite5"), controller.communicableEntitiesInRange("DeviceA"));
    }

    public void testRelayWithSatellitess() {
        BlackoutController controller = new BlackoutController();

        // With Relay
        controller.createSatellite("Satellite1", "StandardSatellite", 35000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite4", "StandardSatellite", 300000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite5", "StandardSatellite", 370000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(175));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(320));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite4"), controller.communicableEntitiesInRange("DeviceA"));
        
        controller = new BlackoutController();
        // With Relay
        controller.createSatellite("Satellite1", "StandardSatellite", 35000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite4", "RelaySatellite", 340000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createSatellite("Satellite5", "StandardSatellite", 400000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(175));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2", "Satellite4", "Satellite5"), controller.communicableEntitiesInRange("DeviceA"));
    }

    @Test
    public void testVisibilityAll() {
        BlackoutController controller = new BlackoutController();

        // With Relay
        controller.createSatellite("Satellite1", "TeleportingSatellite", RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Satellite2", "RelaySatellite", RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(10));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(275));
        
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite3"), controller.communicableEntitiesInRange("Satellite1"));

        controller = new BlackoutController();

        // With Relay
        controller.createSatellite("Satellite1", "TeleportingSatellite", RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Satellite2", "RelaySatellite", RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(10));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(275));
        
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.communicableEntitiesInRange("Satellite3"));
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("Satellite2"));

        controller = new BlackoutController();

        // With Relay
        controller.createSatellite("Satellite1", "TeleportingSatellite", RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("Satellite2", "RelaySatellite", RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(10));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(275));
        
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));
    }

    @Test 
    public void testBandwidthExceptions() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(310));
        controller.createSatellite("Satellite2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(280));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(300));

        // Standard Satellite
        String msg1 = "Bandwidth1";
        String msg2 = "B2";
        String msgLong = "Bandwidth3 File is wayyyyyyy tooooooo big for a standard satellite Whatchu gunna do bout it sucks to be u im running out of things to say to make this message longer like it need to be more than 200 so I guess thats good";
        String msgChar = "J";
        String msg5 = "Test message";
        controller.addFileToDevice("DeviceA", "FileBand1", msg1);
        controller.addFileToDevice("DeviceA", "FileBand2", msg2);
        controller.addFileToDevice("DeviceA", "FileBandLong", msgLong);
        controller.addFileToDevice("DeviceA", "FileBandChar", msgChar);
        controller.addFileToDevice("DeviceA", "FileBand5", msg5);
        assertDoesNotThrow(() -> controller.sendFile("FileBand1", "DeviceA", "Satellite1"));
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("FileBand2", "DeviceA", "Satellite1"));
        controller.simulate(msg1.length() * 2);
        assertDoesNotThrow(() -> controller.sendFile("FileBand2", "DeviceA", "Satellite1"));
        controller.simulate(msg1.length() * 2);
        System.out.println(controller.getInfo("Satellite1").getFiles());
        assertEquals(new FileInfoResponse("FileBand2", "B2", msg2.length(), true), controller.getInfo("Satellite1").getFiles().get("FileBand2"));
        assertDoesNotThrow(() -> controller.sendFile("FileBand5", "DeviceA", "Satellite1"));
        assertDoesNotThrow(() -> controller.sendFile("FileBand1", "Satellite1", "Satellite3"));
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("FileBand2", "Satellite1", "Satellite3"));

    }

    @Test 
    public void testStorageExceptions() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(300));

        // Standard Satellite
        String msg1 = "Bandwidth1";
        String msg2 = "Bandwidth2";
        String msgLong = "Bandwidth3 File is wayyyyyyy tooooooo big for a standard satellite Whatchu gunna do bout it sucks to be u im running out of things to say to make this message longer like it need to be more than 200 so I guess thats good";
        String msgChar = "J";
        String msg5 = "Test message";
        controller.addFileToDevice("DeviceA", "FileBand1", msg1);
        controller.addFileToDevice("DeviceA", "FileBand2", msg2);
        controller.addFileToDevice("DeviceA", "FileBandLong", msgLong);
        controller.addFileToDevice("DeviceA", "FileBandChar", msgChar);
        controller.addFileToDevice("DeviceA", "FileBand5", msg5);
        assertDoesNotThrow(() -> controller.sendFile("FileBand1", "DeviceA", "Satellite1"));
        controller.simulate(msg1.length() * 2);
        assertDoesNotThrow(() -> controller.sendFile("FileBand2", "DeviceA", "Satellite1"));
        controller.simulate(msg2.length() * 2);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("FileBandLong", "DeviceA", "Satellite1"), "Max Storage Reached");
        assertDoesNotThrow(() -> controller.sendFile("FileBand5", "DeviceA", "Satellite1"));
        controller.simulate(msg5.length() * 2);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("FileBandChar", "DeviceA", "Satellite1"), "Max Files Reached");

        // Teleporting Satellite
        controller.addFileToDevice("DeviceB", "FileBand1", msg1);
        controller.addFileToDevice("DeviceB", "FileBand2", msg2);
        controller.addFileToDevice("DeviceB", "FileBandLong", msgLong);
        controller.addFileToDevice("DeviceB", "FileBandChar", msgChar);
        controller.addFileToDevice("DeviceB", "FileBand5", msg5);
        assertDoesNotThrow(() -> controller.sendFile("FileBand1", "Satellite1", "Satellite3"));
        controller.simulate(msg1.length() * 2);
        System.out.println(controller.getInfo("Satellite1").getFiles());
        assertDoesNotThrow(() -> controller.sendFile("FileBand2", "Satellite1", "Satellite3"));
        controller.simulate(msg2.length() * 2);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("FileBandLong", "DeviceB", "Satellite3"), "Max Storage Reached");
        assertDoesNotThrow(() -> controller.sendFile("FileBand5", "DeviceB", "Satellite3"));
        controller.simulate(msg5.length() * 2);
        assertDoesNotThrow(() -> controller.sendFile("FileBandChar", "DeviceB", "Satellite3"));
    }

    @Test 
    public void testSendSpeed() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(330));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));

        String msg1 = "Speed";
        controller.addFileToDevice("DeviceA", "File1", msg1);

        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate(msg1.length() * 2);

        assertDoesNotThrow(() -> controller.sendFile("File1", "Satellite1", "Satellite2"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File1", "S", msg1.length(), false), controller.getInfo("Satellite2").getFiles().get("File1"));

        controller.simulate(2);
        assertEquals(new FileInfoResponse("File1", "Spe", msg1.length(), false), controller.getInfo("Satellite2").getFiles().get("File1"));
    }

    @Test 
    public void testOutOfRange() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 40000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(310));

        String msgLong = "Bandwidth3 File is wayyyyyyy tooooooo big for a standard satellite";
        controller.addFileToDevice("DeviceA", "File1", msgLong);
        assertDoesNotThrow(() -> controller.sendFile("File1", "DeviceA", "Satellite1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("File1", "B", msgLong.length(), false), controller.getInfo("Satellite1").getFiles().get("File1"));
        controller.simulate(79);
    }

    @Test
    public void testOutOfRangeTeleport() {
        
    }
}
