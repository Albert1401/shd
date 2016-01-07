import checker.FileProofChecker;

import java.io.IOException;

public class HW1 {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Enter path to test file");
        } else {
            FileProofChecker proofChecker = new FileProofChecker(args[0], System.out);
            proofChecker.check();
        }
    }
}
