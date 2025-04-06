package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

public class Blob implements Serializable {

    private byte[] fileContent;
    private String blobID;

    public Blob(byte[] fileContent) {
        this.fileContent = fileContent;
        blobID = sha1((Object) fileContent);
    }

    public void saveBlob() {
        if (!Repository.BLOBS_DIR.exists()) {
            Repository.BLOBS_DIR.mkdir();
        }
        File blobFile = join(Repository.BLOBS_DIR, blobID);
        writeObject(blobFile, this);
    }

    public static byte[] getBlobContent(String blobID) {
        File blobFile = join(Repository.BLOBS_DIR, blobID);
        return readObject(blobFile, Blob.class).getFileContent();
    }

    public static String getBlobID(String fileName) {
        File file = join(Repository.CWD, fileName);
        return sha1((Object) readContents(file));
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getBlobID() {
        return blobID;
    }
}
