package expression;

public interface Expression {

    public char getOperator();

    public boolean equals(Expression expression);

    public Expression getOperands(int number);

    public String toString();
}
