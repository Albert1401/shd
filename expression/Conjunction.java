package expression;

public class Conjunction extends AbstractBinOperation {
    public Conjunction(Expression operation1, Expression operation2){
        super(operation1, operation2, '&');
    }
}
