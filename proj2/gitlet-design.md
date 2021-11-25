# Gitlet Design Document

**Name**: Shruti Pai

## Classes and Data Structures

### Main
Driver class for Gitlet, where args contains the command and operands. 

#### Fields

No fields


### Commit
Creates a Commit objects

#### Fields

1. private String message;
   - commit message
2. private Date timestamp;
   - timestamp of commit
3. private Commit parent;
   - the commit that refers to a parent 
4. private ArrayList<Commit,> children;
   - an arraylist of commit objects for the children of current commit
    - when a commit is created, it is added to their parent's children arraylist
5. public static HashMap<String, ArrayList<byte[]>> commitsAndBlobs
    - a hashmap mapping the SHA1 of commits to an ArrayList of the serialized blobs for that commit
6. public ArrayList<byte[]> blobsSerialized
    - an ArrayList of serialized blobs to be added as a value in the commitsAndBlobs HashMap
7. public String sha1Code;
    - the SHA-1 code for a Commit object
    

### DumpObj

#### Fields

### Repository
Handles all the commands. Invokes a specific method based
on the inputted arguments. 

#### Fields
1. public static final File CWD
2. public static final File GITLET_DIR
3. public static final File COMMITS_DIR
4. public static final File STAGING_AREA
5. public static Commit head;
   - pointer to HEAD Commit
6. 

### Utils
Staff provided utility methods.

#### Fields


## Algorithms

### Main
### Repository
- initCommand()
    - handles failure case if a .gitlet directory already exists
    - creates a GITLET_DIR, COMMITS_DIR, and STAGING_AREA
    - creates initialCommit Commit object and serializes it to a file in COMMITS_DIR
- addCommand()
    - 

### Commit
- Constructor()
  - creates Commit object with metadata
    - message, timestamp, parent, children, blobsSerialized, sha1Code
    - calls helper methods to fill HashMap of Commits and Blobs
    - serializes commit object into COMMITS_DIR
    
- getSha1()
    - helper method that returns the SHA-1 of Commit object
    
- serializeBlobs()
    - iterates through files in STAGING_AREA, serializes it, adds byte[] to
      blobsSerialized ArrayList, and deletes file from STAGING_AREA
      
- fillCommitsAndBlobs()
    - if parent to Commit is not null, parent's blobsSerialized is cloned into current
    Commit's
   - calls serializeBlobs to serialize blobs in staging area
    - calls sha1Code to set the sha1Code of current Commit to the SHA-1 hash
    - serializes files staged for removal and removes value from blobsSerialized
        - adds sha1A and blobsSerialized to commitsAndBlobs

## Persistence
- .gitlet directory
    - .commits
        - directory with a folder for each commit made
    - .stagingArea
        - director with serialized File objects that are added

