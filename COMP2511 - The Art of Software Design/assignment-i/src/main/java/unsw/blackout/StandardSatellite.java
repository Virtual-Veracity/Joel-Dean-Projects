package unsw.blackout;

//===========================  Libraries ============================

import unsw.utils.Angle;

public class StandardSatellite extends Satellite{
    
    /*
     *        StandardSatellite Class: Attributes
     * 
     * Values assumed in kilometers (Speed, Range)
     * Value of TransferSpeed in Bytes (Character per second)
     */

    private static final int SPEED = 2500;
    private static final int RANGE = 150000;
    private static final int MAXFILES = 3;
    private static final int MAXBYTES = 80;
    private static final int SENDSPEED = 1;
    private static final int RECEIVESPEED = 1;


    public StandardSatellite(String newsatelliteID, String newType, Double newHeight, Angle newPosition) {
        super(newsatelliteID, newType, newHeight, newPosition);
    }

    public boolean moveSatellite() {
        // Calculate change in position
        Angle change = Angle.fromRadians(SPEED / getHeight());

        // Set new Position
        Angle newPosition = wrapAngle(getPosition().subtract(change));
        setSatellitePosition(newPosition);
        return true;
    }

    public boolean checkRangeDevice(Device device) {
        // Check:
        // 1. Satellite Support
        // 2. Distance
        // 3. Visibility
        if ((device.getType() == "DesktopDevice") 
            || (super.checkDistanceDevice(device) > RANGE)
            || (super.checkVisibilityDevice(device) == false)) {
            return false;
        }
        return true;
    }

    public boolean checkRangeSatellite(Satellite satellite) {
        // Check:
        // 1. Distance
        // 2. Visibility
        // 3. Self
        if ((checkDistanceSatellite(satellite) > RANGE)
        ||  (checkVisibilitySatellite(satellite) == false)
        ||  (super.checkRangeSatellite(satellite))) {
            return false;
        }
        return true;
    }

    // Determine if bandwidth is available
    // Senders/Receivers have different speeds
    public void enoughBandwidth(boolean isSender) throws FileTransferException {
        System.out.println(countUploadingFiles());
        if ((isSender && !(SENDSPEED > countUploadingFiles())) || 
            (!isSender && !(RECEIVESPEED > countFilesDownloading()))) {
            throw new FileTransferException.VirtualFileNoBandwidthException(getID());
        }
    }

    public void checkStorage(int fileLength) throws FileTransferException {

        int filesStored = countStoredFiles();
        int totalBytes = countTotalBytes();

        if (filesStored == MAXFILES) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");

        } else if ((totalBytes + fileLength) > MAXBYTES) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }

    public int getSatelliteSpeed(boolean isSender) {
        int uploadCount = countUploadingFiles();
        int downloadCount = countFilesDownloading();

        if (uploadCount == 0) {
            uploadCount = 1;
        }
        if (downloadCount == 0) {
            downloadCount = 1;
        }

        if (isSender) {return SENDSPEED / uploadCount;}
        return RECEIVESPEED / downloadCount;
    }
}
