import checker.Proofer;
import expression.ExpressionParser;

import java.io.IOException;

public class HW3 {
    public static void main(String[] args) throws IOException {
        Proofer proofer = new Proofer();
        proofer.make(new ExpressionParser().parse("(A->B->C)->(B->A->C)"));
    }
}
