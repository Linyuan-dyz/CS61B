package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.makeInit();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.makeAdd(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args[1].isEmpty() || args[1] == null || args[1] == "") {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.makeCommit(args[1]);
                break;
            case "rm":
                Repository.makeRemove(args[1]);
                break;
            case "log":
                Repository.printAllLog();
                break;
            case "global-log":
                Repository.printAllLog();
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
                    }
                } else {
                    Repository.checkoutBranch(args[1]);
                }
                break;
        }
    }
}
