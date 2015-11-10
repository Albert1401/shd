package expression;

public class Variable implements Expression {

    private final String name;

    public Variable(String name){
        this.name = name;
    }

    public char getOperator() {
        return 'v';
    }

    public boolean equals(Expression expression) {
        if (expression.getOperator() == 'v') {
            if (name.equals(((Variable) expression).name)){
                return true;
            }
        }
        return false;
    }

    public Expression getOperands(int number) {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}