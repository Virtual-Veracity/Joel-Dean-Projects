package unsw.blackout;

import unsw.utils.Angle;

import java.util.List;

interface Machine {
    
    public boolean checkFileExists(String fileName);
    public List<File> getFileList();

    public String getID();
    public String getType();
    public double getHeight();
    public Angle getPosition();
    public int getFileLength(String fileName);
    public File getFile(String fileName);

    public boolean checkRangeDevice(Device device);
    public boolean checkRangeSatellite(Satellite satellite);
    public void checkStorage(int fileLength) throws FileTransferException;
    public void enoughBandwidth(boolean isSender) throws FileTransferException;

    public void startUpload(File uploadFile, String receiverID);
    public void finishUpload(File file);
    public void startDownload(File file, String senderID);
    public void finishDownload(File file);
    public void deadFile(File file);

    public void teleportRangeReceive(File file);
    public void teleportRangeSend(File file);

}
