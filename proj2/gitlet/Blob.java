package gitlet;
import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {

    /** Name of Blob object */
    private String name;
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }


    /** SHA-1 Code for Blob
    private String sha1Code;
    public void setSha1Code(String sha1Code) {
        this.sha1Code = sha1Code;
    }
    public String getSha1Code() {
        return sha1Code;
    } */

    /** Serialized contents of Blob */
    private byte[] serializedBlob;
    public void setSerializedBlob(byte[] serializedBlob) {
        this.serializedBlob = serializedBlob;
    }
    public byte[] getSerializedBlob() {
        return serializedBlob;
    }

    /** Original file */
    private byte[] bytes;

    /** File contents */
    private String contents;
    public void setContents(String contents) {
        this.contents = contents;
    }
    public String getContents() {
        return contents;
    }

    /** Original name of file */
    private String fileName;
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileName() {
        return fileName;
    }

    public Blob(File file, String fileName) {
        this.fileName = fileName;
        this.bytes = Utils.readContents(file);
        this.contents = Utils.readContentsAsString(file);
        //this.sha1Code = Utils.sha1(serializedBlob);
        this.serializedBlob = Utils.serialize(contents + fileName);
        this.name = Utils.sha1((Object) this.serializedBlob);

        /** Serializes Blob into BLOBS_DIR */
        File blobFile = Utils.join(Repository.BLOBS_DIR, this.name);
        Utils.writeObject(blobFile, this);
    }

}
