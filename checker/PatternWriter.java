package checker;

import expression.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatternWriter {
    private static ArrayList<Expression> patterns = new ArrayList<>();
    private static final Integer[] CONJ = {-1, -1, -1, -1};
    private static final Integer[] DISJ = {-1, -1, -1, -1};
    private static final Integer[] IMPL = {-1, -1, -1, -1};
    private static final Integer[] NEG = {-1, -1};
    private static final Integer[] HELP1 = {-1};
    private static final Integer[] HELP2 = {-1};

    static {
        patterns.add(ExpressionParser.parse("A"));
        patterns.add(ExpressionParser.parse("A->B->A"));
        patterns.add(ExpressionParser.parse("B->A"));

        patterns.add(ExpressionParser.parse("A->A->A"));
        patterns.add(ExpressionParser.parse("(A->(A->A))->(A->(A->A)->A)->(A->A)"));
        patterns.add(ExpressionParser.parse("(A->(A->A)->A)->(A->A)"));
        patterns.add(ExpressionParser.parse("A->(A->A)->A"));
        patterns.add(ExpressionParser.parse("A->A"));

        //B - �� ��(�� �����) �- ����� �����
        //patterns.add(ExpressionParser.parse("A->B"));
        patterns.add(ExpressionParser.parse("(A->B)->(A->(B->C))->(A->C)"));
        patterns.add(ExpressionParser.parse("(A->(B->C))->(A->C)"));
        patterns.add(ExpressionParser.parse("A->C"));

        CONJ[0] = patterns.size();
        read("asd/conj/00");
        CONJ[1] = patterns.size();
        read("asd/conj/01");
        CONJ[2] = patterns.size();
        read("asd/conj/10");
        CONJ[3] = patterns.size();
        read("asd/conj/11");

        DISJ[0] = patterns.size();
        read("asd/disj/00");
        DISJ[1] = patterns.size();
        read("asd/disj/01");
        DISJ[2] = patterns.size();
        read("asd/disj/10");
        DISJ[3] = patterns.size();
        read("asd/disj/11");

        IMPL[0] = patterns.size();
        read("asd/impl/00");
        IMPL[1] = patterns.size();
        read("asd/impl/01");
        IMPL[2] = patterns.size();
        read("asd/impl/10");
        IMPL[3] = patterns.size();
        read("asd/impl/11");

        NEG[0] = patterns.size();
        read("asd/neg/0");
        NEG[1] = patterns.size();
        read("asd/neg/1");

        HELP1[0] = patterns.size();
        read("asd/help/1");
        HELP2[0] = patterns.size();
        read("asd/help/2");
    }

    private static void read(String path) {
        try {
            String s;
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while ((s = reader.readLine()) != null){
                patterns.add(ExpressionParser.parse(s));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProofOutput output;
    public PatternWriter(ProofOutput output){
        this.output = output;
    }


    private static Expression usePattern(Expression pattern, Map<String, Expression> toInsert){
        if (pattern.getOperator() == 'v'){
            return toInsert.get(pattern.toString());
        } else {
            if (pattern.getOperator() == '!'){
                return new Negate(usePattern(pattern.getOperands(0), toInsert));
            } else {
                switch (pattern.getOperator()) {
                    case '&':
                        return new Conjunction(usePattern(pattern.getOperands(1), toInsert), usePattern(pattern.getOperands(2), toInsert));
                    case '|':
                        return new Disjunction(usePattern(pattern.getOperands(1), toInsert), usePattern(pattern.getOperands(2), toInsert));
                    case '-':
                        return new Implication(usePattern(pattern.getOperands(1), toInsert), usePattern(pattern.getOperands(2), toInsert));
                    default:
                        throw new RuntimeException("Unknown operator");
                }
            }
        }
    }

    public void forAxiomsAndAssumptions(Expression expression, Expression mainAssumption) throws IOException {
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", expression);
        toInsert.put("B", mainAssumption);

        for (int i = 0; i < 3; i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void forMainAssumption(Expression mainAssumption) throws IOException {
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", mainAssumption);

        for (int i = 3; i < 8; i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void forMP(Expression exprLeft, Expression exprRight, Expression mainAssumption) throws IOException {
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", mainAssumption);
        toInsert.put("B", exprLeft);
        toInsert.put("C", exprRight);
        for (int i = 8; i < 11; i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void useBin(Expression A, Expression B, char op, boolean resA, boolean resB) throws IOException {
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", A);
        toInsert.put("B", B);
        int from = (resA ? 2 : 0) + (resB ? 1 : 0);
        int to = from;
        if (op == '&'){
            to = from == 3 ? DISJ[0] : CONJ[from + 1];
            from = CONJ[from];
        } else if (op == '|') {
            to = from == 3 ? IMPL[0] : DISJ[from + 1];
            from = DISJ[from];
        } else if (op == '-'){
            to = from == 3 ? NEG[0] : IMPL[from + 1];
            from = IMPL[from];
        }
        for (int i = from; i < to; i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void useUn(Expression A, boolean resA) throws IOException {
        if (!resA){
            return;
        }
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", A);
        int from = NEG[resA ? 1 : 0];
        for (int i = from; i < (resA ? HELP1[0] : NEG[1]); i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void useHelp1(Expression A) throws IOException {
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", A);
        for (int i = HELP1[0]; i < HELP2[0]; i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void useHelp2(Expression A, Expression B) throws IOException {
        Map<String, Expression> toInsert = new HashMap<>();
        toInsert.put("A", A);
        toInsert.put("C", B);
        for (int i = HELP2[0]; i < patterns.size(); i++){
            output.write(usePattern(patterns.get(i), toInsert));
        }
    }

    public void setProofOutput(ProofOutput output){
        this.output = output;
    }
}