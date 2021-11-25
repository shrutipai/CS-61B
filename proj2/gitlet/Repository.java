package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Shruti Pai
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The commits directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, ".commits");

    /** The staging area. */
    public static final File STAGING_AREA = join(GITLET_DIR, ".stagingArea");

    /** Removal stage */
    public static final File RM_STAGE = join(GITLET_DIR, ".rmStage");

    /** The Blobs directory */
    public static final File BLOBS_DIR = join(GITLET_DIR, ".blobs");

    /** The Branch directory */
    public static final File BRANCH_DIR = join(GITLET_DIR, ".branches");

    /** HEAD pointer */
    public static final File HEAD = join(BRANCH_DIR, "head");

    /** Master pointer */
    public static final File MASTER = join(BRANCH_DIR, "master");

    public static void initCommand() throws IOException {

        // Failure case.
        if (Utils.join(CWD, ".gitlet").exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        STAGING_AREA.mkdir();
        BLOBS_DIR.mkdir();
        RM_STAGE.mkdir();
        BRANCH_DIR.mkdir();
        HEAD.createNewFile();
        MASTER.createNewFile();

        Utils.writeContents(HEAD, "master");
        Commit initialCommit = new Commit("initial commit", null, false);

    }

    public static void addCommand(String fileName) throws IOException {

        // file I am adding from CWD
        File toAdd = Utils.join(CWD, fileName);

        // Failure case.
        if (!toAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }


        // creates a Blob object for file
        Blob newBlob = new Blob(toAdd, fileName);

        // File i am adding, but in staging area already (may or may not exist)
        File toAddInStage = Utils.join(STAGING_AREA, newBlob.getName());

        // Blob for file i am adding, but in removal stage (may or may not exist)
        File toAddInRm = Utils.join(RM_STAGE, newBlob.getName());

        // If Blob exists in staging area, return without adding
        if (toAddInStage.exists()) {
            return;
        }

        // If Blob exists in removal stage, take out of removal stage
        if (toAddInRm.exists()) {
            toAddInRm.delete();
            return;
        }


        // If file to add is a blob in most recent commit, do not stage and remove
        // from STAGING_AREA if file with same name exists
        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);

        if (headCommit.getBlobsSha1().contains(newBlob.getName())) {
            for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(
                    Repository.STAGING_AREA))) {
                File staged = Utils.join(Repository.STAGING_AREA, i);
                Blob thisBlob = readObject(staged, Blob.class);
                if (thisBlob.getFileName().equals(fileName)) {
                    staged.delete();
                }
            }
        } else {
            // Serializes blob to STAGING_AREA
            File blobsFile = Utils.join(STAGING_AREA, newBlob.getName());
            blobsFile.createNewFile();
            Utils.writeObject(blobsFile, newBlob);
        }




    }

    public static void commitCommand(String message, String parent, Boolean merged) {
        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        Commit newCommit = new Commit(message, parent, merged);
    }

    public static void rmCommand(String fileName) {
        // removes file from staging area if it exists
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.STAGING_AREA))) {
            File staged = Utils.join(Repository.STAGING_AREA, i);
            Blob thisBlob = readObject(staged, Blob.class);
            if (thisBlob.getFileName().equals(fileName)) {
                staged.delete();
                return;
            }

        }

        // checks if there is a blob in the head commit with filename
        // if so, stages file for removal and deletes from CWD
        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        for (String i : headCommit.getBlobsSha1()) {
            File thisBlobFile = Utils.join(Repository.BLOBS_DIR, i);
            Blob thisBlob = readObject(thisBlobFile, Blob.class);
            if (thisBlob.getFileName().equals(fileName)) {
                File rmFile = Utils.join(Repository.RM_STAGE, i);
                Utils.writeObject(rmFile, thisBlob);

                File rmFileCWD = Utils.join(CWD, fileName);
                restrictedDelete(rmFileCWD);
                return;
            }
        }
        // failure case
        System.out.println("No reason to remove the file.");
        System.exit(0);
    }

    public static void logCommand() {
        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit curr = readObject(headCommitFile, Commit.class);
        while (curr.getParent() != null) {
            System.out.println("===");
            System.out.println("commit " + curr.getSha1Code());
            if (curr.getMerged()) {
                System.out.println("Merge: " + curr.getParent());
            }
            SimpleDateFormat ourFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            System.out.println("Date: " + ourFormat.format(curr.getTimestamp()));
            System.out.println(curr.getMessage());
            System.out.print("\n");

            File nextFile = Utils.join(Repository.COMMITS_DIR, curr.getParent());
            curr = Utils.readObject(nextFile, Commit.class);
        }
        System.out.println("===");
        System.out.println("commit " + curr.getSha1Code());
        SimpleDateFormat ourFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        System.out.println("Date: " + ourFormat.format(curr.getTimestamp()));
        System.out.println(curr.getMessage());
        System.out.print("\n");
    }

    public static void globalLogCommand() {
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.COMMITS_DIR))) {
            File commitFile = Utils.join(Repository.COMMITS_DIR, i);
            Commit curr = readObject(commitFile, Commit.class);

            System.out.println("===");
            System.out.println("commit " + curr.getSha1Code());
            if (curr.getMerged()) {
                System.out.println("Merge: " + curr.getParent());
            }
            SimpleDateFormat ourFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            System.out.println("Date: " + ourFormat.format(curr.getTimestamp()));
            System.out.println(curr.getMessage());
            System.out.print("\n");
        }
    }

    public static void statusCommand() {
        System.out.println("=== Branches ===");
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.BRANCH_DIR))) {
            if (i.equals(readContentsAsString(HEAD))) {
                System.out.println("*" + i);
            } else {
                if (!i.equals("head")) {
                    System.out.println(i);
                }
            }
        }
        System.out.print("\n");


        System.out.println("=== Staged Files ===");
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.STAGING_AREA))) {
            File stagedFile = Utils.join(Repository.STAGING_AREA, i);
            if (stagedFile.exists()) {
                Blob curr = readObject(stagedFile, Blob.class);
                System.out.println(curr.getFileName());
            }
        }
        System.out.print("\n");

        System.out.println("=== Removed Files ===");
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.RM_STAGE))) {
            File rmFile = Utils.join(Repository.RM_STAGE, i);
            if (rmFile.exists()) {
                Blob curr = readObject(rmFile, Blob.class);
                System.out.println(curr.getFileName());
            }
        }
        System.out.print("\n");

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.print("\n");
        System.out.println("=== Untracked Files ===");
        System.out.print("\n");
    }

    public static void findCommand(String message) {
        boolean found = false;
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.COMMITS_DIR))) {
            File commitFile = Utils.join(COMMITS_DIR, i);
            Commit curr = Utils.readObject(commitFile, Commit.class);
            if (curr.getMessage().equals(message)) {
                System.out.println(curr.getSha1Code());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** helper method: checks if there are untracked files in CWD */
    public static void fileCheck() {
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.CWD))) {
            boolean tracked = false;
            File headCommitFile = Utils.join(COMMITS_DIR,
                    readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
            Commit headCommit = readObject(headCommitFile, Commit.class);
            for (String j : headCommit.getBlobsSha1()) {
                File blobFile = Utils.join(BLOBS_DIR, j);
                Blob currBlob = readObject(blobFile, Blob.class);
                if (currBlob.getFileName().equals(i)) {
                    tracked = true;
                }
            }
            for (String k : Objects.requireNonNull(Utils.plainFilenamesIn(
                    Repository.STAGING_AREA))) {
                File stageFile = Utils.join(STAGING_AREA, k);
                Blob stageBlob = Utils.readObject(stageFile, Blob.class);
                if (stageBlob.getFileName().equals(i)) {
                    tracked = true;
                }
            }
            if (!tracked) {
                System.out.println("There is an untracked file in the way; delete it,"
                        + " or add and commit it first.");
                System.exit(0);
            }
        }
    }

    public static void checkoutBranchCommand(String branchName) throws IOException {
        // branch
        if (readContentsAsString(HEAD).equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        fileCheck();

        if (Utils.plainFilenamesIn(Repository.BRANCH_DIR).contains(branchName)) {
            File branchFile = Utils.join(BRANCH_DIR, branchName);
            File commitFile = Utils.join(COMMITS_DIR, readContentsAsString(branchFile));
            Commit curr = Utils.readObject(commitFile, Commit.class);

            // MIGHT BE WRONG, clears cwd of all files, assumes they're all tracked
            for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.CWD))) {
                File cwdFile = Utils.join(CWD, i);
                cwdFile.delete();
            }
            for (String i : curr.getBlobsSha1()) {
                File blobFile = Utils.join(BLOBS_DIR, i);
                Blob currBlob = Utils.readObject(blobFile, Blob.class);
                File checkout = Utils.join(CWD, currBlob.getFileName());
                if (checkout.exists()) {
                    checkout.delete();
                }
                checkout.createNewFile();
                Utils.writeContents(checkout, currBlob.getContents());
            }
        } else {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        Utils.writeContents(HEAD, branchName);

    }

    public static void checkoutFileCommand(String fileName) throws IOException {
        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit headCommit = readObject(headCommitFile, Commit.class);
        for (String i : headCommit.getBlobsSha1()) {
            File blobFile = Utils.join(BLOBS_DIR, i);
            Blob currBlob = Utils.readObject(blobFile, Blob.class);
            if (currBlob.getFileName().equals(fileName)) {
                File checkout = Utils.join(CWD, fileName);
                if (checkout.exists()) {
                    checkout.delete();
                }
                checkout.createNewFile();
                Utils.writeContents(checkout, currBlob.getContents());
                return;
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    public static void checkoutCommand(String id, String fileName) throws IOException {
        File commitFile;
        commitFile = Utils.join(COMMITS_DIR, id);
        if (id.length() < 40) {
            for (String i : Objects.requireNonNull(
                    Utils.plainFilenamesIn(Repository.COMMITS_DIR))) {
                if (i.contains(id)) {
                    commitFile = Utils.join(COMMITS_DIR, i);
                    break;
                }
            }
        }

        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit curr = readObject(commitFile, Commit.class);

        for (String i : curr.getBlobsSha1()) {
            File blobFile = Utils.join(BLOBS_DIR, i);
            Blob currBlob = Utils.readObject(blobFile, Blob.class);
            if (currBlob.getFileName().equals(fileName)) {
                File checkout = Utils.join(CWD, fileName);
                if (checkout.exists()) {
                    checkout.delete();
                }
                checkout.createNewFile();
                Utils.writeContents(checkout, currBlob.getContents());
                return;
            }
        }
        System.out.println("File does not exist in that commit.");
    }

    public static void branchCommand(String branchName) throws IOException {
        File newBranch = Utils.join(BRANCH_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        newBranch.createNewFile();

        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit headCommit = readObject(headCommitFile, Commit.class);

        Utils.writeContents(newBranch, headCommit.getSha1Code());
    }

    public static void rmBranchCommand(String branchName) {
        if (readContentsAsString(HEAD).equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.BRANCH_DIR))) {
            if (i.equals(branchName)) {
                File branchFile = Utils.join(BRANCH_DIR, i);
                branchFile.delete();
                return;
            }
        }
        System.out.println("A branch with that name does not exist.");
    }

    public static void resetCommand(String id) throws IOException {
        File commitFile = Utils.join(COMMITS_DIR, id);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        fileCheck();
        // removes leftover files in cwd
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.CWD))) {
            File cwdFile = Utils.join(CWD, i);
            cwdFile.delete();
        }
        // checkout for every file in commit
        Commit currCommit = readObject(commitFile, Commit.class);
        for (String i : currCommit.getBlobsSha1()) {
            File blobFile = Utils.join(BLOBS_DIR, i);
            Blob currBlob = readObject(blobFile, Blob.class);
            checkoutCommand(id, currBlob.getFileName());
        }

        // changes head pointer
        File activeBranch = Utils.join(BRANCH_DIR,
                Utils.readContentsAsString(Repository.HEAD));
        writeContents(activeBranch, id);

        // clears staging area
        for (String i : Objects.requireNonNull(
                Utils.plainFilenamesIn(Repository.STAGING_AREA))) {
            File staged = Utils.join(STAGING_AREA, i);
            staged.delete();
        }
    }

    public static void mergeCommandFailures(String branchName) {
        // failure cases
        if (Objects.requireNonNull(Repository.STAGING_AREA.list()).length != 0
                || Objects.requireNonNull(Repository.RM_STAGE.list()).length != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        File branchFile = Utils.join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        if (branchName.equals(readContentsAsString(HEAD))) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        fileCheck();

    }

    public static void mergeCommand(String branchName) throws IOException {
        HashMap<String, String> branchMap;
        HashMap<String, String> headMap;
        HashMap<String, String> splitMap;
        ArrayList<String> modifiedInBranchOnly;
        ArrayList<String> inBranchOnly;
        ArrayList<String> unmodifiedInHeadNotInBranch;
        ArrayList<String> conflictedFiles;
        mergeCommandFailures(branchName);
        if (mergeCheckAncestry(branchName)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (mergeCheckFastForward(branchName)) {
            checkoutBranchCommand(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        String branchSha = readContentsAsString(Utils.join(BRANCH_DIR, branchName));
        String headSha = readContentsAsString(Utils.join(BRANCH_DIR,
                readContentsAsString(HEAD)));
        File splitFile = Utils.join(COMMITS_DIR, findSplitCommit(headSha, branchSha));
        Commit splitCommit = readObject(splitFile, Commit.class);
        Commit branchCommit = readObject(Utils.join(COMMITS_DIR, branchSha), Commit.class);
        Commit headCommit = readObject(Utils.join(COMMITS_DIR, headSha), Commit.class);
        // creates hashmaps of each commits filename and respective blob sha1
        branchMap = fileAndBlob(branchCommit);
        headMap = fileAndBlob(headCommit);
        splitMap = fileAndBlob(splitCommit);
        // checks out files modified in given branch only and stages them
        modifiedInBranchOnly = modInBranchOnly(splitMap, headMap, branchMap);
        for (String i : modifiedInBranchOnly) {
            checkoutCommand(branchSha, i);
            addCommand(i);
        }
        // checks out and staged files present only in given branch
        inBranchOnly = presentInBranchOnly(splitMap, headMap, branchMap);
        for (String i : inBranchOnly) {
            checkoutCommand(branchSha, i);
            addCommand(i);
        }
        // removes and untracks files unmodified in head and not present in given branch
        unmodifiedInHeadNotInBranch = unModInHeadNotInBranch(splitMap, headMap, branchMap);
        for (String i : unmodifiedInHeadNotInBranch) {
            rmCommand(i);
        }
        // merge conflicts
        conflictedFiles = mergeConflict(splitMap, headMap, branchMap);
        String headBlobFileContents;
        String branchBlobFileContents;
        for (String i : conflictedFiles) {
            String newContent = "<<<<<<< HEAD" + "\n";
            if (headMap.containsKey(i)) {
                File headBlobFile = Utils.join(BLOBS_DIR, headMap.get(i));
                headBlobFileContents = readObject(headBlobFile, Blob.class).getContents();
            } else {
                headBlobFileContents = "";
            }
            if (branchMap.containsKey(i)) {
                File branchBlobFile = (Utils.join(BLOBS_DIR, branchMap.get(i)));
                branchBlobFileContents  = readObject(
                        branchBlobFile, Blob.class).getContents().replace("\n", "");
            } else {
                branchBlobFileContents = "";
            }
            newContent += headBlobFileContents + "=======" + "\n";
            if (branchBlobFileContents.equals("")) {
                newContent += branchBlobFileContents + ">>>>>>>" + "\n";
            } else {
                newContent += branchBlobFileContents + "\n" + ">>>>>>>" + "\n";
            }
            File mergedFile = Utils.join(CWD, i);
            if (!mergedFile.exists()) {
                mergedFile.createNewFile();
            }
            Utils.writeContents(mergedFile, newContent);
        }
        commitCommand("Merged " + branchName + " into " + Utils.readContentsAsString(HEAD) + ".",
                headSha.substring(0,6) + " " + branchSha.substring(0,6), true);
        checkForMerge(conflictedFiles);
    }

    public static void checkForMerge(ArrayList<String> files) {
        if (files.size() != 0) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static boolean mergeCheckAncestry(String branchName) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        File commitFile = Utils.join(COMMITS_DIR, readContentsAsString(branchFile));
        Commit branchCommit = readObject(commitFile, Commit.class);

        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit curr = readObject(headCommitFile, Commit.class);

        while (curr.getParent() != null) {
            if (curr.getParent().equals(branchCommit.getSha1Code())) {
                return true;
            }
            File currFile = Utils.join(COMMITS_DIR, curr.getParent());
            curr = readObject(currFile, Commit.class);
        }
        return false;
    }

    public static boolean mergeCheckFastForward(String branchName) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        File commitFile = Utils.join(COMMITS_DIR, readContentsAsString(branchFile));
        Commit branchCommit = readObject(commitFile, Commit.class);

        File headCommitFile = Utils.join(COMMITS_DIR,
                readContentsAsString(Utils.join(BRANCH_DIR, readContentsAsString(HEAD))));
        Commit headCommit = readObject(headCommitFile, Commit.class);

        while (branchCommit.getParent() != null) {
            if (branchCommit.getParent().equals(headCommit.getSha1Code())) {
                return true;
            }
            File currFile = Utils.join(COMMITS_DIR, branchCommit.getParent());
            branchCommit = readObject(currFile, Commit.class);
        }
        return false;
    }

    // only works if heads of branches are equal distance from split
    public static String findSplitCommit(String first, String second) {
        File firstFile = Utils.join(COMMITS_DIR, first);
        Commit firstCommit = Utils.readObject(firstFile, Commit.class);

        File secondFile = Utils.join(COMMITS_DIR, second);
        Commit secondCommit = Utils.readObject(secondFile, Commit.class);

        while (firstCommit.getParent() != null) {
            if (firstCommit.getParent().equals(
                    secondCommit.getParent())) {
                return firstCommit.getParent();
            }
            File firstParent = Utils.join(COMMITS_DIR, firstCommit.getParent());
            firstCommit = readObject(firstParent, Commit.class);

            File secondParent = Utils.join(COMMITS_DIR, secondCommit.getParent());
            secondCommit = readObject(secondParent, Commit.class);

        }
        return null;
    }

    public static HashMap<String, String> fileAndBlob(Commit curr) {
        HashMap<String, String> res = new HashMap<>();
        for (String i : curr.getBlobsSha1()) {
            File blobFile = Utils.join(BLOBS_DIR, i);
            Blob currBlob = readObject(blobFile, Blob.class);
            res.put(currBlob.getFileName(), i);
        }
        return res;
    }

    // finds files that have been modified in given branch but not current commit
    // returns filename of modified files in given branch in an arraylist
    public static ArrayList<String> modInBranchOnly(HashMap<String, String> splitMap,
                                                    HashMap<String, String> headMap,
                                                    HashMap<String, String> branchMap) {
        ArrayList<String> res = new ArrayList<String>();
        for (String key : splitMap.keySet()) {
            if (headMap.containsKey(key) && headMap.get(key).equals(splitMap.get(key))
                    && branchMap.containsKey(key) && !branchMap.get(key).equals(
                            splitMap.get(key))) {
                res.add(key);
            }
        }
        return res;
    }

    // finds files that are only present in the given branch
    // returns their file names in an arraylist
    public static ArrayList<String> presentInBranchOnly(HashMap<String, String> splitMap,
                                                        HashMap<String, String> headMap,
                                                        HashMap<String, String> branchMap) {
        ArrayList<String> res = new ArrayList<>();
        for (String key : branchMap.keySet()) {
            if (!headMap.containsKey(key) && !splitMap.containsKey(key)) {
                res.add(key);
            }
        }
        return res;
    }

    public static ArrayList<String> unModInHeadNotInBranch(HashMap<String, String> splitMap,
                                                           HashMap<String, String> headMap,
                                                           HashMap<String, String> branchMap) {
        ArrayList<String> res = new ArrayList<>();
        for (String key : splitMap.keySet()) {
            if (!branchMap.containsKey(key) && headMap.containsKey(key)
                    && splitMap.get(key).equals(headMap.get(key))) {
                res.add(key);
            }
        }
        return res;
    }

    public static ArrayList<String> mergeConflict(HashMap<String, String> splitMap,
                                                  HashMap<String, String> headMap,
                                                  HashMap<String, String> branchMap) {
        ArrayList<String> res = new ArrayList<>();
        for (String key : splitMap.keySet()) {
            if (splitMap.containsKey(key) && headMap.containsKey(key)
                    && branchMap.containsKey(key)) {
                if (!headMap.get(key).equals(splitMap.get(key))
                        && !branchMap.get(key).equals(splitMap.get(key))
                        && !headMap.get(key).equals(branchMap.get(key))) {
                    res.add(key);
                }
            } else if (headMap.containsKey(key) && splitMap.containsKey(key)) {
                if (!headMap.get(key).equals(splitMap.get(key))
                        && !branchMap.containsKey(key)) {
                    res.add(key);
                }
            } else if (branchMap.containsKey(key) && splitMap.containsKey(key)) {
                if (!branchMap.get(key).equals(splitMap.get(key))
                        && !headMap.containsKey(key)) {
                    res.add(key);
                }
            }
        }
        for (String key : headMap.keySet()) {
            if (headMap.containsKey(key) && branchMap.containsKey(key)) {
                if (!splitMap.containsKey(key)
                        && !headMap.get(key).equals(branchMap.get(key))) {
                    res.add(key);
                }
            }
        }
        return res;
    }

}
