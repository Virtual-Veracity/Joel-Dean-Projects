package unsw.blackout;

//===========================  Libraries ============================

import unsw.utils.Angle;

public class RelaySatellite extends Satellite{

    // RelaySatellite Class Attributes
    private final int SPEED = 1500;
    private final int RANGE = 300000;
    private final int MAXFILES = 0;
    private final int MAXBYTES = 0;
    private static final double SENDSPEED = Double.POSITIVE_INFINITY;
    private static final double RECEIVESPEED = Double.POSITIVE_INFINITY;
    private boolean satelliteDirection = CLOCKWISE;
    private boolean currentRelaying = false;


    public RelaySatellite(String newsatelliteID, String newType, Double newHeight, Angle newPosition) {
        super(newsatelliteID, newType, newHeight, newPosition);
    }

    // Method: moveSatellite
    // 
    // Satellite bounces in between 140 and 190 Degree region
    // If initialised outside -> Push into region on fastest route
    //
    public boolean moveSatellite() {
        // ================= Initialise Variables ================
        Angle change = Angle.fromRadians(SPEED / getHeight());
        Angle currentPosition = getPosition();

        // =============== Determine Position ====================
        if (checkOutsideBoundary()) {
            satelliteDirection = boundaryDirection();
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

    // Method: checkOutsideBoundary
    // 
    // Returns true if satellite's position is not within [140, 190] region.
    // Returns false otherwise
    private boolean checkOutsideBoundary() {
        Angle currentPosition = getPosition();
        return (currentPosition.compareTo(Angle.fromDegrees(140)) == -1 ||
                currentPosition.compareTo(Angle.fromDegrees(190)) == 1); 
    }

    // Method: boundaryDirection
    // 
    // Sets direction for out of region relay
    // Detects fastest route
    private boolean boundaryDirection() {

        // If between 141 - 345 (Inclusive)
        // Push anti-clockwise (Positive)
        // --------------------------------------
        // If between [190 - 345] (Not Inclusive)
        // Push clockwise (Negative)
        Angle currentPosition = getPosition();
        return !(currentPosition.compareTo(Angle.fromDegrees(345)) >= 0 ||
                 currentPosition.compareTo(Angle.fromDegrees(140)) == -1);
                
    }

    public boolean checkRangeDevice(Device device) {
        // Check:
        // 2. Distance
        // 3. Visibility
        return !((super.checkDistanceDevice(device) > RANGE) 
             || (super.checkVisibilityDevice(device) == false));
            
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
        int filesTransferring = super.countFilesDownloading();

        if ((isSender && !(SENDSPEED > filesTransferring)) || 
            (!isSender && !(RECEIVESPEED > filesTransferring))) {
            throw new FileTransferException.VirtualFileNoBandwidthException(super.getID());
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
        return -2;
    }

    public boolean getCurrentRelaying() {
        return currentRelaying;
    }
    public void setCurrentRelaying() {
        currentRelaying = !currentRelaying;
    }
}
