package expression;

public abstract class AbstractBinOperation implements Expression {
    protected Expression operation1;
    protected Expression operation2;
    protected char operator;

    protected AbstractBinOperation(Expression operation1, Expression operation2, char operator){
        this.operation1 = operation1;
        this.operation2 = operation2;
        this.operator = operator;
    }

    public char getOperator(){
        return operator;
    }

    public Expression getOperands(int number){
        if (number == 1) {
            return operation1;
        }
        return operation2;
    }

    public boolean isEqual(Expression expression){
        if (operator == expression.getOperator()){
            if (this.operation1.isEqual(expression.getOperands(1)) &&
                    this.operation2.isEqual(expression.getOperands(2)))
                return true;
        }
        return false;
    }
}