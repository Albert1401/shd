package expression;

public class Negate implements Expression {

    private Expression operation1;

    private final char operator = '!';

    public Negate(Expression operation1){
        this.operation1 = operation1;
    }

    public Expression getOperands(int number){
        return operation1;
    }

    public char getOperator() {
        return '!';
    }

    public boolean equals(Expression expression){
        if (operator == expression.getOperator()) {
            if (operation1.equals(expression.getOperands(0))) {
                return true;
            }
        }
        return false;
    }

    public String toString(){
        if (this.operation1.getOperator() == 'v'){
            return "!" + operation1.toString();
        }
        return "!(" + operation1.toString() + ")";
    }
}