package checker;

import expression.Expression;
import expression.ExpressionParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

public class WritingUtil {
    private ExpressionParser parser = new ExpressionParser();
    private Expression mainAssumption;
    private BufferedWriter writer;

    private Vector<Expression> expressionsToPrint = new Vector<>();

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

    private void writingUtil(Expression expression, Vector<String> namesVector, Vector<Expression> expressionsVector) throws IOException {
        if (expression.getOperator() == 'v'){
            writer.write("(" + expressionsVector.elementAt(namesVector.indexOf(expression.toString())).toString() + ")");
        } else {
            if (expression.getOperator() == '!'){
                writingUtil(expression.getOperands(0), namesVector, expressionsVector);
            } else {
                writer.write("(");
                writingUtil(expression.getOperands(1), namesVector, expressionsVector);
                writer.write((String)(expression.getOperator() == '-' ? "->" : expression.getOperator()));
                writingUtil(expression.getOperands(2), namesVector, expressionsVector);
                writer.write(")");
            }
        }
    }

    public void forAxiomsAndAssumptions(Expression expression) throws IOException {
        Vector<String> namesVector = new Vector<>();
        Vector<Expression> expressionsVector = new Vector<>();
        namesVector.add("A");
        expressionsVector.add(expression);
        namesVector.add("B");
        expressionsVector.add(mainAssumption);

        for (int i = 0; i < 3; i++){
            writingUtil(expressionsToPrint.elementAt(i), namesVector, expressionsVector);
            writer.write('\n');
        }
    }

    public void forMainAssumption() throws IOException {
        Vector<String> namesVector = new Vector<>();
        Vector<Expression> expressionsVector = new Vector<>();
        namesVector.add("A");
        expressionsVector.add(mainAssumption);

        for (int i = 3; i < 8; i++){
            writingUtil(expressionsToPrint.elementAt(i), namesVector, expressionsVector);
            writer.write('\n');
        }
    }

    public void forMP(Expression exprLeft, Expression exprRight) throws IOException {
        Vector<String> namesVector = new Vector<>();
        Vector<Expression> expressionsVector = new Vector<>();
        namesVector.add("A");
        expressionsVector.add(mainAssumption);
        namesVector.add("B");
        expressionsVector.add(exprLeft);
        namesVector.add("C");
        expressionsVector.add(exprRight);

        for (int i = 8; i < 12; i++){
            writingUtil(expressionsToPrint.elementAt(i), namesVector, expressionsVector);
            writer.write('\n');
        }
    }
}
