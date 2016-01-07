package checker;

import expression.Expression;

import java.io.IOException;

public interface ProofOutput {
    void write(Expression expression) throws IOException;

}
