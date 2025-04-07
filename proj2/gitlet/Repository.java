package gitlet;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

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
    /** The .gitlet/refs directory. (stores branches) */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The .gitlet/refs/heads directory. (stores local branches) */
    public static final File HEADS_DIR = join(REFS_DIR, "heads");

    /** HEAD file stores the name of the current branch. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists"
                               + "in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();

        COMMITS_DIR.mkdir();
        Commit initialCommit = new Commit();
        initialCommit.saveCommit();
        String initialCommitID = initialCommit.getCommitID();

        BLOBS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();

        String initialBranchName = "master";
        Branch.createBranch(initialBranchName, initialCommitID);
        writeContents(HEAD, initialBranchName);
    }

    // Add a file to the stage.
    public static void add(String fileName) {
        File fileToBeAdded = join(CWD, fileName);
        if (!fileToBeAdded.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        StagingArea stagingArea = StagingArea.getStagingArea();
        String currentCommitID = Branch.getCurrentCommitID();
        stagingArea.addFile(fileToBeAdded, currentCommitID);
        stagingArea.saveStagingArea();
    }

    public static void commit(String message) {
        commitWithMerge(message, null);
    }

    private static void commitWithMerge(String message, String secondParentID) {
        // Failure cases if there is no commit message or changes added to the commit.
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        StagingArea stagingArea = StagingArea.getStagingArea();
        Map<String, String> stageForAddition = stagingArea.getStageForAddition();
        List<String> stageForRemoval = stagingArea.getStageForRemoval();
        if (stageForAddition.isEmpty() && stageForRemoval.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // Operate addition and removal and produce a new commit.
        String currentCommitID = Branch.getCurrentCommitID();
        Commit currentCommit = Commit.findCommit(currentCommitID);
        Map<String, String> newFileMap = new HashMap<>(currentCommit.getFileNameToBlobID());
        for (String fileName : stageForAddition.keySet()) {
            newFileMap.put(fileName, stageForAddition.get(fileName));
        }
        for (String fileName : stageForRemoval) {
            newFileMap.remove(fileName);
        }
        Commit newCommit = Commit.createMergeCommit(message,
                currentCommitID, secondParentID, newFileMap);
        newCommit.saveCommit();

        // Update the current branch and staging area.
        String newCommitID = newCommit.getCommitID();
        Branch.updateBranch(Branch.getCurrentBranchName(), newCommitID);
        stagingArea.clear();
        stagingArea.saveStagingArea();
    }

    // Remove a file from the CWD. The change will be committed in the next commit.
    public static void remove(String fileName) {
        // If the file is neither in the addition stage nor current commit files,
        // then there is no reason to remove it.
        StagingArea stagingArea = StagingArea.getStagingArea();
        Map<String, String> stageForAddition = stagingArea.getStageForAddition();
        String currentCommitID = Branch.getCurrentCommitID();
        Commit currentCommit = Commit.findCommit(currentCommitID);
        Map<String, String> currentFileMap = currentCommit.getFileNameToBlobID();
        if (!stageForAddition.containsKey(fileName) && !currentFileMap.containsKey(fileName)) {
            System.out.println("No reason to remove this file.");
            System.exit(0);
        }

        File fileToBeRemoved = join(CWD, fileName);
        stagingArea.removeFile(fileToBeRemoved, currentFileMap);
        stagingArea.saveStagingArea();
    }

    // Print out commits history from the current commit to the initial commit.
    public static void log() {
        String currentCommitID = Branch.getCurrentCommitID();
        Commit currentCommit = Commit.findCommit(currentCommitID);
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
    public static void globalLog() {
        // 'Commits' returned here are actually their commitID.
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String commit : commits) {
            System.out.println(Commit.findCommit(commit));
        }
    }

    // Find commits with a given message.
    public static void find(String message) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        List<String> matchingCommits = new ArrayList<>();
        for (String commit : commits) {
            if (Commit.findCommit(commit).getMessage().equals(message)) {
                matchingCommits.add(commit);
            }
        }

        if (matchingCommits.isEmpty()) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        } else {
            for (String matchingCommit : matchingCommits) {
                System.out.println(matchingCommit);
            }
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        String currentBranchName = Branch.getCurrentBranchName();
        List<String> branchNames = plainFilenamesIn(HEADS_DIR);
        for (String branchName : branchNames) {
            if (branchName.equals(currentBranchName)) {
                System.out.println("*" + branchName);
                continue;
            }
            System.out.println(branchName);
        }
        System.out.println();

        StagingArea stagingArea = StagingArea.getStagingArea();
        Map<String, String> stageForAddition = stagingArea.getStageForAddition();
        List<String> stageForRemoval = stagingArea.getStageForRemoval();
        System.out.println("=== Staged Files ===");
        for (String fileName : stageForAddition.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName : stageForRemoval) {
            System.out.println(fileName);
        }
        System.out.println();

        String currentCommitID = Branch.getCurrentCommitID();
        Commit currentCommit = Commit.findCommit(currentCommitID);
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String fileName : currentCommit.getModifiedNotStagedFiles()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String fileName : currentCommit.getUntrackedFiles()) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    // Restore the content of a particular file from the current commit.
    public static void checkOutWithFileName(String fileName) {
        checkOutWithCommitIDAndFileName(Branch.getCurrentCommitID(), fileName);
    }

    // Restore the content of a particular file from a particular commit.
    public static void checkOutWithCommitIDAndFileName(String commitID, String fileName) {
        Commit targetCommit;
        if (commitID.length() < Commit.STANDARD_COMMIT_ID_LENGTH) {
            targetCommit = Commit.findCommitWithShortID(commitID);
        } else {
            targetCommit = Commit.findCommit(commitID);
        }
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Map<String, String> targetFileMap = targetCommit.getFileNameToBlobID();
        if (!targetFileMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        String blobID = targetFileMap.get(fileName);
        byte[] blobContent = Blob.getBlobContent(blobID);
        File fileToBeCheckedOut = join(CWD, fileName);
        writeContents(fileToBeCheckedOut, blobContent);
    }

    // Turn to the given branch.
    public static void checkOutWithBranchName(String branchName) {
        File branchToBeCheckedOutFile = join(HEADS_DIR, branchName);
        if (!branchToBeCheckedOutFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        if (branchName.equals(Branch.getCurrentBranchName())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        Branch.checkOutBranchFiles(branchName);
        writeContents(HEAD, branchName);
    }

    public static void branch(String branchName) {
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        if (branches.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }

        Branch.createBranch(branchName, Branch.getCurrentCommitID());
    }

    public static void removeBranch(String branchName) {
        File branchFile = join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(Branch.getCurrentBranchName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        branchFile.delete();
    }

    public static void reset(String commitID) {
        Commit targetCommit;
        if (commitID.length() < Commit.STANDARD_COMMIT_ID_LENGTH) {
            targetCommit = Commit.findCommitWithShortID(commitID);
        } else {
            targetCommit = Commit.findCommit(commitID);
        }
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Branch.checkOutCommit(commitID);
        Branch.updateBranch(Branch.getCurrentBranchName(), commitID);
    }

    public static void merge(String branchName) {
        File branchFile = join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranchName = Branch.getCurrentBranchName();
        if (branchName.equals(currentBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        StagingArea stagingArea = StagingArea.getStagingArea();
        Map<String, String> stageForAddition = stagingArea.getStageForAddition();
        List<String> stageForRemoval = stagingArea.getStageForRemoval();
        if (!stageForAddition.isEmpty()
                || !stageForRemoval.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        Commit currentCommit = Commit.findCommit(Branch.getCurrentCommitID());
        Commit branchCommit = Commit.findCommit(Branch.getBranchCurrentCommitID(branchName));
        Map<String, String> branchFileMap = branchCommit.getFileNameToBlobID();
        List<String> currentUntrackedFiles = new ArrayList<>();
        for (String file : currentCommit.getUntrackedFiles()) {
            String fileBlobID = Blob.getBlobID(file);
            if (!branchFileMap.containsKey(file)
                    || !fileBlobID.equals(branchFileMap.get(file))) {
                currentUntrackedFiles.add(file);
            }
        }
        if (!currentUntrackedFiles.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }

        String splitPointID = Branch.findSplitPoint(branchName);
        String branchCommitID = Branch.getBranchCurrentCommitID(branchName);
        String currentCommitID = Branch.getCurrentCommitID();
        if (Objects.equals(splitPointID, branchCommitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (Objects.equals(splitPointID, currentCommitID)) {
            Repository.checkOutWithBranchName(branchName);
            Branch.updateBranch(currentBranchName,
                    Branch.getCurrentCommitID());
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        boolean hasMergeConflict = Branch.mergeBranch(branchName, splitPointID);
        String message;
        if (hasMergeConflict) {
            message = "Encountered a merge conflict.";
        } else {
            message = String.format("Merged %s into %s.", branchName, currentBranchName);
        }

        commitWithMerge(message, branchCommitID);
    }
}
