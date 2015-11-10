package checker;

import expression.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WritingUtil {
    private ExpressionParser parser = new ExpressionParser();
    private Expression mainAssumption;
    private BufferedWriter writer;

    private ArrayList<Expression> expressionsToPrint = new ArrayList<>();

    public WritingUtil(BufferedWriter writer, Expression mainAssumption) {
        this.writer = writer;

        this.mainAssumption = mainAssumption;

        expressionsToPrint.add(parser.parse("A"));
        expressionsToPrint.add(parser.parse("A->B->A"));
        expressionsToPrint.add(parser.parse("B->A"));

        expressionsToPrint.add(parser.parse("A->A->A"));
        expressionsToPrint.add(parser.parse("(A->(A->A))->(A->(A->A)->A)->(A->A)"));
        expressionsToPrint.add(parser.parse("(A->(A->A)->A)->(A->A)"));
        expressionsToPrint.add(parser.parse("A->(A->A)->A"));
        expressionsToPrint.add(parser.parse("A->A"));

        //B - �� ��(�� �����) �- ����� �����
        expressionsToPrint.add(parser.parse("A->B"));
        expressionsToPrint.add(parser.parse("(A->B)->(A->(B->C))->(A->C)"));
        expressionsToPrint.add(parser.parse("(A->(B->C))->(A->C)"));
        expressionsToPrint.add(parser.parse("A->C"));
    }

    private Expression writingUtil(Expression expression, ArrayList<String> namesVector, ArrayList<Expression> expressionsVector) throws IOException {
        if (expression.getOperator() == 'v'){
            return expressionsVector.get(namesVector.indexOf(expression.toString()));
        } else {
            if (expression.getOperator() == '!'){
                return new Negate(writingUtil(expression.getOperands(0), namesVector, expressionsVector));
            } else {
                switch (expression.getOperator()) {
                    case '&':
                        return new Conjunction(writingUtil(expression.getOperands(1), namesVector, expressionsVector), writingUtil(expression.getOperands(2), namesVector, expressionsVector));
                    case '|':
                        return new Disjunction(writingUtil(expression.getOperands(1), namesVector, expressionsVector), writingUtil(expression.getOperands(2), namesVector, expressionsVector));
                    case '-':
                        return new Implication(writingUtil(expression.getOperands(1), namesVector, expressionsVector), writingUtil(expression.getOperands(2), namesVector, expressionsVector));
                }
            }
        }
        return null;
    }

    public void forAxiomsAndAssumptions(Expression expression) throws IOException {
        ArrayList<String> namesVector = new ArrayList<>();
        ArrayList<Expression> expressionsVector = new ArrayList<>();
        namesVector.add("A");
        expressionsVector.add(expression);
        namesVector.add("B");
        expressionsVector.add(mainAssumption);

        for (int i = 0; i < 3; i++){
            writer.write(writingUtil(expressionsToPrint.get(i), namesVector, expressionsVector).toString());
            writer.write('\n');
        }
    }

    public void forMainAssumption() throws IOException {
        ArrayList<String> namesVector = new ArrayList<>();
        ArrayList<Expression> expressionsVector = new ArrayList<>();
        namesVector.add("A");
        expressionsVector.add(mainAssumption);

        for (int i = 3; i < 8; i++){
            writer.write(writingUtil(expressionsToPrint.get(i), namesVector, expressionsVector).toString());
            writer.write('\n');
        }
    }

    public void forMP(Expression exprLeft, Expression exprRight) throws IOException {
        ArrayList<String> namesVector = new ArrayList<>();
        ArrayList<Expression> expressionsVector = new ArrayList<>();
        namesVector.add("A");
        expressionsVector.add(mainAssumption);
        namesVector.add("B");
        expressionsVector.add(exprLeft);
        namesVector.add("C");
        expressionsVector.add(exprRight);

        for (int i = 8; i < 12; i++){
            writer.write(writingUtil(expressionsToPrint.get(i), namesVector, expressionsVector).toString());
            writer.write('\n');
        }
    }
}
