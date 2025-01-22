package unsw.blackout;

/*
 * Author: Joel Dean (z5208947)
 * Course: COMP2511 Assignment 1 
 * Date: Term 3 2022
 */


//===========================  Libraries ============================
import java.util.List;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;


// ===========================================

public abstract class Satellite implements Machine{
    
    // ======= Defintions ===========
    protected boolean ANTI_CLOCKWISE = false;
    protected boolean CLOCKWISE = true;


    /*
     *        Satellite Class: Attributes
     */
    private String satelliteID;
    private double satelliteHeight;
    private Angle satellitePosition;
    private String satelliteType;
    private FileList satelliteFileList = new FileList();

    /*
     *        Satellite Class: Constructors
     */
    public Satellite(String newsatelliteID, String newType, double newHeight, Angle newPosition) {
        satelliteID = newsatelliteID;
        satelliteHeight = newHeight;
        satellitePosition = newPosition;
        satelliteType = newType;
    }

    /*
     *   =================================  Satellite Class: Methods  ===================================
     */

    /* 
     * ===================== Abstract Methods ======================
     */
    public abstract boolean moveSatellite();

    public abstract boolean checkRangeDevice(Device device);

    public abstract void enoughBandwidth(boolean isSender) throws FileTransferException;

    public abstract void checkStorage(int fileLength) throws FileTransferException;

    public abstract int getSatelliteSpeed(boolean isSender);

    /* 
     * ================== File Methods ==================
     */

    /**
     * Adds a file to the satellite 'storage'
     * @param fileName
     * @param content
     */

    public boolean checkFileExists(String fileName) {
        return satelliteFileList.checkFileExists(fileName);
    }
    public void teleportRangeReceive(File file) {
        // TeleportingSatellite is the Receiver
        satelliteFileList.finishUpload(file);
    }
    public void teleportRangeSend(File file) {
        // TeleportingSatellite is the Sender
        satelliteFileList.finishTeleportDownload(file);
    }

    /*
     * ================ Helper Functions ==================
     * Functions used by subclasses
     */
    public double checkDistanceSatellite(Satellite satellite) {
        return MathsHelper.getDistance(satelliteHeight, satellitePosition, satellite.getHeight(), satellite.getPosition());
    }
    

    public double checkDistanceDevice(Device device) {
        return MathsHelper.getDistance(satelliteHeight, satellitePosition, device.getPosition());
    }

    public boolean checkVisibilitySatellite(Satellite satellite) {
        return MathsHelper.isVisible(satelliteHeight, satellitePosition, satellite.getHeight(), satellite.getPosition());
    }

    public boolean checkVisibilityDevice(Device device) {
        return MathsHelper.isVisible(satelliteHeight, satellitePosition, device.getPosition());
    }

    public boolean checkRangeSatellite(Satellite satellite) {
        return (getID().compareTo(satellite.getID()) == 0);
    }

    public int countTotalBytes() {
        return satelliteFileList.countTotalBytes();
    }

    public int countFilesDownloading() {
        return satelliteFileList.countFilesDownloading();
    }
    public int countUploadingFiles() {
        return satelliteFileList.countFilesUploading();
    }
    public Angle wrapAngle(Angle position) {
        if (position.compareTo(Angle.fromDegrees(360)) >= 0) {
            position = position.subtract(Angle.fromDegrees(360));
        } else if (position.compareTo(Angle.fromDegrees(0)) == -1) { 
            position = position.add(Angle.fromDegrees(360));
        }
        return position;
    }
    public int countStoredFiles() {
        return satelliteFileList.countStoredFiles();
    }

    /*
     *     =============================    Satellite Class: Get Methods   =============================
     */
    public String getID() {
        return satelliteID;
    }
    public double getHeight() {
        return satelliteHeight;
    }
    public Angle getPosition() {
        return satellitePosition;
    }
    public String getType() {
        return satelliteType;
    }
    public List<File> getFileList() {
        return satelliteFileList.getFileList();
    }
    public List<File> getTransferList() {
        return satelliteFileList.getDownloadFileList();
    }
    public String getSpecificFileContent(String fileName) {
        return satelliteFileList.getSpecificFileContent(fileName);
    }
    public int getFileLength(String fileName) {
        return satelliteFileList.getFileLength(fileName);
    }
    public File getFile(String fileName) {
        return satelliteFileList.getFile(fileName);
    }
    public List<File> getUploadList() {
        return satelliteFileList.getUploadFileList();
    }

    /*
     * =============================  Satellite Class: Set Methods   =============================
     */
    public void setSatellitePosition(Angle satellitePosition) {
        this.satellitePosition = satellitePosition;
    }
    public void setSatelliteTransferList(List<File> adjustedFileList) {
        satelliteFileList.setTransferList(adjustedFileList);
    }
    public void finishUpload(File file) {
        satelliteFileList.finishUpload(file);
    }
    public void finishDownload(File file) {
        satelliteFileList.finishDownload(file);
    }
    public void startDownload(File downloadFile, String senderID) {
        satelliteFileList.startDownload(downloadFile, senderID);
    }
    public void startUpload(File uploadFile, String receiverID) {
        satelliteFileList.startUpload(uploadFile, receiverID);
    }
    public void deadFile(File file) {
        satelliteFileList.deadFile(file);
    }
}
