package unsw.blackout;

public class File {
    
    /*
     *        File Class: Attributes     
     */
    String fileName;
    String fileContent;
    String fileCompleteContent;
    Boolean fileComplete = false;
    String senderID;

    /*
     *        File Class: Constructors     
     */
    public File(String name, String content) {
        fileName = name;
        fileContent = content;
        fileCompleteContent = content;
    }

    /*
     *        File Class: Methods   
     */
    public void fileTeleport() {
        String restFile = fileCompleteContent.substring(fileContent.length());
        restFile.replace("t", "");
        fileComplete = true;
    }


     /*
     *        File Class: Set Methods  
     */
    public void setFileComplete(Boolean complete) {
        fileComplete = complete;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    public void setFileCompleteContent(String fileCompleteContent) {
        this.fileCompleteContent = fileCompleteContent;
    }

     /*
     *        File Class: Get Methods  
     */
    public String getFileName() {
        return fileName;
    }
    public String getFileContent() {
        return fileContent;
    }
    public Boolean getFileComplete() {
        return fileComplete;
    }
    public int getFileLength() {
        return fileContent.length();
    }
    public String getSenderID() {
        return senderID;
    }
    public String getFileCompleteContent() {
        return fileCompleteContent;
    }
}
