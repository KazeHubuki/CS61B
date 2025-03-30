package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author George Yuan
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The commit time. */
    private Date timeStamp;
    /** The parent commit's ID. */
    private String parentCommitID;
    /** The message of this commit. */
    private String message;
    /** The commit files and their blob IDs. */
    private Map<String, String> fileNameToBlobID;
    /** The commit ID. */
    private String commitID;

    public Commit() {
        timeStamp = new Date(0);
        parentCommitID = null;
        message = "initial commit";
        fileNameToBlobID = new HashMap<>();
        commitID = sha1((Object) serialize(this));
    }

    public Commit(String message, String parentCommitID, Map<String, String> fileNameToBlobID) {
        timeStamp = new Date();
        this.parentCommitID = parentCommitID;
        this.message = message;
        this.fileNameToBlobID = fileNameToBlobID;
        commitID = sha1((Object) serialize(this));
    }

    public void saveCommit() {
        if (!Repository.COMMITS_DIR.exists()) {
            Repository.COMMITS_DIR.mkdir();
        }
        File commit = join(Repository.COMMITS_DIR, commitID);
        writeObject(commit, this);
    }

    public static Commit findCommit(String commitID) {
        File targetCommit = new File(Repository.COMMITS_DIR, commitID);
        if (!targetCommit.exists()) {
            return null;
        }
        return readObject(targetCommit, Commit.class);
    }

    public String getParentCommitID() {
        return parentCommitID;
    }

    public Map<String, String> getFileNameToBlobID() {
        return  fileNameToBlobID;
    }

    public String getCommitID() {
        return commitID;
    }

    @Override
    public String toString() {
        String s = "===" + "\n";
        s += "commit " + commitID + "\n";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        s += "Date: " + sdf.format(timeStamp) + "\n";
        s += message + "\n";
        return s;
    }
}
