package expression;

public class Disjunction extends AbstractBinOperation {
    public Disjunction(Expression operation1, Expression operation2){
        super(operation1, operation2, '|');
    }
}
