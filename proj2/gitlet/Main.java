package gitlet;

import java.io.File;
import java.util.TreeMap;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.makeInit();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.checkGitletRepository();
                File f = Utils.join(args[1]);
                if (!f.exists()) {
                    System.out.println("File does not exist.");
                    System.exit(0);
                }
                Repository.makeAdd(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args[1].isEmpty() || args[1] == null || args[1] == "") {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.checkGitletRepository();
                TreeMap addTree =  Repository.getAddTree();
                TreeMap removeTree = Repository.getRemoveTree();
                if (addTree.isEmpty() && removeTree.isEmpty()) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                }
                Repository.makeCommit(args[1]);
                break;
            case "rm":
                Repository.checkGitletRepository();
                Repository.makeRemove(args[1]);
                break;
            case "log":
                Repository.printAllLog();
                break;
            case "global-log":
                Repository.printGlobalLog();
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "branch":
                Repository.createBranch(args[1]);
                break;
            case "rm-branch":
                Repository.removeBranch(args[1]);
                break;
            case "checkout":
                if (args[1].equals("--")) {
                    Repository.checkoutFile(args[2]);
                } else if (args[1].length() == 40) {
                    if (args[2].equals("--")) {
                        Repository.checkoutFileWithCommitID(args[1], args[3]);
                    } else {
                        System.out.println("Incorrect operands.");
                    }
                } else {
                    if (Repository.getMaster().getBranchName().equals(args[1])) {
                        System.out.println("No need to checkout the current branch.");
                        System.exit(0);
                    }
                    if (!Utils.join(Head.headsFile, args[1]).exists()) {
                        System.out.println("No such branch exists.");
                        System.exit(0);
                    }
                    Repository.checkoutBranch(args[1]);
                }
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.getMerge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
