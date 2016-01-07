package expression;

import java.util.Map;

public interface Expression {

    char getOperator();

    boolean equals(Expression expression);

    boolean evaluate(Map<String, Boolean> args);

    Expression getOperands(int number);

    String toString();
}
