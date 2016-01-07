package expression;

import java.util.Map;

public class Variable implements Expression {

    private final String name;

    public Variable(String name){
        this.name = name;
    }

    @Override
    public char getOperator() {
        return 'v';
    }

    @Override
    public boolean equals(Expression expression) {
        if (expression.getOperator() == 'v') {
            if (name.equals(((Variable) expression).name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> args) {
        return args.get(name);
    }

    @Override
    public Expression getOperands(int number) {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}