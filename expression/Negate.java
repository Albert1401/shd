package expression;

import java.util.Map;

public class Negate implements Expression {

    private Expression operand1;

    private final char operator = '!';

    public Negate(Expression operation1){
        this.operand1 = operation1;
    }

    @Override
    public Expression getOperands(int number){
        return operand1;
    }

    @Override
    public char getOperator() {
        return '!';
    }

    @Override
    public boolean equals(Expression expression){
        if (operator == expression.getOperator()) {
            if (operand1.equals(expression.getOperands(0))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> args) {
        return !getOperands(0).evaluate(args);
    }

    @Override
    public String toString(){
        if (this.operand1.getOperator() == 'v'){
            return "!" + operand1.toString();
        }
        return "!(" + operand1.toString() + ")";
    }
}