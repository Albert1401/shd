package checker;

import expression.Expression;
import expression.ExpressionParser;
import expression.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class AxiomChecker {

    private final int length = 10;
    private Expression Axioms[] = new Expression[length];

    private Vector<String> namesVector = new Vector<>();
    private Vector<Expression> expressionsVector = new Vector<>();
    private boolean isOk = true;

    private void dfs(Expression Axiom, Expression expression) {
        if (!isOk) return;
        if (Axiom.getOperator() == 'v') {
            if (!namesVector.contains(((Variable) Axiom).getName())){
                namesVector.add(((Variable) Axiom).getName());
                expressionsVector.add(expression);
                return;
            } else {
                int index = namesVector.indexOf(((Variable) Axiom).getName());
                if (index != -1) {
                    if (expressionsVector.elementAt(index).isEqual(expression)) {
                        return;
                    }
                }
                isOk = false;
                return;
            }
        }
        if (Axiom.getOperator() != expression.getOperator()){
            isOk = false;
            return;
        } else {
            if (Axiom.getOperator() == '!') {
                dfs(Axiom.getOperands(0), expression.getOperands(0));
            } else {
                dfs(Axiom.getOperands(1), expression.getOperands(1));
                dfs(Axiom.getOperands(2), expression.getOperands(2));
            }
        }
    }

    private boolean is(int AxiomIndex, Expression expression){
        isOk = true;
        dfs(Axioms[AxiomIndex], expression);
        namesVector.clear();
        expressionsVector.clear();
        return isOk;
    }

    public AxiomChecker(String pathToAxioms) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathToAxioms));
        ExpressionParser parser = new ExpressionParser();
        String line;
        for (int i = 0;(line = reader.readLine()) != null; i++){
            Axioms[i] = parser.parse(line);
        }
    }

    public int isAxiom(Expression expression){
        for (int i = 0; i < Axioms.length; i++){
            if (is(i, expression)){
                return i;
            }
        }
        return -1;
    }
}
