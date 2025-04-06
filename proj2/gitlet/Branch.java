package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

import static gitlet.Utils.*;

public class Branch {

    public static void createBranch(String branchName, String currentCommitID) {
        File branchFile = join(Repository.HEADS_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeContents(branchFile, currentCommitID);
    }

    public static void updateBranch(String branchName, String newCommitID) {
        File branchFile = join(Repository.HEADS_DIR, branchName);
        writeContents(branchFile, newCommitID);
    }

    public static String getCurrentBranchName() {
        return readContentsAsString(Repository.HEAD);
    }

    public static String getCurrentCommitID() {
        return getBranchCurrentCommitID(getCurrentBranchName());
    }

    public static String getBranchCurrentCommitID(String branchName) {
        File branchFile = join(Repository.HEADS_DIR, branchName);
        return readContentsAsString(branchFile);
    }

    public static void checkOutBranchFiles(String targetBranchName) {
        String targetBranchCommitID = getBranchCurrentCommitID(targetBranchName);
        checkOutCommit(targetBranchCommitID);
    }

    public static void checkOutCommit(String targetCommitID) {
        Commit currentCommit = Commit.findCommit(getCurrentCommitID());
        Commit targetCommit = Commit.findCommit(targetCommitID);
        Map<String, String> currentFileMap = currentCommit.getFileNameToBlobID();
        Map<String, String> targetFileMap = targetCommit.getFileNameToBlobID();

        List<String> cwdFiles = plainFilenamesIn(Repository.CWD);
        for (String fileName : cwdFiles) {
            if (!currentFileMap.containsKey(fileName)
                    && targetFileMap.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String fileName : currentFileMap.keySet()) {
            if (!targetFileMap.keySet().contains(fileName)) {
                restrictedDelete(fileName);
            }
        }

        for (String fileName : targetFileMap.keySet()) {
            String blobID = targetFileMap.get(fileName);
            byte[] blobContent = Blob.getBlobContent(blobID);
            File targetFile = join(Repository.CWD, fileName);
            writeContents(targetFile, blobContent);
        }
    }

    public static String findSplitPoint(String branchName) {
        String currentCommitID = getBranchCurrentCommitID(getCurrentBranchName());
        String targetCommitID = getBranchCurrentCommitID(branchName);
        Commit currentCommit = Commit.findCommit(currentCommitID);
        Commit targetCommit = Commit.findCommit(targetCommitID);

        HashSet<String> currentCheckedCommitID = new HashSet<>();
        HashSet<String> targetCheckedCommitID = new HashSet<>();
        while (currentCommitID != null && targetCommitID != null) {
            currentCheckedCommitID.add(currentCommitID);
            targetCheckedCommitID.add(targetCommitID);
            if (currentCheckedCommitID.contains(targetCommitID)) {
                return targetCommitID;
            } else if (targetCheckedCommitID.contains(currentCommitID)) {
                return currentCommitID;
            } else {
                String currentParentCommitID = currentCommit.getParentCommitID();
                String targetParentCommitID = targetCommit.getParentCommitID();
                currentCommitID = (currentParentCommitID != null)
                        ? Commit.findCommit(currentParentCommitID).getCommitID()
                        : null;
                targetCommitID = (targetParentCommitID != null)
                        ? Commit.findCommit(targetParentCommitID).getCommitID()
                        : null;
            }
        }
        return null;
    }

    public static boolean mergeBranch(String branchName)  {
        boolean hasConflict = false;
        String splitPointID = Branch.findSplitPoint(branchName);
        String branchCommitID = Branch.getBranchCurrentCommitID(branchName);
        String currentCommitID = Branch.getCurrentCommitID();
        if (Objects.equals(splitPointID, branchCommitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return hasConflict;
        }
        if (Objects.equals(splitPointID, currentCommitID)) {
            Branch.updateBranch(getCurrentBranchName(),
                    Branch.getBranchCurrentCommitID(branchName));
            System.out.println("Current branch fast-forwarded.");
            return hasConflict;
        }

        Commit splitCommit = Commit.findCommit(splitPointID);
        Commit currentCommit = Commit.findCommit(currentCommitID);
        Commit branchCommit = Commit.findCommit(branchCommitID);
        Map<String, String> splitFiles = splitCommit.getFileNameToBlobID();
        Map<String, String> currentFiles = currentCommit.getFileNameToBlobID();
        Map<String, String> branchFiles = branchCommit.getFileNameToBlobID();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitFiles.keySet());
        allFiles.addAll(currentFiles.keySet());
        allFiles.addAll(branchFiles.keySet());

        for (String fileName : allFiles) {
            String splitBlobID = splitFiles.getOrDefault(fileName, null);
            String currentBlobID = currentFiles.getOrDefault(fileName, null);
            String branchBlobID = branchFiles.getOrDefault(fileName, null);

            // If both are operated by the same way, do nothing.
            if (Objects.equals(currentBlobID, branchBlobID)) {
                continue;
            }

            // Case that file does not exist in the split point, i.e. new file added.
            if (splitBlobID == null) {
                // Note that if any two of these 3 blob IDs are null,
                // then the third one must be not null.
                if (branchBlobID == null) { // Determine that `currentBlobID != null`
                    // Added only in current -> do nothing
                    continue;
                } else if (currentBlobID == null) { // Determine that `branchBlobID != null`.
                    // Added only in target -> checkout the file and stage it.
                    Repository.checkOutWithCommitIDAndFileName(branchCommitID, fileName);
                    Repository.add(fileName);
                } else {
                    // Added in both -> mark conflict.
                    hasConflict = true;
                    System.out.println("Encountered a merge conflict.");
                    handleConflict(fileName, currentBlobID, branchBlobID);
                }
            // Case that file exists in the split point
            } else {
                boolean modifiedInCurrent = !Objects.equals(splitBlobID, currentBlobID);
                boolean modifiedInTarget = !Objects.equals(splitBlobID, branchBlobID);

                // Handle deletes.
                if (currentBlobID == null && !modifiedInTarget) {
                    // Deleted in current, unchanged in target -> do nothing.
                    continue;
                } else if (branchBlobID == null && !modifiedInCurrent) {
                    // Deleted in target, unchanged in current -> remove the file.
                    Repository.remove(fileName);
                } else if (currentBlobID == null || branchBlobID == null) {
                    // Deleted in one, changed in the other -> mark conflict.
                    hasConflict = true;
                    System.out.println("Encountered a merge conflict.");
                    handleConflict(fileName, currentBlobID, branchBlobID);

                // Handle modifications.
                } else if (modifiedInCurrent && !modifiedInTarget) {
                    // Changed only in current -> do nothing.
                    continue;
                } else if (!modifiedInCurrent && modifiedInTarget) {
                    // Changed only in target -> checkout the file and stage it.
                        Repository.checkOutWithCommitIDAndFileName(branchCommitID, fileName);
                        Repository.add(fileName);
                } else {
                    // Changed in both -> mark conflict.
                    hasConflict = true;
                    System.out.println("Encountered a merge conflict.");
                    handleConflict(fileName, currentBlobID, branchBlobID);
                }
            }
        }
        return hasConflict;
    }

    private static void handleConflict(String fileName, String currentBlobID, String branchBlobID) {
        byte[] currentContent = (currentBlobID != null) ? Blob.getBlobContent(currentBlobID) : "".getBytes();
        byte[] branchContent = (branchBlobID != null) ? Blob.getBlobContent(branchBlobID) : "".getBytes();
        String conflictContent = "<<<<<<< HEAD\n"
                + currentContent
                + "=======\n"
                + branchContent
                + ">>>>>>>\n";
        File conflictFile = join(Repository.CWD, fileName);
        writeContents(conflictFile, conflictContent);
        Repository.add(fileName);
    }
}
