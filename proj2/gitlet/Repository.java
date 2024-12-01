package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * .gitlet
     *      |-- objects
     *              |-- commit
     *              |-- blob
     *      |-- refs
     *              |--headsFile
     *                  |-- masterFile
     *      |-- stages
     *              |-- addStage
     *              |-- removeStage
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The objects directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The refs directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The stages directory. */
    public static final File STAGES_DIR = join(GITLET_DIR, "stages");
    /* TODO: fill in the rest of this class. */

    public static void setUpPresistance() {
        CWD.mkdir();
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        Commit.COMMITS.mkdir();
        Blob.BLOBS.mkdir();
        REFS_DIR.mkdir();
        Head.HEADSFILE.mkdir();
        Head.MASTERFILE.mkdir();
        STAGES_DIR.mkdir();
        Add.ADDFILE.mkdir();
        Remove.REMOVESTAGE.mkdir();
    }

    public static Head getMaster() {
        String[] masterContent = Head.MASTERFILE.list();
        String masterName = masterContent[0];
        return Utils.readObject(Utils.join(Head.MASTERFILE, masterName), Head.class);
    }

    public static Commit getMasterCommit() {
        Head master = getMaster();
        return Utils.readObject(Utils.join(Commit.COMMITS, master.getCommitID()), Commit.class);
    }
    public static Head getBranch(String branchName) {
        if (Utils.join(Head.MASTERFILE, branchName).exists()) {
            return Utils.readObject(Utils.join(Head.MASTERFILE, branchName), Head.class);
        } else if (Utils.join(Head.HEADSFILE, branchName).exists()) {
            return Utils.readObject(Utils.join(Head.HEADSFILE, branchName), Head.class);
        } else {
            return null;
        }
    }

    //return commit by commitID, if there is no commit correspond ID, return null.
    public static Commit getCommitFromCommitID(String commitID) {
        File cf = Utils.join(Commit.COMMITS, commitID);
        if (!cf.exists()) {
            return null;
        }
        return Utils.readObject(cf, Commit.class);
    }

    public static TreeMap<String, String> getAddTree() {
        TreeMap<String, String> emptyMap = new TreeMap<>();
        if (!Add.ADDFILE.exists()) {
            return emptyMap;
        }
        TreeMap<String, String> newTreeMap = Utils.readObject(Add.ADDFILE, TreeMap.class);
        return newTreeMap;
    }

    public static TreeMap<String, String> getRemoveTree() {
        TreeMap<String, String> emptyMap = new TreeMap<>();
        if (!Remove.REMOVEFILE.exists()) {
            return emptyMap;
        }
        TreeMap<String, String> newTreeMap = Utils.readObject(Remove.REMOVEFILE, TreeMap.class);
        return newTreeMap;
    }

    public static Blob getBlobFromBlobID(String blobID) {
        File blobFileName = Utils.join(Blob.BLOBS, blobID);
        return Utils.readObject(blobFileName, Blob.class);
    }

    public static void checkGitletRepository() {
        File gitletDir = Utils.join(CWD, ".gitlet");
        if (!gitletDir.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void makeInit() {
        File gitletDir = Utils.join(CWD, ".gitlet");
        if (gitletDir.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        setUpPresistance();
        Commit init = new Commit();
        init.saveCommit();
        init.saveShortCommit();
        Head master = new Head(init);
        master.saveInMaster();
    }

    public static void makeAdd(String path) {
        Blob newBlob = new Blob(path);
        newBlob.saveBlob();
        Add newAdd = new Add(newBlob);
        newAdd.saveAdd();
    }

    public static void makeAddWithBlob(Blob newBlob) {
        newBlob.saveBlob();
        Add newAdd = new Add(newBlob);
        newAdd.saveAdd();
    }

    public static void makeCommit(String message) {
        Commit newCommit = new Commit(message);
        newCommit.saveCommit();
        newCommit.saveShortCommit();
        Commit.cleanAddFile();
        Commit.cleanRemoveFile();
        Head originalMaster = getMaster();
        Head master = new Head(originalMaster.getBranchName(), newCommit);
        master.saveInMaster();
    }

    public static void makeCommitWithParent(String message, LinkedList<String> newParent) {
        Commit newCommit = new Commit(message, newParent);
        newCommit.saveCommit();
        newCommit.saveShortCommit();
        Commit.cleanAddFile();
        Commit.cleanRemoveFile();
        Head originalMaster = getMaster();
        Head master = new Head(originalMaster.getBranchName(), newCommit);
        master.saveInMaster();
    }

    public static void makeRemove(String path) {

        Blob newBlob = new Blob(path);
        Remove newRemove = new Remove(newBlob);
        newRemove.saveRemove();
    }

    //print the single log message of current commit.
    public static void printLog(Commit currentCommit) {
        checkGitletRepository();
        System.out.println("===");
        System.out.println("commit " + currentCommit.getCommitID());
        System.out.println("Date: " + currentCommit.getCommitDate());
        System.out.println(currentCommit.getCommitMessage());
        System.out.printf("\n");
    }

    //print all log message by sequence of all commits.
    public static void printAllLog() {
        checkGitletRepository();
        Commit masterCommit = getMasterCommit();
        printLog(masterCommit);
        Commit currentCommit = masterCommit;
        while (!currentCommit.getParent().isEmpty()) {
            for (String currentCommitID: currentCommit.getParent()) {
                currentCommit = getCommitFromCommitID(currentCommitID);
                printLog(currentCommit);
            }
        }
    }

    public static void printGlobalLog() {
        checkGitletRepository();
        Collection c = Utils.plainFilenamesIn(Commit.COMMITS);
        Iterator commitID = c.iterator();
        while (commitID.hasNext()) {
            Commit newCommit = getCommitFromCommitID((String) commitID.next());
            printLog(newCommit);
        }
    }

    public static void find(String message) {
        checkGitletRepository();
        boolean flag = false;
        Collection c = Utils.plainFilenamesIn(Commit.COMMITS);
        Iterator commitID = c.iterator();
        while (commitID.hasNext()) {
            String newID = (String) commitID.next();
            Commit newCommit = getCommitFromCommitID(newID);
            if (newID.length() != 40) {
                continue;
            }
            if (newCommit.getCommitMessage().equals(message)) {
                flag = true;
                System.out.printf(newCommit.getCommitID() + "\n");
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    //finished the front 3 items, hasn't finished the last two sections.
    public static void status() {
        checkGitletRepository();
        //problems here, Head.setMaster() neeed to finish.
        System.out.println("=== Branches ===");
        String[] dir = Head.MASTERFILE.list();
        System.out.println("*" + dir[0]);
        for (String headName : Utils.plainFilenamesIn(Head.HEADSFILE)) {
            System.out.println(headName);
        }
        System.out.printf("\n");
        System.out.println("=== Staged Files ===");
        File addFile = Utils.join(Add.ADDFILE);
        File removeFile = Utils.join(Remove.REMOVEFILE);
        if (addFile.exists()) {
            TreeMap<String, String> addTreeMap = Utils.readObject(Add.ADDFILE, TreeMap.class);
            Collection addCollection = addTreeMap.keySet();
            Iterator addIter = addCollection.iterator();
            while (addIter.hasNext()) {
                System.out.println(addIter.next());
            }
        }
        System.out.printf("\n");
        System.out.println("=== Removed Files ===");
        if (removeFile.exists()) {
            TreeMap<String, String> removeTreeMap = Utils.readObject(Remove.REMOVEFILE, TreeMap.class);
            Collection removeCollection = removeTreeMap.keySet();
            Iterator removeIter = removeCollection.iterator();
            while (removeIter.hasNext()) {
                System.out.println(removeIter.next());
            }
        }
        System.out.printf("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.printf("\n");
        System.out.println("=== Untracked Files ===");
        System.out.printf("\n");
    }

    public static void createBranch(String brancheName) {
        checkGitletRepository();
        if (Utils.join(Head.HEADSFILE, brancheName).exists() || Utils.join(Head.MASTERFILE, brancheName).exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit masterCommit = getMasterCommit();
        Head newHead = new Head(brancheName, masterCommit);
        newHead.saveInHeads();
    }

    public static void removeBranch(String branchName) {
        checkGitletRepository();
        if (getMaster().getBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        boolean flag = false;
        Collection c = Utils.plainFilenamesIn(Head.HEADSFILE);
        Iterator headIter = c.iterator();
        while (headIter.hasNext()) {
            if (headIter.next().equals(branchName)) {
                Utils.join(Head.HEADSFILE, branchName).delete();
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    //Takes the version of the file as it exists in the head commit and puts it in the working directory
    //if the file isn't in the commit tree, error,
    //otherwise, get the blobID and find out the blob, write the content into the original file path.
    public static void checkoutFile(String fileName) {
        checkGitletRepository();
        Commit masterCommit = getMasterCommit();
        TreeMap masterCommitTree = masterCommit.getTreeMap();
        if (!masterCommitTree.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileBlobID = (String) masterCommitTree.get(fileName);
        Blob fileBlob = getBlobFromBlobID(fileBlobID);
        Utils.writeContents(Utils.join(fileName), fileBlob.getContentAsByte());
    }

    public static void checkoutFileWithCommitID(String targetCommitID, String fileName) {
        checkGitletRepository();
        File targetCommitFile = Utils.join(Commit.COMMITS, targetCommitID);
        if (!targetCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit targetCommit = getCommitFromCommitID(targetCommitID);
        TreeMap targetCommitTree = targetCommit.getTreeMap();
        if (!targetCommitTree.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileBlobID = (String) targetCommitTree.get(fileName);
        Blob fileBlob = getBlobFromBlobID(fileBlobID);
        Utils.writeContents(Utils.join(fileName), fileBlob.getContentAsByte());
    }

    //check whether files in CWD are all tracked.
    public static void checkCWDFileTracked(String branchName) {
        checkGitletRepository();
        Commit currentCommit = getMasterCommit();
        TreeMap currentCommitTree = currentCommit.getTreeMap();

        Head branch = getBranch(branchName);
        String branchCommitID = branch.getCommitID();
        Commit branchCommit = getCommitFromCommitID(branchCommitID);
        TreeMap branchCommitTree = branchCommit.getTreeMap();

        Collection c = Utils.plainFilenamesIn(CWD);
        Iterator cwdIter = c.iterator();
        while (cwdIter.hasNext()) {
            String fileName = (String) cwdIter.next();
            if (!currentCommitTree.containsKey(fileName) && branchCommitTree.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    //go through CWD, check the path
    //go through commitTree, check the path.
    public static void changeCWDFile(String branchName) {
        checkGitletRepository();
        Head newHead = getBranch(branchName);
        newHead.setMaster();
        Commit newCommit = getMasterCommit();
        TreeMap newCommitTree = newCommit.getTreeMap();
        Collection ca = Utils.plainFilenamesIn(CWD);
        Iterator agCWDIter = ca.iterator();
        while (agCWDIter.hasNext()) {
            String newPath = (String) agCWDIter.next();
            if (!newCommitTree.containsKey(newPath)) {
                Utils.join(newPath).delete();
            } else {
                //no matter whether the file is the same or not, just overwrite it.
                String newID = (String) newCommitTree.get(newPath);
                Blob newBlob = getBlobFromBlobID(newID);
                Utils.writeContents(Utils.join(newPath), newBlob.getContentAsByte());
            }
        }

        Collection ct = newCommitTree.keySet();
        Iterator treeIter = ct.iterator();
        while (treeIter.hasNext()) {
            String commitPath = (String) treeIter.next();
            if (!Utils.join(commitPath).exists()) {
                String newID2 = (String) newCommitTree.get(commitPath);
                Blob newBlob2 = getBlobFromBlobID(newID2);
                Utils.writeContents(Utils.join(commitPath), newBlob2.getContentAsByte());
            }
        }
    }

    public static void checkoutBranch(String branchName) {
        checkGitletRepository();

        //check whether files in CWD are all tracked.
        checkCWDFileTracked(branchName);

        //go through CWD, check the path
        //go through commitTree, check the path.
        changeCWDFile(branchName);

        Commit.cleanAddFile();
        Commit.cleanRemoveFile();
    }

    public static void reset(String commitID) {
        checkGitletRepository();
        Commit targetCommit = getCommitFromCommitID(commitID);
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Head master = getMaster();
        String masterName = master.getBranchName();
        Head newMaster = new Head(masterName, targetCommit);

        Head tempMaster = new Head("tempMaster", targetCommit);
        tempMaster.saveInHeads();
        //check whether files in CWD are all tracked.
        checkCWDFileTracked("tempMaster");
        Utils.join(Head.HEADSFILE, "tempMaster").delete();
        newMaster.saveInMaster();
        //go through CWD, check the path
        //go through commitTree, check the path.
        changeCWDFile(masterName);

        Commit.cleanAddFile();
        Commit.cleanRemoveFile();
    }

    public static String getSplitPointID(String branchName) {
        Commit masterCommit = getMasterCommit();
        String masterCommitPointID = masterCommit.getCommitID();
        Head branchHead = getBranch(branchName);
        String branchCommitID = branchHead.getCommitID();
        Commit branchCommit = getCommitFromCommitID(branchCommitID);
        String branchCommitPointID = branchCommit.getCommitID();

        TreeMap masterCommitTrack = new TreeMap<>();
        masterCommitTrack.put(masterCommitPointID, 1);

        Commit currentCommit = masterCommit;
        while (!currentCommit.getParent().isEmpty()) {
            for (String currentCommitID: currentCommit.getParent()) {
                masterCommitTrack.put(currentCommitID, 1);
                currentCommit = getCommitFromCommitID(currentCommitID);
            }
        }

        if (masterCommitTrack.containsKey(branchCommitPointID)) {
            return branchCommitPointID;
        }

        Commit currentBranchCommit = branchCommit;
        while (!currentBranchCommit.getParent().isEmpty()) {
            for (String currentBranchCommitID: currentBranchCommit.getParent()) {
                if (masterCommitTrack.containsKey(currentBranchCommitID)) {
                    return currentBranchCommitID;
                }
                currentBranchCommit = getCommitFromCommitID(currentBranchCommitID);
            }
        }

        return null;
    }

    public static void getMerge(String branchName) {

        if (!getAddTree().isEmpty() || !getRemoveTree().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        Commit masterCommit = getMasterCommit();
        String masterCommitPointID = masterCommit.getCommitID();

        //System.out.println(masterCommitPointID);

        Head branchHead = getBranch(branchName);
        if (branchHead == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String branchCommitID = branchHead.getCommitID();

        checkCWDFileTracked(branchName);

        //System.out.println(branchCommitID);

        Commit branchCommit = getCommitFromCommitID(branchCommitID);
        String branchCommitPointID = branchCommit.getCommitID();
        String spiltPointID = getSplitPointID(branchName);

        //System.out.println(spiltPointID);

        Commit spiltPoint = getCommitFromCommitID(spiltPointID);

        if (masterCommitPointID.equals(branchCommitPointID)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        if (branchCommitPointID.equals(spiltPointID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (masterCommitPointID.equals(spiltPointID)) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        //create three trees, masterTree, branchTree, spiltTree, the key is ID, and the value is path.
        TreeMap masterTree = masterCommit.getTreeMap();
        Collection masterC = masterTree.keySet();
        Iterator masterIter = masterC.iterator();
        TreeMap masterMap = new TreeMap<>();
        while (masterIter.hasNext()) {
            String newPath = (String) masterIter.next();
            masterMap.put(masterTree.get(newPath), newPath);
        }

        TreeMap branchTree = branchCommit.getTreeMap();
        Collection branchC = branchTree.keySet();
        Iterator branchIter = branchC.iterator();
        TreeMap branchMap = new TreeMap<>();
        while (branchIter.hasNext()) {
            String newPath = (String) branchIter.next();
            branchMap.put(branchTree.get(newPath), newPath);
        }

        TreeMap spiltTree = spiltPoint.getTreeMap();
        Collection spiltC = spiltTree.keySet();
        Iterator spiltIter = spiltC.iterator();
        TreeMap spiltMap = new TreeMap<>();
        while (spiltIter.hasNext()) {
            String newPath = (String) spiltIter.next();
            spiltMap.put(spiltTree.get(newPath), newPath);
        }
        //combine all IDToPath in the allMap
        TreeMap allMap = new TreeMap<>();
        Collection c1 = masterMap.keySet();
        Iterator iter1 = c1.iterator();
        while (iter1.hasNext()) {
            String newID = (String) iter1.next();
            allMap.put(newID, masterMap.get(newID));
        }
        Collection c2 = branchMap.keySet();
        Iterator iter2 = c2.iterator();
        while (iter2.hasNext()) {
            String newID = (String) iter2.next();
            if (!allMap.containsKey(newID)) {
                allMap.put(newID, branchMap.get(newID));
            }
        }
        Collection c3 = spiltMap.keySet();
        Iterator iter3 = c3.iterator();
        while (iter3.hasNext()) {
            String newID = (String) iter3.next();
            if (!allMap.containsKey(newID)) {
                allMap.put(newID, spiltMap.get(newID));
            }
        }

        boolean conflict = false;

        //compare allMap to the three trees, judge the seven situations.
        Collection allC = allMap.keySet();
        Iterator allIter = allC.iterator();
        while (allIter.hasNext()) {
            String allID = (String) allIter.next();
            //correspond to situation 1/5/8, need further judge.
            if (!masterMap.containsKey(allID) && branchMap.containsKey(allID) && !spiltMap.containsKey(allID)) {
                String filePath = (String) branchMap.get(allID);
                String masterID = (String) masterTree.get(filePath);
                String spiltID = (String) spiltTree.get(filePath);
                //correspond to situation 5, checkout the file and adds it.
                if (masterID == null && branchTree.get(filePath) != null && spiltID == null) {
                    checkoutFileWithCommitID(branchCommitID, filePath);
                    Blob newBlob = getBlobFromBlobID(allID);
                    makeAddWithBlob(newBlob);
                } else if (masterID != null && spiltID != null && masterID.equals(spiltID)) {
                    //correspond to situation 1, add the file.
                    checkoutFileWithCommitID(branchCommitID, filePath);
                    makeAdd((String) branchMap.get(allID));
                } else {
                    //correspond to situation 8, conflict.
                    File conflictFileInBranch = Utils.join(Blob.BLOBS, allID);
                    Blob conflictBlobInBranch = getBlobFromBlobID(allID);
                    String masterContent = "";
                    if (masterID != null) {
                        File conflictFileInMaster = Utils.join(Blob.BLOBS, masterID);
                        Blob conflictBlobInMaster = getBlobFromBlobID(masterID);
                        masterContent = new String(conflictBlobInMaster.getContentAsByte(), StandardCharsets.UTF_8);
                    }
                    String branchContent = new String(conflictBlobInBranch.getContentAsByte(), StandardCharsets.UTF_8);
                    String newContent = "<<<<<<< HEAD\n" + masterContent + "=======\n" + branchContent + ">>>>>>>\n";
                    //only exist branch file, overwrite the content and stage it.
                    Utils.writeContents(Utils.join(filePath), newContent);
                    conflict = true;
                    //in case of the situation is judged by the latter again, when the two files have different content.
                }
            }

            //correspond to situation 8, conflict.
            if (masterMap.containsKey(allID) && !branchMap.containsKey(allID) && !spiltMap.containsKey(allID)) {
                String filePath = (String) masterMap.get(allID);
                String branchID = (String) branchTree.get(filePath);
                String spiltID = (String) spiltTree.get(filePath);
                if (masterTree.get(filePath) != null && branchID == null && spiltID == null) {
                    //situation 4
                    continue;
                } else if (branchID != null && spiltID != null && branchID.equals(spiltID)) {
                    //situation 2
                    continue;
                } else {
                    //correspond to situation 8, conflict.
                    File conflictFileInMaster = Utils.join(Blob.BLOBS, allID);
                    Blob conflictBlobInMaster = getBlobFromBlobID(allID);
                    String branchContent = "";
                    if (branchID != null) {
                        File conflictFileInBranch = Utils.join(Blob.BLOBS, branchID);
                        Blob conflictBlobInBranch = getBlobFromBlobID(branchID);
                        branchContent = new String(conflictBlobInBranch.getContentAsByte(), StandardCharsets.UTF_8);
                    }
                    String masterContent = new String(conflictBlobInMaster.getContentAsByte(), StandardCharsets.UTF_8);
                    String newContent = "<<<<<<< HEAD\n" + masterContent + "=======\n" + branchContent + ">>>>>>>\n";
                    //masterFile must exist.
                    Utils.writeContents(Utils.join(filePath), newContent);
                    conflict = true;
                }
            }

            //correspond to situation 6, remove the file.
            if (masterMap.containsKey(allID) && !branchMap.containsKey(allID) && spiltMap.containsKey(allID)) {
                String filePath = (String) masterMap.get(allID);
                if (masterTree.get(filePath) != null && branchTree.get(filePath) == null && spiltTree.get(filePath) != null) {
                    makeRemove((String) masterMap.get(allID));
                }
            }
        }
        String commitMessage = "Merged " + branchName + " into " + getMaster().getBranchName() + ".";
        LinkedList<String> newParent = new LinkedList<>(List.of(masterCommitPointID, branchCommitPointID));
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }

        makeCommitWithParent(commitMessage, newParent);
    }
}
