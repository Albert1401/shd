package expression;

public interface Expression {

    public char getOperator();

    public boolean isEqual(Expression expression);

    public Expression getOperands(int number);
}
