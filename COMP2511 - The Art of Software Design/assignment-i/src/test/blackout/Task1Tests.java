package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.Device;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;
import java.util.ArrayList;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task1Tests {

    /* Using Black-Box Unit Tests
     * Test Brainstorming
     * Blackout Controller
     * Class: BlackoutController
     * Section 1: createDevice
     * Test 1: Succesfully creates one device
     */
    @Test
    public void testCreateOneDevice() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(320));
        assertEquals(new EntityInfoResponse("The Machineee", Angle.fromDegrees(320), RADIUS_OF_JUPITER, "DesktopDevice"), controller.getInfo("The Machineee"));
    }
    /*
     * Test 2: Succesfully creates multiple devices of each type
     */
    @Test
    public void testCreateMultipleDevice() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("Beep Boop", "HandheldDevice", Angle.fromRadians(2));
        controller.createDevice("It Burns ", "LaptopDevice", Angle.fromDegrees(360));


        assertEquals(new EntityInfoResponse("The Machineee", Angle.fromDegrees(0), RADIUS_OF_JUPITER, "DesktopDevice"), controller.getInfo("The Machineee"));
        assertEquals(new EntityInfoResponse("Beep Boop", Angle.fromRadians(2), RADIUS_OF_JUPITER, "HandheldDevice"), controller.getInfo("Beep Boop"));
        assertEquals(new EntityInfoResponse("It Burns ", Angle.fromDegrees(360), RADIUS_OF_JUPITER, "LaptopDevice"), controller.getInfo("It Burns "));
    }


    /* Section 2: Remove Device
     * Test 1: Check that all types of devices may be removed 
     */
    @Test
    public void testRemoveOneDevice() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        assertEquals(new EntityInfoResponse("The Machineee", Angle.fromDegrees(0), RADIUS_OF_JUPITER, "DesktopDevice"), controller.getInfo("The Machineee"));

        controller.removeDevice("The Machineee");
        assertEquals(new ArrayList<Device>(), controller.listDeviceIds());
    }
     
     /*
     * Test 2: Check that when 2 devices are created and one is removed, the correct one is removed
     */
    @Test
    public void testRemoveMultipleDevice() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("Beep Boop", "HandheldDevice", Angle.fromRadians(2));
        controller.createDevice("It Burns ", "LaptopDevice", Angle.fromDegrees(360));


        assertEquals(new EntityInfoResponse("The Machineee", Angle.fromDegrees(0), RADIUS_OF_JUPITER, "DesktopDevice"), controller.getInfo("The Machineee"));
        assertEquals(new EntityInfoResponse("Beep Boop", Angle.fromRadians(2), RADIUS_OF_JUPITER, "HandheldDevice"), controller.getInfo("Beep Boop"));
        assertEquals(new EntityInfoResponse("It Burns ", Angle.fromDegrees(360), RADIUS_OF_JUPITER, "LaptopDevice"), controller.getInfo("It Burns "));
    
        controller.removeDevice("The Machineee");
        assertEquals(Arrays.asList("Beep Boop", "It Burns "), controller.listDeviceIds());

        controller.removeDevice("It Burns ");
        assertEquals(Arrays.asList("Beep Boop"), controller.listDeviceIds());

        controller.createDevice("From the Ashes", "LaptopDevice", Angle.fromDegrees(360));
        assertEquals(Arrays.asList("Beep Boop", "From the Ashes"), controller.listDeviceIds());
    } 

     /* Section3: Create Satellite
     * Tests: Copy device creation tests
     */
    @Test
    public void testCreateOneSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Blinky", "StandardSatellite", 90000.0, Angle.fromDegrees(60));
        assertEquals(new EntityInfoResponse("Blinky", Angle.fromDegrees(60), 90000.0, "StandardSatellite"), controller.getInfo("Blinky"));
    }
    @Test
    public void testCreateMultipleSatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("No Privacy", "RelaySatellite", 100000, Angle.fromRadians(2));
        controller.createSatellite("Burn the Stars", "TeleportingSatellite", 82000 , Angle.fromDegrees(360));
        controller.createSatellite("Blinky", "StandardSatellite", 95000, Angle.fromDegrees(60));

        assertEquals(new EntityInfoResponse("Blinky", Angle.fromDegrees(60), 95000, "StandardSatellite"), controller.getInfo("Blinky"));
        assertEquals(new EntityInfoResponse("No Privacy", Angle.fromRadians(2), 100000, "RelaySatellite"), controller.getInfo("No Privacy"));
        assertEquals(new EntityInfoResponse("Burn the Stars", Angle.fromDegrees(360), 82000, "TeleportingSatellite"), controller.getInfo("Burn the Stars"));
    }
     

    /*
     * Test 2: Throw in device creation to mix it up
     */
    @Test
    public void testCreateSatellitesWithDevices() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("No Privacy", "RelaySatellite", 100000, Angle.fromRadians(2));
        controller.createSatellite("Burn the Stars", "TeleportingSatellite", 82000 , Angle.fromDegrees(360));
        controller.createSatellite("Blinky", "StandardSatellite", 95000, Angle.fromDegrees(60));

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("Beep Boop", "HandheldDevice", Angle.fromRadians(2));
        controller.createDevice("It Burns ", "LaptopDevice", Angle.fromDegrees(360));


        assertEquals(new EntityInfoResponse("The Machineee", Angle.fromDegrees(0), RADIUS_OF_JUPITER, "DesktopDevice"), controller.getInfo("The Machineee"));
        assertEquals(new EntityInfoResponse("Beep Boop", Angle.fromRadians(2), RADIUS_OF_JUPITER, "HandheldDevice"), controller.getInfo("Beep Boop"));

        controller.removeDevice("The Machineee");
        assertEquals(Arrays.asList("Beep Boop", "It Burns "), controller.listDeviceIds());

        controller.removeDevice("It Burns ");
        assertEquals(Arrays.asList("Beep Boop"), controller.listDeviceIds());

        assertEquals(new EntityInfoResponse("Blinky", Angle.fromDegrees(60), 95000, "StandardSatellite"), controller.getInfo("Blinky"));
        assertEquals(new EntityInfoResponse("No Privacy", Angle.fromRadians(2), 100000, "RelaySatellite"), controller.getInfo("No Privacy"));
        assertEquals(new EntityInfoResponse("Burn the Stars", Angle.fromDegrees(360), 82000, "TeleportingSatellite"), controller.getInfo("Burn the Stars"));
    }
     

     /* Section 4: Remove Satellite
     * Tests: Copy device removal
     */
    @Test
    public void testRemoveOneSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        assertEquals(new EntityInfoResponse("The Machineee", Angle.fromDegrees(0), RADIUS_OF_JUPITER, "DesktopDevice"), controller.getInfo("The Machineee"));

        controller.removeDevice("The Machineee");
        assertEquals(new ArrayList<Device>(), controller.listDeviceIds());
    }
     
     /*
     * Test 2: Check that when 2 devices are created and one is removed, the correct one is removed
     */
    @Test
    public void testRemoveMultipleSatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("No Privacy", "RelaySatellite", 100000, Angle.fromRadians(2));
        controller.createSatellite("Burn the Stars", "TeleportingSatellite", 82000 , Angle.fromDegrees(360));
        controller.createSatellite("Blinky", "StandardSatellite", 95000, Angle.fromDegrees(60));


        assertEquals(new EntityInfoResponse("Blinky", Angle.fromDegrees(60), 95000, "StandardSatellite"), controller.getInfo("Blinky"));
        assertEquals(new EntityInfoResponse("No Privacy", Angle.fromRadians(2), 100000, "RelaySatellite"), controller.getInfo("No Privacy"));
        assertEquals(new EntityInfoResponse("Burn the Stars", Angle.fromDegrees(360), 82000, "TeleportingSatellite"), controller.getInfo("Burn the Stars"));
    
        controller.removeSatellite("Burn the Stars");
        assertEquals(Arrays.asList("No Privacy", "Blinky"), controller.listSatelliteIds());

        controller.removeSatellite("No Privacy");
        assertEquals(Arrays.asList("Blinky"), controller.listSatelliteIds());
        
        controller.createSatellite("From the Ashes", "TeleportingSatellite", 101111.0, Angle.fromDegrees(360));
        assertEquals(Arrays.asList("Blinky", "From the Ashes"), controller.listSatelliteIds());
    } 
    
     

     /* Section 5: Listing
     * Test 1: List devices when satellites are present
     */
    @Test
    public void testListDevices() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("No Privacy", "RelaySatellite", 100000, Angle.fromRadians(2));
        controller.createSatellite("Burn the Stars", "TeleportingSatellite", 82000 , Angle.fromDegrees(360));
        controller.createSatellite("Blinky", "StandardSatellite", 95000, Angle.fromDegrees(60));
        
        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("Beep Boop", "HandheldDevice", Angle.fromRadians(2));
        controller.createDevice("It Burns ", "LaptopDevice", Angle.fromDegrees(360));

        assertEquals(Arrays.asList("The Machineee", "Beep Boop", "It Burns "), controller.listDeviceIds());
        assertEquals(Arrays.asList("No Privacy", "Burn the Stars", "Blinky"), controller.listSatelliteIds());
    }

     /* Test 2: Same but no satellites
    */
    @Test
    public void testListDeviceOnly() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("Beep Boop", "HandheldDevice", Angle.fromRadians(2));
        controller.createDevice("It Burns ", "LaptopDevice", Angle.fromDegrees(360));

        assertEquals(Arrays.asList("The Machineee", "Beep Boop", "It Burns "), controller.listDeviceIds());

    }

     /* Test 4: Same but no devices
    */  
    @Test
    public void testListSatelliteOnly() {
        BlackoutController controller = new BlackoutController();
        
        controller.createSatellite("No Privacy", "RelaySatellite", 100000, Angle.fromRadians(2));
        controller.createSatellite("Burn the Stars", "TeleportingSatellite", 82000 , Angle.fromDegrees(360));
        controller.createSatellite("Blinky", "StandardSatellite", 95000, Angle.fromDegrees(60));
        
        assertEquals(Arrays.asList("No Privacy", "Burn the Stars", "Blinky"), controller.listSatelliteIds());
    }


     
     /* Section 6: Add files (Devices)
     * Test 1: One Device + One file
     * Test 2: Multiple Devices and multiple files
     */ 
    @Test
    public void testAddFileOneDevice() {
        BlackoutController controller = new BlackoutController();
        
        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.addFileToDevice("The Machineee", "Coding Skill", "Okay for an Organic");
    }

    @Test
    public void testAddFileMutlipleDevice() {
        BlackoutController controller = new BlackoutController();
        
        controller.createDevice("The Machineee", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("Beep Boop", "HandheldDevice", Angle.fromRadians(2));
        controller.createDevice("It Burns ", "LaptopDevice", Angle.fromDegrees(360));
        
        controller.addFileToDevice("The Machineee", "Coding Skill", "Okay for an Organic");
        controller.addFileToDevice("Beep Boop", "Muscle Size", "Massive");
        controller.addFileToDevice("It Burns ", "Cat", "Renny");

        assertEquals(new FileInfoResponse("Coding Skill", "Okay for an Organic", 19, true),controller.getInfo("The Machineee").getFiles().get("Coding Skill"));
        assertEquals(new FileInfoResponse("Muscle Size", "Massive", 7, true),controller.getInfo("Beep Boop").getFiles().get("Muscle Size"));
        assertEquals(new FileInfoResponse("Cat", "Renny", 5, true),controller.getInfo("It Burns ").getFiles().get("Cat"));
    }
}
    
