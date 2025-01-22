package unsw.blackout;

//===========================  Libraries ============================
import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite{
    
    /*
     *        TeleportingSatellite Class: Attributes
     * 
     * Values assumed in kilometers (Speed, Range)
     * Value of TransferSpeed in Bytes (Character per second)
     */
    private static final int SPEED = 1000;
    private static final int RANGE = 200000;
    private static final double MAXFILES = Double.POSITIVE_INFINITY;
    private static final int MAXBYTES = 200;
    private static final int SENDSPEED = 10;
    private static final int RECEIVESPEED = 15;
    private boolean satelliteDirection = ANTI_CLOCKWISE;


    public TeleportingSatellite(String newsatelliteID, String newType, Double newHeight, Angle newPosition) {
        super(newsatelliteID, newType, newHeight, newPosition);
    }

    // Method: moveSatellite
    // 
    // Satellite moves akin to standard satellite
    // At 180 Deg, instantly 'teleports' to 0 and changes direction
    public boolean moveSatellite() {
        
        // ================= Initialise Variables ================
        // Calculate change in position
        Angle change = Angle.fromRadians(SPEED / getHeight());
        Angle currentPosition = getPosition();

        // =============== Teleport ====================
        if ((satelliteDirection == ANTI_CLOCKWISE && currentPosition.add(change).compareTo(Angle.fromDegrees(180)) == 1)
            || (satelliteDirection == CLOCKWISE && currentPosition.subtract(change).compareTo(Angle.fromDegrees(180)) == -1)) {
            return false;
        }

        // =================== Set new Position ===================
        Angle newPosition;
        if (satelliteDirection == CLOCKWISE) {
            newPosition = currentPosition.subtract(change);
        } else {
            newPosition = currentPosition.add(change);
        }
        setSatellitePosition(wrapAngle(newPosition));
        return true;
    }

    public void teleport() {
        setSatellitePosition(Angle.fromDegrees(0));
        satelliteDirection = !satelliteDirection;
    }

    public boolean checkRangeDevice(Device device) {
        // Check:
        // 1. Satellite Support
        // 2. Distance
        // 3. Visibility
        if ((super.checkDistanceDevice(device) > RANGE)
        ||  (super.checkVisibilityDevice(device) == false)) {
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

        if ((isSender && !(SENDSPEED > countStoredFiles())) || 
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
