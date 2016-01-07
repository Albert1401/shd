package checker;

import expression.Expression;

import java.io.IOException;
import java.util.ArrayList;

public interface ProofInput {
    ArrayList<Expression> getAssumptions();
    Expression getMainAssumption();
    Expression nextLine() throws IOException;
}