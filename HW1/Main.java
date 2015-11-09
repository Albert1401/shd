import checker.CorrectProofChecker;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Enter path to test file");
        } else {
            CorrectProofChecker proofChecker = new CorrectProofChecker("axioms.txt", args[0]);
            System.out.print(proofChecker.check() ? "Expression proved" : "Expression not proved");
        }
    }
}
