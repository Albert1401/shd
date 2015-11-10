import checker.ProofChanger;

import java.io.IOException;

public class HW2 {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Enter path to test file");
        } else {
            ProofChanger proofChanger = new ProofChanger("axioms.txt", args[0]);
            proofChanger.change();
        }
    }
}
