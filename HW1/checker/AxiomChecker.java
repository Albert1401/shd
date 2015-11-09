package checker;

import expression.Expression;
import expression.ExpressionParser;
import expression.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AxiomChecker {

    private ArrayList<Expression> Axioms = new ArrayList<>();
    private ArrayList<String> namesVector = new ArrayList<>();
    private ArrayList<Expression> expressionsVector = new ArrayList<>();

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
                    if (expressionsVector.get(index).equals(expression)) {
                        return;
                    }
                }
                isOk = false;
                return;
            }
        }
        if (Axiom.getOperator() != expression.getOperator()){
            isOk = false;
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
        dfs(Axioms.get(AxiomIndex), expression);
        namesVector.clear();
        expressionsVector.clear();
        return isOk;
    }

    public AxiomChecker(String pathToAxioms) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathToAxioms));
        ExpressionParser parser = new ExpressionParser();

        String line;
        while ((line = reader.readLine()) != null){
            Axioms.add(parser.parse(line));
        }
    }

    public int isAxiom(Expression expression){
        for (int i = 0; i < Axioms.size(); i++){
            if (is(i, expression)){
                return i;
            }
        }
        return -1;
    }
}
