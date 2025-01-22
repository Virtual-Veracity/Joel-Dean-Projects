package unsw.blackout;

/*
 * Author: Joel Dean (z5208947)
 * Course: COMP2511 Assignment 1 
 * Date: Term 3 2022
 */

//====================  Libraries ============================
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;


/* Available Libraries for assignment 1 
java.io
java.lang
java.math
java.net
java.nio
java.rmi
java.security
java.text
java.time
java.util
*/

public class BlackoutController {

    // Blackout Class Attributes
    public static final double RADIUS_JUPITER = 69911.0;

    private List<Device> deviceList = new ArrayList<Device>();
    private List<Satellite> satelliteList = new ArrayList<Satellite>();
    private final int MAX_TRANSFER_SPEED = 20;



    //======================================= Blackout Class Methods =============================================
    /**
     * createDevice Function: Creates a device of type (Handheld/Desktop/Laptop) with a position
     * @param deviceId
     * @param type
     * @param position
     */
    public void createDevice(String deviceId, String type, Angle position) {
        deviceList.add(new Device(deviceId, type, position));
    }

    /**
     * removeDevice method: Removes a device by setting the pointer to null and removing from deviceList
     * @param deviceId
     */
    public void removeDevice(String deviceId) {
        for (Device currentDevice : deviceList) {
            String currentDeviceID = currentDevice.getID();

            if (deviceId.compareTo(currentDeviceID) == 0) {
                deviceList.remove(currentDevice);
                break;
            }
        }
    }

    
    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        switch (type) {
            case "StandardSatellite":
                satelliteList.add(new StandardSatellite(satelliteId, type, height, position));     break;
            case "RelaySatellite":
                satelliteList.add(new RelaySatellite(satelliteId, type, height, position));        break;
            case "TeleportingSatellite":
                satelliteList.add(new TeleportingSatellite(satelliteId, type, height, position));  break;
        }
    }

    public void removeSatellite(String satelliteId) {
        for (Satellite currentSatellite : satelliteList) {
            String currentSatelliteID = currentSatellite.getID();

            if (satelliteId.compareTo(currentSatelliteID) == 0) {
                satelliteList.remove(currentSatellite);
                break;
            }
        }
    }

    public List<String> listDeviceIds() {
        List<String> deviceIDList = new ArrayList<String>();
        for (Device currentDevice : deviceList) {
            deviceIDList.add(currentDevice.getID());
        }
        return deviceIDList;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIDList = new ArrayList<String>();
        for (Satellite currentSatellite : satelliteList) {
            satelliteIDList.add(currentSatellite.getID());
        }
        return satelliteIDList;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = findDevice(deviceId);
        device.addFile(filename, content);
    }

    /**
     * Create an EntityInfoResponse object with the relevant info
     * And return it
     * @param id
     * @return
     */
    public EntityInfoResponse getInfo(String id) {

        Machine machine = findMachine(id);

        // Obtain information from device/satellite
        if (machine == null) {
            System.out.println("Id Does not exist");
        }   

        List<File> fileList = machine.getFileList();
        EntityInfoResponse entity = null;
        if (fileList.isEmpty()) {
            entity = new EntityInfoResponse(id, machine.getPosition(), machine.getHeight(), machine.getType());
        } else {
            Map<String, FileInfoResponse> fileResponse = createFileThing(fileList);
            entity = new EntityInfoResponse(id, machine.getPosition(), machine.getHeight(), machine.getType(), fileResponse);
        }
        return entity;
    }

    /*
     * Each time the simulate function is called, another minute passes
     */
    public void simulate() {
        // For every Satellite
            // Move Satellite Position
            // Update all file transfers
        for (Satellite satellite : satelliteList) {

            boolean teleport = satellite.moveSatellite();
            if (!teleport) {teleport(satellite);}
            updateFileTransfer(satellite.getTransferList(), satellite.getID());
        }
        // For every Device
            // Update all files transfers
        for (Device device : deviceList) {
            updateFileTransfer(device.getTransferList(), device.getID());
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {

        Machine machine = findMachine(id);
        List<String> inRangeList = new ArrayList<String>();

        // Check all Devices
        for (Device currentDevice : deviceList) {
            if (machine.checkRangeDevice(currentDevice)) {
                inRangeList.add(currentDevice.getID());
            }
        }

        // Check all Satellites
        for (Satellite currentSatellite : satelliteList) {
            if (machine.checkRangeSatellite(currentSatellite)) {
                
                // If Relay -> Extend Range
                if (currentSatellite instanceof RelaySatellite) {
                    inRangeList = checkRelayExtend(currentSatellite, inRangeList, id);
                }

                if (!inRangeList.contains(currentSatellite.getID())) {
                    inRangeList.add(currentSatellite.getID());
                }
            }
       }
        return inRangeList;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {

        //  Check file exists Sender (With list or transfer incomplete)
        //  No Bandwidth (To little to send E.g. 8 files can take 8 bytes/per sec)
        //  Check file exists Receiver (With list or transfer incomplete)
        //  Check Storage (Files or bytes)

        // Sender / Receiver
        Machine receiver = findMachine(toId);
        Machine sender = findMachine(fromId);

        // 1. =========== File Not Found ==============
        if (sender.checkFileExists(fileName) == false) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        // 2. =========== No Bandwidth ==============
        sender.enoughBandwidth(true);
        receiver.enoughBandwidth(false);

        // 3. =========== File Already Exists ==============
        if (receiver.checkFileExists(fileName) == true) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }

        // 4. =========== Insufficient Storage ==============
        receiver.checkStorage(sender.getFileLength(fileName));


        // =============================  Send File =========================================
        File fileToSend = sender.getFile(fileName);
        sender.startUpload(fileToSend, toId);
        receiver.startDownload(fileToSend, fromId);
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }


    /*
     *   =========================  BlackoutController Class: Helper Methods  ============================
     */

    private Machine findMachine(String ID) {
        Machine foundMachine = findDevice(ID);
        if (foundMachine == null) {
            return findSatellite(ID);
        }
        return foundMachine;
    }

    /**
     * Searches for a device in deviceList using an id
     * Returns the device or null (If not found)
     * @param deviceID
     * @return
     */
    private Device findDevice(String deviceID) {
        for (Device currentDevice : deviceList) {
            String currentDeviceID = currentDevice.getID();

            if (deviceID.compareTo(currentDeviceID) == 0) {
                return currentDevice;
            }
        }
        return null;
    }
    /**
     * Searches for a satellite in satelliteList using an id
     * Returns the satellite or null (If not found)
     * @param satelliteID
     * @return
     */
    private Satellite findSatellite(String satelliteID) {
        for (Satellite currentSatellite : satelliteList) {
            String currentsatelliteID = currentSatellite.getID();

            if (satelliteID.compareTo(currentsatelliteID) == 0) {
                return currentSatellite;
            }
        }
        return null;
    }

    /**
     * Creates a file object to use within the EntityResponseObject
     * @param files
     * @return
     */
    private Map<String, FileInfoResponse> createFileThing(List<File> files) {

        Map<String, FileInfoResponse> map = new HashMap<String, FileInfoResponse>();

        for (File file : files) {
            map.put(file.getFileName(), new FileInfoResponse(file.getFileName(), file.getFileContent(), file.getFileCompleteContent().length(), file.getFileComplete()));
        }
        return map;
    }

    /**
     * Updates a File Transfer
     *      - Ensure the receiver and sender are within range (Distance/Visibility)
     *      - Find new Transfer Speed
     *      - Add Content to File
     * @param fileList
     * @param receiverID
     * @return
     */
    private void updateFileTransfer(List<File> actualFileList, String receiverID) {
        List<File> fileList = new ArrayList<File>(actualFileList);
        // ============ For every File Transfer ================
        for (File file : fileList) {
            //================ Out Of Range Check ===================
            if (!communicableEntitiesInRange(file.getSenderID()).contains(receiverID)) {
                actualFileList.remove(file);
                findMachine(file.getSenderID()).deadFile(file);
            }

            //================ Determine File Transfer Speed ===================
            int fileTransferSpeed = determineTransferSpeed(receiverID, file.getSenderID());

            // ================ Determine Updated File Content ===================
            String completeFileContent = file.getFileCompleteContent();
            int newContentSize = fileTransferSpeed + file.getFileLength();
            // ========= Transfer Complete ============
            if (newContentSize >= completeFileContent.length()) {
                file.setFileContent(completeFileContent);
                file.setFileComplete(true);
                
                Machine fileSender = findMachine(file.getSenderID());
                Machine fileReceiver = findMachine(receiverID);
                fileSender.finishUpload(file);
                fileReceiver.finishDownload(file);

            //======== Progress File Transfer ============
            } else {
                String updatedContent = completeFileContent.substring(0, newContentSize);
                file.setFileContent(updatedContent);
            }
        }
    }

    /**
     * Determines the File Transfer Speed of the current World State
     * @param receiverID
     * @param senderID
     * @return
     */
    private int determineTransferSpeed(String receiverID, String senderID) {
        int senderTransferSpeed = MAX_TRANSFER_SPEED;
        int receiverTransferSpeed = MAX_TRANSFER_SPEED;
        Satellite fileSender = findSatellite(senderID);
        Satellite fileReceiver = findSatellite(receiverID);

        if (fileSender != null) {
            senderTransferSpeed = fileSender.getSatelliteSpeed(true);
        }
        if (fileReceiver != null) {
            receiverTransferSpeed = fileReceiver.getSatelliteSpeed(false);
        }

        return Math.min(senderTransferSpeed, receiverTransferSpeed);
    }

    /**
     * Extends a device/satellite's range, if a relay is present
     * @param satellite
     * @param inRangeList
     * @return
     */
    private List<String> checkRelayExtend(Satellite satellite, List<String> inRangeList, String currentID) {
        // Relay Satellite's Extend Range
        RelaySatellite relaySatellite = (RelaySatellite) satellite;
        if (relaySatellite.getCurrentRelaying() == false) {
            relaySatellite.setCurrentRelaying();
            List<String> relayList = communicableEntitiesInRange(satellite.getID());
            for (String name : relayList) {
                if (!inRangeList.contains(name) && name != currentID) {
                    inRangeList.add(name);
                }
            }
            relaySatellite.setCurrentRelaying();
        }
        return inRangeList;
    }

    private void teleport(Satellite satellite) {
        for (File file : satellite.getTransferList()) {
            Machine fileSender = findMachine(file.getSenderID());
            fileSender.teleportRangeReceive(file);
            if (fileSender instanceof Device) {
                satellite.deadFile(file);
            } else {
                satellite.teleportRangeSend(file);
            }
        }
        for (File file : satellite.getUploadList()) {
            Machine fileReceiver = findMachine(file.getSenderID());
            fileReceiver.teleportRangeSend(file); 
            satellite.finishUpload(file);
        }
        TeleportingSatellite teleportingSatellite = (TeleportingSatellite) satellite;
        teleportingSatellite.teleport();
    }
}
