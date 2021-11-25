package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Shruti Pai
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    /** The timestamp of this Commit. */
    private Date timestamp;
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    /** The parent of this Commit. */
    private String parent;
    public void setParent(String parent) {
        this.parent = parent;
    }
    public String getParent() {
        return parent;
    }

    /** The children of this Commit. */
    private ArrayList<Commit> children;
    public void setChildren(ArrayList<Commit> children) {
        this.children = children;
    }
    public ArrayList<Commit> getChildren() {
        return children;
    }

    /** The ArrayList of blob SHA-1 for HashMap values */
    private ArrayList<String> blobsSha1;
    public void setBlobsSha1(ArrayList<String> blobsSha1) {
        this.blobsSha1 = blobsSha1;
    }
    public ArrayList<String> getBlobsSha1() {
        return blobsSha1;
    }

    /** SHA-1 for Commit object */
    private String sha1Code;
    public void setSha1Code(String sha1Code) {
        this.sha1Code = sha1Code;
    }
    public String getSha1Code() {
        return sha1Code;
    }

    /** if commit is a merge */
    private boolean merged;
    public void setMerged(Boolean merged) {
        this.merged = merged;
    }
    public Boolean getMerged() {
        return merged;
    }

    /** Constructor */
    public Commit(String message, String parent, Boolean merged) {

        if (Repository.STAGING_AREA.list().length == 0
                && !message.equals("initial commit")
                && Repository.RM_STAGE.list().length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        // meta data and pointers
        this.parent = parent;
        this.merged = merged;
        if (this.parent == null) {
            this.blobsSha1 = new ArrayList<>();
        } else {
            File parentFile = Utils.join(Repository.COMMITS_DIR, this.parent);
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            int parentSize = parentCommit.blobsSha1.size();
            this.blobsSha1 = new ArrayList<>();
            for (int i = 0; i < parentSize; i++) {
                this.blobsSha1.add("holder");
            }
            Collections.copy(this.blobsSha1, parentCommit.blobsSha1);
        }
        this.message = message;
        //this.children = new ArrayList<>();
        if (message.equals("initial commit")) {
            this.timestamp = new Date(0);
        } else {
            this.timestamp = new Date();
            //this.parent.children.add(this);
        }

        fillCommitsAndBlobs();
    }

    /** Returns the SHA-1 hash of the serialized Commit object */
    public String getSha1() {
        return Utils.sha1((Object) Utils.serialize(this));
    }

    public void storeBlobs() {
        // Serializes files in STAGING_DIR, creates Blobs, and adds sha1 to blobSha1 */
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.STAGING_AREA))) {
            File staged = Utils.join(Repository.STAGING_AREA, i);
            Blob stagedBlob = Utils.readObject(staged, Blob.class);

            // checks if commit has file with same name as staged file, if so removes old version
            for (String j : blobsSha1) {
                File blobFile = Utils.join(Repository.BLOBS_DIR, j);
                Blob currBlob = Utils.readObject(blobFile, Blob.class);
                if (stagedBlob.getFileName().equals(currBlob.getFileName())) {
                    blobsSha1.remove(currBlob.getName());
                    break;
                }
            }
            blobsSha1.add(i);

            // Delete file from staging area
            staged.delete();
        }

        // Removes files if they are in removal area
        for (String i : Objects.requireNonNull(Utils.plainFilenamesIn(Repository.RM_STAGE))) {
            File rmFile = Utils.join(Repository.RM_STAGE, i);
            Blob rmBlob = Utils.readObject(rmFile, Blob.class);
            if (blobsSha1.contains(rmBlob.getName())) {
                blobsSha1.remove(rmBlob.getName());
                rmFile.delete();
            }
        }
    }

    public void fillCommitsAndBlobs() {
        // serializes blobs, finds sha1 of commit, adds to head
        storeBlobs();
        this.sha1Code = getSha1();
        File headBranch = Utils.join(Repository.BRANCH_DIR,
                Utils.readContentsAsString(Repository.HEAD));
        Utils.writeContents(headBranch, this.sha1Code);

        /** Serializes Commit into COMMITS_DIR */
        File commitFile = Utils.join(Repository.COMMITS_DIR, this.sha1Code);
        Utils.writeObject(commitFile, this);
    }

}
