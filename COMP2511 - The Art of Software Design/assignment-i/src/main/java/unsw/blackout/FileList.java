package unsw.blackout;

import java.util.List;
import java.util.ArrayList;

public class FileList {
    
    /*
     *   ==========================    FileList Class: Atrributes   ==========================
     */
    private List<File> completeFileList = new ArrayList<File>();
    private List<File> downloadFileList = new ArrayList<File>();
    private List<File> uploadFileList = new ArrayList<File>();


    /*
     *   ==========================   FileList Class: Constructor   ==========================
     */
    public FileList () {}

    /*
     *   ==========================   FileList Class: Methods   ==========================
     */
    public String getSpecificFileContent(String fileName) {
        for (File completeFile : completeFileList) {
            if (fileName.compareTo(completeFile.getFileName()) == 0) {
                return completeFile.getFileCompleteContent();
            }
        }
        for (File downloadFile : downloadFileList) {
            if (fileName.compareTo(downloadFile.getFileName()) == 0) {
                return downloadFile.getFileCompleteContent();
            }
        }
        return "";
    }
    public boolean checkFileExists(String fileName) {
        // Check Completed File List
        for (File file : completeFileList) {
            if (fileName.compareTo(file.getFileName()) == 0) {
                return true;
            }
        }
        // File not Found
        return false;
    }
    public int countFilesDownloading() {
        return downloadFileList.size();
    }
    public int countFilesUploading() {
        return uploadFileList.size();
    }
    public int countTotalBytes() {
        int totalBytes = 0;
        for (File completeFile : completeFileList) {
            totalBytes += completeFile.getFileLength();
        }
        for (File downloadFile : downloadFileList) {
            totalBytes += downloadFile.getFileLength();
        }
        return totalBytes;
    }
    public void finishTeleportDownload(File file) {
        for (File downloadFile : downloadFileList) {
            if (downloadFile.getFileName().compareTo(file.getFileName()) == 0) {
                String newContent = downloadFile.getFileCompleteContent().substring(downloadFile.getFileContent().length());
                newContent = newContent.replaceAll("t", "");
                downloadFile.setFileContent(downloadFile.getFileContent().concat(newContent));
                downloadFile.setFileComplete(true);
                completeFileList.add(downloadFile);
                downloadFileList.remove(downloadFile);
                break;
            }
        }
    }

    public void finishTeleportDevice(File file) {
        uploadFileList.remove(file);
        for (File completeFile : completeFileList) {
            if (completeFile.getFileName().compareTo(file.getFileName()) == 0) {
                String newContent = completeFile.getFileContent().replaceAll("t", "");
                completeFile.setFileContent(newContent);
                completeFile.setFileCompleteContent(newContent);
                break;
            }
        }
    }
    
    public void deadFile(File file){
        uploadFileList.remove(file);
    }

    /*
     * ================= FileList Class: Get Methods ========================
     */
    public List<File> getCompleteFileList() {
        return completeFileList;
    }
    public List<File> getDownloadFileList() {
        return downloadFileList;
    }
    public List<File> getUploadFileList() {
        return uploadFileList;
    }
    public File getFile(String fileName) {
        for (File file : completeFileList) {
            if (fileName.compareTo(file.getFileName()) == 0) {
                return file;
            }
        }
        return null;
    }
    public List<File> getFileList() {
        List<File> fileList = completeFileList;
        for (File file : downloadFileList) {
            fileList.add(file);
        }
        return fileList;
    }
    public int countStoredFiles() {
        return completeFileList.size() + downloadFileList.size();
    }
    public int getFileLength(String fileName) {
        for (File file : completeFileList) {
            if (file.getFileName().compareTo(fileName) == 0) {
                return file.getFileLength();
            }
        }
        return 0;
    }

    /*
     *   ==========================    FileList Class: Set Methods   ==========================
     */
    public void startDownload(File downloadFile, String senderID) {
        downloadFile.setSenderID(senderID);
        downloadFile.setFileContent("");
        downloadFile.setFileComplete(false);
        downloadFileList.add(downloadFile);
    }
    public void finishDownload(File downloadFile) {
        completeFileList.add(downloadFile);
        downloadFileList.remove(downloadFile);
    }
    public void startUpload(File uploadFile, String receiverID) {
        uploadFile.setSenderID(receiverID);
        uploadFileList.add(uploadFile);
    }
    public void finishUpload(File uploadFile) {
        uploadFileList.remove(uploadFile);
    }
    public void addFileDevice(String fileName, String content) {
        File newFile = new File(fileName, content);
        newFile.setFileComplete(true);
        completeFileList.add(newFile);
    }
    public void setTransferList(List<File> transferList) {
        downloadFileList = transferList;
    }
}
