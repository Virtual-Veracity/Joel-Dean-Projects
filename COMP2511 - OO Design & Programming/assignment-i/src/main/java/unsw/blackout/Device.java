package unsw.blackout;

/*
 * Author: Joel Dean (z5208947)
 * Course: COMP2511 Assignment 1 
 * Date: Term 3 2022
 */


//====================  Libraries ============================
import java.util.List;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class Device implements Machine{
    
    /*
     *        Device Class: Attributes
     */
    private String deviceID;
    private String deviceType;
    private Angle devicePosition;
    private int deviceRange;
    private FileList deviceFileList = new FileList();

    /*
     *       =====================  Device Class: Constructors    ===============================
     */
    public Device(String newDeviceID, String newType, Angle newPosition) {
        deviceID = newDeviceID;
        deviceType = newType;
        devicePosition = newPosition;
        setDeviceRange(newType);
    }

    /*
     *       =====================  Device Class: Methods   ===============================
     */
    public boolean checkRangeDevice(Device device) {
        return false;
    }

    public boolean checkRangeSatellite(Satellite satellite) {
        // Check:
        // 1. Satellite Support
        // 2. Distance
        // 3. Visibility
        return !((satellite instanceof StandardSatellite && deviceType == "DesktopDevice") 
            ||  (MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(), devicePosition) > deviceRange)
            ||  (MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), devicePosition) == false));
    }
    public void teleportRangeReceive(File file) {
        // TeleportingSatellite is the Receiver
        deviceFileList.finishTeleportDevice(file);
    }
    public void teleportRangeSend(File file) {
        // TeleportingSatellite is the Sender 
        deviceFileList.finishTeleportDownload(file);
    }

    /*
     * ====================== File Methods ====================
     */
    public boolean checkFileExists(String fileName) {
        return deviceFileList.checkFileExists(fileName);
    }
    public void startDownload(File downloadFile, String senderID) {
        deviceFileList.startDownload(downloadFile, senderID);
    }
    public void startUpload(File uploadFile, String receiverID) {
        deviceFileList.startUpload(uploadFile, receiverID);
    }
    public void finishDownload(File downloadFile) {
        deviceFileList.finishDownload(downloadFile);
    }
    public void finishUpload(File uploadFile) {
        deviceFileList.finishUpload(uploadFile);
    }

    /*
     *   ======================   Device Class: Get Methods  ======================
     */
    public String getID() {
        return deviceID;
    }
    public String getType() {
        return deviceType;
    }
    public double getHeight() {
        return BlackoutController.RADIUS_JUPITER;
    }
    public Angle getPosition() {
        return devicePosition;
    }
    public List<File> getFileList() {
        return deviceFileList.getFileList();
    }
    public List<File> getTransferList() {
        return deviceFileList.getDownloadFileList();
    }
    public String getSpecificFileContent(String fileName) {
        return deviceFileList.getSpecificFileContent(fileName);
    }
    public File getFile(String fileName) {
        return deviceFileList.getFile(fileName);
    }
    public int getFileLength(String fileName) {
        return deviceFileList.getFileLength(fileName);
    }


    /*
     * ============================== Device Class: Set Methods ===================================  
     */
    public void setDeviceTransferList(List<File> adjustedFileList) {
        deviceFileList.setTransferList(adjustedFileList);
    }

    private void setDeviceRange (String deviceType) {
        switch (deviceType) {
            case "HandheldDevice":
                deviceRange = 50000;  break;
            case "LaptopDevice":
                deviceRange = 100000; break;
            case "DesktopDevice":
                deviceRange = 200000; break;
        }
    }

    public void addFile(String fileName, String content) {
        deviceFileList.addFileDevice(fileName, content);
    }
    public void deadFile(File file) {
        deviceFileList.deadFile(file);
    }

    /*
     *       =====================  Device Class: Interface Methods   ===============================
     *          These methods are unnecessary for devices, but the type of machine is unknown before application
     */
    public void checkStorage(int fileLength) {return;}
    public void enoughBandwidth(boolean isSender) throws FileTransferException {return;}
}
