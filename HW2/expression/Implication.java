package expression;


public class Implication extends AbstractBinOperation {
    public Implication(Expression operation1, Expression operation2){
        super(operation1, operation2, '-');
    }
}
