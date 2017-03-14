import checker.Proofer;
import expression.ExpressionParser;

import java.io.IOException;

public class HW3 {
    public static void main(String[] args) throws IOException {
        Proofer proofer = new Proofer();
        if (!proofer.make(new ExpressionParser().parse(args[0]))){
            System.out.println("ложное выражение");
        }
    }
}
