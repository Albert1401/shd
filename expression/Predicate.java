package expression;

import java.util.Map;

/**
 * Created by clitcommander on 07.01.16.
 */
public class Predicate implements Expression {
    @Override
    public char getOperator() {
        return 'P';
    }

    @Override
    public boolean equals(Expression expression) {
        return false;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> args) {
        return false;
    }

    @Override
    public Expression getOperands(int number) {
        return null;
    }
}
