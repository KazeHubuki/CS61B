package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author George Yuan
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository repository = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumAndFormatArgs(args, 1);
                repository.init();
                break;
            case "add":
                validateNumAndFormatArgs(args, 2);
                repository.add(args[1]);
                break;
            case "commit":
                validateNumAndFormatArgs(args, 2);
                repository.commit(args[1]);
                break;
            case "rm":
                validateNumAndFormatArgs(args, 2);
                repository.remove(args[1]);
                break;
            case "log":
                validateNumAndFormatArgs(args, 1);
                repository.log();
                break;
            case "global-log":
                validateNumAndFormatArgs(args, 1);
                repository.globalLog();
                break;
            case "checkout":
                handleCheckOutCall(args, repository);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void validateNumAndFormatArgs(String[] args, int argsNumber) {
        if (args.length != argsNumber) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (!args[0].equals("init") && !Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void validateCheckOutArgs(String[] args) {
        if (!(args.length == 2 || args.length == 3 || args.length == 4)) {
            System.out.println("Incorrect operands.");
        }
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        if (args.length == 3 && !args[1].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (args.length == 4 && !args[2].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void handleCheckOutCall(String[] args, Repository repository) {
        validateCheckOutArgs(args);
        if (args.length == 3) {
            repository.checkOutWithFileName(args[2]);
        }
        if (args.length == 4) {
            repository.checkOutWithCommitIDAndFileName(args[1], args[3]);
        }
        if (args.length == 2) {
            // checkOutWithBranchID
        }
    }
}
