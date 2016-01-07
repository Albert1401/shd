package expression;

import java.util.Map;

public class Disjunction extends AbstractBinOperation {
    public Disjunction(Expression operation1, Expression operation2){
        super(operation1, operation2, '|');
    }

    @Override
    public boolean evaluate(Map<String, Boolean> args) {
        return getOperands(1).evaluate(args) || getOperands(2).evaluate(args);
    }
}
