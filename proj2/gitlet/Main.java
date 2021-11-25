package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.readContentsAsString;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Shruti Pai
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].isBlank() || args[0].isEmpty()) {
            System.out.println("Please enter a command");
            return;
        }
        String firstArg = args[0];
        // If command needs an initialized Gitlet working directory
        if (!firstArg.equals("init") && !Utils.join(System.getProperty("user.dir"),
                ".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        switch (firstArg) {
            case "init":
                Repository.initCommand();
                break;
            case "add":
                Repository.addCommand(args[1]);
                break;
            case "commit":
                if (args.length == 1
                        || args[1].isEmpty() || args[1].isBlank()) {
                    System.out.println("Please enter a commit message.");
                } else if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                File headCommitFile = Utils.join(Repository.COMMITS_DIR,
                        readContentsAsString(Utils.join(Repository.BRANCH_DIR,
                                readContentsAsString(Repository.HEAD))));
                Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
                Repository.commitCommand(args[1], headCommit.getSha1Code(), false);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                Repository.rmCommand(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                }
                Repository.logCommand();
                break;
            case "global-log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                }
                Repository.globalLogCommand();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                Repository.findCommand(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                }
                Repository.statusCommand();
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranchCommand(args[1]);
                } else if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                    }
                    Repository.checkoutFileCommand(args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                    }
                    Repository.checkoutCommand(args[1], args[3]);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                Repository.branchCommand(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                Repository.rmBranchCommand(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                Repository.resetCommand(args[1]);
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                }
                Repository.mergeCommand(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
