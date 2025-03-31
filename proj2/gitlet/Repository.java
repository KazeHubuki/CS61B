package gitlet;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author George Yuan
 */
public class Repository implements Serializable {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    @Serial
    private static final long serialVersionUID = 1L;

    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commits' directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** The .gitlet/blobs directory. */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /** The .gitlet/branches directory. */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");

    /** HEAD points to the current commit. */
    private String HEAD;

    public Repository() {
        if (GITLET_DIR.exists()) {
            File repository = join(GITLET_DIR, "repo");
            if (repository.exists()) {
                Repository repo = readObject(repository, Repository.class);
                this.HEAD = repo.HEAD;
            }
        }
    }

    public void saveRepository() {
        File repository = join(GITLET_DIR, "repo");
        writeObject(repository, this);
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists"
                               + "in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        Commit initialCommit = new Commit();
        initialCommit.saveCommit();
        HEAD = initialCommit.getCommitID();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        this.saveRepository();
    }

    // Add a file to the stage.
    public void add(String fileName) {
        File fileToBeAdded = join(CWD, fileName);
        if (!fileToBeAdded.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        StagingArea stagingArea = new StagingArea();
        stagingArea.addFile(fileToBeAdded, HEAD);
        stagingArea.saveStagingArea();
    }

    public void commit(String message) {
        // Failure cases if there is no commit message or changes added to the commit.
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        StagingArea stagingArea = new StagingArea();
        Map<String, String> additionStage = stagingArea.getStageForAddition();
        List<String> removalStage = stagingArea.getStageForRemoval();
        if (additionStage.isEmpty() && removalStage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // Operate addition and removal and produce a new commit.
        Commit currentCommit = Commit.findCommit(HEAD);
        Map<String, String> newFileMap = new HashMap<>(currentCommit.getFileNameToBlobID());
        for (String fileName : additionStage.keySet()) {
            newFileMap.put(fileName, additionStage.get(fileName));
        }
        for (String fileName : removalStage) {
            newFileMap.remove(fileName);
        }
        Commit newCommit = new Commit(message, HEAD, newFileMap);
        newCommit.saveCommit();

        // Reset the repository and staging area.
        HEAD = newCommit.getCommitID();
        stagingArea.clear();
        stagingArea.saveStagingArea();
        saveRepository();
    }

    // Remove a file from the CWD. The change will be committed in the next commit.
    public void remove(String fileName) {
        File fileToBeRemoved = join(CWD, fileName);
        if (!fileToBeRemoved.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // If the file is neither in the addition stage nor current commit files,
        // then there is no reason to remove it.
        StagingArea stagingArea = new StagingArea();
        Map<String, String> additionStage = stagingArea.getStageForAddition();
        Commit currentCommit = Commit.findCommit(HEAD);
        Map<String, String> currentFileMap = currentCommit.getFileNameToBlobID();
        if (!additionStage.containsKey(fileName) && !currentFileMap.containsKey(fileName)) {
            System.out.println("No reason to remove this file.");
            System.exit(0);
        }
        stagingArea.removeFile(fileToBeRemoved, currentFileMap);
        stagingArea.saveStagingArea();
    }

    // Print out commits history from the current commit to the initial commit.
    public void log() {
        Commit currentCommit = Commit.findCommit(HEAD);
        while (currentCommit != null) {
            System.out.println(currentCommit);
            String parentCommitID = currentCommit.getParentCommitID();
            if (parentCommitID == null) {
                break;
            }
            currentCommit = Commit.findCommit(parentCommitID);
        }
    }

    // "Log" but ignores the order.
    public void globalLog() {
        // 'Commits' returned here are actually their commitID.
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String commit : commits) {
            System.out.println(Commit.findCommit(commit));
        }
    }

    // Restore the content of a particular file from the current commit.
    public void checkOutWithFileName(String fileName) {
        Commit currentCommit = Commit.findCommit(HEAD);
        Map<String, String> currentFileMap = currentCommit.getFileNameToBlobID();
        if (!currentFileMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        // Find the blob stored in the current commit,
        // read the content and write it to the file.
        File fileToBeCheckedOut = join(CWD, fileName);
        File fileBlob = join(BLOBS_DIR, currentFileMap.get(fileName));
        byte[] content = readObject(fileBlob, Blob.class).getFileContent();
        writeContents(fileToBeCheckedOut, content);
    }

    // Restore the content of a particular file from a particular commit.
    public void checkOutWithCommitIDAndFileName(String commitID, String fileName) {
        Commit targetCommit = Commit.findCommit(commitID);
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Map<String, String> targetFileMap = targetCommit.getFileNameToBlobID();
        if (!targetFileMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        File fileToBeCheckedOut = join(CWD, fileName);
        File fileBlob = join(BLOBS_DIR, targetFileMap.get(fileName));
        byte[] content = readObject(fileBlob, Blob.class).getFileContent();
        writeContents(fileToBeCheckedOut, content);
    }
}
