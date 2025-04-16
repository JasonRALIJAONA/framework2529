package mg.itu.prom16.util;

public class APIAttachment {
    String filename;
    byte[] fileData;
    String fileType;
    Object fileObject;

    public String getFilename() {
        return this.filename + "." + this.fileType;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFileData() {
        return this.fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Object getFileObject() {
        return this.fileObject;
    }

    public void setFileObject(Object fileObject) {
        this.fileObject = fileObject;
    }

    public String getContentType() {
        String contentType = null;
        if (fileType != null) {
            switch (getFileType().toLowerCase()) {
                case "jpg":
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                case "gif":
                    contentType = "image/gif";
                    break;
                case "pdf":
                    contentType = "application/pdf";
                    break;
                default:
                    contentType = "application/octet-stream"; // Default for unknown types
            }
        }

        return contentType;
    }
}
