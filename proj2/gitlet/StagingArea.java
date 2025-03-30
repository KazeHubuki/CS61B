package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import static gitlet.Utils.*;

public class StagingArea implements Serializable {

    /** A map stores the file names along with their blobIDs */
    private Map<String, String> stageForAddition;
    private List<String> stageForRemoval;

    public StagingArea() {
        if (Repository.GITLET_DIR.exists()) {
            File stage = join(Repository.GITLET_DIR, "stage");
            if (stage.exists()) {
                StagingArea stagingArea = readObject(stage, StagingArea.class);
                stageForAddition = stagingArea.stageForAddition;
                stageForRemoval = stagingArea.stageForRemoval;
                this.saveStagingArea();
            } else {
                stageForAddition = new HashMap<>();
                stageForRemoval = new ArrayList<>();
                this.saveStagingArea();
            }
        }
    }

    public void saveStagingArea() {
        File stage = join(Repository.GITLET_DIR, "stage");
        writeObject(stage, this);
    }

    public void addFile(File fileToBeAdded, String HEAD) {
        String fileName = fileToBeAdded.getName();
        if (stageForRemoval.contains(fileName)) {
            stageForRemoval.remove(fileName);
        }
        Commit currentCommit = Commit.findCommit(HEAD);
        Map<String, String> currentFileMap = currentCommit.getFileNameToBlobID();
        // Check if the file added is identical to any file in currentFileMap
        Blob newBlob = new Blob(readContents(fileToBeAdded));
        String newBlobID = newBlob.getBlobID();
        if (currentFileMap.containsValue(newBlobID)) {
            return;
        }
        stageForAddition.put(fileName, newBlobID);
        newBlob.saveBlob();
    }

    public void removeFile(File fileToBeRemoved, Map<String, String> currentFileMap) {
        String fileName = fileToBeRemoved.getName();
        if (stageForAddition.containsKey(fileName)) {
            stageForAddition.remove(fileName);
        }

        if (currentFileMap.containsKey(fileName)) {
            stageForRemoval.add(fileName);
            restrictedDelete(fileName);
        }
    }

    public void clear() {
        stageForAddition.clear();
        stageForRemoval.clear();
    }

    public Map<String, String> getStageForAddition() {
        return stageForAddition;
    }

    public List<String> getStageForRemoval() {
        return stageForRemoval;
    }
}
