import checker.Proofer;
import expression.ExpressionParser;

import java.io.IOException;

/**
 * Created by clitcommander on 04.01.16.
 */
public class HW3 {
    public static void main(String[] args) throws IOException {
        Proofer proofer = new Proofer();
        proofer.make(new ExpressionParser().parse("(P->Q)|(!P->Q)"));
    }
}
